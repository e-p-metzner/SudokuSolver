package sudoku.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.data.JSONObject;
import sudoku.gameplay.Sudoku;
import sudoku.gui.drawings.SudokuDraw;
import sudoku.json.JSONSerializable;
import sudoku.solver.SolvingStrategy;

public abstract class Rule implements JSONSerializable {

	private static final long serialVersionUID = -3818913929648597383L;

	protected List<SolvingStrategy> strategies;
	protected List<Rule> subrules;
	protected boolean replaceBySubrules;
	protected transient Map<String, List<SudokuDraw>> drawings;

	public Rule() {
		strategies = new ArrayList<SolvingStrategy>();
		subrules = new ArrayList<Rule>();
		replaceBySubrules = false;
		drawings = new HashMap<String, List<SudokuDraw>>();
	}

	public abstract boolean possible(char candidate, Sudoku sudoku, int row, int col);

	public abstract String printInfo();

	public List<SolvingStrategy> getStrategies() {
		return strategies;
	}

	public boolean shouldBeReplacedBySubrules() {
		return replaceBySubrules;
	}

	public List<Rule> getSubrules() {
		return subrules;
	}

	public Map<String, List<SudokuDraw>> getDrawings() {
		return drawings;
	}

	@Override
	public JSONObject serialize2json() {
		JSONObject json = new JSONObject();
		json.setString("type", this.getClass().getCanonicalName());
		return json;
	}
}
