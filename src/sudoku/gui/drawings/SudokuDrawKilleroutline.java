package sudoku.gui.drawings;

import processing.core.PGraphics;
import processing.data.JSONObject;
import sudoku.tools.Tools;

public class SudokuDrawKilleroutline extends SudokuDrawOutline {

	private int result;
	private float tlx;
	private float tly;
	private int txtColor;
	private float txtsize;

	public SudokuDrawKilleroutline() {
		super();
		result = Integer.MIN_VALUE;
		tlx = Float.NaN;
		tly = Float.NaN;
		txtsize = 0f;
	}

	@Override
	public void setPosition(float... coords) {
		super.setPosition(coords);
		tlx = coords[0];
		tly = coords[1];
		for (int i = 2; i < coords.length; i += 2) {
			int cmp = Float.compare(tly, coords[i + 1]);
			if (cmp > 0) {
				tlx = coords[i];
			}
			if (cmp == 0 && coords[i] < tlx) {
				tlx = coords[i];
			}
		}
	}

	@Override
	public void setupFromJSON(JSONObject json) {
		super.setupFromJSON(json);
		txtsize = json.getFloat("textsize");
		if (json.hasKey("colors")) {
			JSONObject colors = json.getJSONObject("colors");
			txtColor = Tools.str2color(colors.getString("text", "#555555"));
		} else {
			txtColor = 0xff555555;
		}
	}

	@Override
	public void setExtraParam(String name, Object value) {
		if ("clue".equalsIgnoreCase(name)) {
			result = ((Integer) value).intValue();
		}
	}

	@Override
	public void draw(PGraphics g, float offx, float offy, float cellsize) {
		// draw cage
		super.draw(g, offx, offy, cellsize);

		// draw clue
		if (result == Integer.MIN_VALUE || Float.isNaN(tlx) || Float.isNaN(tly) || txtsize <= 0f) {
			return;
		}
		g.textSize(txtsize * cellsize);
		g.textAlign(CENTER, CENTER);
		String txt = "" + result + "";
		float tw = g.textWidth(txt) + 4;
		float th = txtsize * cellsize;
		float x = offx + (tlx - 0.5f) * cellsize + Math.max(3f, 0.1f * cellsize - 0.5f * tw);
		float y = offy + (tly - 0.5f) * cellsize + Math.max(3f, 0.1f * cellsize - 0.5f * th);
		g.fill(0xffffffff);
		g.noStroke();
		g.rect(x, y, x + tw, y + th+4);
		g.fill(txtColor);
		g.text(txt, x + 0.5f * tw, y + 0.375f * th+2);
	}

}
