package midiHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MidiFileFinder {

	public static List<File> findMidiFromDir(String string)
	{
		return addFilesRecursively(new File(string),
				new ArrayList<File>());
	}

	private static List<File> addFilesRecursively(File file,
			ArrayList<File> all)
	{
		final File[] children = file.listFiles();
		if (children != null)
		{
			for (File child : children)
			{
				if (endsWithMidiExtension(child.toPath()))
					all.add(child);
				else if (child.isDirectory())
					addFilesRecursively(child, all);
			}
		}
		return all;
	}

	private static boolean endsWithMidiExtension(Path path)
	{
		return path.toString().endsWith(".mid")
				|| path.toString().endsWith(".midi");
	}
}
