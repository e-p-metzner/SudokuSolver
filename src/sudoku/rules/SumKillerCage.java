package sudoku.rules;

import java.util.Arrays;

import sudoku.gameplay.Sudoku;

public class SumKillerCage extends KillerCage {

	private static final long serialVersionUID = 6279418388909456694L;

	public SumKillerCage() {
		super();
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String printInfo() {
		String[] lstr = new String[cages.length];
		for (int l = 0; l < cages.length; l++) {
			lstr[l] = Arrays.toString(cages[l]);
		}
		return "SumKillerCage( sums=" + Arrays.toString(results) + ", cages=" + Arrays.toString(lstr) + " )";
	}

}
