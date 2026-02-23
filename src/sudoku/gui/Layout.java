package sudoku.gui;

import jgui.JGui;
import jgui.elements.JGuiElement;
import processing.core.PGraphics;
import sudoku.gameplay.Sudoku;

public class Layout extends JGuiElement {

	JGuiElement sudoku;
	JGuiElement solver_tools;
	JGuiElement builder_tools;

	public Layout(JGui gui) {
		super();
		sudoku = null;
		solver_tools = null;
		builder_tools = null;
		gui.add(this);
		setRelativeToJGuiWindow(gui);
		setPos(0, 0);
		setSize(100, 100);
	}

	public void setSudoku(Sudoku sudoku) {
		this.sudoku = sudoku;
		sudoku.setRelativeToJGuiElement(this);
		sudoku.setPos(0, 0).setSize(100, 100);
	}

	@Override
	public void update(int pos_x, int pos_y, int available_width, int available_height) {
		if (!visible) {
			return;
		}
		bounding_box[0] = pos_x;
		bounding_box[1] = pos_y;
		bounding_box[2] = available_width;
		bounding_box[3] = available_height;
//		if (available_height > available_width) {
//			// use a vertical design
//
//		} else {
//
//		}
		int m = 10 * Math.min(available_width, available_height) / 11;
		if (sudoku != null) {
			sudoku.update(pos_x + (available_width - m) / 2, pos_y + (available_height - m) / 2, m, m);
		}
	}

	@Override
	public void draw(PGraphics g, int[] mouseStats) {
		if (sudoku != null) {
			sudoku.draw(g, mouseStats);
		}
		if (solver_tools != null) {
			solver_tools.draw(g, mouseStats);
		}
		if (builder_tools != null) {
			builder_tools.draw(g, mouseStats);
		}
	}

	@Override
	public void validate() {
		if (sudoku != null) {
			sudoku.validate();
		}
		if (solver_tools != null) {
			solver_tools.validate();
		}
		if (builder_tools != null) {
			builder_tools.validate();
		}
		visible = true;
	}

	@Override
	public void key_press(char key, int code, int modifier) {
		super.key_press(key, code, modifier);
	}

	@Override
	public void key_release(char key, int code, int modifier) {
		super.key_release(key, code, modifier);
	}

	@Override
	public JGuiElement mouse_start(int[] mouseStats) {
		return super.mouse_start(mouseStats);
	}

	@Override
	public void mouse_finish(int[] mouseStats) {
		super.mouse_finish(mouseStats);
	}

	@Override
	public boolean mouse_wheel(int[] mouseStats) {
		return super.mouse_wheel(mouseStats);
	}

	@Override
	public boolean mouseOver(int[] mouseStats) {
		return super.mouseOver(mouseStats);
	}
}
