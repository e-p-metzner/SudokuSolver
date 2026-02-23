package sudoku.solver;

public class SolverException extends RuntimeException {

	private static final long serialVersionUID = 5229141054805431797L;

	public SolverException() {
		super();
	}

	public SolverException(String message) {
		super(message);
	}

	public SolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public SolverException(Throwable cause) {
		super(cause);
	}
}
