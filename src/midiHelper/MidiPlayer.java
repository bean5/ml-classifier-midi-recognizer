package midiHelper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

public class MidiPlayer implements MetaEventListener {

	// Midi meta event
	public static final int END_OF_TRACK_MESSAGE = 47;

	private boolean loop = false;

	private Sequencer sequencer;

	private boolean paused;

	static boolean first = true;

	/**
	 * Creates a new MidiPlayer object.
	 */
	public MidiPlayer() {
		try
		{
			this.sequencer = MidiSystem.getSequencer();
			this.sequencer.open();
			this.sequencer.addMetaEventListener(this);
		}
		catch (MidiUnavailableException ex)
		{
			this.sequencer = null;
		}
	}

	public static void compare(Sequence sequence, Sequence sequence2)
	{
		compatePatches(sequence, sequence2);
		compareTracks(sequence.getTracks(), sequence2.getTracks());
	}

	private static void compatePatches(Sequence sequence, Sequence sequence2)
	{
		Patch[] patches0 = sequence.getPatchList();
		Patch[] patches1 = sequence2.getPatchList();

		List<Patch> patchesList0 = new ArrayList<Patch>();
		List<Patch> patchesList1 = new ArrayList<Patch>();

		for (Patch p : patches0)
		{
			patchesList0.add(p);
			System.out.println(p.toString());
		}
		for (Patch p : patches1)
		{
			patchesList1.add(p);
			System.out.println(p.toString());
		}

		for (int j = 0; j < patchesList0.size(); j++)
		{
			for (int i = 0; i < patchesList1.size(); i++)
			{
				if (patchesList0.get(j).equals(patchesList1.get(i)))
				{
					System.out.println("Are equal! at pos: " + i);
					assert (false);
				}
			}
		}
	}

	static void printSequenceInfo(Sequence sequence)
	{
		for (Track t : sequence.getTracks())
		{
			System.out.println(t.toString());
			for (int i = 0; i < t.size() - 1; i++)
			{
				MidiEvent e = t.get(i);
				System.out.println("Event: " + e.toString());
				System.out.println("EventMsg: " + e.getMessage().toString());
				System.out.println("Event's ticks: " + e.getTick());
			}
			System.out.println("Track's ticks: " + t.ticks());
		}
		System.out.println("Sequences's ticks: " + sequence.getTickLength());
		System.out.println(sequence.toString());
	}

	private static void compareTracks(Track[] tracks1, Track[] tracks2)
	{
		List<Track> tracksList0 = new ArrayList<Track>();
		List<Track> tracksList1 = new ArrayList<Track>();

		for (Track t : tracks1)
		{
			tracksList0.add(t);
		}
		for (Track t : tracks2)
		{
			tracksList1.add(t);
		}

		for (int i = 0; i < tracksList0.size(); i++)
		{
			for (int j = 0; j < tracksList1.size(); j++)
			{
				Track t0 = tracksList0.get(i);
				Track t1 = tracksList1.get(j);
				compareEventsOf(t0, t1);
			}
		}
	}

	private static void compareEventsOf(Track t0, Track t1)
	{
		for (int i = 0; i < t0.size(); i++)
		{
			MidiEvent e0 = t0.get(i);
			if (i == t0.size() - 1)
			{
				first = false;
				break;
			}
			if (first)
			{
				printMidiEventInfo(e0);
			}
			for (int j = 0; j < t1.size(); j++)
			{
				MidiEvent e1 = t1.get(j);
				compareEvent(e0, e1);
			}
		}
	}

	private static void printMidiEventInfo(MidiEvent e)
	{
		System.out.println(e.getMessage());
		System.out.println("Tick: " + e.getTick());
		System.out.println(e.getClass());
		System.out.println("");
	}

	private static void compareEvent(MidiEvent e0, MidiEvent e1)
	{
		if (e0.equals(e1))
		{
			assert (false);
		}
	}

	public void reveal(Sequence sequence)
	{
		printPatchesInfo(sequence);
		printSequenceInfo(sequence);
	}

	private void printPatchesInfo(Sequence sequence)
	{
		Patch[] patches = sequence.getPatchList();
		for (Patch p : patches)
		{
			System.out.println(p.toString());
		}
	}

	/**
	 * Loads a sequence from the file system. Returns null if an error occurs.
	 * 
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 * @throws FileNotFoundException
	 */
	public Sequence getSequence(String filename) throws FileNotFoundException,
			InvalidMidiDataException, IOException
	{
		return getSequence(new FileInputStream(filename));
	}

	/**
	 * Loads a sequence from an input stream. Returns null if an error occurs.
	 * 
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public static Sequence getSequence(InputStream is)
			throws InvalidMidiDataException, IOException
	{
		if (!is.markSupported())
		{
			is = new BufferedInputStream(is);
		}
		Sequence s = MidiSystem.getSequence(is);
		is.close();
		return s;
	}

	/**
	 * Plays a sequence, optionally looping. This method returns immediately.
	 * The sequence is not played if it is invalid.
	 */
	public void play(Sequence sequence, boolean loop)
	{
		if (this.sequencer != null && sequence != null
				&& this.sequencer.isOpen())
		{
			try
			{
				this.sequencer.setSequence(sequence);
				this.sequencer.start();
				this.loop = loop;
			}
			catch (InvalidMidiDataException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * This method is called by the sound system when a meta event occurs. In
	 * this case, when the end-of-track meta event is received, the sequence is
	 * restarted if looping is on.
	 */
	@Override
	public void meta(MetaMessage event)
	{
		if (event.getType() == END_OF_TRACK_MESSAGE)
		{
			if (this.sequencer != null && this.sequencer.isOpen() && this.loop)
			{
				this.sequencer.start();
			}
		}
	}

	/**
	 * Stops the sequencer and resets its position to 0.
	 */
	public void stop()
	{
		if (this.sequencer != null && this.sequencer.isOpen())
		{
			this.sequencer.stop();
			this.sequencer.setMicrosecondPosition(0);
		}
	}

	/**
	 * Closes the sequencer.
	 */
	public void close()
	{
		if (this.sequencer != null && this.sequencer.isOpen())
		{
			this.sequencer.close();
		}
	}

	/**
	 * Gets the sequencer.
	 */
	public Sequencer getSequencer()
	{
		return this.sequencer;
	}

	/**
	 * Sets the paused state. Music may not immediately pause.
	 */
	public void setPaused(boolean paused)
	{
		if (isPaused() != paused && this.sequencer != null
				&& this.sequencer.isOpen())
		{
			this.paused = paused;
			if (paused)
			{
				this.sequencer.stop();
			}
			else
			{
				this.sequencer.start();
			}
		}
	}

	/**
	 * Returns the paused state.
	 */
	public boolean isPaused()
	{
		return this.paused;
	}

}