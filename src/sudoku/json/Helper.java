package sudoku.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Helper {

	public static String[] readRessource(String filename) {
		List<String> lines = new ArrayList<String>();
		ClassLoader cl = Helper.class.getClassLoader();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(filename)))) {
			String line = br.readLine();
			while (line != null) {
				lines.add("" + line + "");
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return lines.toArray(new String[0]);
	}

}
