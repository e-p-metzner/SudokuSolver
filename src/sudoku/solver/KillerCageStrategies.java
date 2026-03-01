package sudoku.solver;

import static sudoku.solver.SolveHelper.calcvismask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.KillerCage;
import sudoku.solver.SolveHelper.Formula;

public class KillerCageStrategies implements SolvingStrategy {

	private KillerCage killer;
	private Formula formula;

	public KillerCageStrategies(KillerCage killer, Formula formula) {
		super();
		this.killer = killer;
		this.formula = formula;
	}

	@Override
	public void clearCache() {
	}

	@Override
	public int difficulty() {
		return 4;
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		int[] results = killer.getResults();
		int[][] cages = killer.getCages();
		List<Integer> resultList = new ArrayList<Integer>();
		List<int[]> reductions = new ArrayList<int[]>();
		Cell[][] grid = sudoku.getGrid();
		for (int ci = 0; ci < results.length; ci++) {
			resultList.clear();
			resultList.add(Integer.valueOf(results[ci]));
			boolean[][] visibility = calcvismask(grid, cages[ci], !killer.canRepeat());
			reductions.addAll(SolveHelper.reduceCombination( //
					resultList, formula, grid, cages[ci], visibility));
		}
		// error check:
		for (int[] coord : reductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 0) {
				throw new SolverException("No valid digit available for R" + (coord[1] + 1) + "C" + (coord[0] + 1) + " from killer cage reduction.");
			}
		}
		// log successful reductions
		for (int[] coord : reductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 1) {
				cell.setContent(cell.getAvailables()[0]);
				log.logStep("Only one digit possible for killer-cage-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, cell.getContent());
			} else {
				log.logStep("Reduced content for killer-cage-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, Arrays.toString(cell.getCandidatesArray()));
			}
		}
		return !reductions.isEmpty();
	}

}
