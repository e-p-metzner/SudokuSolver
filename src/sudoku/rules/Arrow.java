package sudoku.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;
import sudoku.solver.ArrowStrategies;

public class Arrow extends Rule {

	private static final long serialVersionUID = 4888217891829905089L;

	private boolean alldistinct;
	private int[] pillsizes;
	private int[][] lines;

	public Arrow() {
		super();
		alldistinct = false;
		pillsizes = new int[0];
		lines = new int[0][];
		strategies.add(new ArrowStrategies(this));
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		alldistinct = json.getBoolean("distinct");
		JSONArray set = json.getJSONArray("arrows");
		pillsizes = new int[set.size()];
		lines = new int[set.size()][];
		for (int l = 0; l < lines.length; l++) {
			JSONObject jobj = set.getJSONObject(l);
			pillsizes[l] = jobj.getInt("pill");
			JSONArray cells = jobj.getJSONArray("cells");
			lines[l] = new int[cells.size()];
			for (int u = 0; u < lines[l].length; u++) {
				lines[l][u] = cells.getInt(u) - 1; // Coordinate offset (sudoku idx starts with 1)
			}
		}

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
				drawing.setExtraParam("pill", pillsizes[l]);
				list.add(drawing);
			}
		}
	}

	@Override
	public boolean possible(char candidate, Sudoku sudoku, int row, int col) {
		return true;
	}

	@Override
	public String printInfo() {
		String[] lstr = new String[lines.length];
		for (int l = 0; l < lines.length; l++) {
			lstr[l] = Arrays.toString(lines[l]);
		}
		return "Arrow( pillsizes=" + Arrays.toString(pillsizes) + ", arrowlines=" + Arrays.toString(lstr) + " )";
	}

	public boolean allDistinct() {
		return alldistinct;
	}

	public int[] getPillLengths() {
		return pillsizes;
	}

	public int[][] getLines() {
		return lines;
	}
}
