package project5midiAlignment;

public class MidiSequenceScore {
	MidiSequence sequence;
	Double score;

	public MidiSequenceScore(MidiSequence m, Double s) {
		sequence = m;
		score = s;
	}
	
	public MidiSequence getSequence()
	{
		return sequence;
	}

	public Double getScore()
	{
		return score;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof MidiSequenceScore) {
			return equals((MidiSequenceScore) o);
		}
		return false;
	}
	
	public boolean equals(MidiSequenceScore o) {
		return sequence.equals(o.getSequence()) && getScore() == o.getScore();
	}

	@Override
	public int hashCode() {return sequence.hashCode() + score.hashCode();}
}
