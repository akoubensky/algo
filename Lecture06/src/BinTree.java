import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Простое двоичное дерево с итерацией. Поддерживаются операции:
 * - iterate(action) Внутренняя итерация дерева
 * - iterator() Выдает внешний итератор дерева, построенный на основе рекурсии
 * - stackIterator() Выдает внешний итератор дерева, построенный на основе стека
 * - iteratorsIterator() Выдает внешний итератор дерева, построенный на основе
 *                       операций над итераторами
 * @param <T> Тип элементов дерева
 */
class BinTree<T> implements Iterable<T> {
	/**
	 * Узел дерева содержит ссылки на левое и правое поддеревья и на
	 * родительский узел.
	 * @param <T>
	 */
	public static class Node<T> {
		private T info;
		private Node<T> parent;
		private Node<T> left;
		private Node<T> right;

		/**
		 * Конструктор узла дерева.
		 * @param info
		 * @param left
		 * @param right
		 * @param parent
		 */
		public Node(T info, Node<T> left, Node<T> right, Node<T> parent) {
			this.info = info;
			this.left = left;
			this.right = right;
			this.parent = parent;
		}

		/**
		 * Конструктор узла дерева, не связанного с другими узлами
		 * @param info
		 */
		public Node(T info) { this(info, null, null, null); }

		/**
		 * Добавление нового узла в качестве левого поддерева
		 * @param info Добавляемый элемент
		 * @return Созданный новый узел
		 */
		public Node<T> addLeft(T info) {
			assert left == null;

			Node<T> newNode = new Node<T>(info);
			this.left = newNode;
			newNode.parent = this;
			return newNode;
		}

		/**
		 * Добавление нового узла в качестве правого поддерева
		 * @param info Добавляемый элемент
		 * @return Созданный новый узел
		 */
		public Node<T> addRight(T info) {
			assert right == null;

			Node<T> newNode = new Node<T>(info);
			this.right = newNode;
			newNode.parent = this;
			return newNode;
		}
	}

	/* Корень дерева */
	private Node<T> root = null;

	/**
	 * Конструктор пустого дерева
	 */
	public BinTree() {}

	/**
	 * Конструктор дерева с заданным корнем
	 * @param node Корень дерева
	 */
	public BinTree(Node<T> node) {
		root = node;
	}

	/**
	 * Внутренний итератор дерева.
	 * @param action Действие, выполняемое с узлом.
	 */
	public void iterate(Action<T> action) { iterate(action, root); }

	/**
	 * Рекурсивная вспомогательная функция, 
	 * реализующая внутреннюю итерацию дерева
	 * @param action Действие, выполняемое с узлом дерева
	 * @param node Корень итерируемого поддерева.
	 */
	private static <T> void iterate(Action<T> action, Node<T> node) {
		if (node != null) {
			iterate(action, node.left);
			action.action(node.info);
			iterate(action, node.right);
		}
	}

	/**
	 * Вспомогательная функция, реализующая создание внешнего итератора
	 * дерева на основе выполнения операций над итераторами.
	 * @param node Корень итерируемого дерева
	 * @return Итератор (под)дерева.
	 */
	private Iterator<T> iterator(Node<T> node) {
		if (node == null) {
			return Iterators.nullIterator();
		} else {
			return Iterators.join(iterator(node.left), 
					Iterators.join(
							Iterators.singleElement(node.info), 
							iterator(node.right)));
		}
	}

	/**
	 * Реализация внешнего итератора дерева.
	 * @param <T> Тип итерируемых элементов.
	 */
	private static class TreeIterator<T> implements Iterator<T> {
		/* Очередной узел в порядке итерации */
		private Node<T> nextNode;

		/**
		 * Конструктор
		 * @param root Корень итерируемого дерева
		 */
		public TreeIterator(Node<T> root) {
			nextNode = root == null ? null : firstNode(root);
		}

		/**
		 * Вспомогательная функция поиска самого левого узла
		 * в поддереве с заданным корнем.
		 * @param node Корень поддерева.
		 * @return Самый левый узел поддерева.
		 */
		private Node<T> firstNode(Node<T> node) {
			Node<T> current = node;
			while (current.left != null) { 
				current = current.left; 
			}
			return current;
		}

