package sudoku.rules;

import java.util.Arrays;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.SolverException;

public class KropkiV extends KropkiDot {

	private static final long serialVersionUID = -3801988184890640662L;

	private boolean askedFor0;
	private char minCandidate = '1';
	private char maxCandidate = '4';

	public KropkiV() {
		super();
		askedFor0 = false;
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		if (!askedFor0) {
			boolean hasZero = false;
			for (char c : sudoku.allValidInputs()) {
				if (c == '0') {
					hasZero = true;
				}
			}
			if (hasZero) {
				minCandidate = '0';
				maxCandidate = '5';
			}
			askedFor0 = true;
		}
		for (int[] pair : pairs) {
			Cell other = null;
			if (pair[0] == col && pair[1] == row) {
				other = sudoku.getGrid()[pair[3]][pair[2]];
			}
			if (pair[2] == col && pair[3] == row) {
				other = sudoku.getGrid()[pair[1]][pair[0]];
			}
			if (other == null) {
				continue;
			}
			if (!other.isActive()) {
				throw new SolverException("Both cells on a Kropki-Dot have to be within sudoku region!");
			}
			if (candidate < minCandidate || candidate > maxCandidate) {
				return false;
			}
			if (!other.isSolved()) {
				continue;
			}
			int thisDigit = candidate - '0';
			int otherDigit = other.getContent() - '0';
			if (thisDigit + otherDigit != 5) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String printInfo() {
		String[] pstr = new String[pairs.length];
		for (int p = 0; p < pairs.length; p++) {
			pstr[p] = Arrays.toString(pairs[p]);
		}
		return "KropkiV( negative=" + negativeConstrain + ", pairs=" + Arrays.toString(pstr) + " )";
	}

}
