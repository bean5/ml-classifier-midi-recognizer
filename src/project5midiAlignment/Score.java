package project5midiAlignment;

public class Score {
	int score;

	public Score(int s) {
		setScore(s);
	}

	public void setScore(int s)
	{
		score = s;
	}

	public int getValue()
	{
		return getScore();
	}

	public int getScore()
	{
		return score;
	}

	@Override
	public int hashCode() {return ((Integer) score).hashCode();}
	
	public boolean equals(Score o) {return score == o.getScore();}
}
