package sudoku.gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Registry<T extends Object> {

	private Map<String, T> object_map;

	private Registry() {
		object_map = new HashMap<String, T>();
	}

	public static <C> Registry<C> getRegistry(Class<C> clazz) {
		return new Registry<C>();
	}

	public void addObject(String name, T object) {
		object_map.put(name, object);
	}

	public List<T> getObjects() {
		return new ArrayList<T>(object_map.values());
	}

	public T getObjectForName(String name) {
		return object_map.get(name);
	}
}
