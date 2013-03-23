package GeneSequence;

import java.util.HashMap;

import project5midiAlignment.Score;

import edu.byu.nlp.util.Pair;

/*
 * Gene sequencing alignment using N*m space and time 
 * A more space efficient way exists, but in case that alignments are important (perhaps for missing recovery)
 * the less memory-efficient way is used
 * 
 * Note: caching is used on GeneSequencerCells to avoid uneccessary calls to "new"
 */
public class GeneSequenceAligner<T> {
	// gene sequence alignment operation costs
	private final int indelCost;
	private final int replaceCost;
	private final int matchCost;

	private int indexOfLastRow;
	private int indexOfLastColumn;

	private final HashMap<GeneSequencerCell, Pair<GeneSequencerCell, Score>> alignmentMatrix = new HashMap<GeneSequencerCell, Pair<GeneSequencerCell, Score>>();
	private final HashMap<Integer, HashMap<Integer, GeneSequencerCell>> cellCache = new HashMap<Integer, HashMap<Integer, GeneSequencerCell>>();

	public GeneSequenceAligner(int i, int r, int m) {
		indelCost = i;
		replaceCost = r;
		matchCost = m;
	}
	
	/*
	 * dynamic program version of Gene Sequencing
	 */
	public void align(String[] gene1, String[] gene2)
	{
		indexOfLastRow = gene1.length;
		indexOfLastColumn = gene2.length;
	
		//System.out.println("About to use a matrix of theoretical maximum size: " + indexOfLastRow + "/" + indexOfLastColumn);
		//System.out.println("Size of hash before action: " + alignmentMatrix.size());
		
		//alignmentMatrix.clear();
		if(alignmentMatrix.containsKey(useCellCache(indexOfLastRow, indexOfLastColumn))==false)
			initializeZeros();
		
		for(int i = 1; i <= indexOfLastRow; i++) {
			for(int j = 1; j <= indexOfLastColumn; j++) {
				//if(i % 100 == 0 && j % 100 == 0) System.out.println("Building Matrix: " + i + " " + j);
				int diagonalCost = (gene1[i-1].equals(gene2[j-1])) ? matchCost : replaceCost;
				
				setCell(i, j, diagonalCost);
			}
		}
		//System.out.println("" + findProfileScore());
		//System.out.println("Size of hash after action: " + alignmentMatrix.size());
	}

	private void initializeZeros()
	{
		alignmentMatrix.put(useCellCache(0, 0), new Pair<GeneSequencerCell, Score>(useCellCache(-1, -1), new Score(0)));
		initializeZeroRow(indexOfLastRow);
		initializeZeroColumn(indexOfLastColumn);
	}
	
	private void initializeZeroRow(int largestX)
	{
		int cost = indelCost;
		for(int i = 1; i <= largestX; i++) {//new GeneSequencerCell(i, 0)
			alignmentMatrix.put(useCellCache(i, 0), new Pair<GeneSequencerCell, Score>(useCellCache(i-1, 0), new Score(cost)));//new GeneSequencerCell(i-1, 0)
			cost += indelCost;
		}
	}

	private void initializeZeroColumn(int largestY)
	{
		int cost = indelCost;
		for(int i = 1; i <= largestY; i++) {//new GeneSequencerCell(0, i)
			alignmentMatrix.put(useCellCache(0, i), new Pair<GeneSequencerCell, Score>(useCellCache(0, i-1), new Score(cost)));//new GeneSequencerCell(0, i-1)
			cost += indelCost;
		}
	}

	private void setCell(int i, int j, int diagonalCost)
	{
		GeneSequencerCell cell_left = cellCache.get(i).get(j-1);// new GeneSequencerCell(i, j - 1);
		GeneSequencerCell cell_diagonal = cellCache.get(i-1).get(j-1);//new GeneSequencerCell(i - 1, j - 1);
		GeneSequencerCell cell_right = cellCache.get(i-1).get(j);//new GeneSequencerCell(i - 1, j);

		Pair<GeneSequencerCell, Score> left = new Pair<GeneSequencerCell, Score>(
			cell_left, getCellValueAfterCost(cell_left, indelCost)
		);
		
		Pair<GeneSequencerCell, Score> diagonal = new Pair<GeneSequencerCell, Score>(
			cell_diagonal, getCellValueAfterCost(cell_diagonal, diagonalCost)
		);
		
		Pair<GeneSequencerCell, Score> down = new Pair<GeneSequencerCell, Score>(
			cell_right, getCellValueAfterCost(cell_right, indelCost)
		);

		alignmentMatrix.put(useCellCache(i, j), getBestOfScores(left, diagonal, down));//new GeneSequencerCell(i, j)
	}

