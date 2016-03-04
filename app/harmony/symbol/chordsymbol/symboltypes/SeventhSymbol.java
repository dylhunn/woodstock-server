package harmony.symbol.chordsymbol.symboltypes;

import harmony.single.Pitch;
import harmony.single.Pitch.Role;
import harmony.symbol.chordsymbol.support.ChordConstraint;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;

import java.util.*;

public abstract class SeventhSymbol extends ChordSymbol {

	public SeventhSymbol(Pitch root, Inversion inversion) {
		super(root, inversion);
	}

	public SeventhSymbol(Pitch root, Inversion inversion, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		super(root, inversion, modifications, constraint);
		// TODO Auto-generated constructor stub
	}

	public SeventhSymbol(Pitch root, Pitch bass, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		super(root, bass, modifications, constraint);
	}

	@Override
	protected Map<Role, Integer> chordVoicingMapWithoutModifications() {
		Map<Pitch.Role, Integer> roleMap = new HashMap<>();
		roleMap.put(Pitch.Role.ROOT, 1);
		roleMap.put(Pitch.Role.THIRD, 1);
		roleMap.put(Pitch.Role.FIFTH, 1);
		roleMap.put(Pitch.Role.SEVENTH, 1);
		return roleMap;
	}

	@Override
	protected Set<Role> mandatoryRolesWithoutModifications() {
		Set<Role> roles = new HashSet<>();
		roles.add(Role.ROOT);
		roles.add(Role.THIRD);
		roles.add(Role.SEVENTH);
		return roles;
	}

	@Override
	protected String name() {
		return "Seventh Chord";
	}

}
