package harmony.symbol.numeral;

import harmony.core.Library;
import harmony.exception.ProgressionInputException;
import harmony.single.Accidental;
import harmony.single.Key;
import harmony.single.Pitch;
import harmony.symbol.Symbol;
import harmony.symbol.chordsymbol.support.ChordConstraint;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;
import harmony.symbol.chordsymbol.symboltypes.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// TODO implement caching so we don't generate a new ChordSymbol every request
// TODO refactor parsing code elsewhere
public class NumeralSymbol implements Symbol {

	public enum QualityModifier {
		dim, halfdim, aug, none
	}

	private final int SEMITONES_IN_MINOR_SEVENTH = 10;

	private Numeral numeral;
	private QualityModifier qualityMod;
	private final String symbolMods;
	private final Inversion inversion;
	private final Optional<ChordConstraint> constraints;
	//private Optional<ChordSymbolConstraint> constraint;
	//private List<Alteration> alterations;

	public NumeralSymbol(Numeral numeral, QualityModifier qualityMod, String symbolMods, Optional<ChordConstraint> constraint) {
		this.numeral = numeral;
		this.qualityMod = qualityMod;
		this.symbolMods = symbolMods;
		this.inversion = computeInversion(symbolMods);
		this.constraints = constraint;
	}

	// TODO Clean up -- don't just use regex
	// TODO Move into NumeralSymbolParser
	private Inversion computeInversion(String symbolMods) {
		if (symbolMods.matches("(.*)4(.*)2(.*)"))
			return Inversion.THIRD;
		if (symbolMods.matches("(.*)4(.*)3(.*)"))
			return Inversion.SECOND;
		if (symbolMods.matches("(.*)6(.*)4(.*)"))
			return Inversion.SECOND;
		if (symbolMods.matches("(.*)6(.*)5(.*)"))
			return Inversion.FIRST;
		if (symbolMods.matches("(.*)6(.*)"))
			return Inversion.FIRST;
		return Inversion.ROOT;
	}

	// TODO cache
	@Override
	public ChordSymbol getChordSymbol(Optional<Key> key) throws ProgressionInputException {
		if (!key.isPresent()) throw new ProgressionInputException("No key was specified so numeral "
				+ this.toString() + " could not be parsed.");
		// TODO handle different chord types with alterations
		ChordSymbol cs;
		if (qualityMod == QualityModifier.dim)
			cs = new DiminishedTriadSymbol(numeral.getRoot(key.get()), inversion, new HashSet<>(), constraints);
		else if (qualityMod == QualityModifier.aug)
			cs = new AugmentedTriadSymbol(numeral.getRoot(key.get()), inversion, new HashSet<>(), constraints);
		else if (qualityMod == QualityModifier.halfdim)
			cs = new HalfDiminishedSeventhSymbol(numeral.getRoot(key.get()), inversion, new HashSet<>(), constraints);
		// Major or Neapolitan
		else if (numeral.isUpper())
			cs = new MajorTriadSymbol(numeral.getRoot(key.get()), inversion, new HashSet<>(), constraints);
		// Minor
		else if (numeral.isLower())
			cs = new MinorTriadSymbol(numeral.getRoot(key.get()), inversion, new HashSet<>(), constraints);
		// Aug 6? What else? TODO
		else
			throw new RuntimeException("We don't know about this numeral yet (probably an Aug6)."); // TODO

		cs.attachModifications(getAlterationsFromSymbolText(symbolMods, cs, key.get()));
		return cs;
	}

