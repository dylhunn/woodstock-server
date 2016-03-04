package harmony.chord;

import harmony.core.Library;
import harmony.interval.Interval;
import harmony.interval.IntervalQuality;
import harmony.interval.MelodicInterval;
import harmony.single.Note;
import harmony.single.Pitch;
import harmony.single.VoiceRange;
import harmony.symbol.chordsymbol.symboltypes.ChordSymbol;

import java.util.*;

// Subclasses of Chord should override meetsResolutionRequirements(Chord) and
// chordVoicingMap()
// Default behavior is a typical triad with no special resolution requirements
// TODO Implement score caching
// TODO Handle suspension resolutions -- evaluate so that they should resolve
// stepwise
// TODO Make scoring more predictable; each factor should account for a
// consistent portion of the weighting
// TODO Bonus for open chords
public abstract class Chord {

	// Single-chord evaluation coefficients for scoring
	public static final double TOTAL_SINGLE_SCORE_WEIGHT = .45;
	public static final double RANGE_WEIGHT = 3;
	public static final double RANGE_CENTRALITY_WEIGHT = 1;
	public static final double SPACING_WEIGHT = 10;
	public static final double DOUBLING_WEIGHT = 1.5;
	public static final double MANDATORY_ROLES_WEIGHT = 10;
	public static final double CROSSING_WEIGHT = 4;
	public static final double TOO_NARROW_WEIGHT = 2;
	public static final double DOUBLED_VOICE_WEIGHT = 1;

	// Multi-chord evaluation coefficients for scoring
	public static final double TOTAL_PROGRESSION_SCORE_WEIGHT = .55;
	public static final double VOICE_LEADING_DISTANCE_WEIGHT = 2;
	public static final double APPOGGIATURA_WEIGHT = 1;
	public static final double ILLEGAL_CONSECUIVES_WEIGHT = 100;
	public static final double ILLEGAL_DIRECT_INTERVAL_WEIGHT = 10;
	public static final double ILLEGAL_MELODIC_INTERVAL_WEIGHT = 2;
	public static final double MEETS_RESOLUTION_RULES_WEIGHT = 2;
	public static final double INTERCHORD_VOICE_CROSSING = 5;

	// Maximum points to award randomly
	public static final double MAX_RANDOM = 0;

	// Settings
	public static final int APPOGGIATURA_LEAP = 5; // Minimum interval that qualifies as a leap
	public static final int STEP_INTERVAL = 2;
	public static final boolean COUNT_BASS_LEADING_BETWEEN_CHORDS = false;
	public static final boolean COUNT_BASS_DISTANCE_WITHIN_CHORD = false;
	public static final int TOO_NARROW = 4; // Less than this interval is considered too narrow

	private List<Note> notes;
	private Optional<Double> singleChordScore; // for caching
	private final ChordSymbol symbol; // To access data from the enclosing symbol

	// incoming list of notes should be in voice order, bass to soprano
	// it will not be sorted internally because voice-crossing is possible
	public Chord(Collection<Note> notes, ChordSymbol sym) {
		if (notes.size() < 2)
			throw new RuntimeException("Cannot construct empty or one-note chord.");
		this.notes = new ArrayList<>(notes);
		singleChordScore = Optional.empty();
		symbol = sym;
	}

	public int numVoices() {
		return notes.size();
	}

