package harmony.interval;

import harmony.core.Library;
import harmony.single.Accidental;
import harmony.single.Note;
import harmony.single.Pitch;

public class Interval {

	public static enum IntervalType {
		HARMONIC, MELODIC
	}

	public final Note LOW_NOTE;
	public final Note HIGH_NOTE;

	public final int INTERVAL; // 1st, 2nd, etc
	public final int REDUCED_INTERVAL; // no larger than, but including, an octave
	public final int NUM_SEMITONES;
	public final int REDUCED_NUM_SEMITONES; // no larger than, but including, 12 semitones

	private final IntervalQuality quality;

	public Interval(Note one, Note two) {
		LOW_NOTE = Library.min(one, two);
		HIGH_NOTE = Library.max(one, two);
		INTERVAL = computeInterval(LOW_NOTE, HIGH_NOTE);
		REDUCED_INTERVAL = computeReducedInterval(INTERVAL);
		// This can be negative! TODO: Check all invocations for proper use; client must take absolute value.
		NUM_SEMITONES = HIGH_NOTE.midiNumber() - LOW_NOTE.midiNumber();
		REDUCED_NUM_SEMITONES = computeReducedNumSemitones(HIGH_NOTE.midiNumber() - LOW_NOTE.midiNumber());
		quality = new IntervalQuality(INTERVAL, NUM_SEMITONES);
	}

	public Interval(Note bottom, int interval, IntervalQuality quality, Pitch.Role newRole) { // TODO simplify
		int newPitchNameOrdinal = (bottom.PITCH.NAME.ordinal() + interval - 1) % Pitch.NUM_DISTINCT_NOTE_NAMES;
		Pitch.Name newPitchName = Pitch.Name.values()[newPitchNameOrdinal];

		int newPitchClass = bottom.PITCH_CLASS + ((bottom.PITCH.NAME.ordinal() + interval - 1) / Pitch.NUM_DISTINCT_NOTE_NAMES);

		int semitones = IntervalQuality.semitonesInInterval(interval, quality);
		int semitonesInNaturalInterval = new Interval(bottom,
				new Note(new Pitch(newPitchName, new Accidental(Accidental.AccidentalType.NATURAL), newRole), newPitchClass)).NUM_SEMITONES;

		// accidentalSteps away from NATURAL; -2 is double-flat, 1 is sharp, etc
		int newNoteAccidentalSteps = semitones - semitonesInNaturalInterval;
		Pitch newPitch = new Pitch(newPitchName, new Accidental(newNoteAccidentalSteps), newRole);
		Note newNote = new Note(newPitch, newPitchClass);

		// Load constants
		LOW_NOTE = Library.min(bottom, newNote);
		HIGH_NOTE = Library.max(bottom, newNote);
		INTERVAL = interval;
		REDUCED_INTERVAL = computeReducedInterval(INTERVAL);
		NUM_SEMITONES = HIGH_NOTE.midiNumber() - LOW_NOTE.midiNumber();
		REDUCED_NUM_SEMITONES = computeReducedNumSemitones(HIGH_NOTE.midiNumber() - LOW_NOTE.midiNumber());
		this.quality = quality;
	}

	public int getInterval() {
		return INTERVAL;
	}

	public int getReducedInterval() {
		return REDUCED_INTERVAL;
	}

	public int getNumSemitones() {
		return NUM_SEMITONES;
	}

	public int getReducedNumSemitones() {
		return REDUCED_NUM_SEMITONES;
	}

	public IntervalQuality getQuality() {
		return quality;
	}

	/*
	 * Indicates whether the note is contained in this interval. If it is not,
	 * returns the number of semitones by which the interval is out of range,
	 * negative for too low and positive for too high.
	 */
	public int contains(Note n) {
		if (n.comparePitch(LOW_NOTE) < 0)
			return n.midiNumber() - LOW_NOTE.midiNumber();
		if (n.comparePitch(HIGH_NOTE) > 0)
			return n.midiNumber() - HIGH_NOTE.midiNumber();
		return 0; // in range
	}

	public boolean smallerThan(Interval other) {
		return (NUM_SEMITONES < other.NUM_SEMITONES); // TODO Also compare by spelling
	}

	/*
	 * Reduces the width of the interval to between a unison (1) and an octave
	 * (8), inclusive.
	 */
	protected final static int computeReducedInterval(int interval) {
		while (interval > Pitch.NUM_DISTINCT_NOTE_NAMES + 1)
			interval -= Pitch.NUM_DISTINCT_NOTE_NAMES;
		return interval;
	}

	protected final static int computeReducedNumSemitones(int semitones) {
		boolean negative = false; // TODO cleanup
		if (semitones < 0) {
			negative = true;
			semitones *= -1;
		}
		int reducedNumSemitones = (semitones) % Pitch.NUM_DISTINCT_PITCHES;
		if (reducedNumSemitones == 0 && semitones != 0)
			reducedNumSemitones = Pitch.NUM_DISTINCT_PITCHES; // Octaves are not reduced
		if (negative)
			reducedNumSemitones *= -1;
		return reducedNumSemitones;
	}

	/**
	 * Return the numeric component of the interval between the low and high
	 * notes. NOTE: a unison returns one, a "second" returns two, etc. Example:
	 * computeInterval(B2, D#3) should return 10.
	 */
	private static int computeInterval(Note low, Note high) {
		return (high.PITCH.NAME.ordinal() - low.PITCH.NAME.ordinal()) + Pitch.NUM_DISTINCT_NOTE_NAMES * (high.PITCH_CLASS - low.PITCH_CLASS)
				+ 1;
	}
}
