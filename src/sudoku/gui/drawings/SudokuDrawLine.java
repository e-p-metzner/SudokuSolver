package sudoku.gui.drawings;

import processing.core.PGraphics;
import processing.data.JSONObject;
import sudoku.tools.Tools;

public class SudokuDrawLine implements SudokuDraw {

	private float[] x;
	private float[] y;
	private float thick;
	private int strokeColor;

	public SudokuDrawLine() {
		x = new float[0];
		y = new float[0];
		thick = 0.0f;
		strokeColor = 0x00999999;
	}

	@Override
	public void setPosition(float... coords) {
		if (coords == null) {
			throw new NullPointerException("Coords for drawing lines are not allowed to by null!");
		}
		if (coords.length < 4) {
			throw new IllegalArgumentException("At least 4 coordinates for 2 points are needed to draw a line!");
		}
		int len = coords.length >>> 1;
		x = new float[len];
		y = new float[len];
		for (int i = 0; i < len; i++) {
			x[i] = coords[2 * i];
			y[i] = coords[2 * i + 1];
		}
	}

	@Override
	public void setupFromJSON(JSONObject json) {
		thick = json.getFloat("thickness", 0.0f);
		if (json.hasKey("colors")) {
			JSONObject colors = json.getJSONObject("colors");
			strokeColor = Tools.str2color(colors.getString("line", "#555555"));
		} else {
			strokeColor = 0xff555555;
		}
	}

	@Override
	public void setExtraParam(String name, Object value) {
		// nothing to do here
	}

	@Override
	public void draw(PGraphics g, float offx, float offy, float cellsize) {
		if (x.length < 1 || y.length < 1 || thick <= 0.0f) {
			return;
		}
		// TODO Auto-generated method stub
		g.stroke(strokeColor);
		g.strokeWeight(thick * cellsize);
		for (int j = 0, i = 1; i < x.length; j = i++) {
			g.line(offx + x[j] * cellsize, offy + y[j] * cellsize, offx + x[i] * cellsize, offy + y[i] * cellsize);
		}
	}

}
