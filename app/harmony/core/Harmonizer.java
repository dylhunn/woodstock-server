package harmony.core;

import java.util.ArrayList;
import java.util.List;

import harmony.chord.Chord;
import harmony.exception.IllegalChordSymbolException;
import harmony.exception.ProgressionInputException;
import harmony.progression.Progression;
import harmony.single.Note;
import harmony.symbol.parser.ProgressionParser;

public class Harmonizer {
	// Returns a list of chords, each of which contains a list of tones (strings)
	public static List<List<String>> harmonize(String input) throws IllegalChordSymbolException, ProgressionInputException {
		Progression p;
		p = ProgressionParser.parse(input);
		List<List<String>> output = new ArrayList<>();
		for (Chord c : p.getChords()) {
			List<String> voices = new ArrayList<>();
			for (int i = c.numVoices()-1; i >= 0; i--) {
				Note n = c.getVoice(i);
				voices.add(n.toString());
			}
			output.add(voices);
		}
		return output;
	}
}
