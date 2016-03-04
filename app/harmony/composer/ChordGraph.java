package harmony.composer;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import harmony.interval.IntervalQuality;
import harmony.symbol.numeral.NumeralSymbol;

public class ChordGraph {

	public static enum ModulationDirection {
		UP, DOWN, NONE
	}

	public ChordGraph() {

	}

	public void initializeFromFile() {

	}

	// Support classes for the chord graph

	public final class Node {

		private final NumeralSymbol symbol;
		private Set<Edge> transitions;
		// If this chord is a pivot chord, what is the parallel chord?
		private Optional<Node> parallel;

		public Node(NumeralSymbol sym) {
			this.symbol = sym;
			transitions = new HashSet<>();
			this.parallel = Optional.empty();
		}

		public Node(NumeralSymbol sym, Node parallel) {
			this.symbol = sym;
			transitions = new HashSet<>();
			this.parallel = Optional.of(parallel);
		}

		public NumeralSymbol getSymbol() {
			return symbol;
		}

		public void addTransition(Edge e) {
			transitions.add(e);
		}

		public Set<Edge> getTransitions() {
			return transitions;
		}
	}

	public final class Edge {

		private final Node source;
		private final Node destination;

		// Modulation data
		private final int interval;
		private final IntervalQuality quality;

		public Edge(Node source, Node destination) {
			this.interval = 0; // No modulation is a perfect unison
			this.quality = new IntervalQuality(IntervalQuality.Type.PERFECT);
			this.source = source;
			this.destination = destination;
		}

		public Edge(int modulationInterval, IntervalQuality quality, ModulationDirection dir, Node source, Node destination) {
			this.interval = modulationInterval;
			this.quality = quality;
			this.source = source;
			this.destination = destination;
		}

		public Node getSource() {
			return source;
		}

		public Node getDestination() {
			return destination;
		}

		public int getInterval() {
			return interval;
		}

		public IntervalQuality getQuality() {
			return quality;
		}
	}
}
