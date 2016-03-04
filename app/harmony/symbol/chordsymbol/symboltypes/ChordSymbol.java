package harmony.symbol.chordsymbol.symboltypes;

import harmony.chord.Chord;
import harmony.core.Library;
import harmony.interval.Interval;
import harmony.single.Key;
import harmony.single.Note;
import harmony.single.Pitch;
import harmony.single.VoiceRange;
import harmony.symbol.Symbol;
import harmony.symbol.chordsymbol.support.ChordConstraint;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;
import harmony.symbol.chordsymbol.support.InversionConstraint;

import java.util.*;

// Warning: when subclassing ChordSymbol, all constructors must be provided with
// calls to super().
public abstract class ChordSymbol implements Symbol {

	protected final Pitch ROOT;
	protected final ChordConstraint CONSTRAINT;

	private final InversionConstraint inversionConstraint;

	// TODO does this create excessive memory pressure?
	private Optional<List<Chord>> realizations = Optional.empty(); // for caching

	private Set<ChordSymbolAlteration> modifications;

	public ChordSymbol(Pitch root, Inversion inversion) {
		ROOT = root;
		inversionConstraint = new InversionConstraint(inversion);
		CONSTRAINT = ChordConstraint.DEFAULT_CONSTRAINT;
		modifications = new HashSet<>();
	}

	public ChordSymbol(Pitch root, Inversion inversion, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		ROOT = root;
		inversionConstraint = new InversionConstraint(inversion);
		if (constraint.isPresent())
			CONSTRAINT = constraint.get();
		else
			CONSTRAINT = ChordConstraint.DEFAULT_CONSTRAINT;
		this.modifications = modifications;
	}

	public ChordSymbol(Pitch root, Pitch bass, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		ROOT = root;
		if (constraint.isPresent())
			CONSTRAINT = constraint.get();
		else
			CONSTRAINT = ChordConstraint.DEFAULT_CONSTRAINT;
		this.modifications = modifications;
		inversionConstraint = new InversionConstraint(bass);
	}

	@Override
	public ChordSymbol getChordSymbol(Key key) {
		return this;
	}

	public Pitch getRoot() {
		return ROOT;
	}
	
	public boolean knowsBass() {
		for (Pitch p : chordPitches()) {
			if (inversionConstraint.isSatisfied(p)) return true;
		}
		return false;
	}

	public Pitch getBass() {
		for (Pitch p : chordPitches()) {
			if (inversionConstraint.isSatisfied(p)) return p;
		}
		throw new RuntimeException("No pitch was found as the bass.");
	}

	/*
	 * public Pitch getExpectedBass(Key key) { return
	 * InversionConstraint.getBass(key, ROOT); }
	 */

	public void attachModifications(Collection<ChordSymbolAlteration> c) {
		realizations = Optional.empty(); // Clear cache
		modifications.addAll(c);
	}

	public final Set<Pitch> chordPitches() { // TODO cache?
		Set<Pitch> inputPitches = chordPitchesWithoutModifications();
		for (ChordSymbolAlteration m : modifications) {
			switch (m.TYPE) {
			case REMOVE:
				inputPitches.remove(m.PITCH);
				break;
			case ADD:
				inputPitches.add(m.PITCH);
				break;
			default:
				throw new RuntimeException("Unknown chord modification type: " + m.TYPE);
			}
		}
		return inputPitches;
	}

	public final Map<Pitch.Role, Integer> chordVoicingMap() { // TODO should this really be public?
		// TODO Add alteration support
		return chordVoicingMapWithoutModifications();
	}

	public final Set<Pitch.Role> mandatoryRoles() {
		Set<Pitch.Role> res = new HashSet<>();
		res.addAll(mandatoryRolesWithoutModifications());
		for (ChordSymbolAlteration a : modifications) {
			if (a.TYPE.equals(ChordSymbolAlteration.Type.REMOVE)) {
				res.remove(a.PITCH.ROLE);
			} else if (a.TYPE.equals(ChordSymbolAlteration.Type.ADD)) {
				res.add(a.PITCH.ROLE);
			}
		}
		return res;
	}

