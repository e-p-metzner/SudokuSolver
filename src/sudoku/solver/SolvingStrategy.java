package sudoku.solver;

import java.io.Serializable;

import sudoku.gameplay.Sudoku;

public interface SolvingStrategy extends Serializable {

	public void clearCache();

	public int difficulty();

	public boolean reduce(Sudoku sudoku, SolveLog log);

}
