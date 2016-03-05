package harmony.symbol.numeral;

import harmony.exception.IllegalChordSymbolException;
import harmony.single.Accidental;
import harmony.symbol.parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO this class has excessive shared state, and a poor design.
public class NumeralSymbolParser implements Parser {

    private String symbol;
    private final String originalSymbol;

    // TODO ugly
    private String numeralSymbolMods = ""; // used to share data
    NumeralSymbol.QualityModifier qualityMod;

    // Do not construct directly; only use through GenericSymbolParser
    public NumeralSymbolParser(String symbol) {
        // TODO This is a hack
        // Enforce that this object can't be constructed directly
        assert (Arrays.asList(Thread.currentThread().getStackTrace()).toString().contains("GenericSymbolParser"));

        this.symbol = symbol;
        this.originalSymbol = symbol;
    }

    public NumeralSymbol parse() throws IllegalChordSymbolException {
        Numeral num = consumeNumeral();
        return new NumeralSymbol(num, qualityMod, numeralSymbolMods, Optional.empty()); // TODO optional
    }

    // TODO this needs to be cleaned up
    private Numeral consumeNumeral() throws IllegalChordSymbolException {
        Accidental acc = consumeAccidental();
        String symbolMods = "";
        Numeral.Type thisType = null;
        NumeralSymbol.QualityModifier modifiedQuality = NumeralSymbol.QualityModifier.none;
        Optional<Numeral> secondaryOf = Optional.empty();

        // We must sort by descending length to avoid false matches
        List<String> types = new ArrayList<>();
        for (Numeral.Type t : Numeral.Type.values())
            types.add(t.toString());
        types.sort((s1, s2) -> s2.length() - s1.length());

        // Parse the numeral
        // TODO refactor out the common code
        if (symbol.indexOf("/") == -1) {
            for (String s : types) {
                if (symbol.startsWith(s)) {
                    String numeralStr = symbol.substring(0, s.length());
                    thisType = Numeral.Type.valueOf(numeralStr);
                    symbol = symbol.substring(s.length());
                    for (NumeralSymbol.QualityModifier t : NumeralSymbol.QualityModifier.values()) {
                        if (symbol.startsWith(t.toString())) {
                            modifiedQuality = t;
                            symbol = symbol.substring(t.toString().length());
                            break;
                        }
                    }
                    symbolMods = symbol;
                    break;
                }
            }
        } else {
            for (String s : types) {
                if (symbol.startsWith(s)) {
                    String numeralStr = symbol.substring(0, s.length());
                    thisType = Numeral.Type.valueOf(numeralStr);
                    symbol = symbol.substring(s.length());
                    for (NumeralSymbol.QualityModifier t : NumeralSymbol.QualityModifier.values()) {
                        if (symbol.startsWith(t.toString())) {
                            modifiedQuality = t;
                            symbol = symbol.substring(t.toString().length());
                            break;
                        }
                    }
                    symbolMods = symbol.substring(0, symbol.indexOf("/"));
                    symbol = symbol.substring(symbol.indexOf("/") + 1);
                    secondaryOf = Optional.of(consumeNumeral());
                    break;
                }
            }
        }

        if (thisType == null) {
            throw new IllegalChordSymbolException("The numeral (" + originalSymbol + ") was invalid.");
        }
        this.numeralSymbolMods = symbolMods; // overwrite mods so only the first chord remains
        this.qualityMod = modifiedQuality; // ditto (because of the recursion)
        return new Numeral(thisType, acc, secondaryOf);
    }

    private Accidental consumeAccidental() {
        int numChars = Accidental.beginsWithAccidental(symbol);
        Accidental a = Accidental.fromString(symbol);
        symbol = symbol.substring(numChars);
        return a;
    }

}
