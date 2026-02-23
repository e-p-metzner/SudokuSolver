package sudoku.gui.drawings;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.data.JSONObject;
import sudoku.tools.Tools;

public class SudokuDrawArrow implements SudokuDraw {

	private int pilllen;
	private float[] x;
	private float[] y;
	private float thick;
	private int strokeColor;
	private int fillColor;

	public SudokuDrawArrow() {
		x = new float[0];
		y = new float[0];
		pilllen = 1;
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
			fillColor = Tools.str2color(colors.getString("fill", "#555555"));
			strokeColor = Tools.str2color(colors.getString("line", "#555555"));
		} else {
			fillColor = 0xff555555;
			strokeColor = 0xff555555;
		}
	}

	@Override
	public void setExtraParam(String name, Object value) {
		if ("pill".equalsIgnoreCase(name)) {
			pilllen = ((Integer) value).intValue();
		}
	}

	@Override
	public void draw(PGraphics g, float offx, float offy, float cellsize) {
		if (x.length < 1 || y.length < 1 || thick <= 0.0f) {
			return;
		}

		// draw arrow
		g.stroke(strokeColor);
		g.strokeWeight(thick * cellsize);
		int len = x.length;
		for (int j = pilllen - 1, i = pilllen; i < len; j = i++) {
			g.line(offx + x[j] * cellsize, offy + y[j] * cellsize, offx + x[i] * cellsize, offy + y[i] * cellsize);
		}
		float xd = x[len - 2] - x[len - 1];
		float yd = y[len - 2] - y[len - 1];
		float ird = (float) (0.25d * cellsize / Math.sqrt(xd * xd + yd * yd));
		xd *= ird;
		yd *= ird;
		float xx = offx + x[len - 1] * cellsize;
		float yy = offy + y[len - 1] * cellsize;
		g.line(xx, yy, xx + xd + yd, yy + yd - xd);
		g.line(xx, yy, xx + xd - yd, yy + yd + xd);

		// circle / pill
		float xmin = offx + Math.min(x[0], x[pilllen - 1]) * cellsize;
		float xmax = offx + Math.max(x[0], x[pilllen - 1]) * cellsize;
		float ymin = offy + Math.min(y[0], y[pilllen - 1]) * cellsize;
		float ymax = offy + Math.max(y[0], y[pilllen - 1]) * cellsize;
		g.beginShape();
		if (fillColor != 0) {
			g.fill(fillColor);
		} else {
			g.noFill();
		}
		g.stroke(strokeColor);
		g.strokeWeight(thick * cellsize);
		for (int i = 0; i < 40; i++) {
			float a = PI * (i + 0.5f) / 20f;
			float vx = (i < 10 || i > 29 ? xmax : xmin) + 0.35f * cellsize * PApplet.cos(a);
			float vy = (i < 20 ? ymin : ymax) - 0.35f * cellsize * PApplet.sin(a);
			g.vertex(vx, vy);
		}
		g.endShape(CLOSE);
	}
}
