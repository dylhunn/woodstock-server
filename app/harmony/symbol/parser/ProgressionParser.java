package harmony.symbol.parser;

import harmony.chord.Chord;
import harmony.exception.IllegalChordSymbolException;
import harmony.exception.ProgressionInputException;
import harmony.progression.ChordSeries;
import harmony.progression.KeyedChordSeries;
import harmony.progression.Progression;
import harmony.single.Accidental;
import harmony.single.Key;
import harmony.single.Pitch;
import harmony.symbol.Symbol;

import java.util.*;

/**
 * Parses a progression from an input string.
 */
public class ProgressionParser {

    public static Progression parse(String input) throws IllegalChordSymbolException, ProgressionInputException {
        List<Symbol> symbols = new ArrayList<>();
        Scanner line = new Scanner(input);
        if (!line.hasNext()) throw new ProgressionInputException("Cannot harmonize empty progression.");
        String firstToken = line.next();
        Optional<Key> key = Optional.empty();
        if (firstToken.contains(":")) { // Interpret as a key.
            firstToken = firstToken.substring(0, firstToken.indexOf(":"));
            Key k = Key.fromString(firstToken);
            key = Optional.of(k);
        } else { // Interpret as a chord symbol.
            symbols.add(new GenericSymbolParser(firstToken).parse());
        }

        while (line.hasNext()) {
            String token = line.next();
            symbols.add(new GenericSymbolParser(token).parse());
        }

        ChordSeries p = new KeyedChordSeries(symbols, key);
        Progression sp = new Progression(Arrays.asList(p), 4);
        List<Chord> chords = sp.getChords();
        /*for (int i = 0; i < chords.size() - 1; i++) {
            System.out.println(symbols.get(i));
            System.out.println(chords.get(i));
            Optional<Chord> three;
            if (i + 2 < chords.size())
                three = Optional.of(chords.get(i + 2));
            else
                three = Optional.empty();
            //chords.get(i).printEvaluationData(Optional.of(chords.get(i+1)), three);
        }*/
        return sp;
    }
}
