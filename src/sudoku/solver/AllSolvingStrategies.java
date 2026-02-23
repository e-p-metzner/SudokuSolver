package sudoku.solver;

import sudoku.gameplay.Registry;

public class AllSolvingStrategies {

	public static Registry<SolvingStrategy> strategies = Registry.getRegistry(SolvingStrategy.class);

	public static void registerStrategy(SolvingStrategy strategy) {
		strategies.addObject(strategy.getClass().getCanonicalName(), strategy);
	}

}
