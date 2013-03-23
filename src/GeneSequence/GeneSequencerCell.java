package GeneSequence;

import edu.byu.nlp.util.Pair;

/*
 * Simply a pair to represent a position in a gene sequencing alignment
 */
public class GeneSequencerCell {
	Pair<Integer, Integer> position;

	GeneSequencerCell(int i, int j) {
		position = new Pair<Integer, Integer>(i, j);
	}

	@Override
	public int hashCode() {return position.hashCode();}

	public int compare(Object o)
	{
		if (o instanceof GeneSequencerCell)
			return compare((GeneSequencerCell) o);
		else
			return -1;
	}

	public int compare(GeneSequencerCell gsc)
	{
		if (gsc.getRow() == getRow() && gsc.getColumn() == getColumn())
			return 0;
		else
			return -1;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof GeneSequencerCell) {
			return equals((GeneSequencerCell) o);
		}
		return false;
	}
	public boolean equals(GeneSequencerCell o) {
		return getRow() == o.getRow() && getColumn() == o.getColumn();
	}

	public int getRow()
	{
		return position.getFirst();
	}

	public int getColumn()
	{
		return position.getSecond();
	}

	@Override
	public String toString() {return position.toString();}

	public void setFirst(int i)
	{
		position.setFirst(i);
	}
	public void setSecond(int i)
	{
		position.setSecond(i);
	}
}
