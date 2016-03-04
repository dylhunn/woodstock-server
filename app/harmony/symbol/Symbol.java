package harmony.symbol;

import harmony.single.Key;
import harmony.symbol.chordsymbol.symboltypes.ChordSymbol;

public interface Symbol {

	enum Inversion {
		ROOT, FIRST, SECOND, THIRD
	}

	ChordSymbol getChordSymbol(Key key);
}
