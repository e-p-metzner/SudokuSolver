package sudoku.solver;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;

public class NakedSingle implements SolvingStrategy {

	private static final long serialVersionUID = -2931762922737749691L;

	public NakedSingle() {
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
		boolean found = false;
		Cell[][] cells = sudoku.getGrid();
		for (int rw = 0; rw < cells.length; rw++) {
			for (int cl = 0; cl < cells[rw].length; cl++) {
				if (cells[rw][cl].isSolved() || !cells[rw][cl].isActive()) {
					continue;
				}
				if (cells[rw][cl].getCandidates().size() == 1) {
					char candidate = cells[rw][cl].getCandidates().get(0).charValue();
					if (!isvalid(candidate, sudoku.allValidInputs())) {
						continue;
					}
					cells[rw][cl].setContent(candidate);
					log.logStep("Naked single R" + (rw + 1) + "C" + (cl + 1) + " → " + candidate);
					found = true;
				}
			}
		}
		return found;
	}

	boolean isvalid(char candidate, char[] valids) {
		for (char c : valids) {
			if (c == candidate) {
				return true;
			}
		}
		return false;
	}
}
