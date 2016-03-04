package harmony.core;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class LibraryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(Library.readIntFromFrontOfString("0").get().equals(0));
		assertTrue(Library.readIntFromFrontOfString("1").get().equals(1));
		assertTrue(Library.readIntFromFrontOfString("919").get().equals(9));
		assertTrue(Library.readIntFromFrontOfString("9").get().equals(9));
		assertTrue(Library.readIntFromFrontOfString("75").get().equals(7));
		assertTrue(Library.readIntFromFrontOfString("0ffd").get().equals(0));
		assertTrue(Library.readIntFromFrontOfString("1-2").get().equals(1));
		assertTrue(Library.readIntFromFrontOfString("919x").get().equals(9));
		assertTrue(Library.readIntFromFrontOfString("x9").equals(Optional.empty()));
		assertTrue(Library.readIntFromFrontOfString("").equals(Optional.empty()));
	}
}
