package sudoku.solver;

import java.io.File;

import jgui.JGui;
import processing.core.PApplet;
import sudoku.gameplay.Sudoku_old;

public class Solver extends PApplet {

	public static void main(String[] args) {
		System.setProperty("jogl.disable.openglcore", "");
		PApplet.main(Solver.class, args);
	}

	private JGui gui;
	private Sudoku_old sudoku;

	@Override
	public void settings() {
		size(1280, 720, P2D);
	}

	@Override
	public void setup() {
		gui = new JGui(this);
		configureGui();
//		sudoku = new Sudoku();
//		sudoku.standardSudokuRules();
//		sudoku.addConstrain(new SumCage(7,  true, new int[][] {{1,1},{1,2},{1,3}}));
//		sudoku.addConstrain(new SumCage(5,  true, new int[][] {{5,1},{6,1}}));
//		sudoku.addConstrain(new SumCage(6,  true, new int[][] {{6,2},{7,2}}));
//		sudoku.addConstrain(new SumCage(6,  true, new int[][] {{5,3},{6,3}}));
//		sudoku.addConstrain(new SumCage(23, true, new int[][] {{2,5},{3,5},{3,6}}));
//		sudoku.addConstrain(new SumCage(15, true, new int[][] {{8,5},{8,6}}));
//		sudoku.addConstrain(new SumCage(17, true, new int[][] {{2,6},{2,7}}));
//		sudoku.addConstrain(new SumCage(3,  true, new int[][] {{5,8},{6,8}}));
//		sudoku.infoConstrain();

//		sudoku.initField(new int[][] {
//			{ 5, 3, 0, 0, 7, 0, 0, 0, 0 },
//			{ 6, 0, 0, 1, 9, 5, 0, 0, 0 },
//			{ 0, 9, 8, 0, 0, 0, 0, 6, 0 },
//			{ 8, 0, 0, 0, 6, 0, 0, 0, 3 },
//			{ 4, 0, 0, 8, 0, 3, 0, 0, 1 },
//			{ 7, 0, 0, 0, 2, 0, 0, 0, 6 },
//			{ 0, 6, 0, 0, 0, 0, 2, 8, 0 },
//			{ 0, 0, 0, 4, 1, 9, 0, 0, 5 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }
//		});

//		sudoku = new Sudoku();
//		sudoku.addConstrain(new KnightsMove("notsum", 10));
//		sudoku.addConstrain(new Thermometer(true,true,new int[][] {{2,2},{2,1},{1,2},{0,2},{1,1}}));
//		sudoku.addConstrain(new Thermometer(true,true,new int[][] {{6,2},{6,1},{7,2},{8,2},{7,1},{6,0}}));
//		sudoku.addConstrain(new Thermometer(true,true,new int[][] {{1,6},{2,7},{1,7},{0,7},{1,8}}));
//		sudoku.addConstrain(new CircleArrow("sum",new int[][] {{3,1},{3,2},{4,1},{5,1}}));
////		sudoku.a
//		sudoku.standardSudokuRules();
//		sudoku.initField(new int[][] {
//			{ 4, 0, 0, 0, 0, 0, 0, 0, 7 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 5, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }
//		});

		sudoku = new Sudoku_old(9, 9, 9);
//		sudoku.addConstrain(new CircleArrow("sum", 3,
//				new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 3, 1 }, { 3, 2 }, { 3, 3 }, { 3, 4 }, { 3, 5 }, { 3, 6 }, { 3, 7 }, { 3, 8 }, { 4, 8 },
//						{ 5, 8 }, { 5, 7 }, { 5, 6 }, { 5, 5 }, { 5, 4 }, { 5, 3 }, { 5, 2 }, { 5, 1 }, { 5, 0 }, { 6, 0 }, { 7, 0 }, { 8, 0 } }));
//		sudoku.addConstrain(new CircleArrow("sum", 2, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 0, 4 }, { 0, 5 }, { 0, 6 }, { 0, 7 }, { 0, 8 }, { 1, 8 },
//				{ 1, 7 }, { 1, 6 }, { 1, 5 }, { 1, 4 }, { 1, 3 }, { 1, 2 }, { 1, 1 } }));
//		sudoku.addConstrain(new LineConstrain("whisper", 5, new int[][] { { 4, 0 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 }, { 4, 5 } }));

//		sudoku.addConstrain(new SumCage(45, false, new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 } }));
//		sudoku.addConstrain(new SumCage(45, false, new int[][] { { 8, 0 }, { 8, 1 }, { 8, 2 }, { 7, 3 }, { 8, 3 }, { 7, 4 }, { 8, 4 }, { 7, 5 }, { 8, 5 } }));
//		sudoku.addConstrain(new SumCage(45, false, new int[][] { { 0, 3 }, { 0, 4 }, { 0, 5 }, { 0, 6 }, { 1, 6 }, { 0, 7 }, { 1, 7 }, { 0, 8 }, { 1, 8 } }));
//		sudoku.addConstrain(new SumCage(45, false, new int[][] { { 3, 7 }, { 4, 7 }, { 5, 7 }, { 3, 8 }, { 4, 8 }, { 5, 8 }, { 6, 8 }, { 7, 8 }, { 8, 8 } }));
//		sudoku.addConstrain(new Thermometer(true, true, new int[][] { { 3, 4 }, { 4, 3 }, { 5, 3 }, { 4, 4 }, { 3, 3 } }));
//		sudoku.addConstrain(new Thermometer(true, true, new int[][] { { 1, 7 }, { 2, 8 }, { 2, 7 }, { 1, 6 }, { 0, 6 }, { 0, 5 } }));
//		sudoku.addConstrain(new Thermometer(true, true, new int[][] { { 1, 7 }, { 2, 8 }, { 2, 7 }, { 1, 6 }, { 0, 6 }, { 0, 7 }, { 1, 8 } }));
//		sudoku.addConstrain(new KropkiXVDot("plus-one", new int[][] { { 1, 0 }, { 2, 0 } }));
//		sudoku.addConstrain(new KropkiXVDot("plus-one", new int[][] { { 7, 5 }, { 7, 6 } }));
//		sudoku.addConstrain(new KropkiXVDot("plus-one", new int[][] { { 2, 7 }, { 3, 7 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 3, 0 }, { 4, 0 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 0, 2 }, { 1, 2 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 7, 2 }, { 7, 3 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 6, 3 }, { 6, 4 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 2, 5 }, { 2, 6 } }));
//		sudoku.addConstrain(new KropkiXVDot("double", new int[][] { { 4, 6 }, { 5, 6 } }));
//		sudoku.initField(new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 } });

//		sudoku.addConstrain(new Disk(5, new int[] { 2, 2 }, 10));
//		sudoku.addConstrain(new Disk(5, new int[] { 6, 4 }, 10));
//		sudoku.addConstrain(new SumCage(17, false, new int[][] { { 5, 3 }, { 6, 3 }, { 7, 3 } }));
//		sudoku.addConstrain(new SumCage(8, false, new int[][] { { 1, 5 }, { 1, 6 } }));
//		sudoku.addConstrain(new SumCage(15, false, new int[][] { { 5, 6 }, { 5, 7 } }));
//		sudoku.addConstrain(new SumCage(11, false, new int[][] { { 7, 6 }, { 8, 6 } }));

//		sudoku = new Sudoku(4, 4, 4);
//		sudoku.standardSudokuRules();
//		Constrain ca = new CircleArrow("sum", new int[][] { { 1, 2 }, { 2, 0 }, { 3, 0 } });
//		sudoku.addConstrain(ca);
//		sudoku.addConstrain(new SumCage(9, true, new int[][] { { 2, 1 }, { 3, 1 }, { 3, 2 } }));
//		sudoku.addConstrain(new Thermometer(true, true, new int[][] { { 0, 3 }, { 1, 3 }, { 2, 3 } }));
//		sudoku.addConstrain(new Quadruple(new int[] { 1, 2, 3, 4 }, new int[][] { { 0, 1 }, { 1, 1 }, { 0, 2 }, { 1, 2 } }));
//		sudoku.initField(new int[][] { { -1, -1, 2, 1 }, { -1, -1, -1, -1 }, { -1, 3, -1, -1 }, { -1, -1, -1, -1 } });
//		println("Circle arrow fullfilled: " + ca.isFullfilled(sudoku.field));

//		sudoku.addConstrain(new Disk(2, new int[] { 1, 1 }, 5));
//		sudoku.initField(new int[][] { { 0, 0, 0, 0 }, { 0, 0, 4, 0 }, { 0, 4, 0, 0 }, { 0, 0, 0, 0 } });

		textSize(70);
	}

