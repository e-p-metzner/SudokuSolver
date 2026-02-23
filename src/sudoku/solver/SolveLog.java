package sudoku.solver;

public class SolveLog {

	public void logStep(String txt, Object... params) {
		System.out.println(String.format("[SOLVELOG] " + txt, params));
	}

	public void clear() {
		// TODO implement when logger is not only printout
	}

}