	public Note getVoice(int index) {
		return notes.get(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Chord other = (Chord) obj;
		if (numVoices() != other.numVoices())
			return false;
		for (int i = 0; i < numVoices(); i++) {
			if (!getVoice(i).equals(other.getVoice(i)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String output = "{";
		for (Note n : notes) {
			output += n.toString();
			output += ", ";
		}
		return output.substring(0, output.length() - 2) + "}";
	}

	/*
	 * Evaluation functions below this point.
	 */

	public final double evaluateAloneAndInContext(Chord next, Optional<Chord> afterNext) {
		return (evaluateAlone() * TOTAL_SINGLE_SCORE_WEIGHT + evaluateInContext(next, afterNext) * TOTAL_PROGRESSION_SCORE_WEIGHT)
				/ (TOTAL_SINGLE_SCORE_WEIGHT + TOTAL_PROGRESSION_SCORE_WEIGHT) + (Math.random() * MAX_RANDOM);
	}

	// Recomputes and prints the evaluation results. Use for debugging only.
	// This function may become out-of-sync with the actual evaluation functions.
	public void printEvaluationData(Optional<Chord> next, Optional<Chord> afterNext) {
		System.out.println("Single chord");
		System.out.println("stepwiseVoiceRangeErrors " + stepwiseVoiceRangeErrors());
		System.out.println("rangeCentralityDistance " + rangeCentralityDistance());
		System.out.println("averageStepwiseVoiceSpacingError " + averageStepwiseVoiceSpacingError());
		System.out.println("voiceDoublingErrors " + voiceDoublingErrors());
		System.out.println("hasMandatoryRoles " + hasMandatoryRoles());
		System.out.println("voiceCrossingSteps " + voiceCrossingSteps());
		if (!next.isPresent())
			return;

		System.out.println("Contextual");
		System.out.println("averageStepwiseVoiceLeadingDistance "
				+ averageStepwiseVoiceLeadingDistance(next.get(), COUNT_BASS_LEADING_BETWEEN_CHORDS));
		System.out.println("numAppoggiaturas " + ((afterNext.isPresent()) ? numAppoggiaturas(next.get(), afterNext.get(), true) : 0));
		System.out.println("numIllegalConsecutiveIntervals " + numIllegalConsecutiveIntervals(next.get()));
		System.out.println("containsIllegalDirectInterval " + containsIllegalDirectInterval(next.get()));
		System.out.println("containsIllegalMelodicInterval " + containsIllegalMelodicInterval(next.get()));
		System.out.println("meetsResolutionRequirements " + meetsResolutionRequirements(next.get()));
		System.out.println();
	}

	// Evaluate the quality of the voicing of this chord.
	// Higher scores are better.
	public final double evaluateAlone() {
		if (singleChordScore.isPresent())
			return singleChordScore.get(); // Use caching
		double score = 0;

		score -= stepwiseVoiceRangeErrors() * RANGE_WEIGHT;
		score -= rangeCentralityDistance() * RANGE_CENTRALITY_WEIGHT;
		score -= averageStepwiseVoiceSpacingError() * SPACING_WEIGHT;
		score -= voiceDoublingErrors() * DOUBLING_WEIGHT;
		score -= (hasMandatoryRoles() ? 0 : MANDATORY_ROLES_WEIGHT);
		score -= voiceCrossingSteps() * CROSSING_WEIGHT;
		score -= tooNarrowSteps() * TOO_NARROW_WEIGHT;
		score -= numDoubledVoices() * DOUBLED_VOICE_WEIGHT;

		singleChordScore = Optional.of(score);
		return score;
	}

	// Evaluation functions for a single chord

	protected int stepwiseVoiceRangeErrors() {
		int stepsError = 0;
		for (int i = 0; i < numVoices(); i++) {
			VoiceRange.VoiceType expectedRegister = VoiceRange.getVoiceType(i, numVoices());
			stepsError += Math.abs(VoiceRange.getRange(expectedRegister).contains(getVoice(i)));
		}
		return stepsError;
	}

	// Gives the average number of steps each voice is from the center of its range
	// Useful as a very slight factor to favor centralized voices in the ranking
	protected double rangeCentralityDistance() {
		double totalDistance = 0;
		for (int i = 0; i < numVoices(); i++) {
			Interval expectedRegister = VoiceRange.getRange(VoiceRange.getVoiceType(i, numVoices()));
			totalDistance += Math.abs(
					(expectedRegister.HIGH_NOTE.midiNumber() + expectedRegister.LOW_NOTE.midiNumber()) / 2.0 - getVoice(i).midiNumber());
		}
		return totalDistance / numVoices();
	}

	protected double averageStepwiseVoiceSpacingError() {
		int totalError = 0;
		// the bass is exempt from spacing checks
		for (int i = (COUNT_BASS_DISTANCE_WITHIN_CHORD ? 0 : 1); i < numVoices() - 1; i++) {
			int semitones = new Interval(getVoice(i), getVoice(i + 1)).NUM_SEMITONES;
			semitones = Math.abs(semitones); // could be negative in voice crossing
			totalError += Math.max(0, semitones - IntervalQuality.semitonesInMajorOrPerfectInterval(8));
		}
		return (double) totalError / numVoices();
	}

	protected int voiceDoublingErrors() { // TODO update for chords with more than 4 voices
		Map<Pitch.Role, Integer> roleCount = symbol.chordVoicingMap(); // TODO this ignores modifications to the chord
		for (int i = 0; i < numVoices(); i++) {
			if (roleCount.containsKey(getVoice(i).PITCH.ROLE)) {
				roleCount.put(getVoice(i).PITCH.ROLE, roleCount.get(getVoice(i).PITCH.ROLE) - 1);
			}
		}
		return roleCount.values().parallelStream().mapToInt(i -> Math.abs(i)).sum();
	}

	protected boolean hasMandatoryRoles() {
		Set<Pitch.Role> roles = symbol.mandatoryRoles();
		for (int i = 0; i < numVoices(); i++) {
			if (roles.contains(getVoice(i).PITCH.ROLE))
				roles.remove(getVoice(i).PITCH.ROLE);
		}
		return roles.isEmpty();
	}

	protected int voiceCrossingSteps() {
		int steps = 0;
		for (int i = 0; i < numVoices() - 1; i++) {
			steps += Math.max(0, getVoice(i).midiNumber() - getVoice(i + 1).midiNumber());
		}
		return steps;
	}

	protected int tooNarrowSteps() {
		int tooNarrowSteps = 0;
		for (int i = 0; i < numVoices() - 1; i++) {
			Interval interval = new Interval(getVoice(i), getVoice(i + 1));
			if (interval.NUM_SEMITONES < TOO_NARROW)
				tooNarrowSteps += (TOO_NARROW - interval.NUM_SEMITONES);
		}
		return tooNarrowSteps;
	}

	// Gets the number of notes that are repeated in the same register
	protected int numDoubledVoices() {
		int num = 0;
		for (int i = 0; i < numVoices() - 1; i++) {
			if (getVoice(i).equals(getVoice(i + 1)))
				num++;
		}
		return num;
	}

	// Evaluation functions for multiple chords

	// Evaluate the quality of the voice leading in this progression.
	// Higher scores are better.
	public final double evaluateInContext(Chord next, Optional<Chord> afterNext) {
		double score = 0;
		double averageStepwiseDistance = averageStepwiseVoiceLeadingDistance(next, COUNT_BASS_LEADING_BETWEEN_CHORDS);
		// does an appoggiatura excuse a large leap?
		// TODO should we count the bass unconditionally?
		int numAppoggiaturas = (afterNext.isPresent()) ? numAppoggiaturas(next, afterNext.get(), true) : 0;
		double numIllegalConsecutiveIntervals = numIllegalConsecutiveIntervals(next);
		boolean containsIllegalDirectInterval = containsIllegalDirectInterval(next);
		boolean containsIllegalMelodicInterval = containsIllegalMelodicInterval(next);
		boolean meetsResolutionRequirements = meetsResolutionRequirements(next);
		int interChordVoiceCrossing = interChordVoiceCrossing(next);

		score -= averageStepwiseDistance * VOICE_LEADING_DISTANCE_WEIGHT;
		score += numAppoggiaturas * APPOGGIATURA_WEIGHT;
		score -= numIllegalConsecutiveIntervals * ILLEGAL_CONSECUIVES_WEIGHT;
		score -= containsIllegalDirectInterval ? ILLEGAL_DIRECT_INTERVAL_WEIGHT : 0;
		score -= containsIllegalMelodicInterval ? ILLEGAL_MELODIC_INTERVAL_WEIGHT : 0;
		score += meetsResolutionRequirements ? MEETS_RESOLUTION_RULES_WEIGHT : 0;
		score -= interChordVoiceCrossing * INTERCHORD_VOICE_CROSSING;

		return score;
	}

	// Gives the average voice leading distance between a voice and the corresponding voice in the next chord.
	// If the chords have different numbers of voices, finds the nearest voice in the next chord.
	// This function is not necessarily commutative if the chords have different numbers of voices!
	protected double averageStepwiseVoiceLeadingDistance(Chord other, boolean includeBass) {
		int numVoicesUsed = (includeBass ? numVoices() : numVoices() - 1);
		List<MelodicInterval> melodicIntervals = melodicIntervals(other);
		int totalDistance = 0;
		for (int i = (includeBass ? 0 : 1); i < melodicIntervals.size(); i++) {
			totalDistance += Math.abs(melodicIntervals.get(i).NUM_SEMITONES);
		}
		return (double) totalDistance / numVoicesUsed;
	}

	protected int numAppoggiaturas(Chord next, Chord afterNext, boolean includeBass) {
		int numAppoggiaturas = 0;
		for (int i = (includeBass ? 0 : 1); i < numVoices(); i++) {
			MelodicInterval firstInterval = new MelodicInterval(this.getVoice(i), next.getVoice(i));
			if (firstInterval.INTERVAL < APPOGGIATURA_LEAP)
				continue;
			MelodicInterval secondInterval = new MelodicInterval(next.getVoice(i), afterNext.getVoice(i));
			if (secondInterval.INTERVAL != STEP_INTERVAL)
				continue;
			// the next note must step "back" in the direction of the previous note
			if (firstInterval.direction() != secondInterval.direction() && firstInterval.direction() != MelodicInterval.Direction.UNISON
					&& secondInterval.direction() != MelodicInterval.Direction.UNISON) {
				numAppoggiaturas++;
			}
		}
		// there is not actually any large leap in the chord, so all leaps are vacuously appoggiaturas
		return numAppoggiaturas;
	}

	// Checks for illegal consecutive unisons, fifths, octaves, or compounds.
	// Excludes legal unequal fifths, but includes fifths or octaves in contrary motion.
	protected int numIllegalConsecutiveIntervals(Chord next) {
		if (numVoices() != next.numVoices())
			return 0; // TODO chords with unequal voice counts
		int numIllegal = 0;
		List<MelodicInterval> motions = melodicIntervals(next);
		for (int i = 0; i < motions.size() - 1; i++) {
			for (int j = i + 1; j < motions.size(); j++) {
				Interval firstInterval = new Interval(motions.get(i).FIRST_NOTE, motions.get(j).FIRST_NOTE);
				Interval secondInterval = new Interval(motions.get(i).SECOND_NOTE, motions.get(j).SECOND_NOTE);
				// If they are different reduced intervals, we are good
				// The special case ensures unisons and octaves are treated like the same interval
				if (firstInterval.REDUCED_INTERVAL + secondInterval.REDUCED_INTERVAL != 9
						&& firstInterval.REDUCED_INTERVAL != secondInterval.REDUCED_INTERVAL) {
					continue;
				}
				// If they are legal intervals, we are good
				if (!Library.ILLEGAL_INTERVALS.contains(firstInterval.REDUCED_INTERVAL)) {
					continue;
				}
				// If they are legal unequal fifths (P5->d5), we are good
				// we allow the second fifth to be multiple-diminished
				if (firstInterval.REDUCED_INTERVAL == 5 && firstInterval.getQuality().type() == IntervalQuality.Type.PERFECT
						&& secondInterval.getQuality().type() == IntervalQuality.Type.DIMINISHED) {
					continue;
				}
				// If there is no motion at all, we are good
				if (motions.get(i).direction() == MelodicInterval.Direction.UNISON
						&& motions.get(j).direction() == MelodicInterval.Direction.UNISON) {
					continue;
				}
				numIllegal++;
			}
		}
		return numIllegal;
	}

	// Checks for illegal direct/hidden motion between bass and soprano where soprano is not stepwise.
	protected boolean containsIllegalDirectInterval(Chord next) {
		MelodicInterval sopranoMotion = new MelodicInterval(this.getVoice(numVoices() - 1), next.getVoice(next.numVoices() - 1));
		MelodicInterval bassMotion = new MelodicInterval(this.getVoice(0), next.getVoice(0));
		Interval endingInterval = new Interval(next.getVoice(0), next.getVoice(next.numVoices() - 1));
		// If the ending interval is legal, so we are good
		if (!Library.ILLEGAL_INTERVALS.contains(endingInterval.INTERVAL))
			return false;
		// If it is not similar motion, we are good
		if (sopranoMotion.direction() != bassMotion.direction())
			return false;
		// If the soprano is stepwise, we are good
		if (sopranoMotion.INTERVAL == STEP_INTERVAL)
			return false;
		// If the target interval is a diminished fifth, we are good
		return !(endingInterval.INTERVAL == 5 && endingInterval.getQuality().type() == IntervalQuality.Type.DIMINISHED
				&& endingInterval.getQuality().getDegree() == 1);
	}

	protected boolean containsIllegalMelodicInterval(Chord next) {
		MelodicInterval sopranoMotion = new MelodicInterval(this.getVoice(numVoices() - 1), next.getVoice(next.numVoices() - 1));
		if (sopranoMotion.NUM_SEMITONES == 6)
			return true; // Tritone TODO support negatives
		return sopranoMotion.INTERVAL == 2 && sopranoMotion.getQuality().type() == IntervalQuality.Type.AUGMENTED
				&& sopranoMotion.getQuality().getDegree() == 1;
	}

	protected int interChordVoiceCrossing(Chord next) {
		int numCrossSteps = 0;
		if (numVoices() != next.numVoices())
			return numCrossSteps;
		for (int i = 0; i < numVoices() - 1; i++) {
			if (getVoice(i).compareTo(next.getVoice(i + 1)) > 0)
				numCrossSteps += (getVoice(i).midiNumber() - next.getVoice(i + 1).midiNumber());
			if (next.getVoice(i).compareTo(getVoice(i + 1)) > 0)
				numCrossSteps += (next.getVoice(i).midiNumber() - getVoice(i + 1).midiNumber());
		}
		return numCrossSteps;
	}

	// Subclasses that have specific methods of resolution can override this method
	// TODO handle chord modifications
	protected abstract boolean meetsResolutionRequirements(Chord next);

	// Get the intervals between each pair of voices, with the interval involving the lowest note in this chord listed first.
	// If the chords have different numbers of voices, include an interval between each voice in this chord and the nearest
	// voice in the next.
	protected final List<MelodicInterval> melodicIntervals(Chord other) {
		List<MelodicInterval> intervals = new ArrayList<>();
		for (int i = 0; i < numVoices(); i++) {
			if (numVoices() == other.numVoices()) {
				intervals.add(new MelodicInterval(getVoice(i), other.getVoice(i)));
			} else {
				MelodicInterval smallestInterval = new MelodicInterval(getVoice(i), other.getVoice(0));
				for (int j = 1; j < other.numVoices(); j++) {
					MelodicInterval candidate = new MelodicInterval(getVoice(i), other.getVoice(j));
					if (candidate.smallerThan(smallestInterval))
						smallestInterval = candidate; // TODO handle same-distance candidates better
				}
				intervals.add(smallestInterval);
			}
		}
		return intervals;
	}
}
