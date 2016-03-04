package harmony.single;

import harmony.core.Library;
import harmony.interval.Interval;
import harmony.interval.IntervalQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Key {

	public enum KeyType {
		MAJOR, H_MINOR
	}

	private static final List<Integer> stepsInMajorScale = new ArrayList<>(Arrays.asList(2, 2, 1, 2, 2, 2, 1));
	// The default minor scale is harmonic!
	private static final List<Integer> stepsInHMinorScale = new ArrayList<>(Arrays.asList(2, 1, 2, 2, 1, 3, 1));

	public final Pitch PITCH;
	public final KeyType TYPE;

	public Key(Pitch pitch, KeyType type) {
		this.PITCH = pitch;
		this.TYPE = type;
	}

	// TODO clean up this implementation!
	public List<Pitch> getScale() {
		List<Integer> steps;
		switch (TYPE) {
		case MAJOR:
			steps = stepsInMajorScale;
			break;
		case H_MINOR:
			steps = stepsInHMinorScale;
			break;
		default:
			throw new RuntimeException("Unknown scale type.");
		}

		List<Pitch> scale = new ArrayList<>(Arrays.asList(PITCH)); // Scale starting with the root note.
		for (int i = 0; i < Pitch.NUM_DISTINCT_NOTE_NAMES - 1; i++) { // TODO clean up -1
			scale.add(new Interval(new Note(scale.get(scale.size() - 1), -1), 2, // Interval of a second (one pitch)
					new IntervalQuality(2, steps.get(i)), Arrays.asList(Pitch.Role.values()).get(i+1)).HIGH_NOTE.PITCH);
		}
		return scale;
	}

	public Pitch getDegree(int degree) {
		while (degree > Pitch.NUM_DISTINCT_NOTE_NAMES)
			degree -= Pitch.NUM_DISTINCT_NOTE_NAMES;
		return getScale().get(degree);
	}

	@Override
	public String toString() {
		return PITCH.toString() + " " + Library.beginningCase(TYPE.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		return (TYPE == other.TYPE && PITCH.equals(other.PITCH));
	}
}
