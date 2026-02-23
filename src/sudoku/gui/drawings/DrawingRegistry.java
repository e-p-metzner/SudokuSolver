package sudoku.gui.drawings;

import java.util.HashMap;
import java.util.Map;

public class DrawingRegistry {

	private static Map<String, Class<? extends SudokuDraw>> registry;
	static {
		registry = new HashMap<String, Class<? extends SudokuDraw>>();
		register(new SudokuDrawArrow());
		register(new SudokuDrawDot());
		register(new SudokuDrawKilleroutline());
		register(new SudokuDrawLine());
		register(new SudokuDrawOutline());
	}

	public static void register(SudokuDraw drawing) {
		Class<? extends SudokuDraw> clazz = drawing.getClass();
		registry.put(clazz.getSimpleName(), clazz);
	}

	public static SudokuDraw getDrawing(String name) {
		if (!registry.containsKey(name)) {
			return null;
		}
		try {
			return registry.get(name).getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
