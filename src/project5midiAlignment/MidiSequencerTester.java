package project5midiAlignment;

/*
Midi music recognition
By Michael Bean
Utilizes Gene Sequence alignment
*/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;

import midiHelper.MidiFileFinder;
import edu.byu.nlp.util.Counter;
import edu.byu.nlp.util.DoubleCounter;
import edu.byu.nlp.util.PriorityQueue;

/**
* An example that plays a Midi sequence. First, the sequence is played once
* with track 1 turned off. Then the sequence is played once with track 1 turned
* on. Track 1 is the drum track in the example midi file.
*/
@SuppressWarnings("deprecation")
public class MidiSequencerTester {
	private int totalTests = 0;
	private int firstAnswer = 0;
	private int secondAnswer = 0;
	private int thirdAnswer = 0;
	private final GeneSequenceAligner<String> sequencer = new GeneSequenceAligner<String>(-3, -1, 0);
	private final static Random randomNumGen = new Random();

	public void run(String midiMainFolder) 
	{
		List<File> midiFiles = null;
		List<MidiSequence> midiSequences = null;
		// Load in a list of all midi Files
		try {
			midiFiles = MidiFileFinder.findMidiFromDir(midiMainFolder);
			
			//printFileInfo(midiFiles);
	
			// read in all midis from files
			midiSequences = readInMidiSequences(midiFiles);
			printCorporaInfo(midiSequences);
		}
		catch(UnknownError e) {
			e.printStackTrace();
			if(midiSequences != null && midiSequences.size()==0)
				return;
		}
		assert(midiSequences != null && midiSequences.size() > 0);
		
		if(midiSequences == null)
			return;
		
		runIterativeTests(midiSequences);
	}

	private static void printCorporaInfo(List<MidiSequence> midiSequences)
	{
		System.out.println("Number of files: " + midiSequences.size());
		for (MidiSequence m : midiSequences)
		{
			System.out.println(m.getSongInfo());
		}
	}

	private static void printFileInfo(List<File> midiFiles)
	{
		System.out.println("Number of files: " + midiFiles.size());
		for (File f : midiFiles)
		{
			System.out.println(f.toString());
		}
	}

	private void runIterativeTests(List<MidiSequence> midiSequences)
	{
		for (int testSequenceLength = 70; testSequenceLength <= 90; testSequenceLength += 20)
		{
			for (int simulatedNoiseRate = 0; simulatedNoiseRate <= 40; simulatedNoiseRate += 20)
			{
				System.out.println("Length/Noise: " + testSequenceLength + "/" + simulatedNoiseRate);

				totalTests = 0;
				firstAnswer = 0;
				secondAnswer = 0;
				thirdAnswer = 0;
				for (MidiSequence song : midiSequences)
				{
					runTest(song, midiSequences, simulatedNoiseRate, testSequenceLength);

					totalTests++;
				}
				ResultPrinter.printResults(totalTests, firstAnswer, secondAnswer, thirdAnswer);
			}
		}
	}

	private void runTest(
		MidiSequence song,
		List<MidiSequence> midiFiles,
		int simulatedNoiseRate,
		int testSequenceLength
	)
	{
		MidiSequence snippet = getSnippet(song, testSequenceLength);
		simulateNoise(snippet, simulatedNoiseRate);
		
		Counter<MidiSequence> c = new DoubleCounter<MidiSequence>();

		for (MidiSequence current : midiFiles) {
			c.incrementCount(current, Integer.MAX_VALUE + retrieveScoreOfSequenceAlignment(snippet, current));
		}
		
		
		boolean correctAnswerFound = false;
		PriorityQueue<MidiSequence> queue = c.asPriorityQueue();
		int i = 0;
		while (queue.hasNext() && i < 4 && correctAnswerFound == false)
		{
			evaluate(song, i, queue.next());
			i++;
		}
	}

