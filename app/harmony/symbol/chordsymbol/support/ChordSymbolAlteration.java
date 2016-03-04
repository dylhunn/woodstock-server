package harmony.symbol.chordsymbol.support;

import harmony.core.Library;
import harmony.single.Pitch;

public class ChordSymbolAlteration {

	public static enum Type {
		ADD, REMOVE
	}

	public final Pitch PITCH;
	public final Type TYPE;

	public ChordSymbolAlteration(Pitch pitch, Type type) {
		PITCH = pitch;
		TYPE = type;
	}
	
	public boolean isMandatory() {
		return true; // TODO
	}

	@Override
	public String toString() {
		return Library.beginningCase(TYPE.toString()) + " " + Library.beginningCase(PITCH.toString());
	}
}
