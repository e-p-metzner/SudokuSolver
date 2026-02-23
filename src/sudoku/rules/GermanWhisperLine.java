package sudoku.rules;

import java.util.Arrays;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.GermanWhisperStrategies;
import sudoku.solver.SolverException;

public class GermanWhisperLine extends WhisperLine {

	private static final long serialVersionUID = 3896573574965602848L;

	private boolean askedFor0;
	private boolean fiveAllowed;

	public GermanWhisperLine() {
		super();
		mindiff = 5;
		askedFor0 = false;
		fiveAllowed = false;
		strategies.add(new GermanWhisperStrategies(this));
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		if (!askedFor0) {
			fiveAllowed = false;
			for (char c : sudoku.allValidInputs()) {
				if (c == '0') {
					fiveAllowed = true;
				}
				if (('a' <= c && c <= 'f') || ('A' <= c && c <= 'F')) {
					fiveAllowed = true;
				}
			}
			askedFor0 = true;
		}

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
		if (!fiveAllowed && candidate == '5') {
			return false;
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
		return "GermanWhisperLine( diff=5, lines=" + Arrays.toString(lstr) + " )";
	}

}
