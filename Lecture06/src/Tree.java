/**
 * Реализация дерева произвольной структуры на основе списка потомков
 * и некоторых операций с таким деревом. Поддерживаются следующие операции:
 * - height() Выдает высоту дерева
 * - levelSize(level) Выдает число узлов дерева, расположенных на заданном уровне.
 *
 * @param <T>
 */
public class Tree<T> {
	/**
	 * Реализация узла дерева
	 *
	 * @param <T>
	 */
	public static class Node<T> {
		@SuppressWarnings("unused")
		private T info;				// "Полезная" информация, содержащаяся в узле
		@SuppressWarnings("unused")
		private Node<T> parent;		// Ссылка на узел предка
		private Node<T> son;		// Ссылка на "старшего" сына
		private Node<T> brother;	// Ссылка на "брата"

		public Node(T info, Node<T> son, Node<T> brother, Node<T> parent) {
			this.info = info;
			this.son = son;
			this.brother = brother;
			this.parent = parent;
		}

		public Node(T info) { this(info, null, null, null); }
	}

	// Корень дерева
	private Node<T> root = null;

	/**
	 * Вычисление высоты дерева
	 * @return Высота дерева
	 */
	public int height() { return height(root); }

	/**
	 * Вспомогательная рекурсивная функция, осуществляющая подсчет высоты.
	 * @param node Корень поддерева, высота которого ищется
	 * @return Высота поддерева
	 */
	private static <T> int height(Node<T> node) {
		if (node == null) return 0;
		int maxHeight = 0;
		for (Node<T> child = node.son; child != null; child = child.brother) {
			int hChild = height(child);
			if (hChild > maxHeight) maxHeight = hChild;
		}
		return maxHeight + 1;
	}

	/**
	 * Вычисление количества узлов, находящихся на заданном уровне дерева
	 * @param level Уровень 
	 * @return Число узлов на заданном уровне.
	 */
	public int levelSize(int level) {
		return root == null ? 0 : levelSize(level, root);
	}

	/**
	 * Вспомогательная рекурсивная функция, осуществляющая подсчет количества узлов,
	 * находящихся на заданном уровне заданного поддерева.
	 * @param level Уровень
	 * @param node Корень поддерева
	 * @return Число узлов, находящихся на заданном уровне в поддереве.
	 */
	private static <T> int levelSize(int level, Node<T> node) {
		if (level == 0) return 1;
		int count = 0;
		for (Node<T> child = node.son; child != null; child = child.brother) {
			count += levelSize(level - 1, child);
		}
		return count;
	}

	/**
	 * Проверка правильности работы двух реализованных операций (unit test)
	 * @param args
	 */
	 public static void main(String[] args) {
		 Node<Integer> node1 = new Node<Integer>(1);
		 Node<Integer> node2 = new Node<Integer>(2);
		 Node<Integer> node3 = new Node<Integer>(3);
		 Node<Integer> node4 = new Node<Integer>(4);
		 Node<Integer> node5 = new Node<Integer>(5);
		 Node<Integer> node6 = new Node<Integer>(6);
		 Node<Integer> node7 = new Node<Integer>(7);
		 Node<Integer> node8 = new Node<Integer>(8);
		 Node<Integer> node9 = new Node<Integer>(9);
		 Node<Integer> node10 = new Node<Integer>(10);

		 node1.son = node2;
		 node2.son = node5; node2.brother = node3; node2.parent = node1;
		 node3.son = node7; node3.brother = node4; node3.parent = node1;
		 node4.son = node10; node4.parent = node1;
		 node5.brother = node6; node5.parent = node2;
		 node6.parent = node2;
		 node7.son = node8; node7.parent = node3;
		 node8.brother = node9; node8.parent = node7;
		 node9.parent = node7;
		 node10.parent = node4;

		 Tree<Integer> tree = new Tree<Integer>();
		 tree.root = node1;

		 // Высота должна быть равна 4
		 System.out.println("Height = " + tree.height());
		 
		 // На уровне № 2 должно находиться 4 узла.
		 System.out.println("Level # 2 = " + tree.levelSize(2));
	 }

}
