package project5midiAlignment;

/*
 * Project 5 Driver
 * 
 * Sets some parameters on MidiSequenceTester and then runs it
 * 
 * Calls MidiSequenceTester().run()
 */
public class Project5 {
	public static void main(String[] args)
	{
		// Just to keep you in suspense for 1 millisecond
		wait(1);
		
		MidiSequencerRecognizer mt = new MidiSequencerRecognizer();
		mt.setMinimumSnippetLength(20);
		mt.setMaximumSnippetLength(20);
		mt.setStepSnippetLength(20);
		
		mt.setNoiseMimimum(40);
		mt.setNoiseMaximum(60);
		mt.setNoiseStep(20);
		
		mt.setCorpora("corpora\\assignment5midi\\test\\");
		mt.run();
	}
	
	// Just pauses for the given number of milliseconds
	private static void wait(int i)
	{
		try
		{
			Thread.sleep(i);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
