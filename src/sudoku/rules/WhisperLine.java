package sudoku.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gui.drawings.DrawingRegistry;
import sudoku.gui.drawings.SudokuDraw;

public abstract class WhisperLine extends Rule {

	private static final long serialVersionUID = 5568278058605633969L;

	protected int mindiff;
	protected int[][] lines;
	protected Map<int[], int[]> neighbormap;

	protected WhisperLine() {
		mindiff = -1;
		lines = new int[0][];
		neighbormap = new HashMap<int[], int[]>();
	}

	public int getMinimumDifference() {
		return mindiff;
	}

	public void setMinimumDifference(int difference) {
		mindiff = difference;
	}

	public Map<int[], int[]> getNeighbors() {
		return neighbormap;
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		if (mindiff < 0) {
			mindiff = json.getInt("diff");
		}
		JSONArray set = json.getJSONArray("lines");
		lines = new int[set.size()][];
		for (int l = 0; l < lines.length; l++) {
			JSONArray cells = set.getJSONArray(l);
			lines[l] = new int[cells.size()];
			for (int u = 0; u < lines[l].length; u++) {
				lines[l][u] = cells.getInt(u) - 1; // Coordinate offset (sudoku idx starts with 1)
			}
			// build neighbor-map
			for (int u = 2; u < lines[l].length; u += 2) {
				int[] cellA = { lines[l][u - 2], lines[l][u - 1] };
				int[] cellB = { lines[l][u], lines[l][u + 1] };
				addPairToNeighbormap(cellA, cellB);
				addPairToNeighbormap(cellB, cellA);
			}
		}
//		for (int[] pos : neighbormap.keySet()) {
//			System.err.println("[DEBUG] " + pos[0] + "/" + pos[1]);
//		}

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

	private void addPairToNeighbormap(int[] cell, int[] othercell) {
		int[] key = null;
		for (int[] k : neighbormap.keySet()) {
			if (k[0] == cell[0] && k[1] == cell[1]) {
				key = k;
				break;
			}
		}
		if (key == null) {
			neighbormap.put(cell, othercell);
			return;
		}
		int[] neighbors = neighbormap.get(key);
		for (int u = 0; u < neighbors.length; u += 2) {
			if (neighbors[u] == othercell[0] && neighbors[u + 1] == othercell[0]) {
				// other cell is already neighbor
				return;
			}
		}
		int[] new_neighbors = new int[neighbors.length + 2];
		for (int u = 0; u < neighbors.length; u++) {
			new_neighbors[u] = neighbors[u];
		}
		int u = neighbors.length;
		new_neighbors[u] = othercell[0];
		new_neighbors[u + 1] = othercell[1];
		neighbormap.put(key, new_neighbors);
	}
}
