package sudoku.rules;

import java.util.ArrayList;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;
import sudoku.solver.BoxLineReductions;
import sudoku.solver.HiddenPair;
import sudoku.solver.HiddenSingle;
import sudoku.solver.HiddenTriplet;
import sudoku.solver.NakedPair;
import sudoku.solver.SolvingStrategy;

public class SudokuSquare extends Rule {

	private static final long serialVersionUID = 3480640430912777124L;

	private int size;
	private int[][] rows, columns, regions;

	public SudokuSquare() {
		size = 0;
		subrules = new ArrayList<Rule>();
		strategies = new ArrayList<SolvingStrategy>();
	}

	public void init(int size, int colstart, int rowstart) {
		subrules.clear();
		strategies.clear();
		this.size = size;
		this.rows = new int[size][2 * size];
		this.columns = new int[size][2 * size];
		this.regions = null;
		for (int n = 0; n < size; n++) {
			for (int u = 0; u < size; u++) {
				int uc = 2 * u, ur = uc + 1;
				rows[n][uc] = colstart + u;
				rows[n][ur] = rowstart + n;
				columns[n][uc] = colstart + n;
				columns[n][ur] = rowstart + u;
			}
			AllDigitsDistinct rowrule = new AllDigitsDistinct();
			rowrule.setCells(rows[n], new int[] { 0, 0 }, "row " + (n + 1));
			subrules.add(rowrule);
			AllDigitsDistinct columnrule = new AllDigitsDistinct();
			columnrule.setCells(columns[n], new int[] { 0, 0 }, "column " + (n + 1));
			subrules.add(columnrule);
		}
		strategies.add(new HiddenSingle());
		strategies.add(new NakedPair());
		strategies.add(new HiddenPair());
//		strategies.add(new NakedTriplet());
		strategies.add(new HiddenTriplet());
	}

	private void initRegions(JSONArray regarr) {
		if (regarr.size() != size) {
			throw new RuntimeException("Invalid number of regions! Expected " + size + ", got " + regarr.size() + "!");
		}
		regions = new int[size][2 * size];
		for (int n = 0; n < size; n++) {
			JSONArray cellarr = regarr.getJSONArray(n);
			if (cellarr.size() != size + size) {
				throw new RuntimeException("Expect region of size " + (size + size) + ", but got " + cellarr.size());
			}
			for (int u = 0; u < size + size; u++) {
				regions[n][u] = cellarr.getInt(u);
			}
			AllDigitsDistinct regrule = new AllDigitsDistinct();
			regrule.setCells(regions[n], new int[] { 0, 0 }, "region " + (n + 1));
			subrules.add(regrule);
		}
		strategies.add(new BoxLineReductions());
	}

	public void initRegions(int[][] regions) {
		if (regions.length != size) {
			throw new RuntimeException("Expected " + size + " regions, but got " + regions.length);
		}
		this.regions = new int[size][2 * size];
		for (int n = 0; n < size; n++) {
			int[] cells = regions[n];
			if (cells.length != 2 * size) {
				throw new RuntimeException("Expected a region of size " + (2 * size) + ", but got " + cells.length);
			}
			for (int u = 0; u < size + size; u++) {
				this.regions[n][u] = cells[u];
			}
			AllDigitsDistinct regrule = new AllDigitsDistinct();
			regrule.setCells(this.regions[n], new int[] { 0, 0 }, "region " + (n + 1));
			subrules.add(regrule);
		}
		strategies.add(new BoxLineReductions());
	}

	public int[][] getRows() {
		return rows;
	}

	public int[][] getColumns() {
		return columns;
	}

	public int[][] getRegions() {
		return regions;
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		// TODO deserialize
		JSONObject pos = json.getJSONObject("position");
		this.init(json.getInt("size"), pos.getInt("col"), pos.getInt("row"));
		if (json.hasKey("regions")) {
			JSONArray regarr = json.getJSONArray("regions");
			this.initRegions(regarr);
		}
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String printInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
