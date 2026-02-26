package sudoku.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.KropkiBlack;
import sudoku.rules.KropkiDot;
import sudoku.rules.KropkiV;
import sudoku.rules.KropkiX;
import sudoku.tools.Tools;

public class KropkiDotStrategies implements SolvingStrategy {

	private static final long serialVersionUID = -8037803015243546263L;

	private boolean needInit;
	private boolean negativeConstrain;
	private boolean[][] horizontalPairs;
	private boolean[][] verticalPairs;
	private List<int[]> reducedDots;
	private KropkiDot dots;

	public KropkiDotStrategies(KropkiDot dots) {
		this.dots = dots;
		this.needInit = true;
		this.negativeConstrain = false;
		horizontalPairs = null;
		verticalPairs = null;
		reducedDots = new ArrayList<int[]>();
	}

	@Override
	public int difficulty() {
		return 4;
	}

	@Override
	public void clearCache() {
		if (horizontalPairs != null) {
			for (int i = 0; i < horizontalPairs.length; i++) {
				horizontalPairs[i] = null;
			}
			horizontalPairs = null;
		}
		if (verticalPairs != null) {
			for (int i = 0; i < verticalPairs.length; i++) {
				verticalPairs[i] = null;
			}
			verticalPairs = null;
		}
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		if (dots == null) {
			return false;
		}
		if (needInit) {
			negativeConstrain = dots.hasNegativeConstrain();
			needInit = false;
			if (negativeConstrain) {
				initNegativArrays(sudoku);
			}
		}
		reducedDots.clear();
		String dottype = "Kropki-dot";
		if (dots instanceof KropkiBlack) {
			dottype = "black Kropki-dot";
			reduceBlack(sudoku, log, (KropkiBlack) dots);
		}
		if (dots instanceof KropkiV) {
			dottype = "V-dot";
			reduceV(sudoku, log, (KropkiV) dots);
		}
		if (dots instanceof KropkiX) {
			dottype = "X-dot";
			reduceX(sudoku, log, (KropkiX) dots);
		}
		Cell[][] grid = sudoku.getGrid();
		for (int[] dot : reducedDots) {
			log.logStep("Reduction from %s: R%dC%d -> %s", //
					dottype, dot[1] + 1, dot[0] + 1, Arrays.toString(grid[dot[1]][dot[0]].getAvailables()));
		}
		return reducedDots.size() > 0;
	}

	private void initNegativArrays(Sudoku sudoku) {
		Cell[][] grid = sudoku.getGrid();
		int gw = grid[0].length, gh = grid.length;
		horizontalPairs = new boolean[gh][gw - 1];
		verticalPairs = new boolean[gh - 1][gw];
		for (int j = 0; j < gh; j++) {
			for (int i = 0; i < gw; i++) {
				Cell cellA = grid[j][i];
				if (i + 1 < gw) {
					Cell cellB = grid[j][i + 1];
					horizontalPairs[j][i] = cellA.isActive() && cellB.isActive();
				}
				if (j + 1 < gh) {
					Cell cellB = grid[j + 1][i];
					verticalPairs[j][i] = cellA.isActive() && cellB.isActive();
				}
			}
		}
		for (int[] pair : dots.getPairs()) {
			int i = Math.min(pair[0], pair[2]);
			int j = Math.min(pair[1], pair[3]);
			int h = Math.abs(pair[2] - pair[0]);
			int v = Math.abs(pair[3] - pair[1]);
			boolean valid = false;
			if (h == 1 && v == 0) {
				horizontalPairs[j][i] = false;
				valid = true;
			}
			if (h == 0 && v == 1) {
				verticalPairs[j][i] = false;
				valid = true;
			}
			if (!valid) {
				throw new SolverException("Invalid Kropki-dot pair R" + (pair[1] + 1) + "C" + (pair[0] + 1) + "/R" + (pair[3] + 1) + "C" + (pair[2] + 1));
			}
		}
	}

	private void addReducedDot(int col, int row) {
		for (int[] dot : reducedDots) {
			if (dot[0] == col && dot[1] == row) {
				return;
			}
		}
		reducedDots.add(new int[] { col, row });
	}

