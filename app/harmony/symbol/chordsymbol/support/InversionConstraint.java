package harmony.symbol.chordsymbol.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import harmony.core.Library;
import harmony.single.Pitch;
import harmony.symbol.Symbol;
import harmony.symbol.Symbol.Inversion;

// TODO this should not use roles, compute the bass instead
public class InversionConstraint {

	public static final Map<Inversion, Pitch.Role> INVERSION_TO_BASS_ROLE = new HashMap<Inversion, Pitch.Role>() {
		private static final long serialVersionUID = -3830046152760467978L; // randomly generated

		{
			put(Inversion.ROOT, Pitch.Role.ROOT);
			put(Inversion.FIRST, Pitch.Role.THIRD);
			put(Inversion.SECOND, Pitch.Role.FIFTH);
			put(Inversion.THIRD, Pitch.Role.SEVENTH);
		}
	};

	public final Optional<Symbol.Inversion> inv;
	// An InversionConstraint ignores the assigned role of the expectedBass, just like Pitch itself does.
	// However, it does NOT ignore the role of the incoming tentative bass note when we check to see if the
	// constraint is satisfied.
	public final Optional<Pitch> expectedBass;

	public InversionConstraint(Symbol.Inversion inversion) {
		inv = Optional.of(inversion);
		expectedBass = Optional.empty();
	}

	public InversionConstraint(Pitch bass) {
		this.expectedBass = Optional.of(bass);
		inv = Optional.empty();
	}

	public boolean isSatisfied(Pitch bass) {
		if (inv.isPresent()) {
			Symbol.Inversion inversion = inv.get();
			return (INVERSION_TO_BASS_ROLE.get(inversion).equals(bass.ROLE));
		} else {
			return bass.equals(this.expectedBass.get());
		}
	}

	public String getText() {
		if (inv.isPresent()) {
			String posOrInv;
			if (inv.get() == Inversion.ROOT)
				posOrInv = "Position";
			else
				posOrInv = "Inversion";
			return "in " + Library.beginningCase(inv.get().toString()) + " " + posOrInv;
		} else {
			return "over " + expectedBass.get();
		}
	}
}