package harmony.chord;

import harmony.single.Note;
import harmony.symbol.chordsymbol.symboltypes.ChordSymbol;

import java.util.Collection;

public class Triad extends Chord {

	public Triad(Collection<Note> notes, ChordSymbol sym) {
		super(notes, sym);
	}

	@Override
	protected boolean meetsResolutionRequirements(Chord next) {
		return true;
	}
}
