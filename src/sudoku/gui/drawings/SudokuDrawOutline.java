package sudoku.gui.drawings;

import processing.core.PGraphics;
import processing.data.JSONObject;
import sudoku.tools.Tools;

public class SudokuDrawOutline implements SudokuDraw {

	protected float[] x;
	protected float[] y;
	private int[] b;
	private float thick;
	private int strokeColor;
	private int lineType;

	public SudokuDrawOutline() {
		x = new float[0];
		y = new float[0];
		b = new int[0];
		thick = 0.0f;
		strokeColor = 0x00999999;
		lineType = SOLID;
	}

	@Override
	public void setPosition(float... coords) {
		if (coords == null) {
			throw new NullPointerException("Coords for drawing outlines are not allowed to by null!");
		}
		if (coords.length < 2) {
			throw new IllegalArgumentException("At least 2 coordinates for 1 or more cells are needed to draw the outlines!");
		}
		// create dual grid
		DualMap dualmap = new DualMap();
		int len = coords.length >>> 1;
		for (int i = 0; i < len; i++) {
			int c = (int) coords[2 * i];
			int r = (int) coords[2 * i + 1];
			dualmap.updateCoord(c + 1, r + 1, DualMap.TL);
			dualmap.updateCoord(c, r + 1, DualMap.TR);
			dualmap.updateCoord(c + 1, r, DualMap.BL);
			dualmap.updateCoord(c, r, DualMap.BR);
		}
		len = dualmap.size();
		x = new float[len];
		y = new float[len];
		b = new int[len];
		for (int i = 0; i < len; i++) {
			int[] item = dualmap.get(i);
			x[i] = item[0]; // - 0.5f;
			y[i] = item[1]; // - 0.5f;
			b[i] = item[2];
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
		String style = json.getString("style", "solid");
		lineType = SOLID;
		if ("dashed".equalsIgnoreCase(style)) {
			lineType = DASHED;
		}
		if ("dotted".equalsIgnoreCase(style)) {
			lineType = DOTTED;
		}
		if ("dotted-dashed".equalsIgnoreCase(style)) {
			lineType = DOTTED_DASHED;
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
		for (int i = 0; i < x.length; i++) {
			g.stroke(strokeColor);
			g.strokeWeight(thick * cellsize);
			float cx = offx + x[i] * cellsize;
			float cy = offy + y[i] * cellsize;
			float cs = 0.1f * cellsize;
			switch (b[i]) {
				case 0b0001:
					line(g, cx, cy, cs, -1, -5, -1, -1);
					line(g, cx, cy, cs, -1, -1, -5, -1);
					break;
				case 0b0010:
					line(g, cx, cy, cs, 5, -1, 1, -1);
					line(g, cx, cy, cs, 1, -1, 1, -5);
					break;
				case 0b0011:
					line(g, cx, cy, cs, 5, -1, -5, -1);
					break;
				case 0b0100:
					line(g, cx, cy, cs, -5, 1, -1, 1);
					line(g, cx, cy, cs, -1, 1, -1, 5);
					break;
				case 0b0101:
					line(g, cx, cy, cs, -1, -5, -1, 5);
					break;
				case 0b0110:
					line(g, cx, cy, cs, 5, -1, 1, -1);
					line(g, cx, cy, cs, 1, -1, 1, -5);
					line(g, cx, cy, cs, -5, 1, -1, 1);
					line(g, cx, cy, cs, -1, 1, -1, 5);
					break;
				case 0b0111:
					line(g, cx, cy, cs, 5, -1, -1, -1);
					line(g, cx, cy, cs, -1, -1, -1, 5);
					break;
				case 0b1000:
					line(g, cx, cy, cs, 1, 5, 1, 1);
					line(g, cx, cy, cs, 1, 1, 5, 1);
					break;
				case 0b1001:
					line(g, cx, cy, cs, -1, -5, -1, -1);
					line(g, cx, cy, cs, -1, -1, -5, -1);
					line(g, cx, cy, cs, 1, 5, 1, 1);
					line(g, cx, cy, cs, 1, 1, 5, 1);
					break;
				case 0b1010:
					line(g, cx, cy, cs, 1, 5, 1, -5);
					break;
				case 0b1011:
					line(g, cx, cy, cs, 1, 5, 1, -1);
					line(g, cx, cy, cs, 1, -1, -5, -1);
					break;
				case 0b1100:
					line(g, cx, cy, cs, -5, 1, 5, 1);
					break;
				case 0b1101:
					line(g, cx, cy, cs, -1, -5, -1, 1);
					line(g, cx, cy, cs, -1, 1, 5, 1);
					break;
				case 0b1110:
					line(g, cx, cy, cs, 1, -5, 1, 1);
					line(g, cx, cy, cs, 1, 1, 1, -5);
					break;
				default:
					break;
			}
		}
	}

	public void line(PGraphics g, float cx, float cy, float cs, int x1, int y1, int x2, int y2) {
		switch (lineType) {
			default:
			case SOLID:
				g.line(cx + x1 * cs, cy + y1 * cs, cx + x2 * cs, cy + y2 * cs);
				break;
			case DASHED:
				float ax = x2 - x1;
				float ay = y2 - y1;
				float al = Math.max(Math.abs(ax), Math.abs(ay));
				ax /= al;
				ay /= al;
				for (float l = 0.5f; l < al; l += 2f) {
					g.line(cx + (x1 + l * ax) * cs, cy + (y1 + l * ay) * cs, cx + (x1 + l * ax + ax) * cs, cy + (y1 + l * ay + ay) * cs);
				}
				break;
			case DOTTED:
				float ox = x2 - x1;
				float oy = y2 - y1;
				float ol = Math.max(Math.abs(ox), Math.abs(oy));
				ox /= ol;
				oy /= ol;
				for (float l = 0.5f; l < ol; l += 1f) {
					g.point(cx + (x1 + l * ox) * cs, cy + (y1 + l * oy) * cs);
				}
				break;
			case DOTTED_DASHED:
				// TODO: implement dotted-dashed correctly
				g.line(cx + x1 * cs, cy + y1 * cs, cx + x2 * cs, cy + y2 * cs);
				break;
		}
	}

}
