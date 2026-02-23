package sudoku.rules;

import java.util.ArrayList;
import java.util.List;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;

public abstract class KillerCage extends Rule {

	private static final long serialVersionUID = -2950473927180541417L;

	protected int[] results;
	protected int[][] cages;

	protected KillerCage() {
		super();
		results = new int[0];
		cages = new int[0][];
	}

	@SuppressWarnings("unused")
	@Override
	public void deserializeFromJson(JSONObject json) {
		JSONArray set = json.getJSONArray("cages");
		results = new int[set.size()];
		cages = new int[set.size()][];
		for (int l = 0; l < cages.length; l++) {
			JSONObject jobj = set.getJSONObject(l);
			results[l] = jobj.getInt("result");
			JSONArray cells = jobj.getJSONArray("cells");
			cages[l] = new int[cells.size()];
			for (int u = 0; u < cages[l].length; u++) {
				cages[l][u] = cells.getInt(u) - 1; // Coordinate offset (sudoku idx starts with 1)
			}
		}

		if (json.hasKey("draw")) {
			JSONObject drawjson = json.getJSONObject("draw");
			String layer = drawjson.getString("layer");
			if (!drawings.containsKey(layer)) {
				drawings.put(layer, new ArrayList<SudokuDraw>());
			}
			List<SudokuDraw> list = drawings.get(layer);
			for (int c = 0; c < cages.length; c++) {
				if (cages[c].length < 4) {
					// line too short to appear...
					continue;
				}
				SudokuDraw drawing = DrawingRegistry.getDrawing("SudokuDraw" + drawjson.getString("type"));
				float[] pos = new float[cages[c].length];
				for (int u = 0; u < cages[c].length; u++) {
					pos[u] = cages[c][u] + 0.5f;
				}
				drawing.setPosition(pos);
				drawing.setupFromJSON(drawjson);
				drawing.setExtraParam("clue", results[c]);
				list.add(drawing);
			}
		}
	}

}
