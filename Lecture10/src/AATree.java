import java.util.Arrays;

/**
 * Реализация основных операций (вставка и удаление элементов)
 * в AA-дереве (придуманном Арне Андерссоном в 1993 году).
 *
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class AATree<K extends Comparable<K>, V> {
	/**
	 * Реализация узла дерева. Дополнительное поле - уровень узла.
	 * Лист дерева имеет уровень 1. Фиктивный узел SENTINEL, который
	 * играет роль пустой ссылки, имеет уровень 0.
	 */
	private class Node {
		K key = null;
		V value = null;
		int level = 0;
		Node left = null;
		Node right = null;
		
		/**
		 * Конструктор листа
		 * @param key Ключ листа
		 * @param value Значение, хранящееся в листе.
		 */
		Node(K key, V value) {
			this.key = key;
			this.value = value;
			level = 1;
			left = right = SENTINEL;
		}
		
		/**
		 * Конструктор пустого узла (SENTINEL)
		 */
		Node() {}
	}
	
	// Пустой фиктивный узел
	final Node SENTINEL = new Node();
	
	// Корень дерева (вначале - пустой).
	Node root = SENTINEL;
	
	/**
	 * Функция проверки, является ли узел пустым
	 * @param node Проверяемый узел
	 * @return true, если узел пустой, false в противном случае.
	 */
	private boolean nil(Node node) { return node == SENTINEL; }
	
	/**
	 * Функция производит поворот, если нужно
	 * @param node Проверяемый узел
	 * @return Ссылка на корень , возможно, измененного поддерева.
	 */
	private Node skew(Node node) {
		Node result = node;
		if (!nil(node) && !nil(node.left) && node.left.level == node.level) {
			// Нужен правый поворот
			result = node.left;
			node.left = result.right;
			result.right = node;
		}
		return result;
	}
	
	/**
	 * Функция производит расщепление с продвижением узла на более высокий уровень,
	 * если это необходимо.
	 * @param node Проверяемый узел
	 * @return Корень, возможно, преобразованного узла.
	 */
	private Node split(Node node) {
		Node result = node;
		if (!nil(node) && !nil(node.right) && !nil(node.right.right) && 
				node.level == node.right.right.level) {
			result = node.right;
			node.right = result.left;
			result.left = node;
			result.level++;
		}
		return result;
	}
	
	/**
	 * Поиск значения в дереве по ключу.
	 * @param key Ключ поиска
	 * @return Найденое значение или null, если значения в дереве нет.
	 */
	public V get(K key) {
		Node node = search(key);
		return node.value;
	}
	
	/**
	 * Добавление нового значения в дерево. Если ключ уже существовал в дереве,
	 * то значение узла заменяется на новое. Иначе создается новый узел.
	 * @param key Добавляемый или изменяемый ключ
	 * @param value Добавляемое значение
	 * @return
	 */
	public V put(K key, V value) {
		root = put(root, key);
		Node newNode = search(key);
		V oldValue = newNode.value;
		newNode.value = value;
		return oldValue;
	}
	
	/**
	 * Вспомогательная рекурсивная функция добавления ключа в дерево.
	 * Функция не изменяет дерево, если узел с данным ключрм уже существовал.
	 * @param node Начальный узел для вставки (корень поддерева)
	 * @param key Вставляемый ключ
	 * @return Ссылка на, возможно, измененное поддерево
	 */
	private Node put(Node node, K key) {
		if (nil(node)) {
			return new Node(key, null);
		} else if (key.compareTo(node.key) < 0) {
			node.left = put(node.left, key);
		} else if (key.compareTo(node.key) > 0) {
			node.right = put(node.right, key);
		}
		
		node = skew(node);
		node = split(node);
		
		return node;
	}
	
	/**
	 * Поиск узла по ключу.
	 * @param key Ключ поиска
	 * @return Найденный узел или фиктивный узел, если ключ не существует в дереве.
	 */
	private Node search(K key) {
		Node current = root;
		while (!nil(current)) {
			if (current.key.equals(key)) {
				return current;
			} else if (current.key.compareTo(key) < 0) {
				current = current.right;
			} else {
				current = current.left;
			}
		}
		return current;
	}
	
	/**
	 * Удаление узла с заданным ключом.
	 * @param key Ключ удаляемого узла
	 * @return Значение, хранящееся в удаляемом узле, или null, если ключ не существовал.
	 */
	public V remove(K key) {
		Node node = search(key);
		root = remove(root, key);
		return node.value;
	}
	
	/**
	 * Вспомогательная рекурсивная функция, осуществляющая удаление узла из дерева.
	 * @param node Корень поддерева, из которого происходит удаление
	 * @param key Ключ удаляемого узла
	 * @return Корень, возможно, преобразованного поддерева.
	 */
	private Node remove(Node node, K key) {
	    if (nil(node)) {
	    	return node;
	    } else if (key.compareTo(node.key) > 0) {
	    	node.right = remove(node.right, key);
	    } else if (key.compareTo(node.key) < 0) {
	    	node.left = remove(node.left, key);
	    } else if (nil(node.right)) {
	    	// If we're a leaf, easy, otherwise reduce to leaf case. 
            return SENTINEL;
	    } else if (nil(node.left)) {
            Node substNode = successor(node);
            node.right = remove(node.right, substNode.key);
            node.key = substNode.key;
            node.value = substNode.value;
	    } else {
            Node substNode = predecessor(node);
            node.left = remove(node.left, substNode.key);
            node.key = substNode.key;
            node.value = substNode.value;
	    }

	    // Rebalance the tree. Decrease the level of all nodes in this level if
	    // necessary, and then skew and split all nodes in the new level.
	    node = decreaseLevel(node);
	    node = skew(node);
	    node.right = skew(node.right);
	    if (!nil(node.right)) node.right.right = skew(node.right.right);
	    node = split(node);
	    node.right = split(node.right);
	    return node;
	}

	/**
	 * Ищет ближайший справа узел к заданному
	 * @param node Исходный узел
	 * @return Ссылка на ближайший справа узел.
	 */
	private Node successor(Node node) {
		node = node.right;
		while (!nil(node.left)) node = node.left;
		return node;
	}
	
	/**
	 * Ищет ближайший слева узел к заданному
	 * @param node Исходный узел
	 * @return Ссылка на ближайший слева узел.
	 */
	private Node predecessor(Node node) {
		node = node.left;
		while (!nil(node.right)) node = node.right;
		return node;
	}
	
	/**
	 * Уменьшает уровень узла, если у его потомков не хватает элементов
	 * @param node Исходный узел
	 * @return Тот же узел, возможно, с измененным уровнем
	 */
	private Node decreaseLevel(Node node) {
		int shouldBe = Math.min(node.left.level, node.right.level) + 1;
		if (shouldBe < node.level) {
			node.level = shouldBe;
			if (shouldBe < node.right.level) {
				node.right.level = shouldBe;
			}
		}
		return node;
	}
	
	/**
	 * "Красивая" печать дерева
	 */
	public void print() {
		print(root);
		System.out.println("-----------------------");
	}
	
	/**
	 * Вспомогательная рекурсивная функция "красивой" печати дерева
	 * @param node
	 */
	private void print(Node node) {
		if (node.level != 0) {
			print(node.left);
			char[] fill = new char[2 * node.level];
			Arrays.fill(fill, ' ');
			System.out.print(String.valueOf(fill));
			System.out.println("<" + node.key + "," + node.value + "> (" + node.level + ")");
			print(node.right);
		}
	}
	
	public static void main(String[] args) {
		AATree<Integer, Integer> tree = new AATree<Integer, Integer>();
		for (int i = 1; i <= 10; ++i) {
			tree.put(2*i, 2*i);
		}
		tree.put(15, 15);
		tree.print();
		
		for (int i = 1; i <= 10; ++i) {
			tree.remove(2*i);
		}
		tree.print();
	}
}
