import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * Простая реализация словаря на основе хеш-таблиц с постоянной длиной
 * таблицы (без перехеширования). Пустые ключи не допускаются, однако,
 * разрешено хранить пустые значения.
 *
 * @param <V> Тип элементов словаря
 */
public class MyDictionary<V> implements Map<String, V> {
	// Длина хеш-таблицы (постоянная)
	private final int GROUPS = 100;

	// Массив списков пар <hashCode, value>.
	@SuppressWarnings("unchecked")
	private List<SimpleEntry<String, V>>[] hashGroups =
		(List<SimpleEntry<String, V>>[])Array.newInstance(SimpleEntry.class, GROUPS);

	// Количество элементов в словаре
	private int size = 0;

	@Override
	public V put(String key, V value) {
		if (key == null) throw new NullPointerException("Null key");
		int index = Math.abs(key.hashCode() % GROUPS);
		if (hashGroups[index] == null) {
			hashGroups[index] = new LinkedList<SimpleEntry<String, V>>();
		}
		for (SimpleEntry<String, V> entry : hashGroups[index]) {
			if (entry.getKey().equals(key)) {
				V oldValue = entry.getValue();
				entry.setValue(value);
				return oldValue;
			}
		}
		hashGroups[index].add(new SimpleEntry<String, V>(key, value));
		size++;
		return null;
	}

	@Override
	public void clear() {
		for (int i = 0; i < hashGroups.length; ++i) {
			hashGroups[i] = null;
		}
		size = 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return key != null && key instanceof String && find((String)key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		for (List<SimpleEntry<String, V>> list : hashGroups) {
			if (list == null) continue;
			for (SimpleEntry<String, V> entry : list) {
				V v = entry.getValue();
				if (value == null) {
					return v == null;
				} else {
					return value.equals(v);
				}

			}
		}
		return false;
	}

	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		// Организуем новое множество, в которое последовательно перепишем
		// все элементы словаря. Не самая эффективная реализация...
		Set<Map.Entry<String, V>> result = new HashSet<Map.Entry<String, V>>();
		for (List<SimpleEntry<String, V>> list : hashGroups) {
			if (list == null) continue;
			for (SimpleEntry<String, V> entry : list) {
				result.add(entry);
			}
		}
		return result;
	}

	@Override
	public V get(Object key) {
		if (key == null || !(key instanceof String)) {
			return null;
		}
		SimpleEntry<String, V> entry = find((String)key);
		if (entry == null) {
			return null;
		}
		return entry.getValue();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<String> keySet() {
		// Организуем новое множество, в которое последовательно перепишем
		// все ключи словаря. Не самая эффективная реализация...
		Set<String> result = new HashSet<String>();
		for (Map.Entry<String, V> entry : entrySet()) {
			result.add(entry.getKey());
		}
		return result;
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> other) {
		// Добавление элементов происходит по одному.
		for (Entry<? extends String, ? extends V> entry : other.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}

	}

	@Override
	public V remove(Object key) {
		if (key == null || !(key instanceof String)) {
			return null;
		}
		List<SimpleEntry<String, V>> list = hashGroups[Math.abs(key.hashCode()) % GROUPS];
		if (list == null) {
			return null;
		}
		Iterator<SimpleEntry<String, V>> iter = list.iterator();
		while (iter.hasNext()) {
			SimpleEntry<String, V> entry = iter.next();
			if (entry.getKey().equals(key)) {
				iter.remove();
				size--;
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<V> values() {
		// Организуем новый список, в который последовательно перепишем
		// все словарные статьи. Не самая эффективная реализация...
		List<V> result = new ArrayList<V>();
		for (Map.Entry<String, V> entry : entrySet()) {
			result.add(entry.getValue());
		}
		return result;
	}

	private SimpleEntry<String, V> find(String key) {
		if (key == null) throw new IllegalArgumentException("Null key");
		int index = Math.abs(key.hashCode() % GROUPS);
		if (hashGroups[index] == null) {
			return null;
		}
		for (SimpleEntry<String, V> entry : hashGroups[index]) {
			if (entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Тестирующая функция (unit test) проверяет работоспособность словаря
	 * @param args Не используется
	 */
	public static void main(String[] args) {
		// Множество слов (возможно, с повторениями)
		final String[] phrase = { 
				"to", "be", "or", "not", "to", "be",
				"that", "is", "the", "question" };
		// В качестве словарной статьи используется длина слова
		MyDictionary<Integer> dict = new MyDictionary<Integer>();
		// Заполнение словаря
		for (String word : phrase) {
			dict.put(word,  word.length());
		}

		// Проверка содержимого словаря
		System.out.println("Size = " + dict.size());
		System.out.println(dict.get("Question"));
		System.out.println(dict.get("question"));

		// Удаление одного слова
		System.out.println(dict.remove("not"));

		// Печать всего содержимого словаря
		for (Map.Entry<String, Integer> entry : dict.entrySet()) {
			System.out.print("<" + entry.getKey() + ", " + entry.getValue() + ">, ");
		}
		System.out.println();
	}
}
