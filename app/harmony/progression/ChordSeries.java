package harmony.progression;

import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.List;
import java.util.Optional;

public interface ChordSeries {

	List<Symbol> getSymbols();

	Optional<Key> getKeyOfSymbol(int index);

	int length();
}
