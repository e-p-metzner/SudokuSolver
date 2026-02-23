package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.tools.Tools;

public class YWing implements SolvingStrategy {

	private static final long serialVersionUID = -1009779408117331383L;

	public YWing() {
	}

	@Override
	public int difficulty() {
		return 4;
	}

	@Override
	public void clearCache() {
		// there is no cache
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		boolean success = false;

		Cell[][] grid = sudoku.getGrid();
		for (int pr = 0; pr < grid.length; pr++) { // row of pivot
			for (int pc = 0; pc < grid[pr].length; pc++) { // column of pivot
				Cell pivot = grid[pr][pc];
				if (!pivot.isActive() || pivot.isSolved()) {
					continue;
				}
				// need exactly 2 candidates in unsolved cells of the y-wing
				if (pivot.getCandidates().size() != 2) {
					continue;
				}
				char[] cP = { pivot.getCandidates().get(0).charValue(), pivot.getCandidates().get(1).charValue() };

				// found 2 other seen cells with exactly 2 candidates, which form a 2-2-2 triple
				// with only one common candidate in each pair
				List<AllDigitsDistinct> vis_sets = pivot.getVisibleSets();
				for (int v1 = 0; v1 < vis_sets.size(); v1++) {
					int[] vc1 = vis_sets.get(v1).getCells();
					for (int u1 = 0; u1 < vc1.length; u1 += 2) {
						if (vc1[u1] == pc && vc1[u1 + 1] == pr) { // hop over pivot
							continue;
						}
						Cell yA = grid[vc1[u1 + 1]][vc1[u1]];
						if (!yA.isActive()) {
							throw new SolverException("Pivot R" + (pr + 1) + "C" + (pc + 1) + " sees a cell outside valid sudoku solver area!");
						}
						if (yA.isSolved() || yA.getCandidates().size() != 2) {
							continue;
						}
						char[] cA = { yA.getCandidates().get(0).charValue(), yA.getCandidates().get(1).charValue() };

						// check, that pivot and yA do not contain exactly the same candidates
						int count_same = (cP[0] == cA[0] || cP[0] == cA[1] ? 1 : 0) + (cP[1] == cA[0] || cP[1] == cA[1] ? 1 : 0);
						if (count_same != 1) {
							continue;
						}

						for (int v2 = v1 + 1; v2 < vis_sets.size(); v2++) {
							int[] vc2 = vis_sets.get(v2).getCells();
							for (int u2 = 0; u2 < vc2.length; u2 += 2) {
								if ((vc2[u2] == pc && vc2[u2 + 1] == pr) || (vc2[u2] == vc1[u1] && vc2[u2 + 1] == vc1[u1 + 1])) {
									continue; // hop over pivot and yA
								}
								Cell yB = grid[vc2[u2 + 1]][vc2[u2]];
								if (!yB.isActive()) {
									throw new SolverException("Pivot R" + (pr + 1) + "C" + (pc + 1) + " sees a cell outside valid sudoku solver area!");
								}
								if (yB.isSolved() || yB.getCandidates().size() != 2) {
									continue;
								}
								char[] cB = { yB.getCandidates().get(0).charValue(), yB.getCandidates().get(1).charValue() };

								// check, that pivot and yA do not contain exactly the same candidates
								count_same = (cP[0] == cB[0] || cP[0] == cB[1] ? 1 : 0) + (cP[1] == cB[0] || cP[1] == cB[1] ? 1 : 0);
								if (count_same != 1) {
									continue;
								}

								List<Character> set = new ArrayList<Character>();
								set.addAll(pivot.getCandidates());
								Tools.addUnique(yA.getCandidates(), set);
								Tools.addUnique(yB.getCandidates(), set);
								if (set.size() > 3) {
									continue;
								}

								// check, if this is a correct 2-2-2 triple
								char candidate = ' ';
								count_same = 0;
								if (cA[0] == cB[0] || cA[0] == cB[1]) {
									count_same++;
									candidate = cA[0];
								}
								if (cA[1] == cB[0] || cA[1] == cB[1]) {
									count_same++;
									candidate = cA[1];
								}
								if (count_same != 1) {
									continue;
								}

								// a valid y-wing found
								// check if it is useful and the common candidate of the wing cells can be
								// removed from cell visible by both wing cells
//								System.err.println("[DEBUG] pivot=R" + (pr + 1) + "C" + (pc + 1) + " -> " + Arrays.toString(cP));
//								System.err.println("[DEBUG]    yA=R" + (vc1[u1 + 1] + 1) + "C" + (vc1[u1] + 1) + " -> " + Arrays.toString(cA));
//								System.err.println("[DEBUG]    yB=R" + (vc2[u2 + 1] + 1) + "C" + (vc2[u2] + 1) + " -> " + Arrays.toString(cB));
								if (usefulYWing(pivot, yA, yB, candidate, grid, sudoku, log)) {
									success = true;
								}
							}
						}
					}
				}
			}
		}
		return success;
	}

	private boolean usefulYWing(Cell pivot, Cell yA, Cell yB, char candidate, Cell[][] grid, Sudoku sudoku, SolveLog log) {
		// TODO remove candidate from cell visible to both of yA and yB, which is
		// neither yA, yB or pivot
		List<Cell> visited = new ArrayList<Cell>();
		visited.add(pivot);
		visited.add(yA);
		visited.add(yB);
		String removals = "";
		for (AllDigitsDistinct vis_set : yA.getVisibleSets()) {
			int[] vc = vis_set.getCells();
			for (int u = 0; u < vc.length; u += 2) {
				Cell cell = grid[vc[u + 1]][vc[u]];
				if (visited.contains(cell)) {
					continue;
				}
				visited.add(cell);
				if (!cell.isActive() || cell.isSolved()) {
					continue;
				}
				if (!yB.sees_cell(vc[u], vc[u + 1])) {
					continue;
				}
				if (!cell.getCandidates().contains(candidate)) {
					continue;
				}

				cell.removeAvailable(candidate);
				removals += ",R" + (vc[u + 1] + 1) + "C" + (vc[u] + 1);
			}
		}

		if (removals.length() == 0) {
			return false;
		}

		String pstr = Tools.cellPos(pivot, sudoku);
		String astr = Tools.cellPos(yA, sudoku);
		String bstr = Tools.cellPos(yB, sudoku);
		log.logStep("Y-Wing in %s (piv), %s and %s removed %s from {%s}", //
				pstr, astr, bstr, candidate, removals.substring(1));

		return true;
	}
}
