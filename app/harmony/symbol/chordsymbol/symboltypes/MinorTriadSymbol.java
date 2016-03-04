package harmony.symbol.chordsymbol.symboltypes;

import harmony.chord.Chord;
import harmony.chord.Triad;
import harmony.interval.Interval;
import harmony.interval.IntervalQuality;
import harmony.single.Note;
import harmony.single.Pitch;
import harmony.symbol.chordsymbol.support.ChordConstraint;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MinorTriadSymbol extends TriadSymbol {

	public MinorTriadSymbol(Pitch root, Inversion inversion) {
		super(root, inversion);
	}

	public MinorTriadSymbol(Pitch root, Inversion inversion, Set<ChordSymbolAlteration> modifications,
			Optional<ChordConstraint> constraint) {
		super(root, inversion, modifications, constraint);
	}

	public MinorTriadSymbol(Pitch root, Pitch bass, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		super(root, bass, modifications, constraint);
	}

	@Override
	protected Set<Pitch> chordPitchesWithoutModifications() {
		Set<Pitch> pitches = new HashSet<>();
		pitches.add(ROOT);
		// -1 used for pitch class because Note object is temporary and local
		// TODO cleaner solution
		pitches.add(new Interval(new Note(ROOT, -1), 3, new IntervalQuality(IntervalQuality.Type.MINOR), Pitch.Role.THIRD).HIGH_NOTE.PITCH);
		pitches.add(
				new Interval(new Note(ROOT, -1), 5, new IntervalQuality(IntervalQuality.Type.PERFECT), Pitch.Role.FIFTH).HIGH_NOTE.PITCH);
		return pitches;
	}

	@Override
	protected Chord buildChord(Collection<Note> notes) {
		return new Triad(notes, this);
	}

	@Override
	protected String name() {
		return "Minor Chord";
	}
}
