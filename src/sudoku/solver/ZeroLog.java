package sudoku.solver;

public class ZeroLog extends SolveLog {

	@Override
	public void logStep(String txt, Object... params) {
		// Logs nothing
	}

	@Override
	public void clear() {
		// nothing logged, so nothing there to clear
	}

}
