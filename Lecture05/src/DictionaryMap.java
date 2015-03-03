import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация простого словаря со строковым ключом и произвольным
 * содержвнием словарной статьи на основе хеш-таблицы с постоянной длиной.
 * 
 * Поддерживаются операции, характерные для типа {@link java.util.Map<String, V>}.
 * Реализация большинства операций поддерживается базовым классом {@link java.util.AbstractMap<String, V>}
 * 
 * @param <V> Тип содержимого словарных статей
 */
public class DictionaryMap<V> extends AbstractMap<String, V> {
	/**
	 * Класс, составляющий основу реализации словаря. Представляет набор
	 * пар <слово, словарная статья>. Для того, чтобы служить основой реализации
	 * словаря, класс расширяет абстрактную реализацию {@link java.util.AbstractSet},
	 * реализуя необходимые операции итерации, добавления элемента и выдачи
	 * размера словаря.
	 *
	 * @param <V>
	 */
	private static class MyEntrySet<V> extends AbstractSet<Map.Entry<String, V>> {
		// Длина хеш-таблицы (постоянная)
		private final int GROUPS = 100;

		// Массив списков пар <hashCode, value>.
		@SuppressWarnings("unchecked")
		private List<Map.Entry<String, V>>[] hashGroups =
			(List<Map.Entry<String, V>>[])Array.newInstance(LinkedList.class, GROUPS);

		/**
		 * Реализует итерацию элементов абстрактного множества. Это одно
		 * из необходимых условий использования типа {@link AbstractSet}
		 */
		@Override
		public Iterator<Map.Entry<String, V>> iterator() {
			Iterator<Map.Entry<String, V>> current = Iterators.empty();
			for (List<Map.Entry<String, V>> list : hashGroups) {
				if (list != null) {
					current = Iterators.join(current, list.iterator());
				}
			}
			return current;
		}	// iterator

		/**
		 * Реализует выдачу размера словаря. Это одно
		 * из необходимых условий использования типа {@link AbstractSet}
		 */
		@Override
		public int size() {
			int size = 0;
			for (List<Map.Entry<String, V>> list : hashGroups) {
				if (list != null) {
					size += list.size();
				}
			}
			return size;
		}	// size

		/**
		 * Реализует добавление пары <слово, словарная статья> в множество.
		 * Это одно из необходимых условий использования типа {@link AbstractSet}
		 */
		@Override
		public boolean add(Map.Entry<String, V> entry) {
			if (entry == null) throw new IllegalArgumentException();
			int hash = 0;
			if (entry.getKey() != null) hash = Math.abs(entry.getKey().hashCode()) % GROUPS;
			if (hashGroups[hash] == null) hashGroups[hash] = 
					new LinkedList<Map.Entry<String, V>>();
			for (Map.Entry<String, V> e : hashGroups[hash]) {
				if (entry.getKey() == null ? e.getKey() == null : entry.getKey().equals(e.getKey())) {
					e.setValue(entry.getValue());
					return false;
				}
			}
			hashGroups[hash].add(entry);
			return true;
		}	// add
	}	// MyEntrySet

	// Собственно, это и есть реализация словаря
	private Set<Map.Entry<String, V>> mySet = new MyEntrySet<V>();


	/**
	 * Одно из двух необходимых условий реализации на основе абстрактного
	 * отображения {@link AbstractMap} - операция, выдающая множество
	 * пар <слово, словарная статья> 
	 */
	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		return mySet;
	}

	/**
	 * Одно из двух необходимых условий реализации на основе абстрактного
	 * отображения {@link AbstractMap} - операция, добавляющая в словарь
	 * пару <слово, словарная статья> 
	 */
	@Override
	public V put(String key, V value) {
		V oldValue = get(key);
		mySet.add(new AbstractMap.SimpleEntry<String, V>(key, value));
		return oldValue;
	}

	/**
	 * Тестирующая функция (unit test) проверяет работоспособность словаря
	 * @param args
	 */
	public static void main(String[] args) {
		// Множество слов (возможно, с повторениями)
		final String[] phrase = { 
				"to", "be", "or", "not", "to", "be",
				"that", "is", "the", "question" };
		// В качестве словарной статьи используется длина слова
		DictionaryMap<Integer> dict = new DictionaryMap<Integer>();
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
