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
		
		// Проверяем сбалансированность и исправляем, если нужно.
		if (node.left.color.isBlack() && node.right.color.isRed()) {
			node = pivotLeft(node);
		}
		if (node.left.color.isRed() && node.left.left.color.isRed()) {
			node = pivotRight(node);
		}
		if (node.left.color.isRed() && node.right.color.isRed()) {
			node.color = Color.RED;
			node.left.color = Color.BLACK;
			node.right.color = Color.BLACK;
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
	
	/**
	 * Элемент списка пройденных узлов. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ
	 * к полям объектов этого класса осуществляется непосредственно.
	 * 
	 * Элемент списка будет содержать ссылку на узел дерева и направление
	 * прохождения этого узла при поиске места для нового элемента.
	 *
	 * @param <E> тип элементов списка
	 */
	private static class ListNode<E> {
		// Значение элемента списка:
		E info;
		// Ссылка на следующий элемент списка:
		ListNode<E> next;

		/**
		 * Конструктор элемента списка.
		 * @param info элемент.
		 * @param d направление.
		 * @param next ссылка на следующий элемент списка.
		 */
		ListNode(E info, ListNode<E> next) {
			this.info = info; this.next = next;
		}
	}

	/**
	 * Функция поворота меняет местами два узла дерева: p1 и p2
	 * (p1 поднимается наверх, p2 опускается вниз).
	 * @param p1 нижний узел пары узлов.
	 * @param p2 верхний узел пары узлов.
	 * @param p3 предок узла p2 (может отсутствовать).
	 */
	private void pivot(ListNode<Node> p1,
			ListNode<Node> p2,
			ListNode<Node> p3) {
		// 1. Перевешиваем узел p1 наверх
		if (p3 == null) {
			root = p1.info;
		} else if (p3.info.left == p2.info) {
			p3.info.left = p1.info;
		} else {
			p3.info.right = p1.info;
		}

		// Изменяем остальные ссылки.
		if (p2.info.left == p1.info) {
			p2.info.left = p1.info.right;
			p1.info.right = p2.info;
		} else {
			p2.info.right = p1.info.left;
			p1.info.left = p2.info;
		}
	}

//	public V put(K key, V value) {
//		// Проверка: ключ не может быть пустым.
//		if (key == null) throw new IllegalArgumentException("null key");
//
//		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
//		ListNode<Node> path = null;
//		Node current = root;
//		boolean stepLeft = true;
//		while (current != SENTINEL) {
//			if (key.compareTo(current.key) == 0) {
//				// Ключ найден; заменяем старое значение и заканчиваем работу.
//				V oldValue = current.value;
//				current.value = value;
//				return oldValue;
//			} else if (key.compareTo(current.key) < 0) {
//				path = new ListNode<Node>(current, path);
//				stepLeft = true;
//				current = current.left;
//			} else {
//				path = new ListNode<Node>(current, path);
//				stepLeft = false;
//				current = current.right;
//			}
//		}
//
//		// 2. Вставляем новый (красный) узел в дерево.
//		Node newNode = new Node(key, value);
//		if (path == null) {
//			root = newNode;
//			newNode.color = Color.BLACK;
//			return null;
//		} else if (stepLeft) {
//			path.info.left = newNode;
//		} else {
//			path.info.right = newNode;
//		}
//
//		// 3. Проходим вверх по дереву и проверяем цвета узлов.
//		//    Три последовательных элемента пути - p1 (красный), p2 и path.
//		ListNode<Node> p1 = new ListNode<Node>(newNode, path);
//		ListNode<Node> p2 = path;
//		path = path.next;
//		while (p2.info.color == Color.RED) {
//			if (path == null) {
//				// Добрались до корня дерева
//				p2.info.color = Color.BLACK;
//			} else {
//				Node uncle = path.info.left == p2.info ? path.info.right : path.info.left;
//				if (uncle.color == Color.BLACK) {
//					// Случай "черного дяди"
//					if (p2.info.left == p1.info && path.info.left == p2.info ||
//						p2.info.right == p1.info && path.info.right == p2.info) {
//						// простой поворот
//						pivot(p2, path, path.next);
//						p2.info.color = Color.BLACK;
//					} else {
//						// двойной поворот
//						pivot(p1, p2, path);
//						pivot(p1, path, path.next);
//						p1.info.color = Color.BLACK;
//					}
//					path.info.color = Color.RED;
//					break;
//				} else {
//					// Случай "красного дяди"
//					uncle.color = Color.BLACK;
//					p2.info.color = Color.BLACK;
//					path.info.color = Color.RED;
//					p1 = path;
//					p2 = path.next;
//					if (p2 == null) {
//						// Добрались до корня
//						p1.info.color = Color.BLACK;
//						break;
//					} else {
//						path = p2.next;
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	public V remove(K key) {
		// Проверка: ключ не может быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
		ListNode<Node> path = null;
		Node current = root;
		while (current != SENTINEL) {
			if (key.compareTo(current.key) == 0) {
				// Ключ найден; выходим из цикла.
				break;
			} else if (key.compareTo(current.key) < 0) {
				path = new ListNode<Node>(current, path);
				current = current.left;
			} else {
				path = new ListNode<Node>(current, path);
				current = current.right;
			}
		}

		if (current == SENTINEL) return null;

		V oldValue = current.value;

		// 2. Производим удаление ключа
		if (current.left != SENTINEL && current.right != SENTINEL) {
			Node safe = current;
			path = new ListNode<Node>(current, path);
			current = current.right;
			while (current.left != SENTINEL) {
				path = new ListNode<Node>(current, path);
				current = current.left;
			}
			safe.key = current.key;
			safe.value = current.value;
		}

		Node subst = (current.left == SENTINEL ? current.right : current.left);
		if (path == null) {
			root = subst;
			root.color = Color.BLACK;
			return oldValue;
		}
		if (path.info.right == current) {
			path.info.right = subst;
		} else {
			path.info.left = subst;
		}
		
		// 3. Производим балансировку
		if (current.color == Color.RED) {
			// Удаленный узел - красный, коррекция не нужна
		} else if (subst.color == Color.RED) {
			// Коррекция состоит в простом перекрашивании узла
			subst.color = Color.BLACK;
		} else {
			current = subst;
			while (current != null) {
				if (path == null) {
					// Дошли до корня
					break;
				}
				Node brother = path.info.left == current ? path.info.right : path.info.left;
				ListNode<Node> listBrother = new ListNode<Node>(brother, path);
				if (brother.color == Color.RED) {
					pivot(listBrother, path, path.next);
					brother.color = Color.BLACK;
					path.info.color = Color.RED;
					listBrother.next = path.next;
					path.next = listBrother;
				} else if (brother.left.color == Color.BLACK && brother.right.color == Color.BLACK) {
					brother.color = Color.RED;
					if (path.info.color == Color.RED) {
						path.info.color = Color.BLACK;
						break;
					} else {
						current = path.info;
						path = path.next;
					}
				} else {
					Node nephew = path.info.right == brother ? brother.right : brother.left;
					if (nephew.color == Color.RED) {
						pivot(listBrother, path, path.next);
						nephew.color = Color.BLACK;
						brother.color = path.info.color;
						path.info.color = Color.BLACK;
						break;
					} else {
						ListNode<Node> listNephew = new ListNode<Node>(nephew, listBrother);
						pivot(listNephew, listBrother, path);
						brother.color = Color.RED;
						nephew.color = Color.BLACK;
					}
				}
			}
		}

		return oldValue;
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
