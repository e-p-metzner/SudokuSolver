package sudoku.gui.drawings;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.data.JSONObject;

public interface SudokuDraw extends PConstants {

	public static final int SOLID = 1;
	public static final int DASHED = 2;
	public static final int DOTTED = 4;
	public static final int DOTTED_DASHED = 6;

	public void setPosition(float... coords);

	public void setupFromJSON(JSONObject json);

	public void setExtraParam(String name, Object value);

	public void draw(PGraphics g, float offx, float offy, float cellsize);

}
