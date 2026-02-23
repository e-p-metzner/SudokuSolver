package sudoku;

import jgui.JGui;
import jgui.JGuiTheme;
import processing.core.PApplet;
import sudoku.gameplay.Sudoku;
import sudoku.gui.Layout;
import sudoku.services.SudokuService;
import sudoku.solver.LuaSolver;

public class AppMain extends PApplet {

	public static void main(String[] args) {
		PApplet.main(AppMain.class, args);
	}

	Sudoku sudoku = null;
	SudokuService service = SudokuService.getInstance();
	LuaSolver solver = null;
	JGui gui;
	Layout appLayout;

	@Override
	public void settings() {
//		size(1280, 720, P2D);
		size(800, 800, P2D);
	}

	@Override
	public void setup() {
//		frameRate(20);

		gui = new JGui(this);
		JGuiTheme.rounding_scale = 1;
		appLayout = new Layout(gui);
		sudoku = new Sudoku();
		appLayout.setSudoku(sudoku);

//		sudoku.deserializeFromJson(loadJSONObject("res/examples/sudokuwiki_X-Wing_x3.esf"));
//		sudoku.deserializeFromJson(loadJSONObject("res/examples/logicmasters_SennyK_Erica-6.esf"));
//		sudoku.deserializeFromJson(loadJSONObject("res/examples/logicmasters_NotSoMagnifique_TheHive.esf"));
//		sudoku.deserializeFromJson(loadJSONObject("res/examples/logicmasters_MrToffee_Sciencelab-8x8.esf"));
		sudoku.deserializeFromJson(loadJSONObject("res/examples/logicmasters_WillPower_BlowDryer.esf"));
//		sudoku.printInfo();
		sudoku.validateRules();
		sudoku.printInfo();

		solver = new LuaSolver();
//		solver.solve(sudoku);
		solver.solverStart(sudoku);

		gui.validate();
		surface.setResizable(true);
	}

	@Override
	public void draw() {
		background(0xff6f00aa);
		rectMode(CORNERS);
	}

	@Override
	public void keyReleased() {
		if (key == ' ') {
			solver.solveStep(sudoku);
		}
	}
}