	private boolean evaluate(MidiSequence origianlSong, int i, MidiSequence posited)
	{
		boolean guessIsRight = posited.getName().equals(origianlSong.getName());
		if (guessIsRight == false) {
			return false;
		}

		if(guessIsRight) {
			if (i == 0) 		{firstAnswer++;}
			else if (i == 1)	{secondAnswer++;System.out.println(origianlSong.getName() + " correct as second");}
			else if (i == 2)	{thirdAnswer++;System.out.println(origianlSong.getName() + " correct as third");}
		}
		else {
			if (i == 0) 		{System.out.println(origianlSong.getName() + " has false first: " + posited.getName());}
			else if (i == 1)	{System.out.println(origianlSong.getName() + " has false second: " + posited.getName());}
			else if (i == 2)	{System.out.println(origianlSong.getName() + " has false third: " + posited.getName());}
			else {
				System.out.println(origianlSong.getName() + " not correct within first 3");
			}
		}
		
		return guessIsRight;
	}

	private int retrieveScoreOfSequenceAlignment(MidiSequence snippet, MidiSequence c)
	{
		sequencer.align(snippet.toArray(), c.toArray());

		int score = sequencer.findProfileScore();
		return score;
	}

	private static MidiSequence getSnippet(MidiSequence originalMidi, int testSequenceLength)
	{
		MidiSequence snippet = new MidiSequence(originalMidi);

		List<Note> originalNotes = originalMidi.getNoteSequence();

		if (testSequenceLength >= originalNotes.size())
			return snippet;

		int cropLocation = randomNumGen.nextInt(originalNotes.size() - testSequenceLength);

		List<Note> snippetNotes = new ArrayList<Note>(testSequenceLength);

		int end = cropLocation + testSequenceLength;
		for(int i = cropLocation; i < end; i++) {
			snippetNotes.add(new Note(originalNotes.get(i)));
		}
		
		snippet.setNoteSequence(snippetNotes);

		assert (snippetNotes.size() == testSequenceLength);
		assert (snippet.getNoteSequence().size() == testSequenceLength);

		return snippet;
	}

	static enum NoiseOption {Insert, Remove, Replace}
	private static void simulateNoise(MidiSequence sequence,
			int simulatedNoiseRate)
	{
		if (simulatedNoiseRate > 0)
		{
			//System.out.println("Generating noise");
			
			List<Note> notes = sequence.getNoteSequence();
			assert (notes.size() > 0);
			int max = notes.size();
			for (int i = 0; i < max; i++)
			{
				if (randomNumGen.nextInt(99) < NoiseOption.Insert.ordinal())
				{
					int indel_Or_Replace = randomNumGen.nextInt(3);
					if (indel_Or_Replace == 0)
					{
						notes.add(i + 1, new Note(randomNumGen.nextInt(128)));
						// System.out.println("Random Note Generated:" + notes.get(i + 1).toString());
						//if (i > 0 && i + 2 < notes.size())
						//	System.out.println("Between " + notes.get(i) + " and " + notes.get(i + 2).toString());
						
						i++;
						max++;
					}
					else if (indel_Or_Replace == NoiseOption.Remove.ordinal())
					{
						//System.out.println("Removing note: " + notes.get(i).toString());
						notes.remove(i);
						i--;
						max--;
					}
					else if (indel_Or_Replace == NoiseOption.Replace.ordinal())
					{
						//System.out.println("Replacing: " + notes.get(i).toString());
						notes.get(i).setNote(randomNumGen.nextInt(128));
						//System.out.println("Replaced with: " + notes.get(i));
					}
				}
			}
		}
	}

	private static List<MidiSequence> readInMidiSequences(List<File> midiFiles)
	{
		List<MidiSequence> midis = new ArrayList<MidiSequence>(midiFiles.size());

		for (File f : midiFiles)
		{
			try
			{
				midis.add(new MidiSequence(f));
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			catch (InvalidMidiDataException ex)
			{
				ex.printStackTrace();
			}
		}
		return midis;
	}

}


