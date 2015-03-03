import java.util.Arrays;
import java.util.Objects;


public abstract class BSTree<K extends Comparable<K>, V> {
	/**
	 * Добавление в дерево новой ассоциативной пары.
	 * 
	 * @param key	Ключ.
	 * @param value	Значение.
	 * @return		Значение, которое было ассоциировано раньше с этим ключом
	 *				или null, если такого значения не было в дереве.
	 */
	public abstract V put(K key, V value);

	/**
	 * Удаление ассоциативной пары из дерева.
	 * 
	 * @param key	Ключ поиска удаляемой пары
	 * @return		Удаленное значение или null, если этого значения не было в дереве.
	 */
	public abstract V remove(K key);

	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	protected class BSNode {
		// Ссылки на левое и правое поддеревья:
		public BSNode left, right;
		// Ключ:
		public K key;
		// Значение:
		public V value;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		public BSNode(K key, V value, BSNode left, BSNode right) {
			this.key = key; this.value = value;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		public BSNode(K key, V value) { this(key, value, null, null); }
		
		@Override
		public String toString() {
			return "<" + key + ", " + value + ">";
		}
	}
	
	// Корень дерева
	BSNode root = null;

	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Вызов рекурсивной функции с проверкой: ключ поиска не должен быть пустым.
		return get(Objects.requireNonNull(key, "null key"), root);
	}
	
	/**
	 * Стандартный двоичный поиск в дереве по ключу
	 * @param key	Ключ поиска
	 * @param node	Начальный корень
	 * @return
	 */
	private V get(K key, BSNode node) {
		while (node != null) {
			int cmp = key.compareTo(node.key);
			if (cmp < 0) node = node.left; else
			if (cmp > 0) node = node.right; else
			return node.value;
		}
		// Ключ не найден
		return null;
	}
	
	/**
	 * "Красивая" печать дерева.
	 */
	public void print() {
		print(root, 0);
	}

	/**
	 * Вспомогательная функция для "красивой" печати дерева.
	 * @param node корневой узел.
	 * @param indent начальный отступ при печати.
	 */
	private void print(BSNode node, int indent) {
		// Формируем строку из indent пробелов.
		char[] spaces = new char[indent];
		Arrays.fill(spaces, ' ');
		System.out.print(String.valueOf(spaces));

		if (node == null) {
			System.out.println("..");
		} else {
			// Печать узла и его поддеревьев.
			System.out.println(node);
			print(node.left, indent + 2);
			print(node.right, indent + 2);
		}
	}
}
