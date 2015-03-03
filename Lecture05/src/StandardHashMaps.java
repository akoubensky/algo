import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Исследование поведения стандартных классов HashMap и LinkedHashMap
 */
public class StandardHashMaps {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unchecked")
		Map<String, Integer>[] hashMaps = new Map[] {
				new HashMap<String, Integer>(),
				new LinkedHashMap<String, Integer>(),
				new LinkedHashMap<String, Integer>(10, 0.75f, true)
		};

		for (Map<String, Integer> map : hashMaps) {
			map.put("alfa", 1);
			map.put("beta", 2);
			map.put("gamma", 111);
			map.put("delta", 4);
			map.put("epsilon", 5);
			map.put("dzeta", 6);
			map.put("eta", 7);
			map.put("gamma", 3);
			map.get("dzeta");
			// Исследуем, в каком порядке перебираются элементы в структурах.
			// Для типа HashMap порядок не определен.
			// Для типа LinkedHashMap порядок определен порядком добавления элементов
			//                        или порядком доступа к элементам (get и put)
			for (Integer i : map.values()) {
				System.out.print("  " + i);
			}
			System.out.println();
		}
	}

}
