package sudoku.rules;

import java.util.Arrays;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.SolverException;

public class DutchWhisperLine extends WhisperLine {

	private static final long serialVersionUID = 1650739107338260482L;

	public DutchWhisperLine() {
		super();
		mindiff = 4;
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		int[] current = null;
		for (int[] k : neighbormap.keySet()) {
			if (k[0] == col && k[1] == row) {
				current = k;
			}
		}
		if (current == null) {
			// this rule has no influence on this cell
			return true;
		}
		int[] neighbors = neighbormap.get(current);
		boolean pos = '0' <= candidate && candidate <= '9';
		for (int u = 0; u < neighbors.length; u += 2) {
			Cell cell = sudoku.getGrid()[neighbors[u + 1]][neighbors[u]];
			if (!cell.isActive()) {
				throw new SolverException("Neighboring cell on a MinimumDifference-Line is out of sudoku area!");
			}
			if (!cell.isSolved()) {
				continue;
			}
			pos = pos && Math.abs(candidate - cell.getContent()) >= mindiff;
		}
		return pos;
	}

	@Override
	public String printInfo() {
		String[] lstr = new String[lines.length];
		for (int l = 0; l < lines.length; l++) {
			lstr[l] = Arrays.toString(lines[l]);
		}
		return "DutchWhisperLine( diff=5, lines=" + Arrays.toString(lstr) + " )";
	}

}
