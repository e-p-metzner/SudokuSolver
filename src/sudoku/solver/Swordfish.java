package sudoku.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;
import sudoku.tools.Tools;

public class Swordfish implements SolvingStrategy {

	private static final long serialVersionUID = -8491570709972869731L;

	private boolean cacheFilled;
	private Map<String, Lineset> cache;

	public Swordfish() {
		cacheFilled = false;
		cache = new HashMap<String, Swordfish.Lineset>();
	}

	@Override
	public int difficulty() {
		return 9;
	}

	@Override
	public void clearCache() {
		cacheFilled = false;
		cache.clear();
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		if (!cacheFilled) {
			fillCache(sudoku);
		}
		for (String setID : cache.keySet()) {
			Lineset set = cache.get(setID);
			if (swordfish(set.rows, set.columns, sudoku, log, setID)) {
				return true;
			}
			if (swordfish(set.columns, set.rows, sudoku, log, setID)) {
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
			if (blkey.length() < 3) {
				continue;
			}
			String setID = blkey.substring(2);
			if (!Tools.isnatnum(setID)) {
				continue;
			}
			if (blkey.startsWith("RW") || blkey.startsWith("CL")) {
				if (!cache.containsKey(setID)) {
					cache.put(setID, new Lineset());
				}
				if (blkey.startsWith("RW")) {
					cache.get(setID).rows.add(rule);
				} else {
					cache.get(setID).columns.add(rule);
				}
			}
		}
//		System.err.println("Cache for X-Wings:");
//		for (String blkey : cache.keySet()) {
//			System.err.println("  \"" + blkey + "\" - rows");
//			for (AllDigitsDistinct a : cache.get(blkey).rows) {
//				System.err.println("      - " + a.printInfo());
//			}
//			System.err.println("          - columns");
//			for (AllDigitsDistinct a : cache.get(blkey).columns) {
//				System.err.println("      - " + a.printInfo());
//			}
//		}
		cacheFilled = true;
	}

	private boolean swordfish(List<AllDigitsDistinct> lines, List<AllDigitsDistinct> colines, Sudoku sudoku, SolveLog log, String setID) {
		boolean reduction = false;
		long[] bitmasks = new long[lines.size()];
		for (char candidate : sudoku.allValidInputs()) {

			// create bitmasks for cells occupied by the candidate within each line
			for (int li = 0; li < lines.size(); li++) {
				bitmasks[li] = 0L;
				int[] lc = lines.get(li).getCells();
				for (int u = 0; u < lc.length; u += 2) {
					int cli = u >>> 1;
					Cell cell = sudoku.getGrid()[lc[u + 1]][lc[u]];
					if (!cell.isActive() || cell.isSolved()) {
						continue;
					}
					if (cell.getCandidates().contains(candidate)) {
						bitmasks[li] |= 1L << cli;
					}
				}
			}

			// a pair of lines containing the candidate only in the same pair of co-lines
			// form the x-wing
			List<String> rmcell = new ArrayList<String>();
			Set<String> sfrows = new TreeSet<String>(); // TreeSet for ordered row/column enumeration
			Set<String> sfcols = new TreeSet<String>();
			for (int l1 = 0; l1 < bitmasks.length; l1++) {
				if (Long.bitCount(bitmasks[l1]) == 0) {
					continue;
				}
				int[] lc1 = lines.get(l1).getCells();
				for (int l2 = l1 + 1; l2 < bitmasks.length; l2++) {
					if (Long.bitCount(bitmasks[l2]) == 0) {
						continue;
					}
					int[] lc2 = lines.get(l2).getCells();
					for (int l3 = l2 + 1; l3 < lines.size(); l3++) {
						if (Long.bitCount(bitmasks[l3]) == 0) {
							continue;
						}
						long mask = bitmasks[l1] | bitmasks[l2] | bitmasks[l3];
						if (Long.bitCount(mask) != 3) {
							continue;
						}
						int[] lc3 = lines.get(l3).getCells();
						// than remove every other occurence of the candidate in the colines, which is a
						// successful application of the x-wing strategy
						boolean useful_xwing = false;
						rmcell.clear();
						sfrows.clear();
						sfcols.clear();
						for (int cli = 0; cli < 64; cli++) {
							if (((mask >>> cli) & 1) == 0) {
								continue;
							}
							if (cli >= colines.size()) {
								throw new SolverException("Set (RW" + setID + "/CL" + setID + ") of rows and columns is not compatible.");
							}
							int u = 2 * cli;
							int[] cc = colines.get(cli).getCells();
							for (int v = 0; v < cc.length; v += 2) {
								Cell cell = sudoku.getGrid()[cc[v + 1]][cc[v]];
								if (!cell.isActive() || cell.isSolved()) {
									continue;
								}
								if ((cc[v] == lc1[u] && cc[v + 1] == lc1[u + 1]) || //
										(cc[v] == lc2[u] && cc[v + 1] == lc2[u + 1]) || //
										(cc[v] == lc3[u] && cc[v + 1] == lc3[u + 1])) {
									sfrows.add("" + (cc[v + 1] + 1) + "");
									sfcols.add("" + (cc[v] + 1) + "");
									continue;
								}
								if (cell.getCandidates().contains(candidate)) {
									useful_xwing = true;
									cell.removeAvailable(candidate);
									rmcell.add("R" + (cc[v + 1] + 1) + "C" + (cc[v] + 1));
								}
							}
						}
						if (useful_xwing) {
//							System.err.println("[DEBUG] bitmasks:");
//							System.err.println(" (" + l1 + ") " + Tools.binarystr(bitmasks[l1]));
//							System.err.println(" (" + l2 + ") " + Tools.binarystr(bitmasks[l2]));
//							System.err.println(" (" + l3 + ") " + Tools.binarystr(bitmasks[l3]));
//							System.err.println("[DEBUG] mask: " + Tools.binarystr(mask));
//							for (int i = 0; i < 1000; i++) {
//								for (int j = 0; j < 1000; j++) {
//									double d = Math.random();
//									if (d < 0.001) {
//										j--;
//									}
//								}
//							}
							String rowstr = String.join("/", sfrows);
							String colstr = String.join("/", sfcols);
							String cellstr = String.join(",", rmcell);
							log.logStep("Swordfish of %s in rows %s and columns %s, removed candidate from {%s}", //
									candidate, rowstr, colstr, cellstr);
							reduction = true;
						}
//						if (useful_xwing) {
//							return true;
//						}
					}
				}
			}
		}

		return reduction;
	}

	private class Lineset {
		public List<AllDigitsDistinct> rows;
		public List<AllDigitsDistinct> columns;

		public Lineset() {
			rows = new ArrayList<AllDigitsDistinct>();
			columns = new ArrayList<AllDigitsDistinct>();
		}
	}
}
