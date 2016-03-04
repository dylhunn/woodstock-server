package harmony.symbol.numeral;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import harmony.single.Accidental;
import harmony.single.Key;
import harmony.single.Pitch;

public class NumeralTest {

	Numeral n1 = new Numeral(Numeral.Type.V, new Accidental(Accidental.AccidentalType.NATURAL), Optional.empty());
	Key k1 = new Key(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	Pitch p1 = new Pitch(Pitch.Name.G, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT);

	Numeral n2 = new Numeral(Numeral.Type.III, new Accidental(Accidental.AccidentalType.SHARP), Optional.empty());
	Key k2 = new Key(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.TRIPLE_SHARP), Pitch.Role.ROOT), Key.KeyType.H_MINOR);
	Pitch p2 = new Pitch(Pitch.Name.F, new Accidental(4), Pitch.Role.ROOT);

	Numeral n3 = new Numeral(Numeral.Type.vi, new Accidental(Accidental.AccidentalType.TRIPLE_FLAT), Optional.empty());
	Key k3 = new Key(new Pitch(Pitch.Name.B, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	Pitch p3 = new Pitch(Pitch.Name.G, new Accidental(-3), Pitch.Role.ROOT);

	Numeral n4 = new Numeral(Numeral.Type.vii, new Accidental(Accidental.AccidentalType.NATURAL), Optional.of(n1));
	Key k4 = new Key(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	Pitch p4 = new Pitch(Pitch.Name.F, new Accidental(1), Pitch.Role.ROOT);

	Numeral n5 = new Numeral(Numeral.Type.III, new Accidental(Accidental.AccidentalType.SHARP), Optional.of(n3));
	Key k5 = new Key(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.H_MINOR);
	Pitch p5 = new Pitch(Pitch.Name.D, new Accidental(-3), Pitch.Role.ROOT);

	Numeral n6 = new Numeral(Numeral.Type.iii, new Accidental(Accidental.AccidentalType.NATURAL), Optional.of(n4));
	Key k6 = new Key(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	Pitch p6 = new Pitch(Pitch.Name.A, new Accidental(0), Pitch.Role.ROOT);

	Numeral n7 = new Numeral(Numeral.Type.N, new Accidental(Accidental.AccidentalType.NATURAL), Optional.empty());
	Key k7 = new Key(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	Pitch p7 = new Pitch(Pitch.Name.D, new Accidental(-1), Pitch.Role.ROOT);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetRoot() {
		assertTrue(n1.getRoot(k1).equals(p1));
		assertTrue(n2.getRoot(k2).equals(p2));
		assertTrue(n3.getRoot(k3).equals(p3));
		assertTrue(n4.getRoot(k4).equals(p4));
		assertTrue(n5.getRoot(k5).equals(p5));
		assertTrue(n6.getRoot(k6).equals(p6));
	}
	
	@Test
	public void testGetAsIfPrimary() {
		assertTrue(k1.equals(n1.getKeyOfNumeralAsIfPrimary(k1)));
		Key k8 = new Key(new Pitch(Pitch.Name.G, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
		assertTrue(k8.equals(n4.getKeyOfNumeralAsIfPrimary(k1)));
		// TODO test fancy chord types like Aug6 and N
		// TODO test triple, quadruple secondaries
		// TODO test minor keys
	}
}