	@Override
	public void draw() {
		background(0xff676767);
		rectMode(CORNERS);
//		if (frameCount > 2) {
//			updateGui();
//		}
		if (sudoku != null) {
			sudoku.draw(g);
		}
	}

	public void loadSudoku(File selection) {
		if (selection == null) {
			return;
		}
		sudoku = Sudoku_old.loadDescription(selection);
	}

	public void saveSudoku(File selection) {
		if (selection == null) {
			return;
		}
		sudoku.saveDescription(selection);
	}

	public void saveSolutions(File selection) {
		if (selection == null) {
			return;
		}
		sudoku.saveSolutions(selection);
	}

	private void configureGui() {
		gui.addButton(1180, 50, 100, 50, "load", 0xff999999, 0xff000000, 0xff000000, 0xffaaaaaa).setClickTask(false, () -> {
			selectInput("select sudoku", "loadSudoku");
		});
		gui.addButton(1180, 120, 100, 50, "save", 0xff999999, 0xff000000, 0xff000000, 0xffaaaaaa).setClickTask(false, () -> {
			selectOutput("select save location", "saveSudoku");
		});
		gui.addButton(1180, 190, 100, 50, "check", 0xffffaa00, 0xffaa9900, 0xff000000, 0xffffff00).setClickTask(false, () -> {
			sudoku.check(gui);
		});
		gui.addButton(1180, 550, 100, 50, "solve", 0xff00ff00, 0xff00aa00, 0xff000000, 0xff99ff33).setClickTask(true, () -> {
			sudoku.solve();
		});
		gui.addButton(1180, 620, 100, 80, "save\nsolutions", 0xff999999, 0xff000000, 0xff000000, 0xffaaaaaa).setClickTask(false, () -> {
			selectOutput("save solutions", "saveSolutions");
		});

		gui.validate();
	}

//	private void updateGui() {
//		for (JGuiElement e : gui.) {
//			e.setPos(width - e.getBBox()[2], e.getBBox()[1]);
//		}
//	}
}
