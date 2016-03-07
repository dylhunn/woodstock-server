package harmony.symbol.parser;

import harmony.exception.IllegalChordSymbolException;
import harmony.symbol.Symbol;
import harmony.symbol.chordsymbol.support.ChordSymbolParser;
import harmony.symbol.numeral.NumeralSymbolParser;

import java.util.Optional;

public class GenericSymbolParser implements Parser {

    private String originalSymbol;
    private String symbol;
    private Optional<Symbol> result; // must cache because symbol is consumed

    public GenericSymbolParser(String symbol) {
        this.symbol = symbol.trim();
        originalSymbol = this.symbol;
        result = Optional.empty();
    }

    public Symbol parse() throws IllegalChordSymbolException {
        if (result.isPresent())
            return result.get();
        if (symbol.isEmpty())
            throw new IllegalChordSymbolException("A chord symbol cannot be empty.");
        Symbol output;
        // TODO this is a hack; improve numeral vs letter detection
        if (symbol.contains("I") || symbol.contains("i") || symbol.contains("V") || symbol.contains("v") ||
                symbol.contains("N") || symbol.contains("n") || symbol.contains("Fr") || symbol.contains("It")
                || symbol.contains("Ger")) {
            output = new NumeralSymbolParser(symbol).parse();
        } else {
            output = new ChordSymbolParser(symbol).parse();
        }
        result = Optional.of(output);
        return output;
    }
}
