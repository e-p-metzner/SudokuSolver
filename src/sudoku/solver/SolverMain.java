package sudoku.solver;

import processing.core.PApplet;

public class SolverMain extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SolverMain.class, args);
    }

    @Override
    public void settings() {
        size(1280, 720, P2D);
    }

    @Override
    public void setup() {

    }

    @Override
    public void draw() {
        background(0xff000000);
    }
}