	private static Pair<GeneSequencerCell, Score> getBestOfScores(
			Pair<GeneSequencerCell, Score> left,
			Pair<GeneSequencerCell, Score> diagonal,
			Pair<GeneSequencerCell, Score> down)
	{
		Pair<GeneSequencerCell, Score> least = left;
		if (least.getSecond().getScore() < diagonal.getSecond().getScore())
			least = diagonal;
		if (least.getSecond().getScore() < down.getSecond().getScore())
			least = down;
		
		return least;
	}

	private Score getCellValueAfterCost(GeneSequencerCell gsc, int actionCost)
	{
		return new Score(getCellValue(gsc) + actionCost);
	}
	
	private int getCellValue(GeneSequencerCell gsc) {
		//System.out.println("Cell: "+ gsc.toString());
		assert(alignmentMatrix.containsKey(gsc) == true);
		return alignmentMatrix.get(gsc).getSecond().getScore();
	}
	
	/*
	 * A way to prevent unnecessary calls to "new" when it comes to GeneSequencerCell
	 */
	private GeneSequencerCell useCellCache(int i, int j) {
		GeneSequencerCell cell = null;
		
		HashMap<Integer, GeneSequencerCell> row = cellCache.get(i);
		if(row != null) {
			cell = row.get(j);
			if(cell == null) {
				cell = new GeneSequencerCell(i,j);
				row.put(j, cell);
			}
		}
		else {
			cell = new GeneSequencerCell(i,j);
			row = new HashMap<Integer, GeneSequencerCell>();
			row.put(j, cell);
			cellCache.put(i, row);
		}
		assert(cell != null);
		return cell;
	}

	/*
	 * Since the difference of the length of the snippet and the length of the compared song may differ
	 * This is used to compute the cost without the required indels (although other indels may occur)
	 */
	public int findProfileScore()
	{
		int numberOfIndels = indexOfLastColumn - indexOfLastRow;
		int costOfAllIndels = numberOfIndels * indelCost;
		
		int totalAlignmentScore = alignmentMatrix.get(useCellCache(indexOfLastRow, indexOfLastColumn)).getSecond().getScore();//new GeneSequencerCell(indexOfLastRow, indexOfLastColumn)
		int profileScore = totalAlignmentScore - costOfAllIndels; 
		return profileScore;
	}
	
	/*
	 * Deprecated (buggy)
	 * This was the first way to score the alignment, but tended to give higher scores to the wrong genes
	 */
	public int findProfileScore2()
	{
		assert(alignmentMatrix.containsKey(new GeneSequencerCell(indexOfLastRow, indexOfLastColumn)));
		assert(alignmentMatrix.get(new GeneSequencerCell(0,0)).getSecond().getScore() == 0);
		
		GeneSequencerCell currentCell = new GeneSequencerCell(indexOfLastRow, indexOfLastColumn);
		//System.out.println("Upper Corner Score: " + alignmentMatrix.get(currentCell).getSecond().getScore());

		GeneSequencerCell nextCell = alignmentMatrix.get(currentCell).getFirst();
		while (nextCell.getRow() == indexOfLastRow)
		{
			currentCell = nextCell;
			nextCell = alignmentMatrix.get(currentCell).getFirst();
		}
		int upperScore = alignmentMatrix.get(currentCell).getSecond().getScore();
		//System.out.println("Upper Score: " + upperScore);
		
		
		while (nextCell.getRow() > 0)
		{
			currentCell = nextCell;
			nextCell = alignmentMatrix.get(currentCell).getFirst();
		}
		currentCell = nextCell;
		
		int lowerScore = alignmentMatrix.get(currentCell).getSecond().getScore();
		//System.out.println("Lower Score: " + lowerScore);
		
		int totalScore = upperScore - lowerScore;
		
		//System.out.println(totalScore + " " + (alignmentMatrix.get(new GeneSequencerCell(indexOfLastRow, indexOfLastColumn)).getSecond().getScore() - (indexOfLastColumn - indexOfLastRow)*indelCost));
		//assert(totalScore <= (alignmentMatrix.get(new GeneSequencerCell(indexOfLastRow, indexOfLastColumn)).getSecond().getScore() - (indexOfLastColumn - indexOfLastRow)*indelCost));
		
		//System.out.println("Total Score: " + totalScore + "\n\n");
		return totalScore;
	}
}
