package sudoku.services;

public final class SudokuService {

	private static SudokuService instance;

	private SudokuService() {
	}

	public static SudokuService getInstance() {
		if (instance == null) {
			instance = new SudokuService();
		}
		return instance;
	}
}
