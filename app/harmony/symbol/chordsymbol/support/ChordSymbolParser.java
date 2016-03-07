package harmony.symbol.chordsymbol.support;

import harmony.core.Logger;
import harmony.exception.IllegalChordSymbolException;
import harmony.single.Accidental;
import harmony.single.Pitch;
import harmony.symbol.chordsymbol.symboltypes.*;
import harmony.symbol.parser.Parser;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// TODO this class has excessive shared state, and a poor design.
// It's actually slightly horrifying.
public class ChordSymbolParser implements Parser {

    private String symbol;

    public ChordSymbolParser(String symbol) {
        // TODO This is a hack
        // Enforce that this object can't be constructed directly
        assert (Arrays.asList(Thread.currentThread().getStackTrace()).toString().contains("GenericSymbolParser"));

        this.symbol = symbol;
    }

    public ChordSymbol parse() throws IllegalChordSymbolException {
        Pitch root = consumePitch(Pitch.Role.ROOT);
        Class<? extends ChordSymbol> chordSymbolClass = consumeSymbol(root);
        Set<ChordSymbolAlteration> modifications = new HashSet<>();
        while (!symbol.isEmpty() && symbol.charAt(0) != '/') {
            consumeModifier(modifications);
        }
        Pitch bass = consumeChordSymbolInversion(root);
        // TODO only add if the bass isn't already there!
        // This is not statically typechecked. Change this if the ChordSymbol constructor changes.
        ChordSymbol chordSymbol;

        try {
            chordSymbol = chordSymbolClass.getConstructor(Pitch.class, Pitch.class, Set.class, Optional.class).newInstance(root, bass,
                    modifications, Optional.empty());
            if (!chordSymbol.chordPitches().contains(bass)) {
                Set<ChordSymbolAlteration> addedBass = new HashSet<>();
                addedBass.add(new ChordSymbolAlteration(bass, ChordSymbolAlteration.Type.ADD));
                chordSymbol.attachModifications(addedBass);  // TODO finish thinking about this!
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            Logger.log(e.getStackTrace().toString());
            throw new RuntimeException("The Symbol Processor couldn't construct the Chord Symbol.");
        }
        return chordSymbol;
    }

    /*
     * Consume information from the front of the symbol string, returning the
     * corresponding ChordSymbol.
     */
    private Class<? extends ChordSymbol> consumeSymbol(Pitch root) {
        if (symbol.startsWith("Mm7")) {
            symbol = symbol.substring(3);
            return MajorMinorSeventhSymbol.class;
        }
        if (symbol.startsWith("M7")) {
            symbol = symbol.substring(2);
            return MajorSeventhSymbol.class;
        }
        if (symbol.startsWith("7")) {
            symbol = symbol.substring(1);
            return MajorMinorSeventhSymbol.class;
        }
        if (symbol.startsWith("D")) {
            symbol = symbol.substring(1);
            return MajorMinorSeventhSymbol.class;
        }
        if (symbol.startsWith("m")) {
            symbol = symbol.substring(1);
            return MinorTriadSymbol.class;
        }
        if (symbol.startsWith("M")) {
            symbol = symbol.substring(1);
            return MajorTriadSymbol.class;
        }
        return MajorTriadSymbol.class;
    }

    /*
     * Reads information regarding the third and fifth from the chord symbol,
     * adding the appropriate intervals to the list.
     */
    private void consumeModifier(Set<ChordSymbolAlteration> modifications) throws IllegalChordSymbolException {
        throw new IllegalChordSymbolException("Unknown chord symbol component: " + symbol);
    }

    private Pitch consumeChordSymbolInversion(Pitch root) throws IllegalChordSymbolException {
        if (symbol.isEmpty())
            return new Pitch(root.NAME, root.ACCIDENTAL, Pitch.Role.NO_ROLE);
        symbol = symbol.substring(1); // remove the "/"
        return consumePitch(Pitch.Role.NO_ROLE);
    }

    private Pitch consumePitch(Pitch.Role role) throws IllegalChordSymbolException {
        // Process the Pitch
        Pitch.Name name = consumePitchName();
        symbol = symbol.trim();
        // Process the accidental
        Accidental acc = consumeAccidental();
        symbol = symbol.trim();
        return new Pitch(name, acc, role);
    }

    private Pitch.Name consumePitchName() throws IllegalChordSymbolException {
        Character letter = symbol.charAt(0);
        symbol = symbol.substring(1); // Consume the pitch name
        Pitch.Name name;
        try {
            name = Pitch.Name.valueOf(Character.toUpperCase(letter) + "");
        } catch (IllegalArgumentException e) {
            throw new IllegalChordSymbolException("\"" + letter + "\" is not a valid pitch name.");
        }
        return name;
    }

    private Accidental consumeAccidental() {
        int numChars = Accidental.beginsWithAccidental(symbol);
        Accidental a = Accidental.fromFrontOfString(symbol);
        symbol = symbol.substring(numChars);
        return a;
    }
}
