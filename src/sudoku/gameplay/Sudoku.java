package sudoku.gameplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jgui.elements.JGuiElement;
import processing.core.PGraphics;
import processing.data.JSONArray;
import processing.data.JSONObject;
import sudoku.gui.Design;
import sudoku.gui.drawings.SudokuDraw;
import sudoku.json.JSONSerializable;
import sudoku.rules.AllDigitsDistinct;
import sudoku.rules.LatinSquare;
import sudoku.rules.Rule;
import sudoku.rules.RuleRegistry;

public class Sudoku extends JGuiElement implements JSONSerializable {

	private static final long serialVersionUID = 4997529790753450762L;

	private float gridOffX, gridOffY;
	private float cellSize;
	private Cell[][] grid;
	private int[][] regions;
	private List<Rule> ruleset;
	private char[] validInputs;

	private List<SudokuDraw> linedrawings;
	private List<SudokuDraw> overlayedrawings;

	public Sudoku() {
		grid = null;
		regions = null;
		ruleset = new ArrayList<Rule>();
		validInputs = new char[0];
		linedrawings = new ArrayList<SudokuDraw>();
		overlayedrawings = new ArrayList<SudokuDraw>();
	}

	public void init(int wid, int hgt, boolean stdRegions) {
		grid = new Cell[hgt][wid];
		for (int j = 0; j < hgt; j++) {
			for (int i = 0; i < wid; i++) {
				grid[j][i] = new Cell();
			}
		}
		LatinSquare lsqr = new LatinSquare();
		lsqr.deserializeFromJson(JSONObject.parse("{\"size\":" + wid + ",\"startcolumn\":0,\"startrow\":0}"));
		ruleset.add(lsqr);

		if (wid == hgt && stdRegions) {
			switch (wid) {
				case 4:
					regions = new int[][] { //
							{ 0, 0, 1, 0, 0, 1, 1, 1 }, //
							{ 2, 0, 3, 0, 2, 1, 3, 1 }, //
							{ 0, 2, 1, 2, 0, 3, 1, 3 }, //
							{ 2, 2, 3, 2, 2, 3, 3, 3 } //
					};
					break;
				case 6:
					regions = new int[][] { //
							{ 0, 0, 1, 0, 2, 0, 0, 1, 1, 1, 2, 1 }, //
							{ 3, 0, 4, 0, 5, 0, 3, 1, 4, 1, 5, 1 }, //
							{ 0, 2, 1, 2, 2, 2, 0, 3, 1, 3, 2, 3 }, //
							{ 3, 2, 4, 2, 5, 2, 3, 3, 4, 3, 5, 3 }, //
							{ 0, 4, 1, 4, 2, 4, 0, 5, 1, 5, 2, 5 }, //
							{ 3, 4, 4, 4, 5, 4, 3, 5, 4, 5, 5, 5 } //
					};
					break;
				case 8:
					regions = new int[][] { //
							{ 0, 0, 1, 0, 2, 0, 3, 0, 0, 1, 1, 1, 2, 1, 3, 1 }, //
							{ 4, 0, 5, 0, 6, 0, 7, 0, 4, 1, 5, 1, 6, 1, 7, 1 }, //
							{ 0, 2, 1, 2, 2, 2, 3, 2, 0, 3, 1, 3, 2, 3, 3, 3 }, //
							{ 4, 2, 5, 2, 6, 2, 7, 2, 4, 3, 5, 3, 6, 3, 7, 3 }, //
							{ 0, 4, 1, 4, 2, 4, 3, 4, 0, 5, 1, 5, 2, 5, 3, 5 }, //
							{ 4, 4, 5, 4, 6, 4, 7, 4, 4, 5, 5, 5, 6, 5, 7, 5 }, //
							{ 0, 6, 1, 6, 2, 6, 3, 6, 0, 7, 1, 7, 2, 7, 3, 7 }, //
							{ 4, 6, 5, 6, 6, 6, 7, 6, 4, 7, 5, 7, 6, 7, 7, 7 } //
					};
					break;
				case 9:
					regions = new int[][] { //
							{ 0, 0, 1, 0, 2, 0, 0, 1, 1, 1, 2, 1, 0, 2, 1, 2, 2, 2 }, //
							{ 3, 0, 4, 0, 5, 0, 3, 1, 4, 1, 5, 1, 3, 2, 4, 2, 5, 2 }, //
							{ 6, 0, 7, 0, 8, 0, 6, 1, 7, 1, 8, 1, 6, 2, 7, 2, 8, 2 }, //
							{ 0, 3, 1, 3, 2, 3, 0, 4, 1, 4, 2, 4, 0, 5, 1, 5, 2, 5 }, //
							{ 3, 3, 4, 3, 5, 3, 3, 4, 4, 4, 5, 4, 3, 5, 4, 5, 5, 5 }, //
							{ 6, 3, 7, 3, 8, 3, 6, 4, 7, 4, 8, 4, 6, 5, 7, 5, 8, 5 }, //
							{ 0, 6, 1, 6, 2, 6, 0, 7, 1, 7, 2, 7, 0, 8, 1, 8, 2, 8 }, //
							{ 3, 6, 4, 6, 5, 6, 3, 7, 4, 7, 5, 7, 3, 8, 4, 8, 5, 8 }, //
							{ 6, 6, 7, 6, 8, 6, 6, 7, 7, 7, 8, 7, 6, 8, 7, 8, 8, 8 } //
					};
					break;
				default:
					if (wid < 8) {
						System.err.println("[WARNING] No Regioning exists for square sudoku of size " + wid + ".");
					} else {
						System.err.println("[WARNING] Std-regions for sudokus of size " + wid + "x" + hgt + " not implemented. Has to be set manually.");
					}
					break;
			}
			if (regions != null) {
				int regId = 0;
				for (int[] region : regions) {
					regId++;
					AllDigitsDistinct rule = new AllDigitsDistinct();
					rule.setCells(region, new int[] { 0, 0 }, "region " + regId);
					addRule(rule);
				}
			}
		} else {
			System.err.println("[WARNING] Non-standard-regions used!");
		}
		if (wid == hgt && wid < 10) {
			validInputs = "123456789".substring(0, wid).toCharArray();
		} else {
			throw new RuntimeException("Valid Input is unknown. Has to be set separatly!");
		}
	}