	// Return a map from each role to its expected number of occurrences in the chord
	// This should be overridden in child classes for other chord types
	protected abstract Map<Pitch.Role, Integer> chordVoicingMapWithoutModifications();

	// Return a set of roles that are mandatory in the chord.
	protected abstract Set<Pitch.Role> mandatoryRolesWithoutModifications();

	// Returns a list of many realizations for this chord
	// The list is sorted by each individual chord's voicing score, best to worst
	public List<Chord> getChordRealizations(int numVoices) {
		if (this.realizations.isPresent())
			return this.realizations.get();

		Set<Pitch> inputPitches = chordPitches();

		// Each set in the list is the set of all possible pitches for that voice
		List<Set<Note>> candidateSetsByVoice = new ArrayList<>();
		for (int i = 0; i < numVoices; i++) {
			Set<Note> voiceCandidates = new HashSet<>();
			VoiceRange.VoiceType range = VoiceRange.getVoiceType(i, numVoices);
			Interval limits = VoiceRange.getRange(range);

			for (Pitch p : inputPitches) {
				// The pitch is specified for the bass note because inversion is specified
				if (i == 0 && !inversionConstraint.isSatisfied(p))
					continue;
				// TODO better solution than +1 and -1 for covering all out-of-range notes
				for (int pitchClass = limits.LOW_NOTE.PITCH_CLASS - 1; pitchClass <= limits.HIGH_NOTE.PITCH_CLASS + 1; pitchClass++) {
					Note candidate = new Note(p, pitchClass);
					if (Math.abs(limits.contains(candidate)) < Library.OUT_OF_RANGE_THRESHHOLD)
						voiceCandidates.add(candidate);
				}
			}
			candidateSetsByVoice.add(voiceCandidates);
		}

		// must use list because elements with the same score should not be considered equal
		List<Chord> realizations = new ArrayList<>();
		generateAllChords(realizations, candidateSetsByVoice);

		// sort so that the highest-scoring chords are first
		Collections.sort(realizations, (c1, c2) -> {
			if (c1.evaluateAlone() == c2.evaluateAlone())
				return 0;
			return (c1.evaluateAlone() > c2.evaluateAlone() ? -1 : 1);
		});

		this.realizations = Optional.of(realizations); // cache
		return realizations;
	}

	private void generateAllChords(List<Chord> realizations, List<Set<Note>> candidateSetsByVoice) {
		generateAllChords(realizations, candidateSetsByVoice, new ArrayList<Note>(), 0);
		// TODO handle more gracefully
		if (realizations.isEmpty())
			throw new RuntimeException("No chord realization possible; constraint might be too strict.");
	}

	private void generateAllChords(List<Chord> realizations, List<Set<Note>> candidateSetsByVoice, List<Note> accumulatedVoices,
			int currentPosition) {
		if (currentPosition >= candidateSetsByVoice.size()) {
			Chord candidate = buildChord(accumulatedVoices);
			if (CONSTRAINT.isSatisfiedBy(candidate))
				realizations.add(candidate); // TODO improve efficiency for simple pitch/voice constraints
			return;
		}
		for (Note n : candidateSetsByVoice.get(currentPosition)) {
			accumulatedVoices.add(n);
			generateAllChords(realizations, candidateSetsByVoice, accumulatedVoices, currentPosition + 1);
			accumulatedVoices.remove(accumulatedVoices.size() - 1); // backtrack
		}
	}

	protected String modificationSuffix() {
		String suffix = "";
		for (ChordSymbolAlteration m : modifications) {
			suffix += (" " + m.toString());
		}
		return suffix;
	}

	@Override
	public final String toString() {
		return ROOT.toString() + " " + name() + modificationSuffix() + " " + inversionConstraint.getText();
	}

	protected abstract Set<Pitch> chordPitchesWithoutModifications();

	// Every ChordSymbol type should be able to construct the appropriate, corresponding subclass of Chord.
	protected abstract Chord buildChord(Collection<Note> notes);

	// Returns the name of the chord type (such as "Major" or "Minor 7th")
	protected abstract String name();
}
