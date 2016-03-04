package harmony.symbol.parser;

import harmony.exception.IllegalChordSymbolException;
import harmony.symbol.Symbol;

public interface Parser {
    Symbol parse() throws IllegalChordSymbolException;
}
