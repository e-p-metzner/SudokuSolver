package sudoku.rules;

import java.util.HashMap;
import java.util.Map;

import processing.data.JSONObject;

public class RuleRegistry {

	static Map<String, Class<? extends Rule>> registry = new HashMap<String, Class<? extends Rule>>();
	static {
		register(new AllDigitsDistinct());
		register(new LatinSquare());
		register(new MainDiagonal());
		register(new DutchWhisperLine());
		register(new GermanWhisperLine());
		register(new KropkiV());
		register(new KropkiX());
		register(new KropkiBlack());
		register(new Arrow());
		register(new SumKillerCage());
		register(new Thermometer());
	}

	public static void register(Rule rule) {
		Class<? extends Rule> clazz = rule.getClass();
		registry.put(clazz.getSimpleName(), clazz);
	}

	public static Rule getRule(JSONObject json) {
		// TODO:
		String name = null;
		if (json.hasKey("type")) {
			name = json.getString("type");
		}
		if (json.hasKey("class")) {
			name = json.getString("class");
		}
		if (name == null) {
			throw new RuntimeException("Illformed esf file. Needs a \"type\" or \"class\" entry for rules.");
		}
		if (!registry.containsKey(name)) {
			throw new RuntimeException("Cannot find rule with name \"" + name + "\"");
		}
		try {
			Rule rule = registry.get(name).getConstructor().newInstance();
			System.out.println("Registry<Rule> import " + name);
			return rule;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load rule with name \"" + name + "\"", e);
		}
//		String clazz = json.getString("class");
//		System.out.println("Registry<Rule> import " + clazz);
//		return new TestRule();
	}
}
