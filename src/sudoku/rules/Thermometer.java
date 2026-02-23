package sudoku.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;
import sudoku.solver.SolverException;

public class Thermometer extends Rule {

	private static final long serialVersionUID = 3884572804891014661L;

	boolean strict;
	protected int[][] lines;

	public Thermometer() {
		super();
		strict = true;
		lines = new int[0][];
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		JSONArray set = json.getJSONArray("lines");
		lines = new int[set.size()][];
		for (int l = 0; l < lines.length; l++) {
			JSONArray cells = set.getJSONArray(l);
			lines[l] = new int[cells.size()];
			for (int u = 0; u < lines[l].length; u++) {
				lines[l][u] = cells.getInt(u) - 1; // Coordinate offset (sudoku idx starts with 1)
			}
		}
		strict = json.getBoolean("strict");

		if (json.hasKey("draw")) {
			JSONObject drawjson = json.getJSONObject("draw");
			String layer = drawjson.getString("layer");
			if (!drawings.containsKey(layer)) {
				drawings.put(layer, new ArrayList<SudokuDraw>());
			}
			List<SudokuDraw> list = drawings.get(layer);
			for (int l = 0; l < lines.length; l++) {
				if (lines[l].length < 4) {
					// line too short to appear...
					continue;
				}
				SudokuDraw drawing = DrawingRegistry.getDrawing("SudokuDraw" + drawjson.getString("type"));
				float[] pos = new float[lines[l].length];
				for (int u = 0; u < lines[l].length; u++) {
					pos[u] = lines[l][u] + 0.5f;
				}
				drawing.setPosition(pos);
				drawing.setupFromJSON(drawjson);
				list.add(drawing);
			}
		}
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		int lineID = -1;
		int thidx = -1;
		for (int li = 0; li < lines.length; li++) {
			int[] line = lines[li];
			for (int ti = 0; ti < line.length; ti += 2) {
				if (line[ti] == col && line[ti + 1] == row) {
					lineID = li;
					thidx = ti;
					break;
				}
				if (lineID >= 0) {
					break;
				}
			}
		}
		if (lineID < 0) {
			// not on any of these thermometers
			return true;
		}
		Cell[][] grid = sudoku.getGrid();
		char[] valids = sudoku.allValidInputs();
		int[] line = lines[lineID];
		int minval = strict ? thidx / 2 : 0;
		for (int u = 0; u < thidx; u += 2) {
			Cell cell = grid[line[u + 1]][line[u]];
			if (!cell.isActive()) {
				throw new SolverException("Thermometer-cell not part of valid sudoku region!");
			}
			if (!cell.isGiven() && !cell.isSolved()) {
				continue;
			}
			char c = cell.getContent();
			// get index of cell conten within array of all valid inputs:
			int idx = -1;
			for (int i = 0; i < valids.length; i++) {
				if (c == valids[i]) {
					idx = i;
				}
			}
			if (idx < 0) {
				throw new SolverException("Content of cell R" + (line[u + 1] + 1) + "C" + (line[u] + 1) + " is not valid!");
			}
			if (strict) {
				minval = idx + (thidx - u) / 2;
			} else {
				minval = idx;
			}
		}
		int maxval = strict ? valids.length - (line.length - thidx) / 2 : valids.length - 1;
		for (int u = line.length - 2; u > thidx; u -= 2) {
			Cell cell = grid[line[u + 1]][line[u]];
			if (!cell.isActive()) {
				throw new SolverException("Thermometer-cell not part of valid sudoku region!");
			}
			if (!cell.isGiven() && !cell.isSolved()) {
				continue;
			}
			char c = cell.getContent();
			// get index of cell conten within array of all valid inputs:
			int idx = -1;
			for (int i = 0; i < valids.length; i++) {
				if (c == valids[i]) {
					idx = i;
				}
			}
			if (idx < 0) {
				throw new SolverException("Content of cell R" + (line[u + 1] + 1) + "C" + (line[u] + 1) + " is not valid!");
			}
			if (strict) {
				maxval = idx - (u - thidx) / 2;
			} else {
				maxval = idx;
			}
		}
		// check if candidate is part of the valid input
		for (int idx = minval; idx <= maxval; idx++) {
			if (candidate == valids[idx]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String printInfo() {
		String[] lstr = new String[lines.length];
		for (int l = 0; l < lines.length; l++) {
			lstr[l] = Arrays.toString(lines[l]);
		}
		return "Thermometer( strict=" + strict + ", lines=" + Arrays.toString(lstr) + " )";
	}

}
