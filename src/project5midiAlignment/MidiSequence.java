package project5midiAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import midiHelper.MidiPlayer;

public class MidiSequence {
	private final String fileName;
	private List<Note> noteSequence = new ArrayList<Note>();
	private static MidiPlayer player = new MidiPlayer();

	public MidiSequence(File file) throws FileNotFoundException,
			InvalidMidiDataException, IOException {
		fileName = file.toPath().toString();
		Sequence s = player.getSequence(file.toPath().toString());
		setSequentialNotes(s);
	}

	// for each track
	private void setSequentialNotes(Sequence s)
	{
		int max = 0;
		for (Track t : s.getTracks())
		{
			if (max == 0)
				setSequentialNotes(t);
			// max--;
		}
	}

	// appends all starting notes to the list of notes
	private void setSequentialNotes(Track t)
	{
		for (int i = 0; i < t.size(); i++)
		{
			MidiEvent me = t.get(i);
			MidiMessage m = me.getMessage();
			if (m instanceof ShortMessage && ((ShortMessage) m).getCommand() == ShortMessage.NOTE_ON)
			{
				try
				{
					noteSequence.add(new Note(((ShortMessage) m).getData1()));
				}
				catch (UnknownError e)
				{
					System.out.println(e.toString());
				}
			}
		}
	}

	public MidiSequence(MidiSequence song)
	{
		fileName = song.fileName;
		setSequence(song);
	}

	private void setSequence(MidiSequence song)
	{
		List<Note> sequence = song.getNoteSequence();
		int max = sequence.size();
		for(int i = 0; i < max; i++) {
			noteSequence.add(new Note(sequence.get(i)));
		}
	}
	
	public void setNoteSequence(List<Note> snippetNotes)
	{
		noteSequence = snippetNotes;
	}

	List<Note> getNoteSequence() {return noteSequence;}

	String getName() {return fileName;}
	public Note[] toNoteArray()
	{
		Note[] notes = new Note[noteSequence.size()];
		int i = 0;
		for (Note n : noteSequence)
		{
			notes[i] = n;
			i++;
		}
		return notes;
	}
	public String[] toArray()
	{
		String[] notes = new String[noteSequence.size()];
		int i = 0;
		for (Note n : noteSequence)
		{
			notes[i] = n.toString();
			i++;
		}
		return notes;
	}

	public void printSongInfo()
	{
		System.out.println(getSongInfo());
	}
	
	public String getSongInfo() {
		return 
			new String("Song Information: " 
				+ "\n\tTitle: " + fileName 
				+ "\n\tNumber of notes in song: " + noteSequence.size()
				+ "\n" + notesToString(10)
				+ "\n"
			);
	}


	private String notesToString() {return notesToString(noteSequence.size());}
	private String notesToString(int max)
	{
		StringBuilder str = new StringBuilder();

		for(int i = 0; i < max; i++) {
			Note n = noteSequence.get(i);
			str.append(n.toString() + " ");
		}
		
		return "\t" + str.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof MidiSequence) {
			return equals((MidiSequence) o);
		}
		return false;
	}
	
	public boolean equals(MidiSequence o) {
		return fileName.equals(o.getName()) && compareNotes(o.getNoteSequence());
	}
	
	public boolean compareNotes(List<Note> otherNotes) {
		if(noteSequence.size() != otherNotes.size())
			return false;
		
		for(int i = 0; i < noteSequence.size(); i ++) {
			if(noteSequence.get(i).equals(otherNotes.get(i)) == false)
				return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {return noteSequence.hashCode();}
}