	public void setGivens(String... content) {
		if (grid == null) {
			throw new RuntimeException("Cannot set content for not initialized sudoku grid!");
		}
		if (grid.length != content.length) {
			throw new RuntimeException("Content does not match sudoku grid size.");
		}
		for (int r = 0; r < grid.length; r++) {
			if (content[r].length() < 2 * grid[0].length - 1) {
				throw new RuntimeException("Content does not match sudoku grid size.");
			}
			for (int c = 0; c < grid[0].length; c++) {
				char d = content[r].charAt(2 * c);
				if (d == '#' || d == '.') {
					continue;
				}
				grid[r][c].setGiven(d);
			}
		}
	}

	public void addRule(Rule rule) {
		ruleset.add(rule);
	}

	public List<Rule> getRules() {
		return Collections.unmodifiableList(ruleset);
	}

	public void validateRules() {
		int ruleid = ruleset.size() - 1;
		while (ruleid >= 0) {
			Rule rule = ruleset.get(ruleid);
			if (!rule.shouldBeReplacedBySubrules()) {
				ruleid--;
				continue;
			}
			ruleset.remove(ruleid);
			for (Rule subrule : rule.getSubrules()) {
				ruleset.add(ruleid, subrule);
				ruleid++;
			}
			ruleid--;
		}
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public int[][] getRegions() {
		return regions;
	}

	public char[] allValidInputs() {
		return validInputs.clone();
	}

	public JSONObject currentStateAsJSON() {
		JSONObject obj = new JSONObject();
		JSONArray rows = new JSONArray();
		for (int r = 0; r < grid.length; r++) {
			JSONArray row = new JSONArray();
			for (int c = 0; c < grid[r].length; c++) {
				JSONObject cell = new JSONObject();
				cell.setBoolean("active", grid[r][c].isActive());
				cell.setString("content", "" + grid[r][c].getContent());
				cell.put("candidates", grid[r][c].getCandidates().toArray(new Character[0]));
				cell.setString("available", new String(grid[r][c].getAvailables()));
				cell.setString("inner", grid[r][c].getInnerMarks());
				cell.setString("outer", grid[r][c].getOuterMarks());
				row.append(cell);
			}
			rows.append(row);
		}
		obj.setJSONArray("state", rows);
		return obj;
	}

	public void readStateFromJSON(JSONObject obj) {
		JSONArray rows = obj.getJSONArray("state");
		if (rows.size() != grid.length) {
			throw new RuntimeException("Cannot read state from JSON, different number of rows!");
		}
		for (int r = 0; r < grid.length; r++) {
			JSONArray row = rows.getJSONArray(r);
			if (row.size() != grid[r].length) {
				throw new RuntimeException("Cannot read state from JSON, different number of columns in row " + (r + 1) + "!");
			}
			for (int c = 0; c < grid[r].length; c++) {
				JSONObject json = row.getJSONObject(c);
				Cell cell = grid[r][c];
				cell.setActivation(json.getBoolean("active"));
				cell.setGiven(json.getString("content").charAt(0));
				cell.clearCandidates();
				cell.addAllCandidates((Character[]) json.get("candidates"));
				cell.setAvailables(json.getString("available").toCharArray());
				cell.setInnerMarks(json.getString("inner"));
				cell.setOuterMarks(json.getString("outer"));
			}
		}
	}

	@Override
	public void draw(PGraphics g, int[] mouseStats) {
		if (!visible) {
			return;
		}
		g.fill(Design.BGcolour);
		g.noStroke();
		drawBoundingBox(g, 20);
		if (grid == null) {
			return;
		}
		// compute grid layout
		cellSize = 0.9f * Math.min((float) bounding_box[2] / (float) grid[0].length, (float) bounding_box[3] / (float) grid.length);
		g.textSize(4f * cellSize);
		gridOffX = bounding_box[0] + 0.5f * (bounding_box[2] - cellSize * grid[0].length);
		gridOffY = bounding_box[1] + 0.5f * (bounding_box[3] - cellSize * grid.length);

		// draw line constrains
		for (SudokuDraw line : linedrawings) {
			line.draw(g, gridOffX, gridOffY, cellSize);
		}

		// draw grid
		g.noFill();
		g.stroke(Design.GridColour);
		g.strokeWeight(2f);
		for (int j = 0; j < grid.length; j++) {
			float yt = gridOffY + j * cellSize;
			for (int i = 0; i < grid[0].length; i++) {
				if (!grid[j][i].isActive()) {
					continue;
				}
				float xl = gridOffX + i * cellSize;
				g.rect(xl, yt, xl + cellSize, yt + cellSize, 3);
			}
		}

		if (regions != null) {
			g.noFill();
			g.stroke(Design.RegionBorderColour);
			g.strokeWeight(3f);
			for (int[] reg : regions) {
				for (int u = 0; u < reg.length; u += 2) {
					// check, if cell has neighbors
					int nb = 0b1111;
					for (int v = 0; v < reg.length; v += 2) {
						if (reg[u] - reg[v] == 0) {
							if (reg[u + 1] - reg[v + 1] == -1) {
								nb &= 0b1011; // south
							}
							if (reg[u + 1] - reg[v + 1] == 1) {
								nb &= 0b1110; // north
							}
						}
						if (reg[u + 1] - reg[v + 1] == 0) {
							if (reg[u] - reg[v] == -1) {
								nb &= 0b1101; // east
							}
							if (reg[u] - reg[v] == 1) {
								nb &= 0b0111; // west
							}
						}
					}
					if (nb == 0) {
						continue;
					}
					float xl = gridOffX + reg[u] * cellSize;
					float yt = gridOffY + reg[u + 1] * cellSize;
					if ((nb & 1) != 0) { // north
						g.line(xl, yt, xl + cellSize, yt);
					}
					if ((nb & 2) != 0) { // east
						g.line(xl + cellSize, yt, xl + cellSize, yt + cellSize);
					}
					if ((nb & 4) != 0) { // south
						g.line(xl, yt + cellSize, xl + cellSize, yt + cellSize);
					}
					if ((nb & 8) != 0) { // west
						g.line(xl, yt, xl, yt + cellSize);
					}
				}
			}
		}

		// draw overlays
		for (SudokuDraw overlay : overlayedrawings) {
			overlay.draw(g, gridOffX, gridOffY, cellSize);
		}

		// draw numbers (aka sudoku grid content)
		for (int j = 0; j < grid.length; j++) {
			float yc = gridOffY + (j + 0.5f) * cellSize;
			for (int i = 0; i < grid[j].length; i++) {
				float xc = gridOffX + (i + 0.5f) * cellSize;
				Cell cell = grid[j][i];
				if (cell.isSolved()) {
					g.fill(cell.isGiven() ? 0xff000000 : 0xff0099ff);
					g.textSize(0.8f * cellSize);
					g.textAlign(CENTER, CENTER);
					g.text("" + cell.getContent() + "", xc, yc - 0.1f * cellSize);
					continue;
				}
				if (cell.getInnerMarks().length() > 0) {
					g.fill(0xff0099ff);
					g.textSize(0.2f * cellSize);
					g.textAlign(CENTER, CENTER);
					g.text(cell.getInnerMarks(), xc, yc - 0.025f * cellSize);
				}
				if (cell.getOuterMarks().length() > 0) {
					char[] marks = cell.getOuterMarks().toCharArray();
					int uppercount = Math.max(2, (marks.length + 1) / 2);
					g.fill(0xff0099ff);
					g.textSize(0.2f * cellSize);
					g.textAlign(CENTER, CENTER);
					for (int m = 0; m < marks.length; m++) {
						float x = (2 * m + 1 - uppercount) * 0.4f * cellSize;
						if (xc > 0.5f) {
							xc -= 1f;
						}
						float y = (m < uppercount ? -0.4f : 0.4f) * cellSize;
						g.text("" + marks[m] + "", xc + x, yc + y - 0.025f * cellSize);
					}
				}
			}
		}
	}

	@Override
	public void key_press(char key, int code, int modifier) {
		super.key_press(key, code, modifier);
	}

	@Override
	public void key_release(char key, int code, int modifier) {
		super.key_release(key, code, modifier);
	}

	@Override
	public JGuiElement mouse_start(int[] mouseStats) {
		return super.mouse_start(mouseStats);
	}

	@Override
	public void mouse_finish(int[] mouseStats) {
		super.mouse_finish(mouseStats);
	}

	@Override
	public void validate() {
		visible = true;
	}

	@Override
	public JSONObject serialize2json() {
		JSONObject jo = new JSONObject();
		jo.setInt("width", grid[0].length);
		jo.setInt("height", grid.length);
		if (!ruleset.isEmpty()) {
			JSONArray ja = new JSONArray();
			for (Rule r : ruleset) {
				ja.append(r.serialize2json());
			}
			jo.setJSONArray("rules", ja);
		}
		return jo;
	}

	@Override
	public void deserializeFromJson(JSONObject json) {
		String formatstr = json.getString("format", "");
		if (formatstr.length() < 1) {
			System.err.println("No proper format entry in sudoku file.");
		} else {
			System.out.println("Sudoku-format: " + formatstr);
		}
		JSONArray validarr = json.getJSONArray("valid");
		validInputs = new char[validarr.size()];
		for (int i = 0; i < validInputs.length; i++) {
			String entry = validarr.getString(i);
			if (entry.length() != 1) {
				System.out.println("WARNING: entry for valid 'digits' is longer than one alpha-numerical symbol");
			}
			validInputs[i] = entry.charAt(0);
		}
		int w = json.getInt("width");
		if (w > 64) {
			throw new RuntimeException("A sudoku width greater than 64 is not supported");
		}
		int h = json.getInt("height");
		if (h > 64) {
			throw new RuntimeException("A sudoku height greater than 64 is not supported");
		}
		grid = new Cell[h][w];
		JSONArray givenarr = json.getJSONArray("given");
		JSONArray activarr = json.getJSONArray("active");
		for (int j = 0; j < h; j++) {
			String givenstr = givenarr.getString(j);
			String activstr = activarr.getString(j);
			for (int i = 0; i < w; i++) {
				grid[j][i] = new Cell();
				grid[j][i].setActivation(activstr.charAt(2 * i) == '#');
				char content = givenstr.charAt(2 * i);
				if (content == '.') {
					continue;
				}
				grid[j][i].setGiven(content);
			}
		}
		if (json.hasKey("regions")) {
			JSONArray regarr = json.getJSONArray("regions");
			regions = new int[regarr.size()][];
			for (int regid = 0; regid < regions.length; regid++) {
				JSONArray regjson = regarr.getJSONArray(regid);
				regions[regid] = new int[regjson.size()];
				for (int u = 0; u < regions[regid].length; u++) {
					regions[regid][u] = regjson.getInt(u) - 1; // Coordinate offset (sudoku idx starts with 1)
				}
				AllDigitsDistinct regrule = new AllDigitsDistinct();
				regrule.setCells(regions[regid], new int[] { 0, 0 }, "region " + (regid + 1));
				regrule.setBoxlineKey("BLREG");
				addRule(regrule);
			}
		}
		if (json.hasKey("rules")) {
			JSONArray ja = json.getJSONArray("rules");
			for (int i = 0; i < ja.size(); i++) {
				JSONObject rulejson = ja.getJSONObject(i);
				Rule rule = RuleRegistry.getRule(rulejson);
				rule.deserializeFromJson(rulejson);
				addRule(rule);
			}
		}

		validateRules();

		linedrawings.clear();
		overlayedrawings.clear();
		for (Rule rule : getRules()) {
			Map<String, List<SudokuDraw>> drawingmap = rule.getDrawings();
			for (String key : drawingmap.keySet()) {
				if ("overlay".equalsIgnoreCase(key)) {
					overlayedrawings.addAll(drawingmap.get(key));
					continue;
				}
				if ("lines".equalsIgnoreCase(key)) {
					linedrawings.addAll(drawingmap.get(key));
					continue;
				}
				System.err.println("Unknown layer \"" + key + "\" for drawing features");
			}
		}
	}

	public void printInfo() {
		System.out.println(this.toString() + "{");
		if (grid != null) {
			System.out.println("  size: " + grid[0].length + " x " + grid.length);
		} else {
			System.out.println("  size: 0 x 0");
		}
		System.out.println("  valid digits: " + Arrays.toString(validInputs));
		System.out.println("  rules: {");
		for (Rule rule : ruleset) {
			System.out.println("    " + rule.printInfo());
		}
		System.out.println("  }");
		System.out.println("}");
	}
}
