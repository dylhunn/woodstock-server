package harmony.core;

import harmony.exception.IllegalChordSymbolException;
import harmony.exception.ProgressionInputException;
import harmony.progression.Progression;
import harmony.symbol.parser.ProgressionParser;

import java.util.Scanner;

public class Main {

	// Main class for testing purposes only

	public static void main(String[] args) {
		System.out.println("Enter chord symbols or roman numerals.");
		Scanner input = new Scanner(System.in);
		while (true) {
			Progression p;
			try {
				p = ProgressionParser.parse(input.nextLine());
			} catch (IllegalChordSymbolException | ProgressionInputException e) {
				System.out.println(e.getMessage() + "\n");
				continue;
			} catch (Exception e) {
				System.out.println("Unexpected internal error. " + e.getMessage() + "\n");
				continue;
			}
			/*System.out.println(symbols.get(symbols.size() - 1));
			System.out.println(chords.get(chords.size() - 1));*/
			System.out.println("Generating PDF file...");
			System.out.println(p);
			new LilypondWriter(p).show();
			//chords.get(chords.size() - 1).printEvaluationData(Optional.empty(), Optional.empty());
		}
	}
}
