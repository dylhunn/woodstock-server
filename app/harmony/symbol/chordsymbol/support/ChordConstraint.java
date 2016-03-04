package harmony.symbol.chordsymbol.support;

import harmony.chord.Chord;

// A ChordConstraint provides constraints on chords that are unique to an
// individual occurrence.
// For example, it might be used to enforce that a chord has a specific melody
// note.
// This should NOT be used to enforce details of how certain chords are voiced
// in general.
// For that, create new subclasses of Chord and ChordSymbol.
@FunctionalInterface
public interface ChordConstraint {

	public static final ChordConstraint DEFAULT_CONSTRAINT = c -> true;

	public boolean isSatisfiedBy(Chord c);
}