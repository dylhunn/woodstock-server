package harmony.interval;

import harmony.single.Note;

public class MelodicInterval extends Interval {

	public static enum Direction {
		UP, DOWN, UNISON
	}

	public final Note FIRST_NOTE;
	public final Note SECOND_NOTE;

	public MelodicInterval(Note first, Note second) {
		super(first, second);
		FIRST_NOTE = first;
		SECOND_NOTE = second;
	}

	public Direction direction() {
		int difference = SECOND_NOTE.compareTo(FIRST_NOTE);
		if (difference > 0)
			return Direction.UP;
		else if (difference < 0)
			return Direction.DOWN;
		return Direction.UNISON;
	}
}
