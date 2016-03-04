package harmony.symbol.numeral;

import harmony.exception.IllegalChordSymbolException;
import harmony.single.Accidental;
import harmony.single.Key;
import harmony.single.Pitch;
import harmony.symbol.parser.GenericSymbolParser;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NumeralParserTest {

	@Before
	public void setUp() throws Exception {
	}

	// Does not check for root, only pitch contents of chords
	@Test
	public void test() {
		validateNumeral("I", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G"));
		validateNumeral("i", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "G"));
		validateNumeral("I6", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G"));
		validateNumeral("i64", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "G"));
		validateNumeral("idim", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "Gb"));
		validateNumeral("Iaug", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G#"));
		validateNumeral("#I", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C#", "E#", "G#"));
		validateNumeral("#i", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C#", "E", "G#"));
		validateNumeral("#idim", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C#", "E", "G"));
		validateNumeral("#Iaug", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C#", "E#", "GX"));
		validateNumeral("I", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "G", "Bb"));
		validateNumeral("i", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "Gb", "Bb"));
		validateNumeral("idim", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "Gb", "Bbb"));
		validateNumeral("Iaug", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "G", "B"));
		validateNumeral("idim64", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "Gb", "Bbb"));
		validateNumeral("Iaug6", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Eb", "G", "B"));
		validateNumeral("iii", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Gb", "Bbb", "Db"));
		// TODO: failing cases
		// validateNumeral("bbiii", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Gbb", "Bbbb", "Dbb"));
		validateNumeral("viidim", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("D", "F", "Ab"));
		validateNumeral("bVII", new Key(parsePitch("Eb"), Key.KeyType.H_MINOR), parsePitches("Db", "F", "Ab"));
		validateNumeral("I7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G", "B"));
		validateNumeral("V7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("G", "B", "D", "F"));
		validateNumeral("ii7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "A", "C"));
		validateNumeral("II7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F#", "A", "C"));
		validateNumeral("II#7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F#", "A", "C#"));
		validateNumeral("i7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "G", "Bb"));
		validateNumeral("i7", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("C", "Eb", "G", "Bb"));
		validateNumeral("ihalfdim7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "Gb", "Bb"));
		validateNumeral("ihalfdim7", new Key(parsePitch("Cbbb"), Key.KeyType.MAJOR), parsePitches("Cbbb", "Ebbbb", "Gbbbb", "Bbbbb"));
		validateNumeral("idim7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "Eb", "Gb", "Bbb"));
		validateNumeral("iihalfdim7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "Ab", "C"));
		validateNumeral("iidim7", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "Ab", "Cb"));
		validateNumeral("viidim7", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("B", "D", "F", "Ab"));
		validateNumeral("#viidim4b3", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("B#", "D#", "F#", "Ab"));
		validateNumeral("I65", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G", "B"));
		validateNumeral("V43", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("G", "B", "D", "F"));
		//validateNumeral("ii42", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "A", "C"));
		validateNumeral("II43", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F#", "A", "C"));
		validateNumeral("iihalfdim65", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "Ab", "C"));
		//validateNumeral("iidim42", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("D", "F", "Ab", "Cb"));
		validateNumeral("I7", new Key(parsePitch("E"), Key.KeyType.MAJOR), parsePitches("E", "G#", "B", "D#"));
		//validateNumeral("Inat7", new Key(parsePitch("E"), Key.KeyType.MAJOR), parsePitches("E", "G#", "B", "D"));
		//validateNumeral("Ib7", new Key(parsePitch("E"), Key.KeyType.MAJOR), parsePitches("E", "G#", "B", "Db"));
		validateNumeral("I6b5", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G", "Bb"));
		validateNumeral("V42", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("G", "B", "D", "F"));
		//validateNumeral("v42", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("G", "Bb", "D", "F"));
		validateNumeral("bviidim4b2", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("Bbb", "Db", "Fb", "Abb"));
		validateNumeral("vii42", new Key(parsePitch("C"), Key.KeyType.H_MINOR), parsePitches("B", "D", "F#", "A"));
		validateNumeral("viidim42", new Key(parsePitch("D"), Key.KeyType.H_MINOR), parsePitches("C#", "E", "G", "Bb"));
		validateNumeral("Vaug42", new Key(parsePitch("C"), Key.KeyType.MAJOR), parsePitches("C", "E", "G#", "B"));
		// TODO write more tests for 9th, 11th, 13ths, "nat" notes, secondary functions, odd combinations of keys,
		// modified pitches, and accidentals.
	}

	private Set<Pitch> parsePitches(String... pitches) {
		Set<Pitch> pp = new HashSet<>();
		for (String s : pitches) {
			pp.add(parsePitch(s));
		}
		return pp;
	}

	private Pitch parsePitch(String pitch) {
		Pitch.Name p = Pitch.Name.valueOf(pitch.substring(0, 1));
		pitch = pitch.substring(1);
		Accidental a = Accidental.fromString(pitch);
		return new Pitch(p, a, Pitch.Role.NO_ROLE);
	}

	public void validateNumeral(String numeral, Key key, Set<Pitch> expectedPitches) {
		Set<Pitch> actualPitches = null;
		try {
			actualPitches = new GenericSymbolParser(numeral).parse().getChordSymbol(key).chordPitches();
		} catch (IllegalChordSymbolException e) {
			fail("Parsing failed for symbol " + numeral);
		}
		assertTrue(actualPitches.equals(expectedPitches));
	}
}
