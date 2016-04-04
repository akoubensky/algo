import java.util.Iterator;
import java.util.Stack;

/**
 * Простая реализация двоичного дерева поиска по ключу.
 * Поддерживаются операции:
 * - get(key) Поиск значения по ключу
 * - put(key, value) Добавление нового элемента в лист дерева
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
	 */
	private class Node {
		K key;
		V value;
		Node less;
		Node more;
		
		public Node(K key, V value) {
			this(key, value, null, null);
		}
		
		public Node(K key, V value, Node less, Node more) {
			this.key = key;
			this.value = value;
			this.less = less;
			this.more = more;
		}
	}

	// Корень дерева
	private Node root = null;
	
	//----------------------------------------------------------
	// Основные интерфейсные функции
	//----------------------------------------------------------

	/**
	 * Поиск по ключу
	 * @param key Ключ поиска
	 * @return Хранимое значение или null, если такого ключа нет в дереве
	 */
	public V get(K key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
	    Node current = root;
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
	public V put(K key, V value) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		V oldValue = get(key);
		root = put(root, key, value);
		return oldValue;
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
		if (key == null) {
			throw new IllegalArgumentException();
		}
		// Поиск значения узла по ключу
		V oldValue = get(key);
		
		root = new Node(key, value, lessTree(root, key), moreTree(root, key));
		return oldValue;
	}
	
	/**
	 * Удаление узла по заданному ключу
	 * @param key Ключ
	 * @return "Старое" значение узла, если оно ранее было в дереве, иначе null.
	 */
	public V remove(K key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		V oldValue = get(key);
		root = remove(root, key);
		return oldValue;
	}
	
	@Override
	public Iterator<V> iterator() { 
		return new StackTreeIterator(root); 
	}

	/**
	 * Операция выдает итератор узлов из заданного диапазона [from, to) 
	 * @param from Минимальное значение ключа в итерации
	 * @param to Максимальное значение ключа в итерации (не входит в итерацию)
	 * @return Итератор
	 */
	public Iterator<V> iterator(K from, K to) {
		return new StackTreeIterator(root, from, to);
	}
  
	/**
	 * Вычисление высоты дерева
	 * @return Высота дерева
	 */
	public int height() {
		return height(root);
	}
	
	/**
	 * Построение оптимального дерева по заданному массиву ключей.
	 * @param keys Упорядоченный массив ключей
	 * @return Оптимальное дерево, содержащее заданный набор ключей.
	 *         Со всеми ключами ассоциируются пустые значения.
	 */
	public static <K extends Comparable<K>, V> BinSearchTree<K, V> buildOptimalTree(K[] keys) {
		// Полагаем массив ключей keys отсортированным
		BinSearchTree<K, V> t = new BinSearchTree<>();
		t.root = buildOptimalTree(t, keys, 0, keys.length);
		return t;
	}

	//--------------------------------------------------------------
	// Private functions
	//--------------------------------------------------------------
	
	/**
	 * Рекурсивно добавляет новую ассоциативную пару из ключа и значения
	 * в дерево, корень которого назодится в заданном узле.
	 * 
	 * @param node	Корень дерева
	 * @param key	Ключ
	 * @param value	Значение
	 * @return		Поддерево с добавленным узлом
	 */
	private Node put(Node node, K key, V value) {
		if (node == null) {
			return new Node(key, value);
		} else if (node.key.equals(key)) {
			node.value = value;
		} else if (node.key.compareTo(key) > 0) {
			node.less = put(node.less, key, value);
		} else {
			node.more = put(node.more, key, value);
		}
		return node;
	}

	/**
	 * Строит дерево из узлов заданного дерева, выбирая те из них,
	 * которые меньше заданного.
	 * 
	 * @param node	Исходное дерево.
	 * @param key	Заданное значение для фильтрации.
	 * @return		Результирующее отфильтрованное дерево.
	 */
	private Node lessTree(Node node, K key) {
		if (node == null) {
			return null;
		} else if (key.equals(node.key)) {
			// Просто левое поддерево
			return node.less;
		} else if (node.key.compareTo(key) < 0) {
			// Левое поддерево берется целиком, а из правого выбираются узлы,
			// меньшие заданного значения (рекурсивным применением функции)
			return new Node(node.key, node.value, node.less, lessTree(node.more, key));
		} else {
			// Фильтруем только левое поддерево
			return lessTree(node.less, key);
		}
	}
	
	/**
	 * Строит дерево из узлов заданного дерева, выбирая те из них,
	 * которые больше заданного.
	 * 
	 * @param node	Исходное дерево.
	 * @param key	Заданное значение для фильтрации.
	 * @return		Результирующее отфильтрованное дерево.
	 */
	private Node moreTree(Node node, K key) {
		if (node == null) {
			return null;
		} else if (key.equals(node.key)) {
			// Просто правое поддерево
			return node.more;
		} else if (node.key.compareTo(key) > 0) {
			// Правое поддерево берется целиком, а из левого выбираются узлы,
			// большие заданного значения (рекурсивным применением функции)
			return new Node(node.key, node.value, moreTree(node.less, key), node.more);
		} else {
			// Фильтруем только правое поддерево
			return moreTree(node.more, key);
		}
	}
	
	/**
	 * Удаляет из дерева узел с заданным значением ключа.
	 * 
	 * @param node	Корень дерева
	 * @param key	Заданный ключ
	 * @return		Модифицированное дерево.
	 */
	private Node remove(Node node, K key) {
		if (node == null) {
			return null;
		} else if (node.key.equals(key)) {
			// Удаляем корень дерева
			if (node.less == null) {
				// Левого поддерева нет, просто возвращаем правое поддерево
				return node.more;
			} else if (node.more == null) {
				// Правого поддерева нет, просто возвращаем левое поддерево
				return node.less;
			} else {
				// Оба поддерева есть, из правого поддерева удаляем узел
				// с минимальным значением ключа и переносим информацию из него в корень
				node.more = removeMin(node.more, node);
			}
		} else if (node.key.compareTo(key) < 0) {
			// Рекурсивное удаление из правого поддерева
			node.more = remove(node.more, key);
		} else {
			// Рекурсивное удаление из левого поддерева
			node.less = remove(node.less, key);
		}
		return node;
	}
	
	/**
	 * Удаляет из дерева узел с минимальным значением ключа.
	 * 
	 * @param node		Корень дерева
	 * @param removed	Узел, в который переписывается информация из удаленного узла
	 * @return			Модифицированное дерево
	 */
	private Node removeMin(Node node, Node removed) {
		if (node.less == null) {
			// Минимальный узел найден, переписываем информацию из него.
			removed.key = node.key;
			removed.value = node.value;
			return node.more;
		} else {
			node.less = removeMin(node.less, removed);
			return node;
		}
	}
	
	
	/**
	 * Реализация итератора двоичного дерева с помощью стека.
	 * В общем случае задаются "границы итерации" - минимальное и максимальное 
	 * значения (максимальное при этом не входит в диапазон итерации)
	 */
	private class StackTreeIterator implements Iterator<V> {
		Stack<Node> stack = new Stack<>();
		K to;

		/**
		 * Создание итератора всех узлов
		 * @param root Корень дерева
		 */
		public StackTreeIterator(Node root) { this(root, null, null); }
		
		/**
		 * Создание итератора узлов из заданного диапазона [from, to)
		 * @param root Корень дерева
		 * @param from Минимальное значение в итерации или null, если не задано
		 * @param to Максимальное значение в итерации (не входит в итерацию)
		 *           или null, если не задано
		 */
		public StackTreeIterator(Node root, K from, K to) {
			toStack(root, from); 
			this.to = to;
		}

		@Override
		public boolean hasNext() {
			return !stack.empty() && (to == null || stack.peek().key.compareTo(to) < 0);
		}
	  
		@Override
		public V next() {
			if (!hasNext()) throw new IllegalStateException();
			V element = stack.peek().value;
			toStack(stack.pop().more, null);
			return element;
		}
	  
		/**
		 * Поиск минимального ключа в дереве, большего или равного заданному (если задано).
		 * Узлы, находящиеся на пути поиска, добавляются в стек.
		 * @param node  Начальный узел для поиска
		 * @param key   Ключ поиска
		 */
		private void toStack(Node node, K key) {
			if (node == null) {
				return;
			}
			if (key != null && node.key.compareTo(key) < 0) {
				toStack(node.more, key);
			} else if (key == null || node.key.compareTo(key) >= 0) {
				stack.push(node);
				toStack(node.less, key);
			}
		}
	}

	/**
	 * Вспомогательная рекурсивная функция, вычисляющая высоту поддерева.
	 * @param node Корень поддерева
	 * @return Высота поддерева
	 */
	private int height(Node node) {
		if (node == null) {
			return 0;
		}
		return Math.max(height(node.less), height(node.more)) + 1;
	}
	
	/**
	 * Вспомогательная функция, реализующая построение оптимального дерева
	 * по заданному набору ключей с индексами [begin, end)
	 * @param keys Весь упорядоченный массив ключей
	 * @param begin начальный индекс в массиве
	 * @param end конечный индекс в массиве
	 * @return  Корневой узел построенного дерева.
	 */
	private static <K extends Comparable<K>, V> BinSearchTree<K, V>.Node
			buildOptimalTree(BinSearchTree<K, V> t, K[] keys, int begin, int end) {
		if (begin == end) return null;
		int mid = (begin + end) / 2;
		BinSearchTree<K, V>.Node root = t.new Node(keys[mid], null);
		root.less = buildOptimalTree(t, keys, begin, mid);
		root.more = buildOptimalTree(t, keys, mid + 1, end);
		return root;
	}
	
	//------------------------------------------------------------
	// Тестирование
	//------------------------------------------------------------
	
	/**
	 * Тестирование некоторых функций
	 * @param args Не используется
	 */
	public static void main(String[] args) {
		// Построение дерева с произвольно взятым набором ключей
		BinSearchTree<Integer, String> tree = new BinSearchTree<>();
		
		int[] keys = {6, 3, 9, 2, 5, 7, 10, 1, 8};
		String[] values = {"six", "three", "nine", "two", "five", "seven", "ten", "one", "eight"};
		
		for (int i = 0; i < keys.length; i++) {
			tree.put(keys[i],  values[i]);
		}
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
		
		// Удаление узлов
		for (int key : keys) {
			System.out.print(tree.remove(key) + " ");
		}
		System.out.println();
		for (String s : tree) {
			System.out.print(s + " ");
		}
		System.out.println();
		
		// Построение оптимального дерева
		Integer[] optKeys = new Integer[] {
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		};
		BinSearchTree<Integer, Integer> optTree = buildOptimalTree(optKeys);
		for (Integer i : optKeys) optTree.put(i, i);
		
		// Итерация построенного оптимального дерева
		for (Integer s : optTree) {
			System.out.print(" " + s);
		}
		System.out.println();
		
		// Вычисление высоты построенного дерева
		System.out.println("Height = " + optTree.height());
	}
}