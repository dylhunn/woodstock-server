package harmony.progression;

import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.List;

public interface ChordSeries {

	List<Symbol> getSymbols();

	Key getKeyOfSymbol(int index);

	int length();
}
