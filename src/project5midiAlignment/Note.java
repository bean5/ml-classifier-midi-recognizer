package project5midiAlignment;

/*
 * A simpler representation of a Midi note. 
 * For now, duration is not taken into account 
 */
public class Note {
	int asInt;
	String note;
	int octave;
	int duration = 0;
	
	static final public String[] keyNames =
	{ "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	// Constructors
	public Note(int nKeyNumber) {
		setNote(nKeyNumber);
	}

	public Note(Note n) {
		setIntegerRepresentation(n.getIntegerRepresentation());
		setNote(n.getNote(), n.getOctave());
		setDuration(n.getDuration());
	}

	private void setIntegerRepresentation(int integerRep) {this.asInt = integerRep;}
	public int getIntegerRepresentation() {return asInt;}
	private int getOctave() {return octave;}
	private String getNote() {return note;}
	private int getDuration() {return duration;}

	public void setNote(int nKeyNumber)
	{
		if (nKeyNumber > 127)
			throw new UnknownError("Note does not exist");

		int truncated = (nKeyNumber / 12);
		int nNote = nKeyNumber - truncated * 12;

		assert (nNote == nKeyNumber % 12);
		
		setIntegerRepresentation(nKeyNumber);
		setNote(keyNames[nNote]);
		setOctave(truncated - 1);
	}
	
	public void setNote(String n, int o)
	{
		setNote(n);
		setOctave(o);
	}

	public void setNote(String n) {note = n;}

	public void setOctave(int o) {octave = o;}

	private void setDuration(int d) {this.duration = d;}


	@Override
	public String toString() {
		return note + octave;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Note) {
			return equals((Note) o);
		}
		return false;
	}
	
	public boolean equals(Note o) {
		return getIntegerRepresentation() == o.getIntegerRepresentation();
	}
}
