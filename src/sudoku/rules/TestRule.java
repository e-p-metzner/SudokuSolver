package sudoku.rules;

import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;

public class TestRule extends Rule {

	private static final long serialVersionUID = 3534028139177996089L;

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		return true;
	}

	@Override
	public String printInfo() {
		return "TestRule( --- )";
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
	}

}
