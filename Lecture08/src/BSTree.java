import java.util.Arrays;


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
		BSNode left, right;
		// Ключ:
		K key;
		// Значение:
		V value;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param balance показатель сбалансированности узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		BSNode(K key, V value, BSNode left, BSNode right) {
			this.key = key; this.value = value;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		BSNode(K key, V value) { this(key, value, null, null); }
	}

	// Корень дерева.
	BSNode root = null;


	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// Проход по дереву от корня до искомого узла.
		BSNode current = root;
		while (current != null) {
			if (key.compareTo(current.key) == 0) {
				return current.value;
			} else if (key.compareTo(current.key) < 0) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		// Ключ не найден.
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
		System.out.print(new String(spaces));

		// Представление пустого дерева.
		if (node == null) {
			System.out.println("--");
			return;
		}

		// Печать узла и его поддеревьев.
		System.out.println("<" + node.key + ", " + node.value + ">");
		print(node.left, indent + 2);
		print(node.right, indent + 2);
	}
}
