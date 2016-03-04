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
        if (symbol.indexOf("I") != -1 && symbol.indexOf("i") != -1 && symbol.indexOf("V") != -1 && symbol.indexOf("v") != -1
                && symbol.indexOf("N") != -1 && symbol.indexOf("n") != -1 && !symbol.contains("Fr") && !symbol.contains("It")
                && !symbol.contains("Ger")) {
            output = new ChordSymbolParser(symbol).parse();
        } else {
            output = new NumeralSymbolParser(symbol).parse();
        }
        result = Optional.of(output);
        return output;
    }
}
