package project5midiAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/*
 * Performs simple metric conversions of some of the results and prints them to the stdout
 */
public class ResultPrinter {

	/*
	 * Performs simple metrics and prints them
	 */
	public static void printResults(int totalTests, int firstAnswer, int secondAnswer, int thirdAnswer)
	{
		System.out.println("Total Tests: " + totalTests);
		System.out.println("Total First Answer: " + firstAnswer);
		System.out.println("Total Second Answer: " + secondAnswer);
		System.out.println("Total Third Answer: " + thirdAnswer);

		double first = ((double) firstAnswer)/totalTests;
		double second = ((double) secondAnswer)/totalTests;
		double third = ((double) thirdAnswer)/totalTests;
		double total = ((double) (firstAnswer + secondAnswer + thirdAnswer))/totalTests;

		System.out.format("First Efficiency: %f\n", first);
		System.out.format("Second Efficiency: %f\n", second);
		System.out.format("Third Efficiency: %f\n", third);
		System.out.format("Overall Efficiency: %f\n", total);
		System.out.println("\n\n");
	}

	/*
	 * For future use
	 */
	private static void printSequenceInfo(MidiSequenceTestConfig config)
	{
		System.out.println("Confusion:");
		System.out.println("Noise: " + config.noiseRate);
		System.out.println("SnippetLength: " + config.sequenceLength);
		System.out.println("");
	}


	/*
	 * For future use
	 */
	private static void printConfusionMatrix(HashMap<MidiSequence, List<MidiSequenceScore>> value)
	{
		for (Entry<MidiSequence, List<MidiSequenceScore>> e : value.entrySet())
		{
			System.out.println("Song: " + e.getKey().getName()
					+ " confused as: ");
			int count = 0;
			for (MidiSequenceScore ms : e.getValue())
			{
				if (count > 3)
					break;
				System.out.println("\t" + ms.getSequence().getName()
						+ " score: " + ms.getScore());
			}
		}
	}
}
