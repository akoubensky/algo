
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
public class AVLTreeB<K extends Comparable<K>, V> extends AVLTree<K, V> {
	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 *
	 * @param <K> тип ключа
	 * @param <V> тип значения
	 */
	private static class Node<K, V> extends TreeNode<K, V> {
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
			super(key, value, left, right);
			this.balance = balance;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) {
			super(key, value);
			this.balance = 0;
		}
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

	@Override
	public V put(K key, V value) {
		// Проверка: ключ не может быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
		ListNode<Node<K, V>> path = null;
		Node<K, V> current = (Node<K, V>)root;
		while (current != null) {
			if (key.compareTo(current.key) == 0) {
				// Ключ найден; заменяем старое значение и заканчиваем работу.
				V oldValue = current.value;
				current.value = value;
				return oldValue;
			} else if (key.compareTo(current.key) < 0) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = (Node<K, V>)current.left;
			} else {
				path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
				current = (Node<K, V>)current.right;
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

	@Override
	public V remove(K key) {
		// Проверка: ключ не может быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// 1. Поиск (почти как в методе get, только с запоминанием пройденного пути).
		ListNode<Node<K, V>> path = null;
		Node<K, V> current = (Node<K, V>)root;
		while (current != null) {
			if (key.compareTo(current.key) == 0) {
				// Ключ найден; выходим из цикла.
				break;
			} else if (key.compareTo(current.key) < 0) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = (Node<K, V>)current.left;
			} else {
				path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
				current = (Node<K, V>)current.right;
			}
		}

		if (current == null) return null;

		V oldValue = current.value;

		// 2. Производим удаление ключа
		if (current.left != null && current.right != null) {
			Node<K, V> safe = current;
			path = new ListNode<Node<K, V>>(current, Direction.RIGHT, path);
			current = (Node<K, V>)current.right;
			while (current.left != null) {
				path = new ListNode<Node<K, V>>(current, Direction.LEFT, path);
				current = (Node<K, V>)current.left;
			}
			safe.key = current.key;
			safe.value = current.value;
		}

		Node<K, V> subst = (Node<K, V>)(current.left == null ? current.right : current.left);
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
					pred = new ListNode<Node<K, V>>((Node<K, V>)path.info.left, Direction.LEFT, path);
				} else {
					path.d = Direction.RIGHT;
					pred = new ListNode<Node<K, V>>((Node<K, V>)path.info.right, Direction.LEFT, path);
				}
				if (pred.info.balance * path.info.balance >= 0) {
					// один поворот
					pivot(pred, path, next);
				} else {
					// двойной поворот
					ListNode<Node<K, V>> pred1 = null;
					if (pred.info.balance == 1) {
						pred.d = Direction.LEFT;
						pred1 = new ListNode<Node<K, V>>((Node<K, V>)pred.info.left, Direction.LEFT, pred);
					} else {
						pred.d = Direction.RIGHT;
						pred1 = new ListNode<Node<K, V>>((Node<K, V>)pred.info.right, Direction.LEFT, pred);
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
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		AVLTreeB<Integer, Integer> tree = new AVLTreeB<Integer, Integer>();
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
