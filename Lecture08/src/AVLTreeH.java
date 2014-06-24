import java.util.Arrays;


/**
 * Реализация основных операций АВЛ-дерева. В узлах дерева хранятся высоты
 * соответствующих поддеревьев, что делает реализацию операций вставки и
 * удаления элементов проще, чем для случая хранения в узлах показателей
 * сбалансированности.
 * 
 * Реализованы следующие основные операции:
 * -    V get(K key) - поиск по ключу;
 * -    V put(K key, V value) - добавление или изменение ассоциативной пары;
 * -    V remove(K key) - удаление ассоциативной пары по ключу.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class AVLTreeH<K extends Comparable<K>, V> {
	
	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 *
	 * @param <K> тип ключа
	 * @param <V> тип значения
	 */
	private static class Node<K, V> {
		// Ссылки на левое и правое поддеревья:
		Node<K, V> left, right;
		// Ключ:
		K key;
		// Значение:
		V value;
		// Высота соответствующего поддерва:
		int height;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param height высота узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, int height, Node<K, V> left, Node<K, V> right) {
			this.key = key; this.value = value; this.height = height;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) { this(key, value, 1, null, null); }
	}

	// Корень дерева.
	Node<K, V> root = null;

	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// Проход по дереву от корня до искомого узла.
		Node<K, V> current = root;
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
	
	public V put(K key, V value) {
		V oldValue = get(key);
		root = put(key, value, root);
		return oldValue;
	}
	
	private Node<K,V> put(K key, V value, Node<K,V> node) {
		if (node == null) {
			return new Node<K,V>(key, value);
		} else {
			if (key.compareTo(node.key) < 0) {
				node.left = put(key, value, node.left);
			} else if (key.compareTo(node.key) > 0) {
				node.right = put(key, value, node.right);
			} else {
				node.value = value;
			}
			if (Math.abs((node.left == null ? 0 : node.left.height) -
					     (node.right == null ? 0 : node.right.height)) == 2) {
				node = balance(node);
			}
			recalcHeight(node);
			return node;
		}
	}
	
	private Node<K,V> balance(Node<K,V> node) {
		int heightLeft = node.left == null ? 0 : node.left.height;
		int heightRight = node.right == null ? 0 : node.right.height;
		if (heightLeft > heightRight) {
			Node<K,V> child = node.left;
			if ((child.left == null ? 0 : child.left.height) <
			    (child.right == null ? 0 : child.right.height)) {
				node.left = pivotRight(child);
			}
			return pivotLeft(node);
		} else {
			Node<K,V> child = node.right;
			if ((child.left == null ? 0 : child.left.height) >
			    (child.right == null ? 0 : child.right.height)) {
				node.right = pivotLeft(child);
			}
			return pivotRight(node);
		}
	}

	private Node<K,V> pivotLeft(Node<K,V> node) {
		Node<K,V> child = node.left;
		node.left = child.right;
		child.right = node;
		recalcHeight(node);
		recalcHeight(child);
		return child;
	}
	
	private Node<K,V> pivotRight(Node<K,V> node) {
		Node<K,V> child = node.right;
		node.right = child.left;
		child.left = node;
		recalcHeight(node);
		recalcHeight(child);
		return child;
	}
	
	private void recalcHeight(Node<K,V> node) {
		node.height = 1 + Math.max(node.left == null ? 0 : node.left.height,
                node.right == null ? 0 : node.right.height);
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
	private void print(Node<K, V> node, int indent) {
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

	public static void main(String[] args) {
		AVLTreeH<Integer, Integer> tree = new AVLTreeH<Integer, Integer>();
		for (int i = 1; i <= 10; i++) {
			tree.put(2*i, 2*i);
		}
		tree.put(15, 15);
		tree.print();
		System.out.println("----------------------------");

		for (int i = 10; i >= 1; i--) {
			//tree.remove(2*i);
			tree.print();
			System.out.println("----------------------------");
		}
	}

}
