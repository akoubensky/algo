import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация простого отображения с помощью подстановки в качестве контейнера в
 * AbstractMap списка пар <ключ, значение>.
 * 
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class ListMap<K, V> extends AbstractMap <K, V> {
	/**
	 * Реализация набора пар <ключ, значение> на основе AbstractSet.
	 * 
	 * @param <K>
	 * @param <V>
	 */
	private static class ListSet<K, V> extends AbstractSet<Map.Entry<K, V>> {
		/**
		 * Список пар <ключ, значение>.
		 */
		private List<Map.Entry<K, V>> list = new LinkedList<>();

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return list.iterator();
		}

		@Override
		public int size() {
			return list.size();
		}
		
		@Override
		public boolean add(Map.Entry<K, V> elem) {
			return list.add(elem);
		}
		
	}
	
	/**
	 * Основа реализации отображения - набор пар <ключ, значение>.
	 */
	ListSet<K, V> listSet = new ListSet<>();

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return listSet;
	}

	@Override
	public V put(K key, V value) {
		for (Map.Entry<K, V> entry : listSet) {
			if (entry.getKey().equals(key)) {
				V oldValue = entry.getValue();
				entry.setValue(value);
				return oldValue;
			}
		}
		listSet.add(new AbstractMap.SimpleEntry<>(key, value));
		return null;
	}
	
	public static void main(String[] args) {
		Map<String, Integer> myMap = new ListMap<>();
		
		myMap.put("alfa", 1);
		myMap.put("beta", 2);
		myMap.put("gamma", 111);
		myMap.put("delta", 4);
		myMap.put("epsilon", 5);
		myMap.put("dzeta", 6);
		myMap.put("eta", 7);
		myMap.put("gamma", 3);
		System.out.println(myMap.get("dzeta"));

		for (String i : myMap.keySet()) {
			System.out.print("  " + i);
		}
		System.out.println();

		for (Integer i : myMap.values()) {
			System.out.print("  " + i);
		}
		System.out.println();
	}
}
