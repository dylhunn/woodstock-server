package harmony.progression;

import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.List;
import java.util.Optional;

public class PivotChordSeries implements ChordSeries {

	KeyedChordSeries oldP;
	KeyedChordSeries newP;

	/*
	 * Both input progressions must be the same chords, expressed in different
	 * keys.
	 */
	public PivotChordSeries(KeyedChordSeries oldKey, KeyedChordSeries newKey) {
		// TODO Validate the chords are the same
		// TODO structured exception handling
		if (oldKey.length() != newKey.length())
			throw new RuntimeException("Parallel pivot progressions must have the same length.");
		oldP = oldKey;
		newP = newKey;
	}

	@Override
	public List<Symbol> getSymbols() {
		// TODO improve
		return newP.getSymbols();
	}

	@Override
	public int length() {
		return newP.length();
	}

	@Override
	public Optional<Key> getKeyOfSymbol(int index) {
		return newP.getKey();
	}
}
