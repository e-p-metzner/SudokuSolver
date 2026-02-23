package sudoku.rules;

import processing.data.JSONObject;

public class MainDiagonal extends AllDigitsDistinct {

	private static final long serialVersionUID = 7871190041634157152L;

	private boolean posdiag;
	private int size;
	private int[] start;

	public MainDiagonal() {
		super();
		posdiag = true;
		size = 0;
		start = new int[] { -1, -1 };
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		String diag = json.getString("diagonal");
		boolean validDiag = false;
		if ("positive".equals(diag)) {
			posdiag = true;
			validDiag = true;
		}
		if ("negative".equals(diag)) {
			posdiag = false;
			validDiag = true;
		}
		if (!validDiag) {
			throw new RuntimeException("Invalid diagonal for MainDiagonal-rule");
		}
		size = json.getInt("length", -1);
		cells = new int[2 * size];
		for (int u = 0; u < size; u++) {
			cells[2 * u] = start[0] + u;
			cells[2 * u + 1] = start[1] + (posdiag ? -u : u);
		}
		subsetname = (posdiag ? "positive" : "negative") + " diagonal";
	}

	@Override
	public String printInfo() {
		return "MainDiagonal( dir=" + (posdiag ? "positive" : "negative") + ", length=" + size + ", start=R" + (start[1] + 1) + "C" + (start[0] + 1) + " )";
	}
}
