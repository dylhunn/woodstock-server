package harmony.single;

import harmony.interval.Interval;

public class VoiceRange {

	public static enum VoiceType {
		BASS, TENOR, ALTO, SOPRANO
	}

	public static Interval getRange(VoiceType voice) {
		switch (voice) {
		case BASS:
			return new Interval(new Note(Pitch.Name.E, new Accidental(Accidental.AccidentalType.NATURAL), 2),
					new Note(Pitch.Name.E, new Accidental(Accidental.AccidentalType.NATURAL), 4));
		case TENOR:
			return new Interval(new Note(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), 3),
					new Note(Pitch.Name.A, new Accidental(Accidental.AccidentalType.NATURAL), 4));
		case ALTO:
			return new Interval(new Note(Pitch.Name.G, new Accidental(Accidental.AccidentalType.NATURAL), 3),
					new Note(Pitch.Name.E, new Accidental(Accidental.AccidentalType.NATURAL), 5));
		case SOPRANO:
			return new Interval(new Note(Pitch.Name.C, new Accidental(Accidental.AccidentalType.NATURAL), 4),
					new Note(Pitch.Name.A, new Accidental(Accidental.AccidentalType.NATURAL), 5));
		default:
			throw new RuntimeException("Unknown voice type: " + voice);
		}
	}

	// voiceNumber is the position of the voice in the list of voices, which is ordered from low to high
	// Warning: voiceNUmber is 0-indexed
	public static final VoiceType getVoiceType(int voiceNumber, int numVoices) {
		int voiceIndex = voiceNumber * 4 / numVoices;
		switch (voiceIndex) {
		case 0:
			return VoiceType.BASS;
		case 1:
			return VoiceType.TENOR;
		case 2:
			return VoiceType.ALTO;
		case 3:
			return VoiceType.SOPRANO;
		default:
			throw new RuntimeException("The voice ID could not be identified."); // Impossible with intended code path.
		}
	}

}
