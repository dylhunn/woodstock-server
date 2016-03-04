package harmony.core;

import harmony.chord.Chord;
import harmony.progression.Progression;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// TODO clean this up and make it portable
public class LilypondWriter {

	private final String filenamePrefix = "lilypond_output";

	// Path to the Lilypond executable
	private final String execPath = "C:\\Program Files (x86)\\LilyPond\\usr\\bin\\lilypond.exe";
	private final String execPathMac = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";

	// The first parameter is the treble chord list, the second is the bass chord list
	// A chord list is formatted like: <c3 d4 f4> <e5 g5>
	private final String outputFormatString = "\\new GrandStaff << \\new Staff { \\absolute { %s } } \\new Staff"
			+ " { \\clef \"bass\" \\absolute { %s } } >> }  \\version \"2.18.2\"";

	String output = ""; // The formatted Lilypond data

	public LilypondWriter(Progression p) {
		List<Chord> chords = p.getChords();
		String trebleOutput = "";
		String bassOutput = "";
		for (Chord c : chords) {
			trebleOutput += "<";
			bassOutput += "<";

			// TODO doesn't work if there are no bass or treble voices on a given beat
			// TODO doesn't support more than double sharp or flat
			for (int i = 0; i < c.numVoices(); i++) {
				if (i < c.numVoices() / 2) {
					bassOutput = bassOutput + c.getVoice(i).toLilypondString() + " ";
				} else {
					trebleOutput = trebleOutput + c.getVoice(i).toLilypondString() + " ";
				}
			}
			trebleOutput = trebleOutput.substring(0, trebleOutput.length() - 1) + "> ";
			bassOutput = bassOutput.substring(0, bassOutput.length() - 1) + "> ";
		}
		this.output = String.format(outputFormatString, trebleOutput, bassOutput);
	}

	public void show() {
		File f = new File(filenamePrefix + ".ly");
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(f));
			b.write(output);
			b.close();
		} catch (IOException e) {
			System.out.println("Failed to write intermediate Lilypond file");
		}
		try {
			boolean windows = System.getProperty("os.name").contains("Windows");
			String myExecPath = windows ? execPath : execPathMac;
			Process p = Runtime.getRuntime().exec(myExecPath + " " + filenamePrefix + ".ly");
			p.waitFor();
			// Choose either Windows or *nix separators
			// Even if we get the platform wrong, surprisingly, this might work anyway!
			// That's because the path will end up omitting anything but the bare filename,
			// which might be in the current directory.
			String sep = windows ? "\\" : "/";
			String dir = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(sep) + 1);
			String pdf = dir + filenamePrefix + ".pdf";
			System.out.println(pdf);
			Desktop.getDesktop().open(new File(pdf));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to invoke Lilypond or to open the resulting PDF.");
			System.out.println("Are you sure you have Lilypond installed at the right path?");
			System.out.println("We expect to find it at this path: " + execPath);
		}

	}
}
