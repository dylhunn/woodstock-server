package harmony.single;

import java.util.Arrays;
import java.util.Scanner;

import harmony.exception.ProgressionInputException;
import harmony.interval.Interval;

public class Pitch {

	public static final int NUM_DISTINCT_NOTE_NAMES = Name.values().length;
	public static final int NUM_DISTINCT_PITCHES = 12;

	// ascending order must be preserved
	// must begin with C, because C is the first note in each pitch class
	public static enum Name {
		C, D, E, F, G, A, B
	}

	// The Roles indicate the role of this pitch in the parent chord, if present
	// Try to set this when using a Pitch, or bad things might happen
	public static enum Role {
		// WARNING: numbers 1-9 must remain in order
		ROOT, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, ROOT_O, NINTH, NO_ROLE
	}

	public final Name NAME;
	public final Accidental ACCIDENTAL;
	public final Role ROLE;

	public Pitch(Name name, Accidental accidental, Role role) {
		NAME = name;
		ACCIDENTAL = accidental;
		ROLE = role;
	}

	@Override
	public String toString() {
		return NAME.toString() + ACCIDENTAL.toString();
	}

	public static Pitch fromString(String str) {
		return null; // TODO
	}

	// Returns a new pitch with its accidental raised by the specified number of steps.
	// Warning -- this does not modify the calling Pitch.
	// TODO This does not correctly handle the distinction between actively having a natural
	// and having no accidental
	public Pitch raised(int n) {
		return new Pitch(NAME, new Accidental(ACCIDENTAL.getAccidentalNumber() + n), ROLE);
	}


	// Accepts a positive interval and raises the Pitch Name by that interval.
	public static Name raiseNameByInterval(Name name, int interval) {
		assert(interval > 0);
		return Arrays.asList(Name.values()).get((name.ordinal() + interval - 1) % Pitch.NUM_DISTINCT_NOTE_NAMES);
	}

	public static Name lowerNameByInterval(Name name, int interval) {
		assert(interval < 0);
		// Complimentary intervals add to 9.
		interval = 9 - interval; // TODO No magic number
		return raiseNameByInterval(name, interval);
	}

	// no larger than, but including, 12 semitones
	public int semitonesUpTo(Pitch other) {
		return new Interval(new Note(this, 1), new Note(other, 2)).REDUCED_NUM_SEMITONES;
	}

	public static Pitch fromFrontOfString(String str) throws ProgressionInputException {
		Pitch p;
		try {
			Name name = Name.valueOf(str.substring(0, 1));
			Accidental acc = Accidental.fromFrontOfString(str.substring(1));
			p = new Pitch(name, acc, Role.NO_ROLE);
		} catch (IllegalArgumentException e) {
			throw new ProgressionInputException("The input " + str + " cannot be part of a pitch.");
		}
		return p;
	}

	@Override
	public int hashCode() {
		return NAME.ordinal() * 100 + ACCIDENTAL.getAccidentalNumber();
	}

	// Ignores role
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Pitch))
			return false;
		if (!((Pitch) o).NAME.equals(NAME))
			return false;
		if (!((Pitch) o).ACCIDENTAL.equals(ACCIDENTAL))
			return false;
		return true;
	}
}