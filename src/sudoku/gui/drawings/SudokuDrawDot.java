package sudoku.gui.drawings;

import processing.core.PGraphics;
import processing.data.JSONObject;
import sudoku.tools.Tools;

public class SudokuDrawDot implements SudokuDraw {

	private float posX;
	private float posY;
	private float relsize;
	private int fillColor;
	private int strokeColor;
	private String txt;
	private int txtColor;

	public SudokuDrawDot() {
		posX = Float.NaN;
		posY = Float.NaN;
		relsize = 0.0f;
		fillColor = 0x00999999;
		strokeColor = 0x00999999;
		txtColor = 0x00999999;
		txt = "";
	}

	@Override
	public void setPosition(float... coords) {
		if (coords == null) {
			throw new NullPointerException("Coords for drawing dots are not allowed to by null!");
		}
		if (coords.length < 2) {
			throw new IllegalArgumentException("At least 2 coordinates are needed for a dot to draw!");
		}
		if (coords.length > 2) {
			System.err.println("A dot only needs 2 coordinates");
		}
		posX = coords[0];
		posY = coords[1];
	}

	@Override
	public void setupFromJSON(JSONObject json) {
		relsize = json.getFloat("size", 0.0f);
		if (json.hasKey("colors")) {
			JSONObject colors = json.getJSONObject("colors");
			fillColor = Tools.str2color(colors.getString("fill", "#FFFFFF"));
			strokeColor = Tools.str2color(colors.getString("border", "#000000"));
			txtColor = Tools.str2color(colors.getString("txt", "#000000"));
		} else {
			fillColor = 0xffffffff;
			strokeColor = 0xff000000;
			txtColor = 0xff000000;
		}
		txt = json.getString("text", "");
	}

	@Override
	public void setExtraParam(String name, Object value) {
		// nothing to do here
	}

	@Override
	public void draw(PGraphics g, float offx, float offy, float cellsize) {
		if (relsize <= 0.0f) {
			return;
		}
		g.fill(fillColor);
		g.stroke(strokeColor);
		g.strokeWeight(3f);
		switch (g.ellipseMode) {
			case CENTER:
				g.circle(offx + posX * cellsize, offy + posY * cellsize, relsize * cellsize);
				break;
			case CORNER:
				g.circle(offx + (posX - 0.5f * relsize) * cellsize, offy + (posY - 0.5f * relsize) * cellsize, relsize * cellsize);
				break;
			case CORNERS:
				g.ellipse(offx + (posX - 0.5f * relsize) * cellsize, offy + (posY - 0.5f * relsize) * cellsize, offx + (posX + 0.5f * relsize) * cellsize,
						offy + (posY + 0.5f * relsize) * cellsize);
				break;
			case RADIUS:
				g.circle(offx + posX * cellsize, offy + posY * cellsize, 0.5f * relsize * cellsize);
				break;
			default:
				break;
		}
		if (txt.length() > 0) {
			g.textAlign(CENTER, CENTER);
			g.textSize(0.9f * relsize * cellsize);
			g.fill(txtColor);
			g.text(txt, offx + posX * cellsize, offy + (posY - 0.16f * relsize) * cellsize);
		}
	}

}
