package sudoku.gui.drawings;

import java.util.ArrayList;

class DualMap extends ArrayList<int[]> {

	private static final long serialVersionUID = 8882841638082496706L;

	public static final int TL = 0b0001;
	public static final int TR = 0b0010;
	public static final int BL = 0b0100;
	public static final int BR = 0b1000;

	public void updateCoord(int col, int row, int dir) {
		for (int[] item : this) {
			if (item[0] == col && item[1] == row) {
				item[2] |= dir;
				return;
			}
		}
		int[] newItem = { col, row, dir };
		add(newItem);
	}

}
