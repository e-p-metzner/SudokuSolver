package sudoku.solver;

import java.util.ArrayList;
import java.util.List;

import sudoku.gameplay.Cell;
import sudoku.gameplay.Sudoku;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.Rule;

public class LogicalSolver {

//	List<StrategyWrapper> strategies;
	SolveLog logger;
	List<SolvingStrategy> strategies;

	public LogicalSolver() {
//		strategies = new ArrayList<StrategyWrapper>();
		logger = new SolveLog();
		strategies = new ArrayList<SolvingStrategy>();
	}

	public void solverStart(Sudoku sudoku) {
		logger.clear();
		strategies.clear();
		strategies.add(new NakedSingle());
		for (Rule rule : sudoku.getRules()) {
			for (SolvingStrategy strategy : rule.getStrategies()) {
				if (!strategies.contains(strategy)) {
					strategies.add(strategy);
				}
			}
		}
		if (sudoku.getRegions() != null) {
			strategies.add(new BoxLineReductions());
		}
		strategies.add(new XWing());
		strategies.add(new YWing());
		strategies.add(new Swordfish());

		// sort strategies in ascending order. Without changing order, when they are
		// equally difficult
		for (int s = 0; s < strategies.size(); s++) {
			for (int i = strategies.size() - 1; i > s; i--) {
				if (strategies.get(i).difficulty() >= strategies.get(i - 1).difficulty()) {
					continue;
				}
				SolvingStrategy st = strategies.get(i);
				strategies.remove(i);
				strategies.add(i - 1, st);
			}
		}

		// init candidates
//		System.out.println("\n\nInit candidate:");
		Cell[][] grid = sudoku.getGrid();
		for (int rw = 0; rw < grid.length; rw++) {
			for (int cl = 0; cl < grid[rw].length; cl++) {
				Cell cell = grid[rw][cl];
				if (cell.isGiven()) {
					cell.setAvailables(new char[] { cell.getContent() });
				} else {
					cell.setAvailables(sudoku.allValidInputs());
				}
				cell.getVisibleSets().clear();
			}
		}
		for (Rule rule : sudoku.getRules()) {
			if (!(rule instanceof AllDigitsDistinct)) {
				continue;
			}
			AllDigitsDistinct alldd = (AllDigitsDistinct) rule;
			int[] cc = alldd.getCells();
			for (int u = 0; u < cc.length; u += 2) {
				grid[cc[u + 1]][cc[u]].getVisibleSets().add(alldd);
			}
		}
		findCandidates(sudoku);
	}

	public void solveStep(Sudoku sudoku) {
		boolean finished = true;
		for (Cell[] row : sudoku.getGrid()) {
			for (Cell cell : row) {
				if (!cell.isActive()) {
					continue;
				}
				if (!cell.isSolved()) {
					finished = false;
					break;
				}
			}
			if (!finished) {
				break;
			}
		}
		if (finished) {
			logger.logStep("Sudoku solved! :)");
			return;
		}
		try {
			boolean foundSomething = false;
			for (SolvingStrategy strg : strategies) {
				if (strg.reduce(sudoku, logger)) {
					foundSomething = true;
					break;
				}
			}
			findCandidates(sudoku);
			if (!foundSomething) {
				logger.logStep("No logical step found.");
			}
		} catch (SolverException se) {
			return;
		}
	}

	public void solve(Sudoku sudoku) {
//		strategies.clear();
		solverStart(sudoku);

		boolean foundSomething = true;
		int difficulty = 0;
		while (foundSomething) {
			foundSomething = false;
			try {
				for (SolvingStrategy strg : strategies) {
					if (strg.reduce(sudoku, logger)) {
						foundSomething = true;
						difficulty = Math.max(difficulty, strg.difficulty());
						findCandidates(sudoku);
						break;
					}
				}
			} catch (SolverException se) {
				foundSomething = false;
				break;
			}
		}
		boolean allSolved = true;
		for (Cell[] arr : sudoku.getGrid()) {
			for (Cell c : arr) {
				if (!c.isActive()) {
					continue;
				}
				allSolved &= c.isSolved();
			}
		}
		if (!allSolved) {
			logger.logStep("No logical step found.");
		} else {
			logger.logStep("Sudoku solved! :)");
		}
	}

	private void findCandidates(Sudoku sudoku) {
//		System.out.println("\n\n[DEBUG] refresh candidates within cells:");
		Cell[][] grid = sudoku.getGrid();
		for (int rw = 0; rw < grid.length; rw++) {
			for (int cl = 0; cl < grid[rw].length; cl++) {
				Cell cell = grid[rw][cl];
				if (cell.isSolved()) {
					continue;
				}
				cell.clearCandidates();
				for (char candidate : cell.getAvailables()) {
					boolean isPossible = true;
					for (Rule rule : sudoku.getRules()) {
						isPossible &= rule.possible(candidate, sudoku, rw, cl);
					}
					if (isPossible) {
						cell.addCandidate(candidate);
					}
				}
				cell.candidates2innermarks();
//				System.out.println("  R" + (rw + 1) + "C" + (cl + 1) + " → " + cell.getInnerMarks() + " from " + Arrays.toString(cell.getAvailables()));
			}
		}
	}
}
