package harmony.single;

// TODO make this an optional property of a pitch? (Natural vs. no accidental)
public class Accidental implements Comparable<Accidental> {

	// A convenience enum for accidental names
	public static enum AccidentalType {
		TRIPLE_FLAT, DOUBLE_FLAT, FLAT, NATURAL, SHARP, DOUBLE_SHARP, TRIPLE_SHARP
	}

	private final int accidental;

	public Accidental(AccidentalType accidental) {
		this.accidental = accidental.ordinal() - AccidentalType.NATURAL.ordinal();
	}

	// 0 is natural, 1 is sharp, -1 is flat, 2 is double-sharp, etc
	public Accidental(int accidental) {
		this.accidental = accidental;
	}

	public int getAccidentalNumber() {
		return accidental;
	}
	
	public Accidental raised(int i) {
		return new Accidental(getAccidentalNumber() + i);
	}

	@Override
	public int compareTo(Accidental other) {
		return accidental - other.getAccidentalNumber();
	}

	/**
	 * Indicates whether a String begins with an accidental. If so, returns the
	 * number of characters at the front of the string that are part of the
	 * accidental. If not, returns 0;
	 */
	public static int beginsWithAccidental(String ss) {
		if (ss.startsWith("nat"))
			return 0;
		int numChars = 0;
		while (ss.startsWith("b") || ss.startsWith("#")) {
			ss = ss.substring(1); // consume the accidental
			numChars++;
		}
		while (ss.toUpperCase().startsWith("X")) {
			numChars += 1;
			ss = ss.substring(1);
		}
		while (ss.startsWith("#")) {
			numChars++;
			ss = ss.substring(1);
		}
		return numChars;
	}

	/**
	 * Reads an accidental from the front of a String.
	 */
	public static Accidental fromFrontOfString(String ss) {
		// TODO Serious: This is broken for symbols like natIII if the root of the three was flat
		// to begin with. In other words, # is always considered +1, bb is always considered -2,
		// regardless of the original pitch. Also, there is no support for naturals in this context at all.
		int accidental = 0;
		while (ss.startsWith("b")) {
			ss = ss.substring(1); // consume the accidental
			accidental--;
		}
		while (ss.startsWith("#")) {
			accidental++;
			ss = ss.substring(1);
		}
		while (ss.toUpperCase().startsWith("X")) {
			accidental += 2;
			ss = ss.substring(1);
		}
		while (ss.startsWith("#")) {
			accidental++;
			ss = ss.substring(1);
		}
		return new Accidental(accidental);

	}

	@Override
	public String toString() {
		String accidentalStr = "";
		if (accidental < 0) {
			for (int i = 0; i > accidental; i--)
				accidentalStr += "b";
		} else if (accidental > 0) {
			if (accidental % 2 == 1)
				accidentalStr += "#";
			for (int i = 0; i < accidental / 2; i++)
				accidentalStr += "X";
		}
		return accidentalStr;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Accidental))
			return false;
		return compareTo((Accidental) other) == 0 ? true : false;
	}
}
