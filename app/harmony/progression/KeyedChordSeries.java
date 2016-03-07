package harmony.progression;

import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.List;
import java.util.Optional;

public class KeyedChordSeries implements ChordSeries {

	private final List<Symbol> symbols;
	private final Optional<Key> key;

	public KeyedChordSeries(List<Symbol> symbols, Optional<Key> key) {
		this.symbols = symbols;
		this.key = key;
	}

	public Optional<Key> getKey() {
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
	public Optional<Key> getKeyOfSymbol(int index) {
		return key;
	}
}
