package sudoku.solver;

import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;
import sudoku.tools.Tools;

public class HiddenTriplet implements SolvingStrategy {

	private static final long serialVersionUID = -5380007696043472040L;

	public HiddenTriplet() {
		AllSolvingStrategies.registerStrategy(this);
	}

	@Override
	public int difficulty() {
		return 3;
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

			char[] validInputs = sudoku.allValidInputs();
			int[] counts = new int[validInputs.length];
			long[] bitmasks = new long[validInputs.length];
			for (int u = 0; u < cells.length; u += 2) {
				Cell cell = grid[cells[u + 1]][cells[u]];
				if (!cell.isActive() || cell.isSolved()) {
					continue;
				}
				List<Character> candidates = cell.getCandidates();
				for (int ci = 0; ci < validInputs.length; ci++) {
					if (candidates.contains(validInputs[ci])) {
						counts[ci]++;
						bitmasks[ci] |= 1L << (u >> 1);
					}
				}
			}

			for (int c1 = 0; c1 < validInputs.length; c1++) {
				if (counts[c1] == 0) {
					continue;
				}
				for (int c2 = c1 + 1; c2 < validInputs.length; c2++) {
					if (counts[c2] == 0) {
						continue;
					}
					for (int c3 = c2 + 1; c3 < validInputs.length; c3++) {
						if (counts[c3] == 0) {
							continue;
						}

						long mask = bitmasks[c1] | bitmasks[c2] | bitmasks[c3];
						if (Long.bitCount(mask) != 3) {
							continue;
						}
						char[] valid = { validInputs[c1], validInputs[c2], validInputs[c3] };
						String validstr = valid[0] + "/" + valid[1] + "/" + valid[2];
						boolean reduced = false;
						String pair = "";
						for (int u = 0; u < 64; u++) {
							if (((mask >>> u) & 1L) == 0L) {
								continue;
							}
							int cl = cells[2 * u];
							int rw = cells[2 * u + 1];
							Cell cell = grid[rw][cl];
							char[] candidates = cell.getAvailables().clone();
							pair += ",R" + (rw + 1) + "C" + (cl + 1);
							for (char c : candidates) {
								if (!Tools.contains(c, valid)) {
									if (cell.getCandidates().contains(c)) {
										reduced = true;
									}
									cell.removeAvailable(c);
									cell.removeCandidate(c);
								}
							}
						}
						if (reduced) {
							success = true;
							log.logStep("Hidden Triplet %s in %s of %s:", //
									validstr, pair.substring(1), addrule.getSubSetName());
						}
					}
				}
			}
		}

		return success;
	}
}