	/**
	 * Accepts a string containing all of the modifications of this chord
	 * symbol; a modification is typically an optional accidental followed by an
	 * Arabic numeral.
	 * 
	 * Also accepts the chord symbol (that does not yet have its modifications applied).
	 */
	// TODO Move into NumeralSymbolParser
	private Set<ChordSymbolAlteration> getAlterationsFromSymbolText(String symbolMods, ChordSymbol cs, Key actualKey) {
		Set<ChordSymbolAlteration> altSet = new HashSet<>();
		while (!symbolMods.isEmpty()) {
 			// Extract the accidental and number from the modifications string
			int numCharsInAccidental = Accidental.beginsWithAccidental(symbolMods);
			Accidental accidental = Accidental.fromFrontOfString(symbolMods);
			symbolMods = symbolMods.substring(numCharsInAccidental);
			Optional<Integer> numberOpt = Library.readIntFromFrontOfString(symbolMods);
			if (!numberOpt.isPresent()) throw new RuntimeException("Unable to parse symbol modification: " + symbolMods);
			symbolMods = symbolMods.substring(numberOpt.get().toString().length());
			int number = numberOpt.get();

			// TODO what if the bass is the added tone?
			Pitch bass = cs.getBass();
			Pitch root = cs.getRoot();
			// If the chord is a secondary, the effective key is the key as if the first secondary base were the tonic.
			Key effectiveKey = numeral.getKeyOfNumeralAsIfPrimary(actualKey);

			// Name of added Pitch. Find the pitch name we expect the arabic numeral represents before we apply any accidental
			// modification to it.
			Pitch.Name newUnadornedPitch = Pitch.raiseNameByInterval(bass.NAME, number);
			Pitch unmodifiedPitch = null; // the Pitch before application of the accidental
			for (Pitch p : effectiveKey.getScale()) {
				if (p.NAME.equals(newUnadornedPitch)) unmodifiedPitch = p;
			}

			// If this implied pitch is already present in the chord, use the version already present.
			// This is significant because the "unadorned" pitch might already have an accidental on it.
			for (Pitch p : cs.chordPitches()) if (p.NAME.equals(unmodifiedPitch.NAME)) unmodifiedPitch = p;

			if (unmodifiedPitch == null) throw new RuntimeException("Could not find base pitch in scale.");

			Pitch pitchWithoutRole = unmodifiedPitch;

			// Determine the role of the pitch in the context of the chord.
			Pitch newPitch = null;
			Key keyOfRoot = new Key(root, Key.KeyType.MAJOR); // scale type is arbitrary
			for (Pitch p : keyOfRoot.getScale()) {
				if (p.NAME.equals(pitchWithoutRole.NAME)) {
					newPitch = new Pitch(pitchWithoutRole.NAME, pitchWithoutRole.ACCIDENTAL, p.ROLE);
				}
			}
			if (newPitch == null) throw new RuntimeException("Unable to determine role of added tone.");

			// If it is minor, the 7th should be minor as well (TODO: is this true?)
			if (newPitch.ROLE.equals(Pitch.Role.SEVENTH) && numeral.isLower()) {
				newPitch = newPitch.raised(SEMITONES_IN_MINOR_SEVENTH - root.semitonesUpTo(newPitch)); // TODO this might need to be -1 once we fix the handling of chords like i7
			}
			// If it is fully diminished and the added tone is a 7th, lower by one more half step
			if (newPitch.ROLE.equals(Pitch.Role.SEVENTH) && qualityMod.equals(NumeralSymbol.QualityModifier.dim)) {
				newPitch = newPitch.raised(SEMITONES_IN_MINOR_SEVENTH - 1 - root.semitonesUpTo(newPitch)); // TODO this might need to be -1 once we fix the handling of chords like i7
			}

			// Add the indicated accidental modification.
			// TODO this still doesn't handle a natural as a modification, etc
			newPitch = newPitch.raised(accidental.getAccidentalNumber());

			// Add the modification to the set of alterations. Additionally, remove the unmodified
			// pitch from the chord, if present.
			if (!unmodifiedPitch.equals(newPitch) && cs.chordPitches().contains(unmodifiedPitch)) {
				altSet.remove(new ChordSymbolAlteration(unmodifiedPitch, ChordSymbolAlteration.Type.REMOVE));
			}
			if (!cs.chordPitches().contains(newPitch)) {
				altSet.add(new ChordSymbolAlteration(newPitch, ChordSymbolAlteration.Type.ADD));
			}
		}
		return altSet;
	}

	@Override
	public String toString() {
		String altText = symbolMods.isEmpty() ? "" : " with alteration " + symbolMods;
		String posOrInv;
		if (inversion == Inversion.ROOT)
			posOrInv = "Position";
		else
			posOrInv = "Inversion";
		return numeral.toString() + " in " + Library.beginningCase(inversion.toString()) + " " + posOrInv + altText;
	}
}
