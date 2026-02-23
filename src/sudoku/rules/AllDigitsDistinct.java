package sudoku.rules;

import java.util.Arrays;

import processing.data.JSONObject;
import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.solver.HiddenPair;
import sudoku.solver.HiddenSingle;
import sudoku.solver.HiddenTriplet;
import sudoku.solver.NakedPair;
import sudoku.solver.NakedQuadruple;
import sudoku.solver.NakedTriplet;

public class AllDigitsDistinct extends Rule {

	private static final long serialVersionUID = 3534028139177996089L;

	protected int[] cells;
	protected String subsetname;
	/**
	 * key for use in BoxLineReductions, only set by main-code
	 */
	private String boxlineKey;

	public AllDigitsDistinct() {
		cells = new int[0];
		strategies.add(new HiddenSingle());
		strategies.add(new NakedPair());
		strategies.add(new HiddenPair());
		strategies.add(new NakedTriplet());
		strategies.add(new HiddenTriplet());
		strategies.add(new NakedQuadruple());
//		strategies.add(new NakedMultiples());
//		strategies.add(new HiddenMultiples());
		subsetname = "";
		boxlineKey = "##???";
	}

	public void setCells(int[] cells, int[] offset, String name) {
		this.cells = cells.clone();
		for (int u = 0; u < cells.length; u++) {
			this.cells[u] -= offset[u & 1];
		}
		subsetname = name;
	}

	public int[] getCells() {
		return cells;
	}

	public String getSubSetName() {
		return subsetname;
	}

	public String getBoxlineKey() {
		return boxlineKey;
	}

	/**
	 * key for use in BoxLineReductions, only set by main-code
	 */
	public void setBoxlineKey(String key) {
		boxlineKey = key;
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		boolean within = false;
		for (int u = 0; u < cells.length && !within; u += 2) {
			within = (cells[u] == col && cells[u + 1] == row);
		}
		if (!within) {
			return true;
		}

		for (int u = 0; u < cells.length; u += 2) {
			Cell cell = sudoku.getGrid()[cells[u + 1]][cells[u]];
			if (!cell.isSolved()) {
				continue;
			}
			if (cell.getContent() == candidate) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String printInfo() {
		return "AllDigitsDistinct( cells=" + Arrays.toString(cells) + ", blkey=" + boxlineKey + " )";
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
	}

}
