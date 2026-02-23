package sudoku.tools;

import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;

public class Tools {

	public static boolean contains(char candidate, char[] array) {
		for (char entry : array) {
			if (entry == candidate) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(int[] vector, int count, List<int[]> vectors) {
		for (int[] p : vectors) {
			boolean equal = true;
			for (int i = 0; i < count && equal; i++) {
				equal = p[i] == vector[i];
			}
			if (equal) {
				return true;
			}
		}
		return false;
	}

	public static <T extends Object> boolean contains(T candidate, T[] array) {
		if (candidate == null) {
			return false;
		}
		for (T entry : array) {
			if (entry == null) {
				continue;
			}
			if (entry.equals(candidate)) {
				return true;
			}
		}
		return false;
	}

	public static <T extends Object> void addUnique(List<T> source, List<T> dest) {
		for (T s : source) {
			if (dest.contains(s)) {
				continue;
			}
			dest.add(s);
		}
	}

	/**
	 * checks, if a string is the representation of a natural number
	 *
	 * @param str
	 * @return
	 */
	public static boolean isnatnum(String str) {
		for (char c : str.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	public static String binarystr(long l) {
		String s = "";
		for (int i = 63; i >= 0; i--) {
			s += ((l >>> i) & 1) + (i % 4 == 0 ? " " : "");
		}
		return s;
	}

	public static int str2color(String str) {
		if (str.startsWith("#")) {
			int col = Integer.parseInt(str.substring(1), 16);
			if (str.length() < 8) {
				col |= 0xff000000;
			}
			return col;
		}
		return Integer.parseInt(str);
	}

	public static int[] cellCoords(Cell cell, Sudoku sudoku) {
		int[] cc = cell.getVisibleSets().get(0).getCells();
		for (int u = 0; u < cc.length; u += 2) {
			if (sudoku.getGrid()[cc[u + 1]][cc[u]].equals(cell)) {
				return new int[] { cc[u], cc[u + 1] };
			}
		}
		return null;
	}

	public static String cellPos(Cell cell, Sudoku sudoku) {
		int[] p = cellCoords(cell, sudoku);
		return "R" + (p[1] + 1) + "C" + (p[0] + 1);
	}
}
