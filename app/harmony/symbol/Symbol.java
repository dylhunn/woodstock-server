package harmony.symbol;

import harmony.exception.ProgressionInputException;
import harmony.single.Key;
import harmony.symbol.chordsymbol.symboltypes.ChordSymbol;

import java.util.Optional;

public interface Symbol {

	enum Inversion {
		ROOT, FIRST, SECOND, THIRD
	}

	// Key is only required if it is a NumeralSymbol.
	// However, if the Key is not provided in that case, an exception is thrown.
	ChordSymbol getChordSymbol(Optional<Key> key) throws ProgressionInputException;
}
