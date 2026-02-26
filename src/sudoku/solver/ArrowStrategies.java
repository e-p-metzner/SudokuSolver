package sudoku.solver;

import static sudoku.solver.SolveHelper.SimpleSum;
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
				log.logStep("Only one digit possible arrow-pill-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, cell.getContent());
			} else {
				log.logStep("Reduced content for arrow-pill-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, Arrays.toString(cell.getCandidatesArray()));
			}
		}
		for (int[] coord : arrowreductions) {
			Cell cell = grid[coord[1]][coord[0]];
			if (cell.getAvailables().length == 1) {
				cell.setContent(cell.getAvailables()[0]);
				log.logStep("Only one digit possible arrow-cell: R%dC%d -> %s", coord[1] + 1, coord[0] + 1, cell.getContent());
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

	private boolean[][] calcvismask(Cell[][] grid, int[] cellcoords, boolean distinct) {
		int count = cellcoords.length >> 1;
		boolean[][] vismask = new boolean[count][count];
		for (int j = 0; j < count; j++) {
			vismask[j][j] = false;
			Cell cell = grid[cellcoords[j + j + 1]][cellcoords[j + j]];
			for (int i = j + 1; i < count; i++) {
				vismask[j][i] = cell.sees_cell(cellcoords[i + i], cellcoords[i + i + 1]);
				if (distinct) {
					vismask[j][i] = true;
				}
				vismask[i][j] = vismask[j][i];
			}
		}
		return vismask;
	}

	/*
	 * private boolean reduceCirclePill(Cell[][] grid, SolveLog log, int[] line,
	 * boolean[][] visibility) { Cell pill = grid[line[1]][line[0]]; if
	 * (!pill.isActive()) { throw new SolverException("Invalid Cell coordinates!");
	 * } if (pill.isSolved()) { return false; } boolean reduced = false; int[] arrow
	 * = new int[line.length - 2]; for (int i = 0; i < arrow.length; i++) { arrow[i]
	 * = line[i + 2]; } List<Integer> sums =
	 * SolveHelper.listPossibleCombiResults(SolveHelper.SimpleSum, grid, arrow,
	 * visibility); for (char c : pill.getCandidatesArray()) { int p = c - '0';
	 * boolean isPossible = false; for (int sum : sums) { if (p == sum) { isPossible
	 * = true; } } if (!isPossible) { pill.removeAvailable(c);
	 * pill.removeCandidate(c); reduced = true; } } if (reduced) { if
	 * (pill.getCandidates().size() == 1) {
	 * pill.setContent(pill.getCandidatesArray()[0]);
	 * log.logStep("Only one sum possible for arrow -> R%dC%d = %s", line[1] + 1,
	 * line[0] + 1, pill.getContent()); } else {
	 * log.logStep("Reduced circle-content for arrow -> R%dC%d = %s", line[1] + 1,
	 * line[0] + 1, Arrays.toString(pill.getCandidatesArray())); } } return reduced;
	 * }
	 *
	 * private boolean reducedTwoCellPill(Cell[][] grid, SolveLog log, int[] line,
	 * boolean[][] visibility) { Cell pillTen = grid[line[1]][line[0]]; Cell pillOne
	 * = grid[line[3]][line[2]]; if (!pillTen.isActive() || !pillOne.isActive()) {
	 * throw new SolverException("Invalid Cell coordinates!"); } if
	 * (pillTen.isSolved() && pillOne.isSolved()) { return false; } int[] arrow =
	 * new int[line.length - 4]; for (int i = 0; i < arrow.length; i++) { arrow[i] =
	 * line[i + 4]; } boolean redten = false; boolean redone = false; List<Integer>
	 * sums = SolveHelper.listPossibleCombiResults(SolveHelper.SimpleSum, grid,
	 * arrow, visibility); char[] oneOpts = pillOne.getCandidatesArray(); boolean[]
	 * onePos = new boolean[oneOpts.length]; for (int o = 0; o < oneOpts.length;
	 * o++) { onePos[o] = false; } for (char c : pillTen.getCandidatesArray()) {
	 * boolean tenpos = false; for (int o = 0; o < oneOpts.length; o++) { int p = (c
	 * - '0') * 10 + (oneOpts[o] - '0'); boolean isPossible = false; for (int sum :
	 * sums) { if (p == sum) { isPossible = true; } } if (isPossible) { onePos[o] =
	 * true; tenpos = true; } } if (!tenpos) { pillTen.removeAvailable(c);
	 * pillTen.removeCandidate(c); redten = true; } } for (int o = 0; o <
	 * oneOpts.length; o++) { if (!onePos[o]) { pillOne.removeAvailable(oneOpts[o]);
	 * pillOne.removeCandidate(oneOpts[o]); redone = true; } } if (redten) { if
	 * (pillTen.getCandidates().size() == 1) {
	 * pillTen.setContent(pillTen.getCandidatesArray()[0]);
	 * log.logStep("Only one possibility from arrow for R%dC%d -> %s", line[1] + 1,
	 * line[0] + 1, pillTen.getContent()); } else {
	 * log.logStep("Reduced pill-content for arrow -> R%dC%d = %s", line[1] + 1,
	 * line[0] + 1, Arrays.toString(pillTen.getCandidatesArray())); } } if (redone)
	 * { if (pillOne.getCandidates().size() == 1) {
	 * pillOne.setContent(pillOne.getCandidatesArray()[0]);
	 * log.logStep("Only one possibility from arrow for R%dC%d -> %s", line[3] + 1,
	 * line[2] + 1, pillOne.getContent()); } else {
	 * log.logStep("Reduced pill-content for arrow -> R%dC%d = %s", line[3] + 1,
	 * line[2] + 1, Arrays.toString(pillOne.getCandidatesArray())); } } return
	 * redten || redone; }
	 *
	 * private boolean reducedThreeCellPill(Cell[][] grid, SolveLog log, int[] line,
	 * boolean[][] visibility) { Cell pillHnd = grid[line[1]][line[0]]; Cell pillTen
	 * = grid[line[3]][line[2]]; Cell pillOne = grid[line[5]][line[4]]; if
	 * (!pillHnd.isActive() || !pillTen.isActive() || !pillOne.isActive()) { throw
	 * new SolverException("Invalid Cell coordinates!"); } if (pillHnd.isSolved() &&
	 * pillTen.isSolved() && pillOne.isSolved()) { return false; } int[] arrow = new
	 * int[line.length - 6]; for (int i = 0; i < arrow.length; i++) { arrow[i] =
	 * line[i + 6]; } boolean redhnd = false; boolean redten = false; boolean redone
	 * = false; List<Integer> sums =
	 * SolveHelper.listPossibleCombiResults(SolveHelper.SimpleSum, grid, arrow,
	 * visibility); char[] tenOpts = pillTen.getCandidatesArray(); boolean[] tenPos
	 * = new boolean[tenOpts.length]; for (int t = 0; t < tenOpts.length; t++) {
	 * tenPos[t] = false; } char[] oneOpts = pillOne.getCandidatesArray(); boolean[]
	 * onePos = new boolean[oneOpts.length]; for (int o = 0; o < oneOpts.length;
	 * o++) { onePos[o] = false; } for (char c : pillHnd.getCandidatesArray()) {
	 * boolean hndpos = false; for (int t = 0; t < tenOpts.length; t++) { for (int o
	 * = 0; o < oneOpts.length; o++) { int p = (c - '0') * 10 + (oneOpts[o] - '0');
	 * boolean isPossible = false; for (int sum : sums) { if (p == sum) { isPossible
	 * = true; } } if (isPossible) { onePos[o] = true; hndpos = true; } } } if
	 * (!hndpos) { pillHnd.removeAvailable(c); pillHnd.removeCandidate(c); redhnd =
	 * true; } } for (int t = 0; t < tenOpts.length; t++) { if (!tenPos[t]) {
	 * pillTen.removeAvailable(tenOpts[t]); pillTen.removeCandidate(tenOpts[t]);
	 * redten = true; } } for (int o = 0; o < oneOpts.length; o++) { if (!onePos[o])
	 * { pillOne.removeAvailable(oneOpts[o]); pillOne.removeCandidate(oneOpts[o]);
	 * redone = true; } } // log results if (redhnd) { if
	 * (pillHnd.getCandidates().size() == 1) {
	 * pillHnd.setContent(pillHnd.getCandidatesArray()[0]);
	 * log.logStep("Only one possibility from arrow for R%dC%d -> %s", line[3] + 1,
	 * line[2] + 1, pillHnd.getContent()); } else {
	 * log.logStep("Reduced pill-content for arrow -> R%dC%d = %s", line[3] + 1,
	 * line[2] + 1, Arrays.toString(pillHnd.getCandidatesArray())); } } if (redten)
	 * { if (pillTen.getCandidates().size() == 1) {
	 * pillTen.setContent(pillTen.getCandidatesArray()[0]);
	 * log.logStep("Only one possibility from arrow for R%dC%d -> %s", line[3] + 1,
	 * line[2] + 1, pillTen.getContent()); } else {
	 * log.logStep("Reduced pill-content for arrow -> R%dC%d = %s", line[3] + 1,
	 * line[2] + 1, Arrays.toString(pillTen.getCandidatesArray())); } } if (redone)
	 * { if (pillOne.getCandidates().size() == 1) {
	 * pillOne.setContent(pillOne.getCandidatesArray()[0]);
	 * log.logStep("Only one possibility from arrow for R%dC%d -> %s", line[5] + 1,
	 * line[4] + 1, pillOne.getContent()); } else {
	 * log.logStep("Reduced pill-content for arrow -> R%dC%d = %s", line[5] + 1,
	 * line[4] + 1, Arrays.toString(pillOne.getCandidatesArray())); } } return
	 * redten || redone; } //
	 */
}
