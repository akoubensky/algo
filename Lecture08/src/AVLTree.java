import java.util.Arrays;

/**
 * Реализация основных операций АВЛ-дерева. В узлах дерева хранятся показатели
 * сбалансированности узла - числа, равные 0, +1 или -1 в зависимости от того,
 * равны ли высоты поддеревьев или высота левого поддерева на единицу больше,
 * или высота правого поддерева на единицу больше. Другие значения показателей
 * сбалансированности не допускаются.
 * 
 * Реализованы следующие основные операции:
 * -    V get(K key) - поиск по ключу;
 * -    V put(K key, V value) - добавление или изменение ассоциативной пары;
 * -    V remove(K key) - удаление ассоциативной пары по ключу.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class AVLTree<K extends Comparable<K>, V> {
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
		// Показатель сбалансированности узла:
		short balance;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param balance показатель сбалансированности узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, short balance, Node<K, V> left, Node<K, V> right) {
			this.key = key; this.value = value; this.balance = balance;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) { this(key, value, (short) 0, null, null); }
	}

	/**
	 * Значение направлений при прохождении вниз по дереву.
	 */
	private static enum Direction {
		LEFT, RIGHT
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
		// Направление прохождения узла:
		Direction d;
		// Ссылка на следующий элемент списка:
		ListNode<E> next;

		/**
		 * Конструктор элемента списка.
		 * @param info элемент.
		 * @param d направление.
		 * @param next ссылка на следующий элемент списка.
		 */
		ListNode(E info, Direction d, ListNode<E> next) {
			this.info = info; this.d = d; this.next = next;
		}
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

	/**
	 * Добавление в дерево новой ассоциативной пары.
	 * @param key ключ.
	 * @param value значение.
	 * @return значение, которое было ассоциировано раньше с этим ключом
	 *         (если такое значение было).
	 */
	public V put(K key, V value) {
		// Проверка: ключ не может быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
		ListNode<Node<K, V>> path = null;
		Node<K, V> current = root;
		while (current != null) {
			if (key.compareTo(current.key) == 0) {
				// Ключ найден; заменяем старое значение и заканчиваем работу.
				V oldValue = current.value;
				current.value = value;
				return oldValue;
			} else if (key.compareTo(current.key) < 0) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = current.left;
			} else {
				path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
				current = current.right;
			}
		}

		// 2. Вставляем новый узел в дерево.
		Node<K, V> newNode = new Node<K, V>(key, value);
		if (path == null) {
			root = newNode;
		} else if (path.d == Direction.LEFT) {
			path.info.left = newNode;
		} else {
			path.info.right = newNode;
		}

		// 3. Проходим вверх по дереву и проверяем сбалансированность узлов.
		//    Три последовательных элемента пути - pred1, pred и path.
		ListNode<Node<K, V>> pred = new ListNode<Node<K, V>>(newNode, Direction.LEFT, path);
		ListNode<Node<K, V>> pred1 = null;
		while (path != null) {
			// Корректируем показатель сбалансированности. Если он ноль, то работа
			// по балансировке дерева не нужна.
			if (path.d == Direction.LEFT) {
				if (++path.info.balance == 0) break;
			} else {
				if (--path.info.balance == 0) break;
			}

			if (Math.abs(path.info.balance) < 2) {
				// Дерево пока сбалансировано, но требуется дальнейшая проверка.
				pred1 = pred;
				pred = path;
				path = path.next;
			} else {
				// Обнаружен дисбаланс. Корректируем его простым или двойным вращением.
				if (path.d == pred.d) {
					pivot(pred, path, path.next);
				} else {
					pivot(pred1, pred, path);
					pivot(pred1, path, path.next);
				}
				break;
			}
		}
		return null;
	}

	public V remove(K key) {
		// Проверка: ключ не может быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
		ListNode<Node<K, V>> path = null;
		Node<K, V> current = root;
		while (current != null) {
			if (key.compareTo(current.key) == 0) {
				// Ключ найден; выходим из цикла.
				break;
			} else if (key.compareTo(current.key) < 0) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = current.left;
			} else {
				path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
				current = current.right;
			}
		}

		if (current == null) return null;

		V oldValue = current.value;

		// 2. Производим удаление ключа
		if (current.left != null && current.right != null) {
			Node<K, V> safe = current;
			path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
			current = current.right;
			while (current.left != null) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = current.left;
			}
			safe.key = current.key;
			safe.value = current.value;
		}

		Node<K, V> subst = (current.left == null ? current.right : current.left);
		if (path == null) {
			root = subst;
			return oldValue;
		}
		if (path.d == Direction.RIGHT) {
			path.info.right = subst;
		} else {
			path.info.left = subst;
		}

		// 3. Коррекция балансов
		while (path != null) {
			// Корректируем показатель сбалансированности.
			// Если он +1 или -1, то работа по балансировке дерева закончена.
			// Если 0, то продолжаем подниматься вверх.
			// Если +2 или -2
			if (path.d == Direction.RIGHT) {
				path.info.balance++;
			} else {
				path.info.balance--;
			}
			if (Math.abs(path.info.balance) == 1) break;
			ListNode<Node<K, V>> next = path.next;
			if (path.info.balance != 0) {
				// Надо выполнять поворот(ы)
				ListNode<Node<K, V>> pred = null;
				if (path.info.balance == 2) {
					path.d = Direction.LEFT;
					pred = new ListNode<Node<K, V>>(path.info.left, Direction.LEFT, path);
				} else {
					path.d = Direction.RIGHT;
					pred = new ListNode<Node<K, V>>(path.info.right, Direction.LEFT, path);
				}
				if (pred.info.balance * path.info.balance >= 0) {
					// один поворот
					pivot(pred, path, next);
				} else {
					// двойной поворот
					ListNode<Node<K, V>> pred1 = null;
					if (pred.info.balance == 1) {
						pred.d = Direction.LEFT;
						pred1 = new ListNode<Node<K, V>>(pred.info.left, Direction.LEFT, pred);
					} else {
						pred.d = Direction.RIGHT;
						pred1 = new ListNode<Node<K, V>>(pred.info.right, Direction.LEFT, pred);
					}
					pivot(pred1, pred, path);
					pivot(pred1, path, next);
				}
			}
			path = next;
		}

		return oldValue;
	}

	/**
	 * Функция поворота меняет местами два узла дерева: p1 и p2
	 * (p1 поднимается наверх, p2 опускается вниз).
	 * @param p1 нижний узел пары узлов.
	 * @param p2 верхний узел пары узлов.
	 * @param p3 предок узла p2 (может отсутствовать).
	 * @return Перемещенный узел p1 со скорректированным направлением.
	 */
	private void pivot(ListNode<Node<K, V>> p1,
			ListNode<Node<K, V>> p2,
			ListNode<Node<K, V>> p3) {
		// 1. Перевешиваем узел p1 наверх
		if (p3 == null) {
			root = p1.info;
		} else if (p3.d == Direction.LEFT) {
			p3.info.left = p1.info;
		} else {
			p3.info.right = p1.info;
		}

		// Изменяем остальные ссылки и корректируем показатели сбалансированности узлов.
		if (p2.info.balance > 0) {
			// Относительные высоты поддеревьев (h1, h2, h3 - в порядке слева направо).
			short h1 = (short) Math.min(0, p1.info.balance);
			short h2 = (short) Math.min(0, -p1.info.balance);
			short h3 = (short) (1 - p2.info.balance);
			// Изменение ссылок.
			p2.info.left = p1.info.right;
			p1.info.right = p2.info;
			// Корректировка показателей сбалансированности.
			p2.info.balance = (short) (h2 - h3);
			p1.info.balance = (short) (h1 - Math.max(h2, h3) - 1);
		} else {
			// Относительные высоты поддеревьев (h1, h2, h3 - в порядке слева направо).
			short h2 = (short) Math.min(0, p1.info.balance);
			short h3 = (short) Math.min(0, -p1.info.balance);
			short h1 = (short) (1 + Math.max(h2, h3) + p2.info.balance);
			// Изменение ссылок.
			p2.info.right = p1.info.left;
			p1.info.left = p2.info;
			// Корректировка показателей сбалансированности.
			p2.info.balance = (short) (h1 - h2);
			p1.info.balance = (short) (Math.max(h1, h2) + 1 - h3);
		}
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

	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		AVLTree<Integer, Integer> tree = new AVLTree<Integer, Integer>();
		for (int i = 1; i <= 10; i++) {
			tree.put(2*i, 2*i);
		}
		tree.put(15, 15);
		tree.print();
		System.out.println("----------------------------");

		for (int i = 10; i >= 1; i--) {
			tree.remove(2*i);
			tree.print();
			System.out.println("----------------------------");
		}
	}
}
