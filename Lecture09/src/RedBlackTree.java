import java.util.Arrays;


/**
 * Реализация основной операции красно-черного дерева -
 * операции вставки новой ассоциативной пары <ключ, значение>.
 * 
 * Операция удаления по ключу не реализована, так как корректировка
 * дерева после удаления производится сложнее, чем при вставке.
 * 
 * (c) http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class RedBlackTree<K extends Comparable<K>, V> {
	/**
	 * Цвет узлов дерева
	 */
	private static enum Color {
		RED, BLACK;
		
		Color flip() {
			switch (this) {
			case RED: return BLACK;
			case BLACK: return RED;
			}
			return null;
		}
		
		boolean isRed() { return this == RED; }
		boolean isBlack() { return this == BLACK; }
	}

	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	private class Node {
		// Ссылки на левое и правое поддеревья:
		Node left, right;
		// Ключ:
		K key;
		// Значение:
		V value;
		// Цвет узла:
		Color color;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param color цвет узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, Color color, Node left, Node right) {
			this.key = key; this.value = value; this.color = color;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) { this(key, value, Color.RED, SENTINEL, SENTINEL); }
	}

	// Корень дерева.
	final Node SENTINEL = new Node(null, null, Color.BLACK, null, null);
	Node root = SENTINEL;

	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		return get(key, root);
	}
	
	/**
	 * Добавление в дерево новой ассоциативной пары.
	 * @param key ключ.
	 * @param value значение.
	 * @return значение, которое было ассоциировано раньше с этим ключом
	 *         (если такое значение было).
	 */
	public V put(K key, V value) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		V oldValue = get(key);
		root = put(key, value, root);
		root.color = Color.BLACK;
		return oldValue;
	}
	
	public V remove(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		if (root == SENTINEL) return null;
		V oldValue = get(key);
		if (root.color.isBlack() && root.left.color.isBlack() && root.right.color.isBlack()) {
			root.color = Color.RED;
		}
		root = remove(key, root);
		root.color = Color.BLACK;
		return oldValue;
	}
	
	private V get(K key, Node node) {
		while (node != SENTINEL) {
			int cmp = key.compareTo(node.key);
			if (cmp < 0) node = node.left; else
			if (cmp > 0) node = node.right; else
			return node.value;
		}
		// Ключ не найден
		return null;
	}
	
	private Node put(K key, V value, Node node) {
		if (node == SENTINEL) {
			return new Node(key, value);
		}
		int cmp = key.compareTo(node.key);
		if (cmp < 0) node.left = put(key, value, node.left);
		else if (cmp > 0) node.right = put(key, value, node.right);
		else node.value = value;
		
		return balance(node);
	}
	
	private Node remove(K key, Node node) {
        if (key.compareTo(node.key) < 0)  {
            if (node.left.color.isBlack() && node.left.left.color.isBlack()) {
            	node = moveRedLeft(node);
            }
            node.left = remove(key, node.left);
        } else {
            if (node.left.color.isRed()) {
            	node = pivotRight(node);
            }
            if (key.compareTo(node.key) == 0 && (node.right == SENTINEL)) {
                return SENTINEL;
            }
            if (node.right.color.isBlack() && node.right.left.color.isBlack()) {
                node = moveRedRight(node);
            }
            if (key.compareTo(node.key) == 0) {
            	Node subst = node.right;
            	while (subst.left != SENTINEL) {
            		subst = subst.left;
            	}
                node.key = subst.key;
                node.value = subst.value;
                node.right = deleteMin(node.right);
            } else {
            	node.right = remove(key, node.right);
            }
        }
        return balance(node);
	}
	
    // delete the key-value pair with the minimum key rooted at h
    private Node deleteMin(Node node) { 
        if (node.left == SENTINEL)
            return SENTINEL;

        if (node.left.color.isBlack() && node.left.left.color.isBlack())
            node = moveRedLeft(node);

        node.left = deleteMin(node.left);
        return balance(node);
    }

    // restore red-black tree invariant
    private Node balance(Node node) {
        // assert (h != null);

        if (node.right.color.isRed()) {
        	node = pivotLeft(node);
        }
        if (node.left.color.isRed() && node.left.left.color.isRed()) {
        	node = pivotRight(node);
        }
        if (node.left.color.isRed() && node.right.color.isRed()) {
        	flipColors(node);
        }

        return node;
    }

	/**
	 * Вытаскивает наверх правое поддерево узла при условии, что корень
	 * этого правого поддерева - красный.
	 * 
	 * @param node	Узел - точка поворота
	 * @return		Корень поддерева после поворота
	 */
	private Node pivotLeft(Node node) {
		assert node != SENTINEL && node.right.color == Color.RED;
		
		// Перевешиваем ссылки
		Node child = node.right;
		node.right = child.left;
		child.left = node;
		
		// Перекрашиваем узлы
		child.color = node.color;
		node.color = Color.RED;
		
		return child;
	}
	
	/**
	 * Вытаскивает наверх левое поддерево узла при условии, что корень
	 * этого левого поддерева - красный.
	 * 
	 * @param node	Узел - точка поворота
	 * @return		Корень поддерева после поворота
	 */
	private Node pivotRight(Node node) {
		assert node != SENTINEL && node.left.color == Color.RED;
		
		// Перевешиваем сслки
		Node child = node.left;
		node.left = child.right;
		child.right = node;
		
		// Перекрашиваем узлы
		child.color = node.color;
		node.color = Color.RED;
		
		return child;
	}
	
    // flip the colors of a node and its two children
    private void flipColors(Node node) {
        // h must have opposite color of its two children
        // assert (h != null) && (h.left != null) && (h.right != null);
        // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
        //     || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
        node.color = node.color.flip();
        node.left.color = node.left.color.flip();
        node.right.color = node.right.color.flip();
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node node) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

        flipColors(node);
        if (node.right.left.color.isRed()) { 
            node.right = pivotRight(node.right);
            node = pivotLeft(node);
        }
        return node;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node node) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        flipColors(node);
        if (node.left.left.color.isRed()) { 
            node = pivotRight(node);
        }
        return node;
    }

	/**
	 * "Красивая" печать дерева.
	 */
	public void print() {
		print(root, 0);
		System.out.println("--------------------");
	}

	/**
	 * Вспомогательная функция для "красивой" печати дерева.
	 * @param node корневой узел.
	 * @param indent начальный отступ при печати.
	 */
	private void print(Node node, int indent) {
		if (node != SENTINEL) {
			// Формируем строку из indent пробелов.
			char[] spaces = new char[indent];
			Arrays.fill(spaces, ' ');
			System.out.print(String.valueOf(spaces));

			// Печать узла и его поддеревьев.
			System.out.println(node.color + " <" + node.key + ", " + node.value + ">");
			print(node.left, indent + 2);
			print(node.right, indent + 2);
		}
	}

	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		RedBlackTree<Integer, Integer> tree = new RedBlackTree<Integer, Integer>();
		int[] keys = { 5, 7, 9, 1, 11, 8, 15, 13, 3, 10 };
		for (int key : keys) {
			System.out.println("Added: <" + key + ", " + 2*key + ">");
			tree.put(key, 2*key);
			tree.print();
			System.out.println("----------------------------");
		}

		for (int key : keys) {
			System.out.println("Removed: " + tree.remove(key));
			tree.print();
			System.out.println("----------------------------");
		}
	}
}
