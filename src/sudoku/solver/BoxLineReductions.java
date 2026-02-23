package sudoku.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;
import sudoku.tools.Tools;

public class BoxLineReductions implements SolvingStrategy {

	private static final long serialVersionUID = 1450339546064128064L;

	private boolean cacheFilled;
	private List<AllDigitsDistinct> regions;
	private Map<String, List<AllDigitsDistinct>> rowcols;

	public BoxLineReductions() {
		AllSolvingStrategies.registerStrategy(this);
		cacheFilled = false;
		regions = new ArrayList<AllDigitsDistinct>();
		rowcols = new HashMap<String, List<AllDigitsDistinct>>();
	}

	@Override
	public int difficulty() {
		return 2;
	}

	@Override
	public void clearCache() {
		regions.clear();
		rowcols.clear();
		cacheFilled = false;
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
//		Cell[][] grid = sudoku.getGrid();
//		boolean any_reduction = false;

		if (!cacheFilled) {
			fillCache(sudoku);
		}

		if (rowcols.size() == 0) {
			System.err.println("No rows or columns to interfere with regions in BoxLineReductions");
			return false;
		}

		for (AllDigitsDistinct region : regions) {
			// test against all rows and columns:
			for (String key : rowcols.keySet()) {
				if (reduce(region, rowcols.get(key), sudoku, log)) {
					return true;
				}
			}
		}

		for (String key : rowcols.keySet()) {
			boolean reduced = false;

			// box vs rows/columns
			for (AllDigitsDistinct region : regions) {
				if (reduce(region, rowcols.get(key), sudoku, log)) {
					reduced = true;
				}
			}
			if (reduced) {
				return true;
			}

			// row/column vs boxes
			for (AllDigitsDistinct line : rowcols.get(key)) {
				if (reduce(line, regions, sudoku, log)) {
					reduced = true;
				}
			}
			if (reduced) {
				return true;
			}
		}

		return false;
	}

	private void fillCache(Sudoku sudoku) {
		for (Rule _rule_ : sudoku.getRules()) {
			if (!(_rule_ instanceof AllDigitsDistinct)) {
				continue;
			}
			AllDigitsDistinct rule = (AllDigitsDistinct) _rule_;
			String blkey = rule.getBoxlineKey();
			if ("BLREG".equals(blkey)) {
				regions.add(rule);
				continue;
			}
			if ((blkey.startsWith("RW") || blkey.startsWith("CL")) && Tools.isnatnum(blkey.substring(2))) {
				if (!rowcols.containsKey(blkey)) {
					rowcols.put(blkey, new ArrayList<AllDigitsDistinct>());
				}
				rowcols.get(blkey).add(rule);
				continue;
			}
		}
		cacheFilled = true;
	}

	private boolean reduce(AllDigitsDistinct line, List<AllDigitsDistinct> boxes, Sudoku sudoku, SolveLog log) {
		Cell[][] grid = sudoku.getGrid();
		boolean any_reduction = false;

		int[] lc = line.getCells();
		List<String> rmcells = new ArrayList<String>();
		for (char candidate : sudoku.allValidInputs()) {
			int count = 0;
			int minBox = boxes.size(), maxBox = -1;
			rmcells.clear();
			for (int u = 0; u < lc.length; u += 2) {
				Cell linecell = grid[lc[u + 1]][lc[u]];
				if (!linecell.isActive() || linecell.isSolved()) {
					continue;
				}
				if (!linecell.getCandidates().contains(candidate)) {
					continue;
				}
				count++;
				for (int boxid = 0; boxid < boxes.size(); boxid++) {
					int[] bc = boxes.get(boxid).getCells();
					for (int v = 0; v < bc.length; v += 2) {
						if (lc[u] == bc[v] && lc[u + 1] == bc[v + 1]) {
							minBox = Math.min(minBox, boxid);
							maxBox = Math.max(maxBox, boxid);
							break; // assume, all boxes are pairwise distinct
						}
					}
				}
			}
			// a count of 1 indicates a naked/hidden single, which should be found by other
			// solving strategies!
			if (count == 1) {
				System.err.println("ERROR: only one cell in " + line.getSubSetName() + " contains the digit " + candidate + ", this is a naked/hidden single");
			}
			// if not all cells with this candidate are in one box, no further reductions
			// are possible by this strategy
			if (minBox != maxBox || count < 2) {
				continue;
			}

			// try to remove candidate from other cells of the box, which are not part of
			// the current line
			int[] bc = boxes.get(minBox).getCells();
			for (int v = 0; v < bc.length; v += 2) {
				Cell boxcell = grid[bc[v + 1]][bc[v]];
				if (!boxcell.isActive() || boxcell.isSolved()) {
					continue;
				}
				boolean isinline = false;
				for (int u = 0; u < lc.length && !isinline; u += 2) {
					isinline = (lc[u] == bc[v] && lc[u + 1] == bc[v + 1]);
				}
				if (isinline) {
					continue;
				}
				// it's only a valid reduction, if the cell contains this candidate as an
				// option
				if (boxcell.getCandidates().contains(candidate)) {
					boxcell.removeAvailable(candidate);
					rmcells.add("R" + (bc[v + 1] + 1) + "C" + (bc[v] + 1));
				}
			}
			if (rmcells.size() > 0) {
				String multiple = count == 2 ? "Pair" : count == 3 ? "Triplet" : count == 4 ? "Quadruple" : count == 5 ? "Quintuple" : "multiple";
				log.logStep("Pointing %s in %s removed %s in {%s} of %s", //
						multiple, line.getSubSetName(), candidate, //
						String.join(",", rmcells), boxes.get(minBox).getSubSetName());
				any_reduction = true;
			}
		}

		return any_reduction;
	}
}
