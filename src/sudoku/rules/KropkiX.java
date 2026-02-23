package sudoku.rules;

import java.util.Arrays;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.SolverException;

public class KropkiX extends KropkiDot {

	private static final long serialVersionUID = -7431041269564001297L;

	public KropkiX() {
		super();
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
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
			if (candidate < '1' || candidate > '9') {
				return false;
			}
			if (candidate == '5') {
				return false;
			}
			if (!other.isSolved()) {
				continue;
			}
			int thisDigit = candidate - '0';
			int otherDigit = other.getContent() - '0';
			if (thisDigit + otherDigit != 10) {
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
