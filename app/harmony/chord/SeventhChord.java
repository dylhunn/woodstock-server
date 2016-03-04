package harmony.chord;

import harmony.single.Note;
import harmony.single.Pitch;
import harmony.symbol.chordsymbol.symboltypes.ChordSymbol;

import java.util.Collection;

public class SeventhChord extends Chord {

	public SeventhChord(Collection<Note> notes, ChordSymbol sym) {
		super(notes, sym);
	}

	@Override
	protected boolean meetsResolutionRequirements(Chord next) {
		// TODO what if the next chord has a different number of voices?
		for (int i = 0; i < numVoices(); i++) {
			if (getVoice(i).PITCH.ROLE == Pitch.Role.SEVENTH) {
				// 7th resolves down by step
				return getVoice(i).midiNumber() - next.getVoice(i).midiNumber() == 1;
			}
		}
		return false; // The seventh chord has no seventh!
	}
}
