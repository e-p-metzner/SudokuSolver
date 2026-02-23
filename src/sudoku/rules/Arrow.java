package sudoku.rules;

import java.util.ArrayList;
import java.util.List;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;

public class Arrow extends Rule {

	private static final long serialVersionUID = 4888217891829905089L;

	private int[] pillsizes;
	private int[][] lines;

	public Arrow() {
		super();
		pillsizes = new int[0];
		lines = new int[0][];
	}

	@SuppressWarnings("unused")
	@Override
	public void deserializeFromJson(JSONObject json) {
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
		return null;
	}

}
