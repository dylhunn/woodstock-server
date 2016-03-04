package harmony.interval;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import harmony.single.Accidental;
import harmony.single.Note;
import harmony.single.Pitch;

public class IntervalTest {

	Note n1;
	Note n2;
	Note n3;
	Note n4;
	Note n5;
	Note n6;
	Note n7;
	Note n8;
	Note n9;

	// first constructor
	Interval i1;
	Interval i2;
	Interval i3;
	Interval i4;
	Interval i5;
	Interval i6;
	Interval i7;
	Interval i8;

	// second constructor
	Interval i9;

	@Before
	public void setUp() throws Exception {
		n1 = new Note(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.SHARP), Pitch.Role.ROOT), 3);
		n2 = new Note(new Pitch(Pitch.Name.D, new Accidental(Accidental.AccidentalType.DOUBLE_SHARP), Pitch.Role.ROOT), 3);
		n3 = new Note(new Pitch(Pitch.Name.D, new Accidental(5), Pitch.Role.ROOT), 3);
		n4 = new Note(new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.NATURAL), Pitch.Role.ROOT), 3);
		n5 = new Note(new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.FLAT), Pitch.Role.ROOT), 3);
		n6 = new Note(new Pitch(Pitch.Name.E, new Accidental(Accidental.AccidentalType.TRIPLE_FLAT), Pitch.Role.ROOT), 3);
		n7 = new Note(new Pitch(Pitch.Name.F, new Accidental(-7), Pitch.Role.ROOT), 3);
		n8 = new Note(new Pitch(Pitch.Name.F, new Accidental(-28), Pitch.Role.ROOT), 4);
		n9 = new Note(new Pitch(Pitch.Name.C, new Accidental(16), Pitch.Role.ROOT), 2);

		i1 = new Interval(n1, n4);
		i2 = new Interval(n1, n2);
		i3 = new Interval(n1, n3);
		i4 = new Interval(n1, n5);
		i5 = new Interval(n1, n6);
		i6 = new Interval(n1, n7);
		i7 = new Interval(n1, n8);
		i8 = new Interval(n1, n9);

		i9 = new Interval(n1, 2, new IntervalQuality(2, -1), Pitch.Role.ROOT);
	}

	@Test
	public void test() {
		// TODO More tests for second Interval constructor

		assertSame(i1.HIGH_NOTE, n4);
		assertSame(i1.INTERVAL, 2);
		assertSame(i1.NUM_SEMITONES, 1);
		assertSame(i1.REDUCED_INTERVAL, 2);
		assertSame(i1.REDUCED_NUM_SEMITONES, 1);

		assertSame(i2.HIGH_NOTE, n2);
		assertSame(i2.INTERVAL, 1);
		assertSame(i2.NUM_SEMITONES, 1);
		assertSame(i2.REDUCED_INTERVAL, 1);
		assertSame(i2.REDUCED_NUM_SEMITONES, 1);

		assertSame(i3.HIGH_NOTE, n3);
		assertSame(i3.INTERVAL, 1);
		assertSame(i3.NUM_SEMITONES, 4);
		assertSame(i3.REDUCED_INTERVAL, 1);
		assertSame(i3.REDUCED_NUM_SEMITONES, 4);

		assertSame(i4.HIGH_NOTE, n5);
		assertSame(i4.INTERVAL, 2);
		assertSame(i4.NUM_SEMITONES, 0);
		assertSame(i4.REDUCED_INTERVAL, 2);
		assertSame(i4.REDUCED_NUM_SEMITONES, 0);

		assertSame(i5.HIGH_NOTE, n6);
		assertSame(i5.INTERVAL, 2);
		assertSame(i5.NUM_SEMITONES, -2);
		assertSame(i5.REDUCED_INTERVAL, 2);
		assertSame(i5.REDUCED_NUM_SEMITONES, -2);

		assertSame(i6.HIGH_NOTE, n7);
		assertSame(i6.INTERVAL, 3);
		assertSame(i6.NUM_SEMITONES, -5);
		assertSame(i6.REDUCED_INTERVAL, 3);
		assertSame(i6.REDUCED_NUM_SEMITONES, -5);

		assertSame(i7.HIGH_NOTE, n8);
		assertSame(i7.INTERVAL, 10);
		assertSame(i7.NUM_SEMITONES, -14);
		assertSame(i7.REDUCED_INTERVAL, 3);
		assertSame(i7.REDUCED_NUM_SEMITONES, -2);

		assertSame(i8.HIGH_NOTE, n1);
		assertSame(i8.INTERVAL, 9);
		assertSame(i8.NUM_SEMITONES, -1);
		assertSame(i8.REDUCED_INTERVAL, 2);
		assertSame(i8.REDUCED_NUM_SEMITONES, -1);

		assertSame(i9.LOW_NOTE, n1);
		assertSame(i9.getQuality().getDegree(), 2);
		assertSame(i9.getQuality().type(), IntervalQuality.Type.DIMINISHED);
		assertSame(i9.REDUCED_INTERVAL, 2);
		assertSame(i9.REDUCED_NUM_SEMITONES, -1);
	}
}
