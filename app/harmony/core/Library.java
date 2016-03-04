package harmony.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Library {

	public static final List<Integer> ILLEGAL_INTERVALS = Arrays.asList(1, 5, 8);

	// The number of steps out-of-range of a voice beyond which a note should never be considered
	public static final int OUT_OF_RANGE_THRESHHOLD = 5;

	public static final int MIDDLE_C_MIDI_NUM = 60;

	public static <T extends Comparable<T>> T min(T first, T second) {
		return first.compareTo(second) < 0 ? first : second;
	}

	public static <T extends Comparable<T>> T max(T first, T second) {
		return first.compareTo(second) >= 0 ? first : second;
	}

	public static String beginningCase(String str) {
		String res = str.toString().toUpperCase().charAt(0) + str.toString().toLowerCase().substring(1);
		return res;
	}

	/**
	 * Accepts a string, and reads an int from the front, stopping after one digit.
	 */
	public static Optional<Integer> readIntFromFrontOfString(String str) {
		String number = "";
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i+1).matches("\\d")) number += str.charAt(i);
			break; // TODO what about 11ths and 13ths?
		}
		if (number.isEmpty()) return Optional.empty();
		return Optional.of(Integer.parseInt(number));
	}
}