		/**
		 * Функция поиска следующего узла в процессе итерации.
		 * @param node Предыдущий узел
		 * @return Следующий за предыдущим узел
		 */
		private Node<T> nextNode(Node<T> node) {
			if (node.right != null) {
				// Самый левый в правом поддереве
				return firstNode(node.right); 
			}
			// Поднимаемся по левой ветке к родительским узлам
			Node<T> pred = node, 
					next = node.parent;
			while (next != null && next.left != pred) {
				pred = next; 
				next = next.parent;
			}
			return next;
		}

		@Override
		public boolean hasNext() { 
			return nextNode != null; 
		}

		@Override
		public T next() {
			if (nextNode == null) {
				throw new NoSuchElementException();
			}
			T res = nextNode.info; 
			nextNode = nextNode(nextNode); 
			return res;
		}

		@Override
		/**
		 * Операция удаления узла в процессе итерации не поддерживается.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Реализация внешнего итератора двоичного дерева на основе стека.
	 * @param <T> Тип итерируемых элементов
	 */
	private static class StackTreeIterator<T> implements Iterator<T> {
		// Стек для хранения корней пройденных (под)деревьев.
		Stack<Node<T>> stack = new Stack<Node<T>>();

		/**
		 * Конструктор
		 * @param root Корень дерева
		 */
		public StackTreeIterator(Node<T> root) {
			if (root != null) {
				// Проходим к самому левому узлу,
				// запоминая промежуточные узлы в стеке.
				firstNode(root);
			}
		}

		/**
		 * Функция прохождения по левому гребню дерева
		 * с запоминанием промежуточных узлов в стеке.
		 * @param node Корень (под)дерева
		 */
		private void firstNode(Node<T> node) {
			do {
				stack.push(node); node = node.left;
			} while (node != null);
		}

		@Override
		public T next() {
			// Очередной узел выбирается из стека
			if (stack.empty()) {
				throw new IllegalStateException();
			}
			Node<T> node = stack.pop();
			// Проходим левый гребень правого поддерва.
			if (node.right != null) {
				firstNode(node.right);
			}
			return node.info;
		}

		@Override
		public boolean hasNext() {
			return !stack.empty();
		}

		@Override
		/**
		 * Удаление элементов из дерева в процессе итерации не поддерживается.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	//-------------------------------------------------------------------
	// Разные реализации итераторов
	//-------------------------------------------------------------------

	@Override
	public Iterator<T> iterator() {
		// Реализация на основе прохода по родительским ссылкам
		return new TreeIterator<T>(root);
	}

	/**
	 * Реализация внешнего итератора на основе стека.
	 * 
	 * @return	Итератор на основе стека
	 */
	public Iterator<T> stackIterator() {
		return new StackTreeIterator<T>(root);
	}
	
	/**
	 * Реализация внешнего итератора на основе операций над итераторами.
	 * 
	 * @return	Итератор на основе операций над итераторами
	 */
	public Iterator<T> iteratorsIterator() {
		return iterator(root);
	}

	/**
	 * Проверяем работу всех типов итераторов дерева.
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// Создаем дерево
		Node<Integer> node5 = new Node<Integer>(5);
		Node<Integer> node3 = node5.addLeft(3);
		Node<Integer> node9 = node5.addRight(9);
		Node<Integer> node2 = node3.addLeft(2);
		Node<Integer> node4 = node3.addRight(4);
		Node<Integer> node7 = node9.addLeft(7);
		Node<Integer> node10 = node9.addRight(10);
		Node<Integer> node1 = node2.addLeft(1);
		Node<Integer> node6 = node7.addLeft(6);
		Node<Integer> node8 = node7.addRight(8);

		BinTree<Integer> tree = new BinTree<Integer>(node5);

		// Запускаем внутренний итератор
		tree.iterate(new Action<Integer>() {
			public void action(Integer item) {
				System.out.print(" " + item);
			}
		});
		System.out.println();

		// Запускаем внешний итератор "по умолчанию" (на основе рекурсии)
		for (Integer item : tree) {
			System.out.print(" " + item);
		}
		System.out.println();

		// Запускаем альтернативный внешний итератор (на основе стека)
		for (Iterator<Integer> it = tree.stackIterator(); it.hasNext(); ) {
			System.out.print(" " + it.next());
		}
		System.out.println();

		// Запускаем еще один альтернативный внешний итератор (на основе операций с итераторами)
		for (Iterator<Integer> it = tree.iteratorsIterator(); it.hasNext(); ) {
			System.out.print(" " + it.next());
		}
		System.out.println();
	}

}
