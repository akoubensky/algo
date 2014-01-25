import java.util.Iterator;
import java.util.Stack;

/**
 * Простая реализация двоичного дерева поиска по ключу.
 * Поддерживаются операции:
 * - search(key) Поиск значения по ключу
 * - get(key) То же, что search
 * - add(key, value) Добавление нового элемента в лист дерева
 * - put(key, Value) То же, что add
 * - addRoot(key, value) Добавление нового элемента в корень дерева
 * - remove(key) Удаление узла по ключу
 * - iterator() Итерация узлов дерева
 * - iterator(from, to) Итерация узлов в заданном диапазоне
 * - height() Высота дерева
 * - buildOptimalTree(array) Построение оптимального по структуре дерева
 *                           по заданному упорядоченному массиву элементов
 *
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
class BinSearchTree<K extends Comparable<K>, V> implements Iterable<V> {
	/**
	 * Реализация узла дерева (без ссылки на родительский узел)
	 *
	 * @param <K> Тип ключа
	 * @param <V> Тип хранимого значения
	 */
	private static class Node<K extends Comparable<K>, V> {
		K key;
		V value;
		Node<K, V> less = null;
		Node<K, V> more = null;
		
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	// Корень дерева
	private Node<K, V> root = null;

	/**
	 * Поиск по ключу
	 * @param key Ключ поиска
	 * @return Хранимое значение или null, если такого ключа нет в дереве
	 */
	public V search(K key) {
	    Node<K, V> current = root;
	    while (current != null && !key.equals(current.key)) {
	    	int compare = key.compareTo(current.key);
	    	if (compare < 0) {
	    		current = current.less; 
	    	} else {
	    		current = current.more;
	    	}
	    }
	    return current == null ? null : current.value;
	}

	/** 
	 * Добавление нового узла в лист дерева
	 * @param key Ключ
	 * @param value Добавляемое значение
	 * @return Значение, ранее содержащееся в узле с этим ключом,
	 *         если такое было. Null, если такого ключа не было в дереве.
	 */
	public V add(K key, V value) {
		Node<K, V> pred = null;
		Node<K, V> next = root;
		while (next != null) {
		    if (key.equals(next.key)) {
		    	V oldValue = next.value; 
		    	next.value = value; 
		    	return oldValue;
		    }
		    pred = next;
		    if (key.compareTo(next.key) < 0) {
		      next = next.less;
		    } else {
		      next = next.more;
		    }
		}
		Node<K, V> newNode = new Node<K, V>(key, value);
		if (pred == null) { 
			root = newNode; 
		} else if (key.compareTo(pred.key) < 0) { 
			pred.less = newNode; 
		} else { 
			pred.more = newNode; 
		}
		return null;
	}
	
	/**
	 * Направление прохождения по дереву
	 */
	private enum Dir {
		LEFT, RIGHT
	}
	
	/**
	 * Добавление нового узла в корень дерева. Если ранее в дереве уже был
	 * узел с таким ключом, то он уничтожается (перемещается в корень).
	 * @param key Ключ
	 * @param value Значение
	 * @return "Старое" значение узла с заданным ключом, если оно было в дереве.
	 *         Null, если не было.
	 */
	public V addRoot(K key, V value) {
		// Удаление "старого" узла
		V oldValue = remove(key);
		
		// Создание нового корня
		Node<K, V> newRoot = new Node<K, V>(key, value);
		Node<K, V> left = newRoot;
		
		// Прохождение вниз по дереву
		Node<K, V> node = root;
		Dir leftBranch = Dir.LEFT;
		Node<K, V> right = newRoot;
		Dir rightBranch = Dir.RIGHT;
		while (node != null) {
			if (key.compareTo(node.key) < 0) {
				// Go left
				if (rightBranch == Dir.LEFT) {
					right.less = node; 
				} else {
					right.more = node;
				}
				right = node;
				rightBranch = Dir.LEFT;
				node = node.less;
			} else {
				// Go right
				if (leftBranch == Dir.LEFT) {
					left.less = node; 
				} else {
					left.more = node;
				}
				left = node;
				leftBranch = Dir.RIGHT;
				node = node.more;
			}
		}
		
		// Завершение построения дерева
		if (leftBranch == Dir.LEFT) left.less = null; else left.more = null;
		if (rightBranch == Dir.LEFT) right.less = null; else right.more = null;
		root = newRoot;
		return oldValue;
	}
	
	/**
	 * Удаление узла по заданному ключу
	 * @param key Ключ
	 * @return "Старое" значение узла, если оно ранее было в дереве, иначе null.
	 */
	public V remove(K key) {
		// 1. Поиск удаляемого узла и хранящегося в нем значения.
		Node<K, V> curr = root, pred = null;
		boolean left = false;  // предок слева?
		while (curr != null && !key.equals(curr.key)) {
		    pred = curr;
		    if (key.compareTo(curr.key) < 0) {
		    	left = true; curr = curr.less;
		    } else {
		    	left = false; curr = curr.more;
		    }
		}
		if (curr == null) return null;
		V returnValue = curr.value;
		
		// 2. Поиск "замены" в структуре дерева – ближайшего справа узла.
		if (curr.less != null && curr.more != null) {
		    Node<K, V> toRemove = curr;
		    pred = curr; left = false; curr = curr.more;
		    while (curr.less != null) { 
		    	left = true; pred = curr; curr = curr.less;
		    }
		    toRemove.key = curr.key; toRemove.value = curr.value;
		}
		
		// 3. Собственно удаление узла.
		Node<K, V> replacement = curr.more == null ? curr.less : curr.more;
		if (pred == null) {
		    root = replacement;
		} else if (left) {
		    pred.less = replacement;
		} else {
		    pred.more = replacement;
		}
		return returnValue;
	}
	
	/**
	 * Реализация итератора двоичного дерева с помощью стека.
	 * В общем случае задаются "границы итерации" - минимальное и максимальное 
	 * значения (максимальное при этом не входит в диапазон итерации)
	 *
	 * @param <K> Тип ключа
	 * @param <V> Тип значения
	 */
	private static class StackTreeIterator<K extends Comparable<K>, V> implements Iterator<V> {
		Stack<Node<K, V>> stack = new Stack<Node<K, V>>();
		Node<K, V> nextNode = null; 
		K to;

		/**
		 * Создание итератора всех узлов
		 * @param root Корень дерева
		 */
		public StackTreeIterator(Node<K, V> root) { this(root, null, null); }
		
		/**
		 * Создание итератора узлов из заданного диапазона [from, to)
		 * @param root Корень дерева
		 * @param from Минимальное значение в итерации или null, если не задано
		 * @param to Максимальное значение в итерации (не входит в итерацию)
		 *           или null, если не задано
		 */
		public StackTreeIterator(Node<K, V> root, K from, K to) {
			if (root != null) {
				nextNode = firstNode(root, from); 
				this.to = to;
			}
		}

		/**
		 * Поиск минимального ключа в дереве, большего или равного заданному (если задано)
		 * @param node
		 * @param key
		 * @return
		 */
		private Node<K, V> firstNode(Node<K, V> node, K key) {
			while (true) {
				if ((key == null ? true : key.compareTo(node.key) <= 0)
						&& node.less != null) {
					stack.push(node); node = node.less;
				} else if (node.more != null) {
					node = node.more;
				} else {
					break;
				}
			}
			return key != null && key.compareTo(node.key) > 0 ? nextNode(node) : node;
		}

		/**
		 * Поиск следующего за заданным узла
		 * @param node Предыдущий узел
		 * @return Следующий узел
		 */
		private Node<K, V> nextNode(Node<K, V> node) {
			if (node.more != null) { 
				return firstNode(node.more, null); 
			}
			return stack.empty() ? null : stack.pop();
		}
		
		@Override
		public boolean hasNext() {
			return nextNode != null && (to == null || nextNode.key.compareTo(to) < 0);
		}
	  
		@Override
		public V next() {
			if (!hasNext()) throw new IllegalStateException();
			V element = nextNode.value;
			nextNode = nextNode(nextNode);
			return element;
		}
	  
		@Override
		public void remove() {
			// Удаление узла разрушит структуру стека, поэтому операция не реализована
			throw new UnsupportedOperationException(); 
		}
	}

	@Override
	public Iterator<V> iterator() { 
		return new StackTreeIterator<K, V>(root); 
	}

	/**
	 * Операция выдает итератор узлов из заданного диапазона [from, to) 
	 * @param from Минимальное значение ключа в итерации
	 * @param to Максимальное значение ключа в итерации (не входит в итерацию)
	 * @return Итератор
	 */
	public Iterator<V> iterator(K from, K to) {
		return new StackTreeIterator<K, V>(root, from, to);
	}
  
	// Реализация некоторых функций интерфейса Map
	public V get(K key) { return search(key); }
	public V put(K key, V value) { return add(key, value); }
	
	/**
	 * Вычисление высоты дерева
	 * @return Высота дерева
	 */
	public int height() {
		return height(root);
	}
	
	/**
	 * Вспомогательная рекурсивная функция, вычисляющая высоту поддерева.
	 * @param node Корень поддерева
	 * @return Высота поддерева
	 */
	public int height(Node<K, V> node) {
		if (node == null) {
			return 0;
		}
		return Math.max(height(node.less), height(node.more)) + 1;
	}
	
	/**
	 * Построение оптимального дерева по заданному массиву ключей.
	 * @param keys Упорядоченный массив ключей
	 * @return Оптимальное дерево, содержащее заданный набор ключей.
	 *         Со всеми ключами ассоциируются пустые значения.
	 */
	public static <K extends Comparable<K>, V> BinSearchTree<K, V> 
				buildOptimalTree(K[] keys) {
		// Полагаем массив ключей keys отсортированным
		BinSearchTree<K, V> t = new BinSearchTree<K, V>();
		t.root = buildOptimalTree(keys, 0, keys.length);
		return t;
	}

	/**
	 * Вспомогательная функция, реализующая построение оптимального дерева
	 * по заданному набору ключей с индексами [begin, end)
	 * @param keys Весь упорядоченный массив ключей
	 * @param begin начальный индекс в массиве
	 * @param end конечный индекс в массиве
	 * @return
	 */
	private static <K extends Comparable<K>, V> Node<K, V>
     			buildOptimalTree(K[] keys, int begin, int end) {
		if (begin == end) return null;
		int mid = (begin + end) / 2;
		Node<K, V> root = new Node<K, V>(keys[mid], null);
		root.less = buildOptimalTree(keys, begin, mid);
		root.more = buildOptimalTree(keys, mid+1, end);
		return root;
	}
	
	/**
	 * Тестирование некоторых функций
	 * @param args
	 */
	public static void main(String[] args) {
		// Построение дерева с произвольно взятым набором ключей
		BinSearchTree<Integer, String> tree = new BinSearchTree<Integer, String>();
		
		tree.put(6, "six");
		tree.put(3, "three");
		tree.put(9, "nine");
		tree.put(2, "two");
		tree.put(5, "five");
		tree.put(7, "seven");
		tree.put(10, "ten");
		tree.put(1, "one");
		tree.put(8, "eight");
		tree.addRoot(4, "four");
		// Итерация и печать элементов дерева
		for (String s : tree) {
			System.out.print(s + " ");
		}
		System.out.println();
		
		// Итерация в заданном диапазоне ключей
		for (Iterator<String> it = tree.iterator(3, 9); it.hasNext(); ) {
			String s = it.next();
			System.out.print(s + " ");
		}
		System.out.println();
		
		// Построение оптимального дерева
		Integer[] keys = new Integer[] {
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		};
		BinSearchTree<Integer, Integer> optTree = buildOptimalTree(keys);
		for (Integer i : keys) optTree.put(i, i);
		
		// Итерация построенного оптимального дерева
		for (Integer s : optTree) {
			System.out.print(" " + s);
		}
		System.out.println();
		
		// Вычисление высоты построенного дерева
		System.out.println("Height = " + optTree.height());
	}
}