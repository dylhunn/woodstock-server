package harmony.symbol.chordsymbol.symboltypes;

import harmony.single.Pitch;
import harmony.single.Pitch.Role;
import harmony.symbol.chordsymbol.support.ChordConstraint;
import harmony.symbol.chordsymbol.support.ChordSymbolAlteration;

import java.util.*;

public abstract class TriadSymbol extends ChordSymbol {

	public TriadSymbol(Pitch root, Inversion inversion) {
		super(root, inversion);
	}

	public TriadSymbol(Pitch root, Inversion inversion, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		super(root, inversion, modifications, constraint);
		// TODO Auto-generated constructor stub
	}

	public TriadSymbol(Pitch root, Pitch bass, Set<ChordSymbolAlteration> modifications, Optional<ChordConstraint> constraint) {
		super(root, bass, modifications, constraint);
	}

	// For a typical four-part triad, expect the root, third, and fifth, with a doubled root 
	@Override
	protected Map<Pitch.Role, Integer> chordVoicingMapWithoutModifications() {
		Map<Pitch.Role, Integer> roleMap = new HashMap<>();
		roleMap.put(Pitch.Role.ROOT, 2);
		roleMap.put(Pitch.Role.THIRD, 1);
		roleMap.put(Pitch.Role.FIFTH, 1);
		return roleMap;
	}

	@Override
	protected Set<Role> mandatoryRolesWithoutModifications() {
		Set<Role> roles = new HashSet<>();
		roles.add(Role.ROOT);
		roles.add(Role.THIRD);
		return roles;
	}

	@Override
	protected String name() {
		return "Triad";
	}
}
