package harmony.symbol.numeral;

import java.util.Optional;

import harmony.single.Accidental;
import harmony.single.Key;
import harmony.single.Key.KeyType;
import harmony.single.Pitch;

public class Numeral {

	// Warning: update utility functions in class before changing this
	public static enum Type {
		I, i, II, ii, III, iii, IV, iv, V, v, VI, vi, VII, vii, N, Ger, It, Fr
	}

	// Number of numerals that are plain numbers
	public static final int NUM_STANDARD_NUMERALS = 13;

	private final Type numeral;
	private final Accidental accidental; // modifier before the numeral
	// Is this numeral a secondary of some other numeral?
	private final Optional<Numeral> secondaryOf;

	public Numeral(Type num, Accidental acc, Optional<Numeral> secondaryOf) {
		numeral = num;
		accidental = acc;
		this.secondaryOf = secondaryOf;
	}

	public Pitch getRoot(Key tonic) { // TODO support N6 and Aug6
		if (secondaryOf.isPresent()) {
			Pitch curr = secondaryOf.get().getRoot(tonic);
			Key.KeyType type = secondaryOf.get().isUpper() ? KeyType.MAJOR : KeyType.H_MINOR;
			tonic = new Key(curr, type);
		}
		Pitch resultWithoutAccidental;
		if (numeral.ordinal() <= NUM_STANDARD_NUMERALS) {
			resultWithoutAccidental = tonic.getScale().get(numeralToInt() - 1);
		} else if (numeral == Type.N) {
			resultWithoutAccidental = tonic.getScale().get(1).raised(-1);
		} else {
			throw new RuntimeException("We still don't support Aug6 chords.");
		}
		int newAccidental = resultWithoutAccidental.ACCIDENTAL.getAccidentalNumber() + accidental.getAccidentalNumber();
		Pitch result = new Pitch(resultWithoutAccidental.NAME, new Accidental(newAccidental), Pitch.Role.ROOT);
		return result;
	}
	
	/**
	 * All numerals are implicitly the secondary of some other numeral. 
	 * For example, V is V/I. Therefore, for C:V, V is primary in C.
	 * For C:V7/V, the V7 is primary in G.
	 * @param Key is the base (actual) Key for the entire numeral symbol.
	 */
	protected Key getKeyOfNumeralAsIfPrimary(Key key) {
		if (!secondaryOf.isPresent()) return key;
		Numeral firstSecondary = secondaryOf.get();
		Pitch newTonic = firstSecondary.getRoot(key);
		// TODO account for Aug6 case!!!
		if (firstSecondary.isUpper()) return new Key(newTonic, Key.KeyType.MAJOR);
		else return new Key(newTonic, Key.KeyType.H_MINOR);
		
	}

	private int numeralToInt() {
		// TODO fix
		if (numeral.ordinal() > NUM_STANDARD_NUMERALS)
			throw new RuntimeException("Numeral " + numeral + " is not a number");
		return (numeral.ordinal() + 2) / 2;
	}

	protected final boolean isUpper() {
		return (numeral.toString().equals(numeral.toString().toUpperCase()));
	}

	protected final boolean isLower() {
		return (numeral.toString().equals(numeral.toString().toLowerCase()));
	}

	@Override
	public String toString() {
		return accidental.toString() + numeral.toString() + (secondaryOf.isPresent() ? "/" + secondaryOf.get().toString() : "");
	}
}
