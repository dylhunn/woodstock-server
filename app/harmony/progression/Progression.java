package harmony.progression;

import harmony.chord.Chord;
import harmony.exception.ProgressionInputException;
import harmony.single.Key;
import harmony.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Progression {

	// The maximum number of realizations that are considered for each chord
	// For instance, if this is 15, the 15 best realizations for each chord will be evaluated in context
	private final int MAX_SEARCH_DEPTH = 30;

	private List<ChordSeries> progressions;
	private Optional<List<Symbol>> symbols; // caching
	private Optional<List<Chord>> chords; // for caching
	private final int numVoices;

	public Progression(List<ChordSeries> progressions, int numVoices) {
		this.progressions = progressions;
		this.numVoices = numVoices;
		chords = Optional.empty();
		symbols = Optional.empty();
	}

	public void add(ChordSeries p) {
		progressions.add(p);
		chords = Optional.empty(); // Clear cache
		symbols = Optional.empty();
	}

	public List<Symbol> getSymbols() {
		if (this.symbols.isPresent())
			return this.symbols.get();
		List<Symbol> symbols = new ArrayList<>();
		for (ChordSeries p : progressions) { // must iterate in order
			symbols.addAll(p.getSymbols());
		}
		this.symbols = Optional.of(symbols);
		return symbols;
	}

	public Optional<Key> getKeyOfSymbol(int index) { // TODO inefficient
		if (index >= length())
			throw new RuntimeException("Invalid chord index.");
		for (int i = 0; i < progressions.size(); i++) {
			if (index < progressions.get(i).length())
				return progressions.get(i).getKeyOfSymbol(index);
			index -= progressions.get(i).length();
		}
		throw new RuntimeException("Invalid chord index in overall progression.");
	}

	public List<Chord> getChords() throws ProgressionInputException {
		if (this.chords.isPresent())
			return this.chords.get(); // caching
		List<Symbol> symbols = getSymbols(); // fill cache

		List<Chord> progression = new ArrayList<>();
		for (int i = 0; i < symbols.size(); i++) {
			Symbol current = symbols.get(i);
			List<Chord> currentRealizations = current.getChordSymbol(getKeyOfSymbol(i)).getChordRealizations(numVoices);
			if (progression.isEmpty())
				progression.add(currentRealizations.get(0)); // TODO what if there are no realizations?
			else {
				Chord previous = progression.get(progression.size() - 1);
				progression.add(currentRealizations.stream() // TODO true recursive backtracking algorithm
						.limit(MAX_SEARCH_DEPTH)
						.max((c1,
								c2) -> (previous.evaluateAloneAndInContext(c1, Optional.empty())
										- previous.evaluateAloneAndInContext(c2, Optional.empty()) > 0) ? 1 : -1) // TODO cache scores
						.get()); // TODO what if there are no realizations?
			}
		}

		this.chords = Optional.of(progression);
		return progression;
	}

	public int length() {
		return getSymbols().size();
	}

	/*public String toString() {
		String ret = "";
		List<Chord> chords = getChords();
		List<Symbol> symbols = getSymbols();
		assert (symbols.size() == chords.size());
		for (int i = 0; i < chords.size(); i++) {
			ret = ret + symbols.get(i) + ": " + chords.get(i);
			if (i != chords.size() - 1) ret += ", ";
		}
		return ret;
	}*/
}