	private boolean reduceBlackPair(Cell main, Cell black_partner, boolean negative) {
		if (!main.isActive()) {
			throw new SolverException("Invalid Cell coordinates!");
		}
		if (main.isSolved()) {
			return false;
		}
		boolean reduction = false;
		Character[] cc = main.getCandidates().toArray(new Character[0]);
		char[] bp = black_partner.getCandidatesArray();
		if (black_partner.isSolved()) {
			bp = new char[] { black_partner.getContent() };
		}
		if (negative) {
			if (bp.length > 1) {
				return reduction;
			}
			int j = bp[0] - '0';
			for (char c : cc) {
				int i = c - '0';
				if (i + i == j || i == j + j) {
					main.removeAvailable(c);
					reduction = true;
				}
			}
			return reduction;
		} else {
			for (char c : cc) {
				int i = c - '0';
				char c2 = (char) ('0' + i + i);
				char ch = (char) ('0' + i / 2);
				if (!Tools.contains(c2, bp) && !Tools.contains(ch, bp)) {
					main.removeAvailable(c);
					reduction = true;
				}
			}
		}
		return reduction;
	}

	private void reduceBlack(Sudoku sudoku, SolveLog log, KropkiBlack dots) {
		Cell[][] grid = sudoku.getGrid();
		// simple dot logic
		for (int[] pair : dots.getPairs()) {
			Cell cellA = grid[pair[1]][pair[0]];
			if (!cellA.isActive()) {
				throw new SolverException("Invalid Cell coordinates!");
			}
			Cell cellB = grid[pair[3]][pair[2]];
			if (!cellB.isActive()) {
				throw new SolverException("Invalid Cell coordinates!");
			}
			if (reduceBlackPair(cellA, cellB, false)) {
				addReducedDot(pair[0], pair[1]);
			}
			if (reduceBlackPair(cellB, cellA, false)) {
				addReducedDot(pair[2], pair[3]);
			}
		}
		// dot-chains logic
		for (int[] pairA : dots.getPairs()) {
			Cell c1 = grid[pairA[1]][pairA[0]];
			Cell c2 = grid[pairA[3]][pairA[2]];
			for (int[] pairB : dots.getPairs()) {
				boolean redc1 = false;
				if (pairA[0] == pairB[0] && pairA[1] == pairB[1]) {
					if (pairA[2] == pairB[2] && pairA[3] == pairB[3]) {
						continue;
					}
					if (!c1.isSolved() && c2.sees_cell(pairB[2], pairB[3])) {
						redc1 = true;
					}
				}
				if (pairA[0] == pairB[2] && pairA[1] == pairB[3]) {
					if (!c1.isSolved() && c2.sees_cell(pairB[0], pairB[1])) {
						redc1 = true;
					}
				}
				if (redc1) {
					redc1 = false;
					Character[] cc = c1.getCandidates().toArray(new Character[0]);
					for (Character c : cc) {
						if (c.charValue() == '2' || c.charValue() == '4') {
							continue;
						}
						c1.removeAvailable(c.charValue());
						redc1 = true;
					}
					if (redc1) {
						addReducedDot(pairA[0], pairA[1]);
					}
				}
				boolean redc2 = false;
				if (pairA[2] == pairB[0] && pairA[3] == pairB[1]) {
					if (!c2.isSolved() && c1.sees_cell(pairB[2], pairB[3])) {
						redc2 = true;
					}
				}
				if (pairA[2] == pairB[2] && pairA[3] == pairB[3]) {
					if (!c2.isSolved() && c1.sees_cell(pairB[0], pairB[1])) {
						redc2 = true;
					}
				}
				if (redc2) {
					redc2 = false;
					Character[] cc = c2.getCandidates().toArray(new Character[0]);
					for (Character c : cc) {
						if (c.charValue() == '2' || c.charValue() == '4') {
							continue;
						}
						c2.removeAvailable(c.charValue());
						redc2 = true;
					}
					if (redc2) {
						addReducedDot(pairA[2], pairA[3]);
					}
				}
			}
		}
		if (!negativeConstrain) {
			return;
		}
		// negative constrain
		int gw = grid[0].length, gh = grid.length;
		for (int j = 0; j < gh; j++) {
			for (int i = 0; i < gw; i++) {
				Cell cellA = grid[j][i];
				if (i + 1 < gw && horizontalPairs[j][i]) {
					Cell cellB = grid[j][i + 1];
					if (reduceBlackPair(cellA, cellB, true)) {
						addReducedDot(i, j);
					}
					if (reduceBlackPair(cellB, cellA, true)) {
						addReducedDot(i + 1, j);
					}
				}
				if (j + 1 < gh && verticalPairs[j][i]) {
					Cell cellB = grid[j + 1][i];
					if (reduceBlackPair(cellA, cellB, true)) {
						addReducedDot(i, j);
					}
					if (reduceBlackPair(cellB, cellA, true)) {
						addReducedDot(i, j + 1);
					}
				}
			}
		}
	}

	private void reduceV(Sudoku sudoku, SolveLog log, KropkiV dots) {
	}

	private void reduceX(Sudoku sudoku, SolveLog log, KropkiX dots) {
	}
}
