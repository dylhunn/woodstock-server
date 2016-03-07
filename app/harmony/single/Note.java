package harmony.single;

import java.util.Optional;
import java.util.Scanner;

import harmony.interval.IntervalQuality;

public class Note implements Comparable<Note> {

	public static final int C0_MIDI_NUMBER = 12;

	public final Pitch PITCH;
	public final int PITCH_CLASS;

	private Optional<Integer> midiNumber = Optional.empty();

	public Note(Pitch pitch, int pitchClass) {
		PITCH = pitch;
		PITCH_CLASS = pitchClass;
	}

	// Only use this constructor if the note has no meaning in the context of a chord
	// TODO get rid of this constructor
	public Note(Pitch.Name name, Accidental accidental, int pitchClass) {
		this(new Pitch(name, accidental, Pitch.Role.ROOT), pitchClass);
	}

	public int midiNumber() {
		if (!midiNumber.isPresent())
			midiNumber = Optional.of(computeMidiNumber());
		return midiNumber.get();
	}

	private int computeMidiNumber() {
		int absIdx = IntervalQuality.semitonesInMajorOrPerfectInterval(PITCH.NAME.ordinal() - Pitch.Name.C.ordinal() + 1)
				+ Pitch.NUM_DISTINCT_PITCHES * PITCH_CLASS + PITCH.ACCIDENTAL.getAccidentalNumber();
		return absIdx + C0_MIDI_NUMBER;
	}

	// Compares by note name first, then by absolute pitch.
	@Override
	public int compareTo(Note other) {
		if (PITCH_CLASS != other.PITCH_CLASS)
			return (this.PITCH_CLASS < other.PITCH_CLASS) ? -1 : 1;
		if (PITCH.NAME != other.PITCH.NAME)
			return PITCH.NAME.ordinal() - other.PITCH.NAME.ordinal();
		return comparePitch(other);
	}

	// Compares the actual sounding pitch of the notes; enharmonic pitches are considered equal.
	public int comparePitch(Note other) {
		int result = midiNumber() - other.midiNumber();
		if (result == 0)
			return 0;
		return (result > 0) ? 1 : -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Note other = (Note) obj;
		return (compareTo(other) == 0);
	}

	@Override
	public String toString() {
		return PITCH.toString() + PITCH_CLASS;
	}

	public String toLilypondString() {
		String result = PITCH.NAME.toString().toLowerCase();

		// next do accidentals
		// TODO apparently accidentals greater than double magnitude are unsupported by Lilypond (!!??)
		int acc = PITCH.ACCIDENTAL.getAccidentalNumber();
		if (acc > 1)
			result += "isis";
		else if (acc == 1)
			result += "is";
		else if (acc == 0)
			result += "";
		else if (acc == -1)
			result += "es";
		else if (acc > -1)
			result += "eses";

		// then do pitchclass
		// No modifiers are required for pitch class 3
		int numModifiers = Math.abs(PITCH_CLASS - 3);
		while (numModifiers > 0) {
			if (PITCH_CLASS > 3)
				result += "'";
			else
				result += ",";
			numModifiers--;
		}
		return result;
	}
}
