package harmony.single;

import harmony.single.Pitch.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class KeyTest {

	Key k1 = new Key(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), Key.KeyType.MAJOR);
	List<Pitch> scale1 = Arrays.asList(new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT),
			new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.SECOND),
			new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.THIRD),
			new Pitch(Pitch.Name.F, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.FOURTH),
			new Pitch(Pitch.Name.G, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.FIFTH),
			new Pitch(Pitch.Name.A, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.SIXTH),
			new Pitch(Pitch.Name.B, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.SEVENTH));

	Key k2 = new Key(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.ROOT), Key.KeyType.H_MINOR);
	List<Pitch> scale2 = Arrays.asList(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.ROOT),
			new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.SECOND),
			new Pitch(Pitch.Name.F, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.THIRD),
			new Pitch(Pitch.Name.G, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.FOURTH),
			new Pitch(Pitch.Name.A, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.FIFTH),
			new Pitch(Pitch.Name.B, new Accidental(Accidental.AccidentalType.DOUBLE_FLAT), Pitch.Role.SIXTH),
			new Pitch(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.SEVENTH));

	Key k3 = new Key(new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.TRIPLE_SHARP), Pitch.Role.ROOT), Key.KeyType.H_MINOR);
	List<Pitch> scale3 =
			Arrays.asList(new Pitch(Pitch.Name.E, new Accidental(3), Pitch.Role.ROOT),
					new Pitch(Pitch.Name.F, new Accidental(4), Pitch.Role.SECOND),
					new Pitch(Pitch.Name.G, new Accidental(3),Pitch.Role.THIRD),
					new Pitch(Pitch.Name.A, new Accidental(3), Pitch.Role.FOURTH),
					new Pitch(Pitch.Name.B, new Accidental(3), Pitch.Role.FIFTH),
					new Pitch(Pitch.Name.C, new Accidental(3), Pitch.Role.SIXTH),
					new Pitch(Pitch.Name.D, new Accidental(4), Pitch.Role.SEVENTH));

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(k1.getScale().equals(scale1));
		assertTrue(k1.getScale().get(0).ROLE.equals(Role.ROOT));
		assertTrue(k1.getScale().get(6).ROLE.equals(Role.SEVENTH));
		assertTrue(k2.getScale().equals(scale2));
		assertTrue(k2.getScale().get(0).ROLE.equals(Role.ROOT));
		assertTrue(k2.getScale().get(6).ROLE.equals(Role.SEVENTH));
		assertTrue(k3.getScale().equals(scale3));
	}

}
