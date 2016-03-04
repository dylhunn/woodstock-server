package harmony.progression;

import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.List;

public class KeyedChordSeries implements ChordSeries {

	private final List<Symbol> symbols;
	private final Key key;

	public KeyedChordSeries(List<Symbol> symbols, Key key) {
		this.symbols = symbols;
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public List<Symbol> getSymbols() {
		return symbols;
	}

	@Override
	public int length() {
		return symbols.size();
	}

	@Override
	public Key getKeyOfSymbol(int index) {
		return key;
	}
}
