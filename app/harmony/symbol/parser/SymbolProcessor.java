package harmony.symbol.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import harmony.core.Logger;
import harmony.exception.IllegalChordSymbolException;
import harmony.single.Accidental;
import harmony.single.Pitch;
import harmony.symbol.Symbol;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;
import harmony.symbol.chordsymbol.symboltypes.*;
import harmony.symbol.numeral.Numeral;
import harmony.symbol.numeral.NumeralSymbol;

// TODO this class has excessive shared state, and a poor design.
// It's actually slightly horrifying.
public class SymbolProcessor {

	private String symbol;
	private Optional<Symbol> result; // must cache because symbol is consumed

	// TODO ugly
	private String numeralSymbolMods = ""; // used to share data
	NumeralSymbol.QualityModifier qualityMod;

	public SymbolProcessor(String symbol) {
		this.symbol = symbol.trim();
		result = Optional.empty();
	}

	public Symbol process() throws IllegalChordSymbolException {
		if (result.isPresent())
			return result.get();
		if (symbol.isEmpty())
			throw new IllegalChordSymbolException("A chord symbol cannot be empty.");
		Symbol output;
		List<Character> pitchNames = new ArrayList<>();
		Arrays.asList(Pitch.Name.values()).forEach(p -> pitchNames.add(p.toString().toUpperCase().charAt(0)));
		// TODO this is a hack; improve numeral vs letter detection
		if (symbol.indexOf("I") != -1 || symbol.indexOf("i") != -1 || symbol.indexOf("V") != -1 || symbol.indexOf("v") != -1
				|| symbol.indexOf("N") != -1 || symbol.indexOf("n") != -1) {
			output = processNumeralSymbol();
		} else {
			output = processChordSymbol();
		}
		result = Optional.of(output);
		return output;
	}

	// Numeral symbol processing

	private NumeralSymbol processNumeralSymbol() throws IllegalChordSymbolException {
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
			throw new IllegalChordSymbolException("The numeral was invalid.");
		}
		this.numeralSymbolMods = symbolMods; // overwrite mods so only the first chord remains
		this.qualityMod = modifiedQuality; // ditto (because of the recursion)
		return new Numeral(thisType, acc, secondaryOf);
	}

	// Chord symbol processing

	private ChordSymbol processChordSymbol() throws IllegalChordSymbolException {
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
		Accidental a = Accidental.fromString(symbol);
		symbol = symbol.substring(numChars);
		return a;
	}
}
