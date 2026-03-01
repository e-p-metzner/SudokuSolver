package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.gameplay.Cell;

public class SolveHelper {

	public static Formula SimpleSum = combination -> {
		int sum = 0;
		for (char c : combination) {
			sum += c - '0';
		}
		return sum;
	};

	public static boolean[][] calcvismask(Cell[][] grid, int[] cellcoords, boolean distinct) {
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

	public static List<Integer> listPossibleCombiResults(Formula formula, Cell[][] grid, int[] cellcoords, boolean[][] visibility) {
		int numc = cellcoords.length >>> 1;
		char[] combination = new char[numc];
		char[][] options = new char[numc][];
		int[] choice = new int[numc];
		for (int i = 0; i < numc; i++) {
			options[i] = grid[cellcoords[i + i + 1]][cellcoords[i + i]].getCandidatesArray();
			choice[i] = 0;
		}
		numc--;
		List<Integer> possibleResults = new ArrayList<Integer>();
		while (choice[numc] < options[numc].length) {
			for (int i = 0; i <= numc; i++) {
				combination[i] = options[i][choice[i]];
			}
			boolean valid = true;
			for (int i = 0; i <= numc && valid; i++) {
				for (int j = i + 1; j <= numc && valid; j++) {
					if (visibility[i][j] && combination[i] == combination[j]) {
						valid = false;
					}
				}
			}
			if (valid) {
				int result = formula.calc(combination);
				boolean exists = false;
				for (int pr : possibleResults) {
					exists = (pr == result);
					if (exists) {
						break;
					}
				}
				if (!exists) {
					possibleResults.add(Integer.valueOf(result));
				}
			}
			choice[0]++;
			for (int i = 0; i < numc; i++) {
				if (choice[i] == options[i].length) {
					choice[i] = 0;
					choice[i + 1]++;
				}
			}
		}
		return possibleResults;
	}

	public static List<int[]> reduceCombination(List<Integer> possibleFormulaResults, Formula formula, Cell[][] grid, int[] cellcoords,
			boolean[][] visibility) {
		int numc = cellcoords.length >>> 1;
		boolean[][] reductions = new boolean[numc][];
		char[] combination = new char[numc];
		char[][] options = new char[numc][];
		int[] choice = new int[numc];
		for (int i = 0; i < numc; i++) {
			options[i] = grid[cellcoords[i + i + 1]][cellcoords[i + i]].getCandidatesArray();
			choice[i] = 0;
			reductions[i] = new boolean[options[i].length];
			for (int o = 0; o < options[i].length; o++) {
				reductions[i][o] = true;
			}
		}
		numc--;
		while (choice[numc] < options[numc].length) {
			for (int i = 0; i <= numc; i++) {
				combination[i] = options[i][choice[i]];
			}
			boolean valid = true;
			for (int i = 0; i <= numc && valid; i++) {
				for (int j = i + 1; j <= numc && valid; j++) {
					if (visibility[i][j] && combination[i] == combination[j]) {
						valid = false;
					}
				}
			}
			if (valid) {
				int result = formula.calc(combination);
				boolean exists = false;
				for (int pr : possibleFormulaResults) {
					exists = pr == result;
					if (exists) {
						break;
					}
				}
				if (exists) {
					for (int i = 0; i <= numc; i++) {
						reductions[i][choice[i]] = false;
					}
				}
			}
			choice[0]++;
			for (int i = 0; i < numc; i++) {
				if (choice[i] == options[i].length) {
					choice[i] = 0;
					choice[i + 1]++;
				}
			}
		}
		List<int[]> changedCells = new ArrayList<int[]>();
		for (int i = 0; i <= numc; i++) {
			boolean reduce = false;
			int col = cellcoords[i + i];
			int row = cellcoords[i + i + 1];
			Cell cell = grid[row][col];
			for (int o = 0; o < options[i].length; o++) {
				if (reductions[i][o]) {
					cell.removeAvailable(options[i][o]);
//					cell.removeCandidate(options[i][o]);
					reduce = true;
				}
			}
			if (reduce) {
				changedCells.add(new int[] { col, row });
			}
		}
		return changedCells;
	}

	public static interface Formula {
		public int calc(char[] combination);
	}
}
