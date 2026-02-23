package sudoku.solver;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;

public class HiddenSingle implements SolvingStrategy {

	private static final long serialVersionUID = -2931762922737749691L;

	public HiddenSingle() {
		AllSolvingStrategies.registerStrategy(this);
	}

	@Override
	public int difficulty() {
		return 1;
	}

	@Override
	public void clearCache() {
		// No cache here
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		boolean success = false;
		Cell[][] grid = sudoku.getGrid();

		for (Rule rule : sudoku.getRules()) {
			if (!(rule instanceof AllDigitsDistinct)) {
				continue;
			}
			AllDigitsDistinct addrule = (AllDigitsDistinct) rule;
			int[] cells = addrule.getCells();

			for (char candidate : sudoku.allValidInputs()) {
				int count = 0;
				int cl = -1, rw = -1;
				for (int u = 0; u < cells.length; u += 2) {
					Cell cell = grid[cells[u + 1]][cells[u]];
					if (!cell.isActive()) {
						continue;
					}
					if (cell.isSolved()) {
						if (cell.getContent() == candidate) {
							count = Integer.MIN_VALUE;
						}
						continue;
					}
					if (cell.getCandidates().contains(candidate)) {
						cl = cells[u];
						rw = cells[u + 1];
						count++;
					}
				}
				if (count == 1) {
					grid[rw][cl].setContent(candidate);
					log.logStep("Hidden Single in %s: R%dC%d → %s", //
							addrule.getSubSetName(), rw + 1, cl + 1, candidate);
					success = true;
				}
			}
		}

		return success;
	}
}
