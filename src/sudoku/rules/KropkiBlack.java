package sudoku.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.SolverException;

public class KropkiBlack extends KropkiDot {

	private static final long serialVersionUID = 6226231987429167113L;

	private char[] valid_digits;

	public KropkiBlack() {
		super();
		valid_digits = null;
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		if (valid_digits == null) {
			// think about sudokus larger than 9x9
			List<Character> allowed = new ArrayList<Character>();
			char[] all = sudoku.allValidInputs();
			for (int j = 0; j < all.length; j++) {
				boolean has_double = false;
				int a = all[j] - '0';
				for (int i = 0; i < all.length; i++) {
					int b = all[i] - '0';
					if (a + a == b || b + b == a) {
						has_double = true;
					}
				}
				if (has_double) {
					allowed.add(all[j]);
				}
			}
			valid_digits = new char[allowed.size()];
			for (int i = 0; i < valid_digits.length; i++) {
				valid_digits[i] = allowed.get(i).charValue();
			}
			System.err.println("[DEBUG] check allowed digits in black kropki dot: " + Arrays.toString(valid_digits));
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
			boolean is_valid = false;
			for (char c : valid_digits) {
				if (candidate == c) {
					is_valid = true;
				}
			}
			if (!is_valid) {
				return false;
			}
			if (!other.isSolved()) {
				continue;
			}
			// in case the other cell is allready solved
			int thisDigit = candidate - '0';
			int otherDigit = other.getContent() - '0';
			if (thisDigit + thisDigit != otherDigit && otherDigit + otherDigit != thisDigit) {
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
		return "KropkiBlack( negative=" + negativeConstrain + ", pairs=" + Arrays.toString(pstr) + " )";
	}

}
