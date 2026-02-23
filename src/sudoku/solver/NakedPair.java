package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;
import sudoku.tools.Tools;

public class NakedPair implements SolvingStrategy {

	private static final long serialVersionUID = 5799603246439177529L;

	public NakedPair() {
		AllSolvingStrategies.registerStrategy(this);
	}

	@Override
	public int difficulty() {
		return 2;
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

			List<Character> set = new ArrayList<Character>();
			for (int u1 = 0; u1 < cells.length; u1 += 2) {
				Cell c1 = grid[cells[u1 + 1]][cells[u1]];
				if (c1.isSolved() || c1.getCandidates().size() > 2) {
					continue;
				}

				for (int u2 = u1 + 2; u2 < cells.length; u2 += 2) {
					set.clear();
					set.addAll(c1.getCandidates());

					Cell c2 = grid[cells[u2 + 1]][cells[u2]];
					if (c2.isSolved() || c2.getCandidates().size() > 2) {
						continue;
					}
					Tools.addUnique(c2.getCandidates(), set);

					if (set.size() != 2) {
						continue;
					}

					boolean reduced = false;
					for (int v = 0; v < cells.length; v += 2) {
						if (v == u1 || v == u2) {
							continue;
						}
						Cell cell = grid[cells[v + 1]][cells[v]];
						for (Character c : set) {
							if (cell.isSolved()) {
								continue;
							}
							if (cell.getCandidates().contains(c)) {
								reduced = true;
							}
							cell.removeAvailable(c);
							cell.removeCandidate(c);
						}
					}

					if (reduced) {
						success = true;
						String pair = String.format("R%dC%d,R%dC%d", cells[u1 + 1], cells[u1], cells[u2 + 1], cells[u2]);
						String setstr = set.get(0).charValue() + "/" + set.get(1).charValue();
						log.logStep("Naked Pair %s in {%s} of %s", //
								setstr, pair, addrule.getSubSetName());
					}
				}
			}
		}

		return success;
	}

}
