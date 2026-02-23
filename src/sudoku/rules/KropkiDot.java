package sudoku.rules;

import java.util.ArrayList;
import java.util.List;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;

public abstract class KropkiDot extends Rule {

	private static final long serialVersionUID = 6918052603733045780L;

	protected boolean negativeConstrain;
	protected int[][] pairs;

	public KropkiDot() {
		super();
		negativeConstrain = false;
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		JSONArray arr = json.getJSONArray("pairs");
		pairs = new int[arr.size()][4];
		for (int p = 0; p < pairs.length; p++) {
			JSONArray pair = arr.getJSONArray(p);
			pairs[p][0] = pair.getInt(0) - 1; // Coordinate offset (sudoku idx starts with 1)
			pairs[p][1] = pair.getInt(1) - 1;
			pairs[p][2] = pair.getInt(2) - 1;
			pairs[p][3] = pair.getInt(3) - 1;
		}
		negativeConstrain = json.getBoolean("negative");

		if (json.hasKey("draw")) {
			JSONObject drawjson = json.getJSONObject("draw");
			String layer = drawjson.getString("layer");
			if (!drawings.containsKey(layer)) {
				drawings.put(layer, new ArrayList<SudokuDraw>());
			}
			List<SudokuDraw> list = drawings.get(layer);
			for (int p = 0; p < pairs.length; p++) {
				SudokuDraw drawing = DrawingRegistry.getDrawing("SudokuDraw" + drawjson.getString("type"));
				drawing.setPosition(0.5f * (pairs[p][0] + pairs[p][2] + 1), 0.5f * (pairs[p][1] + pairs[p][3] + 1));
				drawing.setupFromJSON(drawjson);
				list.add(drawing);
			}
		}
	}
}
