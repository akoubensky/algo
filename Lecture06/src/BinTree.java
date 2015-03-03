import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
		private Node<T> left;
		private Node<T> right;
		/**
		 * Ссылка на родительский узел используется только в одном из алгоритмов
		 * внешней итерации узлов. См. {@link BinTree.TreeIterator}
		 */
		private Node<T> parent;

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
	 * Конструктор дерева с заданным корнем.
	 * @param node Корень дерева
	 */
	public BinTree(Node<T> node) {
		root = node;
	}

	/**
	 * Внутренний итератор дерева. В Java 8 можно вместо типа данных {@link Action}
	 * использовать встроенный функциональный интерфейс {@link Consumer}.
	 * 
	 * @param action Действие, выполняемое с узлом.
	 */
	public void iterate(Action<T> action) { iterate(action, root); }

	/**
	 * Рекурсивная вспомогательная функция, реализующая внутреннюю итерацию дерева.
	 * 
	 * @param action Действие, выполняемое с узлом дерева
	 * @param node Корень итерируемого поддерева.
	 */
	private static <T> void iterate(Action<T> action, Node<T> node) {
		if (node != null) {
			iterate(action, node.left);
			action.accept(node.info);
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
	 * Реализация внешнего итератора дерева, использующая для перехода к следующему
	 * узлу дерева проход по ссылке на родительский узел.
	 * 
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
	 * Ссылка на родительский узел не используется.
	 * @param <T> Тип итерируемых элементов
	 */
	private static class StackTreeIterator<T> implements Iterator<T> {
		// Стек для хранения корней поддеревьев, которые вместе со своими правыми
		// поддеревьями подлежат рассмотрению в будущем.
		Stack<Node<T>> stack = new Stack<Node<T>>();

		/**
		 * Конструктор
		 * @param root Корень дерева
		 */
		public StackTreeIterator(Node<T> root) {
			// Проходим к самому левому узлу,
			// запоминая промежуточные узлы в стеке.
			toStack(root);
		}

		/**
		 * Функция прохождения по левому гребню дерева
		 * с запоминанием промежуточных узлов в стеке.
		 * @param node Корень (под)дерева
		 */
		private void toStack(Node<T> node) {
			while (node != null) {
				stack.push(node);
				node = node.left;
			}
		}

		@Override
		public T next() {
			// Очередной узел выбирается из стека
			if (stack.empty()) {
				throw new IllegalStateException();
			}
			Node<T> node = stack.pop();
			// Проходим левый гребень правого поддерва.
			toStack(node.right);
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
	
	/**
	 * Реализация внешнего итератора для параллельной обработки дерева.
	 * Использует средства Java 8.
	 * @param <T>
	 */
	private static class TreeSpliterator<T> implements Spliterator<T> {
		// Стек для хранения корней пройденных (под)деревьев.
		Stack<Node<T>> stack;
		
		/**
		 * Конструктор берет в качестве параметра уже готовый стек непройденных узлов.
		 * 
		 * @param stack	Стек непройденных узлов.
		 */
		TreeSpliterator(Stack<Node<T>> stack) { this.stack = stack; }
		
		/**
		 * Записывает в стек "левый гребень" дерева, корнем которого
		 * является заданный узел.
		 * 
		 * @param stack	Стек, в который происходит запись узлов.
		 * @param node	Корень поддерева, левый гребень которого записывается.
		 */
		static <T> void toStack(Stack<Node<T>> stack, Node<T> node) {
			while (node != null) {
				stack.push(node);
				node = node.left;
			}
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			if (stack.empty()) {
				// Больше нет узлов для итерации
				return false;
			} else {
				// Берем очередной узел для итерации с вершины стека.
				Node<T> node = stack.pop();
				action.accept(node.info);
				// Записываем в стек правое поддерево пройденного узла.
				toStack(stack, node.right);
				return true;
			}
		}

		@Override
		public Spliterator<T> trySplit() {
			// Делим стек непройденных узлов на две неравные части,
			// если в стеке есть хотя бы два узла.
			if (stack.size() <= 1) {
				return null;
			}
			// Отделяем вершину, лежащую на дне стека.
			Node<T> bottom = stack.remove(0);
			Spliterator<T> newSpliterator = new TreeSpliterator<>(stack);
			// Отрезаем от вершины, лежащей на дне стека, левое поддерево
			// и записываем ее вместе с правым поддеревом в новый стек.
			stack = new Stack<>();
			stack.push(new Node<>(bottom.info, null, bottom.right, bottom.parent));
			
			return newSpliterator;
		}

		@Override
		public long estimateSize() {
			// Грубая оценка числа оставшихся вершин по числу вершин в стеке.
			return (1L << stack.size()) - 1;
		}

		@Override
		public int characteristics() {
			return ORDERED | CONCURRENT;
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
	
	@Override
	public Spliterator<T> spliterator() {
		Stack<Node<T>> stack = new Stack<>();
		TreeSpliterator.toStack(stack, root);
		return new TreeSpliterator<>(stack);
	}
	
	/**
	 * Превращает дерево в поток узлов для, возможно, параллельной обработки,
	 * используя {@link BinTree.spliterator}
	 * @return
	 */
	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), true);
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
			public void accept(Integer item) {
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
		
		// Запускаем параллельный итератор всех узлов, больших единицы (на основе
		// операций с потоками - Java 8)
		tree.stream()
		    .filter(x -> x > 1)
		    .forEach(x -> System.out.format(" %d", x));
		System.out.println();
	}

}
