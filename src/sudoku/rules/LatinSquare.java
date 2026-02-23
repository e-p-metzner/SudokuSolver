package sudoku.rules;

import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;

//TODO
//Idea: use some kind of internal subID, for box-line-interactions, if multiple latin-sqaures are present in one sudoku
//e.g. have 5 interlacing latin squares, when 4 outer squares have one 3x3-box in common with the middle square

public class LatinSquare extends Rule {

	private static final long serialVersionUID = 8124698625811730765L;
	private static int lsCounter = 0;

	private int lskey;
	private int size;
	private int[] start;

	public LatinSquare() {
		size = 0;
		start = new int[] { -1, -1 };
		replaceBySubrules = true;
		lskey = ++lsCounter;
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		size = json.getInt("size");
		// Coordinate offset (sudoku idx starts with 1)
		start = new int[] { json.getInt("startcolumn") - 1, json.getInt("startrow") - 1 };
		int[] internal_offset = { 0, 0 };
		// rows
		for (int rw = 0; rw < size; rw++) {
			int[] cells = new int[2 * size];
			for (int u = 0; u < size; u++) {
				cells[2 * u] = start[0] + u;
				cells[2 * u + 1] = start[1] + rw;
			}
			AllDigitsDistinct rowrule = new AllDigitsDistinct();
			rowrule.setCells(cells, internal_offset, "row " + (rw + 1));
			rowrule.setBoxlineKey(String.format("RW%03d", lskey));
			subrules.add(rowrule);
		}
		// columns
		for (int cl = 0; cl < size; cl++) {
			int[] cells = new int[2 * size];
			for (int u = 0; u < size; u++) {
				cells[2 * u] = start[0] + cl;
				cells[2 * u + 1] = start[1] + u;
			}
			AllDigitsDistinct colrule = new AllDigitsDistinct();
			colrule.setCells(cells, internal_offset, "column " + (cl + 1));
			colrule.setBoxlineKey(String.format("CL%03d", lskey));
			subrules.add(colrule);
		}
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		boolean p = true;
		for (Rule subrule : subrules) {
			p = p && subrule.possible(candidate, sudoku, row, col);
		}
		return p;
	}

	@Override
	public String printInfo() {
		return "LatinSquareRule( size=" + size + ", left/top=" + start[0] + "/" + start[1] + " )";
	}

}
