
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
public class AVLTreeH<K extends Comparable<K>, V> extends BSTree<K, V> {
	
	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	private class Node extends BSNode {
		// Высота соответствующего поддерева:
		int height;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param height высота узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, int height, Node left, Node right) {
			super(key, value, left, right);
			this.height = height;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) {
			super(key, value);
			this.height = 1;
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		V oldValue = get(key);
		root = put(key, value, (Node)root);
		return oldValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		V oldValue = get(key);
		root = remove(key, (Node)root);
		return oldValue;
	}
	
	/**
	 * Вставка новой пары (ключ, значение) в поддерево, корнем которого является
	 * заданный узел. При необходимости производится балансировка дерева.
	 * 
	 * @param key	Ключ вставляемой пары.
	 * @param value	Значение во вставляемой паре.
	 * @param node	Узел, являющийся корнем дерева, в которое производится вставка.
	 * @return		Модифицированное и сбалансированное дерево.
	 */
	@SuppressWarnings("unchecked")
	private Node put(K key, V value, Node node) {
		if (node == null) {
			return new Node(key, value);
		} else {
			if (key.compareTo(node.key) < 0) {
				// Вставка в левое поддерево
				node.left = put(key, value, (Node)node.left);
			} else if (key.compareTo(node.key) > 0) {
				// Вставка в правое поддерево
				node.right = put(key, value, (Node)node.right);
			} else {
				// Замена значения в текущем узле
				node.value = value;
				return node;
			}
			// После вставки, возможно, необходимо выполнить балансировку узла.
			if (Math.abs((node.left == null ? 0 : ((Node)node.left).height) -
					     (node.right == null ? 0 : ((Node)node.right).height)) == 2) {
				node = balance(node);
			}
			// Пересчитываем высоту вновь образованного сбалансированного дерева.
			recalcHeight(node);
			return node;
		}
	}
	
	/**
	 * Удаление узла по заданному ключу из поддерева, корнем которого является
	 * заданный узел. При необходимости производится балансировка дерева.
	 * 
	 * @param key	Ключ удаляемого узла.
	 * @param node	Корень поддерева, из которого происходит удаление.
	 * @return		Модифицированное сбалансированное дерево.
	 */
	@SuppressWarnings("unchecked")
	private Node remove(K key, Node node) {
		if (node == null) {
			return null;
		} else if (key.compareTo(node.key) == 0) {
			// Узел найден. Смотрим, можем ли мы его удалить.
			if (node.left == null) {
				// Узел заменяется его правым поддеревом
				return (Node)node.right;
			} else if (node.right == null) {
				// Узел заменяется его левым поддеревом
				return (Node)node.left;
			} else {
				// Ищем узел, который можно поставить на место удаляемого.
				Node current = (Node)node.right;
				while (current.left != null) current = (Node)current.left;
				// Копируем узел
				node.key = current.key;
				node.value = current.value;
				// Теперь удаляем вместо исходного найденный узел.
				node.right = remove(current.key, (Node)node.right);
			}
		} else if (key.compareTo(node.key) < 0) {
			// Удаляем узел из левого поддерева
			node.left = remove(key, (Node)node.left);
		} else {
			// Удаляем узел из правого поддерева
			node.right = remove(key, (Node)node.right);
		}
		// После удаления, возможно, необходимо выполнить балансировку узла.
		if (Math.abs((node.left == null ? 0 : ((Node)node.left).height) -
			     (node.right == null ? 0 : ((Node)node.right).height)) == 2) {
			node = balance(node);
		}
		// Пересчитываем высоту вновь образованного сбалансированного дерева.
		recalcHeight(node);
		return node;
	}
	
	/**
	 * Балансирует поддерево, корнем которого является заданный разбалансированный узел.
	 * 
	 * @param node	Узел, баланс которого больше единицы.
	 * @return		Результат балансировки.
	 */
	@SuppressWarnings("unchecked")
	private Node balance(Node node) {
		// Высота левого поддерева.
		int heightLeft = node.left == null ? 0 : ((Node)node.left).height;
		// Высота правого поддерева.
		int heightRight = node.right == null ? 0 : ((Node)node.right).height;
		
		if (heightLeft > heightRight) {
			Node child = (Node)node.left;
			if ((child.left == null ? 0 : ((Node)child.left).height) <
			    (child.right == null ? 0 : ((Node)child.right).height)) {
				// Необходим двойной поворот
				node.left = pivotRight(child);
			}
			return pivotLeft(node);
		} else {
			Node child = (Node)node.right;
			if ((child.left == null ? 0 : ((Node)child.left).height) >
			    (child.right == null ? 0 : ((Node)child.right).height)) {
				// Необходим двойной поворот
				node.right = pivotLeft(child);
			}
			return pivotRight(node);
		}
	}

	/**
	 * Реализует &quot;левый&quot; поворот вокруг заданного узла дерева с пересчетом
	 * высот узлов, участвующих в повороте.
	 * 
	 * @param node	Корневой узел, вокруг которого происходит поворот.
	 * @return		Результирующее дерево после поворота.
	 */
	@SuppressWarnings("unchecked")
	private Node pivotLeft(Node node) {
		Node child = (Node)node.left;
		node.left = child.right;
		child.right = node;
		recalcHeight(node);
		recalcHeight(child);
		return child;
	}
	
	/**
	 * Реализует &quot;правый&quot; поворот вокруг заданного узла дерева с пересчетом
	 * высот узлов, участвующих в повороте.
	 * 
	 * @param node	Корневой узел, вокруг которого происходит поворот.
	 * @return		Результирующее дерево после поворота.
	 */
	@SuppressWarnings("unchecked")
	private Node pivotRight(Node node) {
		Node child = (Node)node.right;
		node.right = child.left;
		child.left = node;
		recalcHeight(node);
		recalcHeight(child);
		return child;
	}
	
	/**
	 * Пересчитывает высоту поддерева, корнем которого является заданный узел.
	 * 
	 * @param node	Корневой узел, вокруг которого происходит поворот.
	 */
	@SuppressWarnings("unchecked")
	private void recalcHeight(Node node) {
		node.height = 1 + Math.max(node.left == null ? 0 : ((Node)node.left).height,
                node.right == null ? 0 : ((Node)node.right).height);
	}
	
	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		AVLTreeH<Integer, Integer> tree = new AVLTreeH<Integer, Integer>();
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
