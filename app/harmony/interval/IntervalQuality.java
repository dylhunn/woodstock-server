package harmony.interval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import harmony.single.Pitch;

public class IntervalQuality {

	// Sets of intervals grouped by their natural quality
	private static final Set<Integer> MAJOR_INTERVALS = new HashSet<>(Arrays.asList(2, 3, 6, 7));
	private static final Set<Integer> PERFECT_INTERVALS = new HashSet<>(Arrays.asList(1, 4, 5, 8));

	public static enum Type {
		DIMINISHED, MINOR, MAJOR, PERFECT, AUGMENTED
	}

	private final Type quality;
	// This is a multiplier that can be applied to augmented or diminished intervals (i.e. "twice diminished")
	// Should be "1" by default
	private final int degree;

	public IntervalQuality(int interval, int numSemitones) {
		interval = Interval.computeReducedInterval(interval);
		numSemitones = Interval.computeReducedNumSemitones(numSemitones);
		Integer numSemitonesInGivenIntervalOfPerfectQuality = semitonesInMajorOrPerfectInterval(interval);
		int difference = numSemitones - numSemitonesInGivenIntervalOfPerfectQuality;
		if (MAJOR_INTERVALS.contains(interval)) {
			if (difference < -1) {
				quality = Type.DIMINISHED;
				degree = Math.abs(difference + 1);
			} else if (difference == -1) {
				quality = Type.MINOR;
				degree = 1;
			} else if (difference == 0) {
				quality = Type.MAJOR;
				degree = 1;
			} else {
				quality = Type.AUGMENTED;
				degree = difference;
			}
		} else {
			if (difference < 0) {
				quality = Type.DIMINISHED;
				degree = Math.abs(difference);
			} else if (difference == 0) {
				quality = Type.PERFECT;
				degree = 1;
			} else {
				quality = Type.AUGMENTED;
				degree = difference;
			}
		}
	}

	// Convenience constructor
	public IntervalQuality(Type quality) {
		this.quality = quality;
		degree = 1;
	}

	public Type type() {
		return quality;
	}

	// a degree of 1 is standard, a degree of >2 indicates something like "twice diminished"
	// The degree should never be negative!
	public int getDegree() {
		return degree;
	}

	public static final int semitonesInInterval(int interval, IntervalQuality quality) {
		int redInterval = Interval.computeReducedInterval(interval);
		int semitones = semitonesInMajorOrPerfectInterval(interval);
		if (MAJOR_INTERVALS.contains(redInterval)) {
			switch (quality.type()) {
			case DIMINISHED:
				semitones -= (1 + quality.getDegree());
				break;
			case MINOR:
				semitones--;
				break;
			case MAJOR:
				break;
			case AUGMENTED:
				semitones += quality.getDegree();
				break;
			default:
				throw new RuntimeException("Impossible interval: " + quality.toString() + " " + interval);
			}
		} else { // Interval is naturally perfect
			switch (quality.type()) {
			case DIMINISHED:
				semitones -= quality.getDegree();
				break;
			case PERFECT:
				break;
			case AUGMENTED:
				semitones += quality.getDegree();
				break;
			default:
				throw new RuntimeException("Impossible interval: " + quality.toString() + " " + interval);
			}
		}
		int numOctavesReduced = (interval - redInterval) / Pitch.NUM_DISTINCT_NOTE_NAMES;
		return semitones + Pitch.NUM_DISTINCT_PITCHES * numOctavesReduced;
	}

	public static final int semitonesInMajorOrPerfectInterval(int interval) {
		int redInterval = Interval.computeReducedInterval(interval);
		int semitones;
		switch (redInterval) {
		case 1:
			semitones = 0;
			break;
		case 2:
			semitones = 2;
			break;
		case 3:
			semitones = 4;
			break;
		case 4:
			semitones = 5;
			break;
		case 5:
			semitones = 7;
			break;
		case 6:
			semitones = 9;
			break;
		case 7:
			semitones = 11;
			break;
		case 8:
			semitones = 12;
			break;
		default:
			throw new RuntimeException("Error: unable to convert interval: " + interval);
		}
		return semitones + Pitch.NUM_DISTINCT_PITCHES * ((interval - redInterval) / Pitch.NUM_DISTINCT_NOTE_NAMES);
	}
}
