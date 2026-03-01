package sudoku.solver;

import static sudoku.solver.SolveHelper.SimpleSum;
import static sudoku.solver.SolveHelper.calcvismask;
import static sudoku.solver.SolveHelper.listPossibleCombiResults;
import static sudoku.solver.SolveHelper.reduceCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.Arrow;
import sudoku.solver.SolveHelper.Formula;

public class ArrowStrategies implements SolvingStrategy {

	private static final long serialVersionUID = 5127298649995498998L;
	private static Formula ArrowPill = combination -> {
		int sum = 0;
		for (char c : combination) {
			sum = sum * 10 + (c - '0');
		}
		return sum;
	};

	private Arrow arrow;
	int[][] pills, arrows;
	private boolean[][][] pillvismask, arrowvismask;

	public ArrowStrategies(Arrow arrow) {
		this.arrow = arrow;
	}

	@Override
	public int difficulty() {
		return 4;
	}

	@Override
	public void clearCache() {
		pills = null;
		pillvismask = null;
		arrows = null;
		arrowvismask = null;
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		Cell[][] grid = sudoku.getGrid();
		int numa = arrow.getPillLengths().length;
		if (arrowvismask == null) {
			int[] plen = arrow.getPillLengths();
			int[][] lns = arrow.getLines();
			pills = new int[numa][];
			arrows = new int[numa][];
			pillvismask = new boolean[numa][][];
			arrowvismask = new boolean[numa][][];
			for (int a = 0; a < numa; a++) {
				int u = 2 * plen[a], v = lns[a].length;
				pills[a] = new int[u];
				for (int i = 0; i < u; i++) {
					pills[a][i] = lns[a][i];
				}
				arrows[a] = new int[v - u];
				for (int i = 0; i < v - u; i++) {
					arrows[a][i] = lns[a][i + u];
				}
				pillvismask[a] = calcvismask(grid, pills[a], false);
				arrowvismask[a] = calcvismask(grid, arrows[a], arrow.allDistinct());
			}
		}
//		boolean reduction = false;
		List<int[]> pillreductions = new ArrayList<int[]>();
		List<int[]> arrowreductions = new ArrayList<int[]>();
		for (int li = 0; li < numa; li++) {
			// check pill:
			List<Integer> arrowSums = listPossibleCombiResults( //
					SimpleSum, grid, arrows[li], arrowvismask[li]);
			pillreductions.addAll(reduceCombination( //
					arrowSums, ArrowPill, grid, pills[li], pillvismask[li]));
			// check arrow:
			List<Integer> pillSums = listPossibleCombiResults( //
					ArrowPill, grid, pills[li], pillvismask[li]);
			arrowreductions.addAll(reduceCombination( //
					pillSums, SimpleSum, grid, arrows[li], arrowvismask[li]));
//
//			switch (plen[li]) {
//				case 1:
//					if (reduceCirclePill(grid, log, lns[li], arrowvismask[li])) {
//						reduction = true;
//					}
//					break;
//				case 2:
//					if (reducedTwoCellPill(grid, log, lns[li], arrowvismask[li])) {
//						reduction = true;
//					}
//					break;
//				case 3:
//					if (reducedThreeCellPill(grid, log, lns[li], arrowvismask[li])) {
//						reduction = true;
//					}
//				default:
//					break;
//			}
//			// check arrows
		}
		// error check:
		for (int[] coord : pillreductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 0) {
				throw new SolverException("No valid digit available for R" + (coord[1] + 1) + "C" + (coord[0] + 1) + " from Arrow reduction.");
			}
		}
		for (int[] coord : arrowreductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 0) {
				throw new SolverException("No valid digit available for R" + (coord[1] + 1) + "C" + (coord[0] + 1) + " from Arrow reduction.");
			}
		}
		// log successful reductions
		for (int[] coord : pillreductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 1) {
				cell.setContent(cell.getAvailables()[0]);
				log.logStep("Only one digit possible for arrow-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, cell.getContent());
			} else {
				log.logStep("Reduced content for arrow-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, Arrays.toString(cell.getCandidatesArray()));
			}
		}
		for (int[] coord : arrowreductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 1) {
				cell.setContent(cell.getAvailables()[0]);
				log.logStep("Only one digit possible for arrow-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, cell.getContent());
			} else {
				log.logStep("Reduced content for arrow-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, Arrays.toString(cell.getCandidatesArray()));
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		return !(pillreductions.isEmpty() && arrowreductions.isEmpty());
	}
}
