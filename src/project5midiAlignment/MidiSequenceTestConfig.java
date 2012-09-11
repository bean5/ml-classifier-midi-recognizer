package project5midiAlignment;

public class MidiSequenceTestConfig {
	int noiseRate = 0;
	int sequenceLength = 0;

	public MidiSequenceTestConfig(int n, int s) {
		noiseRate = n;
		sequenceLength = s;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof MidiSequenceTestConfig) {
			return equals((MidiSequenceTestConfig) o);
		}
		return false;
	}
	
	public boolean equals(MidiSequenceTestConfig o) {
		return getNoiseRate() == o.getNoiseRate() && getSequenceLength() == o.getSequenceLength();
	}

	private Object getSequenceLength() {return noiseRate;}

	private Object getNoiseRate() {return sequenceLength;}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
