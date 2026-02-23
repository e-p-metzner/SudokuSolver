package sudoku.solver;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.GermanWhisperLine;

public class GermanWhisperStrategies implements SolvingStrategy {

	private static final long serialVersionUID = -8746952701867319239L;

	private GermanWhisperLine line;

	public GermanWhisperStrategies(GermanWhisperLine line) {
		this.line = line;
	}

	@Override
	public void clearCache() {
		// there is no cache
	}

	@Override
	public int difficulty() {
		return 4;
	}

	@Override
	public boolean reduce(Sudoku sudoku, SolveLog log) {
		if (reductionByGivenNeighbors(sudoku, log)) {
			return true;
		}
		if (reduce4and6possibilities(sudoku, log)) {
			return true;
		}

		return false;
	}

	private boolean reductionByGivenNeighbors(Sudoku sudoku, SolveLog log) {
		boolean progress = false;
		for (int[] pos : line.getNeighbors().keySet()) {
			Cell current = sudoku.getGrid()[pos[1]][pos[0]];
			if (!current.isActive()) {
				throw new SolverException("Invalid Cell coordinates!");
			}
			// first check, if any of neighbors is given
			int[] nc = line.getNeighbors().get(pos);
			for (int u = 0; u < nc.length; u += 2) {
				boolean reduction = false;
				Cell other = sudoku.getGrid()[nc[u + 1]][nc[u]];
				if (other.isSolved()) {
					for (char c : current.getCandidates()) {
						// TODO handle sudoku larger than 9x9
						if (Math.abs(c - other.getContent()) < 5) {
							other.removeAvailable(c);
							reduction = true;
						}
					}
				}
				if (reduction) {
					progress = true;
					String remaining = "";
					for (char c : current.getAvailables()) {
						if (current.getCandidates().contains(c)) {
							remaining += "/" + c;
						}
					}
					if (remaining.length() < 1) {
						throw new SolverException("No remaining candidate for R" + (pos[1] + 1) + "C" + (pos[0] + 1) + "!");
					}
					log.logStep("Reduced candidates on R%dC%d to %s", //
							pos[1] + 1, pos[0] + 1, remaining.substring(1));
				}
			}
		}
		return progress;
	}

	private boolean reduce4and6possibilities(Sudoku sudoku, SolveLog log) {
		// TODO check if we really have '1' to '9' as valid sudoku inputs!
		// otherwise this reduction step is meaningless
		boolean progress = false;
		for (int[] pos : line.getNeighbors().keySet()) {
			Cell current = sudoku.getGrid()[pos[1]][pos[0]];
			if (!current.isActive()) {
				throw new SolverException("Invalid Cell coordinates!");
			}
			boolean has4 = current.getCandidates().contains('4');
			boolean has6 = current.getCandidates().contains('6');
			if (!has4 && !has6) {
				continue;
			}
			int[] nc = line.getNeighbors().get(pos);
			if (nc.length < 4) {
				continue;
			}
			int viscount = 0;
			for (int u = 0; u < nc.length; u += 2) {
				if (current.sees_cell(nc[u], nc[u + 1])) {
					viscount++;
				}
			}
			if (viscount > 1) {
				String removals = has4 ? "4" + (has6 ? " and 6" : "") : "6";
				if (has4) {
					current.removeAvailable('4');
				}
				if (has6) {
					current.removeAvailable('6');
				}
				log.logStep("Removed %s from R%dC%d, as would lead to impossibilities on neighbors on the whisper line.", //
						removals, pos[1] + 1, pos[0] + 1);
				progress = true;
			}
		}
		return progress;
	}
}
