import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Реализация основных операций (вставка и удаление элементов,
 * поиск интервалов) в дереве промежутков, основанном на AA-дереве.
 * Реализованы следующие операции:
 * - get(interval)  Поиск какого-либо интервала, пересекающегося с заданным
 * - put(interval)  Добавление нового интервала в структуру данных
 * - remove(interval)  Удаление интервала
 * - searchAll(interval)  Поиск всех интервалов, пересекающихся с заданным
 */
public class IntervalTree {
	/**
	 * Простой класс, реализующий интервал на вещественной оси.
	 * Интервалы упорядочиваются лексикографически, то есть по левым
	 * концам, а в случае совпадения - по правым концам.
	 */
	public static class Interval implements Comparable<Interval> {
		private double low;		// левый конец интервала
		private double high;	// правый конец интервала
		
		/**
		 * Конструктор интервала на основе значений его концов
		 * @param low
		 * @param high
		 */
		public Interval(double low, double high) {
			this.low = low;
			this.high = high;
		}
		
		/**
		 * Конструктор интервала на основе другого интервала
		 * @param interval
		 */
		public Interval(Interval interval) {
			low = interval.low;
			high = interval.high;
		}
		
		/**
		 * Проверка, пересекаются ли интервалы
		 * @param other
		 * @return
		 */
		public boolean intersects(Interval other) {
			return low <= other.high && high >= other.low;
		}
		
		/**
		 * Сравнение интервалов на равенство
		 * @param other
		 * @return
		 */
		public boolean equals(Interval other) {
			return other != null && low == other.low && high == other.high;
		}
		
		/**
		 * {@link #Object.equals()}
		 */
		@Override
		public boolean equals(Object obj) {
			return equals((Interval) obj);
		}
		
		/**
		 * {@link #Object.hashCode()}
		 */
		@Override
		public int hashCode() {
			return Double.valueOf(low).hashCode() ^ Double.valueOf(high).hashCode();
		}
		
		/**
		 * Представление интервала в виде строки
		 */
		@Override
		public String toString() {
			return "<" + low + "," + high + ">";
		}

		/**
		 * Сравнение интервалов в лексикографическом порядке
		 */
		@Override
		public int compareTo(Interval other) {
			return equals(other) ? 0 : low < other.low ? -1 : low > other.low ? 1 : high < other.high ? -1 : 1;
		}
	}
	
	//-----------------------------------------------------------
	// Последующая структура повторяет реализацию AA-дерева.
	// Ключом в дереве являются интервалы, а максимумы - значениями.
	//-----------------------------------------------------------
	
	/**
	 * Реализация узла дерева. Дополнительное поле - уровень узла.
	 * Лист дерева имеет уровень 1. Фиктивный узел SENTINEL, который
	 * играет роль пустой ссылки, имеет уровень 0.
	 */
	private static class Node {
		Interval interval;
		double max;
		int level = 0;
		Node left = null;
		Node right = null;
		
		/**
		 * Конструктор листа
		 * @param interval Ключ листа
		 */
		Node(Interval interval) {
			this.interval = new Interval(interval);
			this.max = interval.high;
			level = 1;
			left = right = SENTINEL;
		}
		
		/**
		 * Конструктор пустого узла (SENTINEL)
		 */
		Node() {
			interval = null;
			max = Double.NEGATIVE_INFINITY; 
		}
		
		/**
		 * Представление узла в виде строки
		 */
		@Override
		public String toString() {
			return interval + "; max=" + max + " (" + level + ")";
		}
	}
	
	// Пустой фиктивный узел
	final static Node SENTINEL = new Node();
	
	// Корень дерева (вначале - пустой).
	Node root = SENTINEL;
	
	/**
	 * Функция проверки, является ли узел пустым
	 * @param node Проверяемый узел
	 * @return true, если узел пустой, false в противном случае.
	 */
	private static boolean nil(Node node) { return node == SENTINEL; }
	
	/**
	 * Функция производит поворот, если нужно
	 * @param node Проверяемый узел
	 * @return Ссылка на корень , возможно, измененного поддерева.
	 */
	private Node skew(Node node) {
		Node result = node;
		if (!nil(node) && !nil(node.left) && node.left.level == node.level) {
			// Нужен правый поворот
			result = node.left;
			node.left = result.right;
			result.right = node;
			result.max = node.max;
			node.max = Math.max(Math.max(node.left.max, node.right.max), node.interval.high);
		}
		return result;
	}
	
	/**
	 * Функция производит расщепление с продвижением узла на более высокий уровень,
	 * если это необходимо.
	 * @param node Проверяемый узел
	 * @return Корень, возможно, преобразованного узла.
	 */
	private Node split(Node node) {
		Node result = node;
		if (!nil(node) && !nil(node.right) && !nil(node.right.right) && 
				node.level == node.right.right.level) {
			result = node.right;
			node.right = result.left;
			result.left = node;
			result.level++;
			result.max = node.max;
			node.max = Math.max(Math.max(node.left.max, node.right.max), node.interval.high);
		}
		return result;
	}
	
	/**
	 * Поиск некоторого интервала, пересекающегося с заданным.
	 * @param key Ключ поиска
	 * @return Найденое значение или null, если значения в дереве нет.
	 */
	public Interval get(Interval src) {
		Node node = search(src);
		return node.interval;
	}
	
	/**
	 * Добавление нового интервала в дерево. Если такой интервал уже существовал 
	 * в дереве, то ничего не меняется. Иначе создается новый узел.
	 * @param src Добавляемый интервал
	 * @param value Добавляемое значение
	 * @return
	 */
	public void put(Interval src) {
		root = put(root, src);
	}
	
	/**
	 * Вспомогательная рекурсивная функция добавления интервала в дерево.
	 * Функция не изменяет дерево, если узел с данным интервалом уже существовал.
	 * @param node Начальный узел для вставки (корень поддерева)
	 * @param src Вставляемый интервал
	 * @return Ссылка на, возможно, измененное поддерево
	 */
	private Node put(Node node, Interval src) {
		if (nil(node)) {
			return new Node(src);
		} else if (src.compareTo(node.interval) < 0) {
			node.left = put(node.left, src);
		} else {
			node.right = put(node.right, src);
		}
		node.max = Math.max(node.max, src.high);
		
		node = skew(node);
		node = split(node);
		
		return node;
	}
	
	/**
	 * Поиск узла, содержащего интервал, пересекающийся с заданным.
	 * @param src Ключ поиска
	 * @return Найденный узел или фиктивный узел, если ключ не существует в дереве.
	 */
	private Node search(Interval src) {
		Node current = root;
		while (!nil(current) && !current.interval.intersects(src)) {
			if (!nil(current.left) && current.left.max >= src.low) {
                // Если такой пересекающийся интервал где-нибудь в дереве есть,
                // то он точно есть в левом поддереве.
				current = current.left;
			} else {
                // Осталось поискать в правом поддереве
				current = current.right;
			}
		}
		return current;
	}
	
	/**
	 * Перечисление всех интервалов в дереве, пересекающихся с заданным.
	 * @param src
	 * @return
	 */
	public List<Interval> searchAll(Interval src) {
		List<Interval> result = new ArrayList<Interval>();
		searchAll(root, src, result);
		return result;
	}
	
	/**
	 * Вспомогательная рекурсивная функция, которая добавляет в переданный ей
	 * список все интервалы из заданного поддерева, пересекающиеся с заданным.
	 * @param current
	 * @param src
	 * @param result
	 */
	private static void searchAll(Node current, Interval src, List<Interval> result) {
		if (nil(current)) {
			return;
		} else if (current.interval.intersects(src)) {
			result.add(current.interval);
		}
		if (current.left.max >= src.low) {
			searchAll(current.left, src, result);
		}
		if (current.interval.low < src.high) {
			searchAll(current.right, src, result);
		}
	}
	
	/**
	 * Удаление узла с заданным ключом.
	 * @param key Ключ удаляемого узла
	 * @return Значение, хранящееся в удаляемом узле, или null, если ключ не существовал.
	 */
	public void remove(Interval src) {
		root = remove(root, src);
	}
	
	/**
	 * Вспомогательная рекурсивная функция, осуществляющая удаление узла из дерева.
	 * @param node Корень поддерева, из которого происходит удаление
	 * @param key Ключ удаляемого узла
	 * @return Корень, возможно, преобразованного поддерева.
	 */
	private Node remove(Node node, Interval src) {
	    if (nil(node)) {
	    	return node;
	    } else if (src.equals(node.interval)) {
	    	if (nil(node.right)) {
		    	// Все легко, если удаляется лист,
	    		// иначе удаляем ближайший лист в левом или правом поддереве. 
	            return SENTINEL;
		    } else if (nil(node.left)) {
	            Node substNode = successor(node);
	            node.right = remove(node.right, substNode.interval);
	            node.interval.low = substNode.interval.low;
	            node.interval.high = substNode.interval.high;
		    } else {
	            Node substNode = predecessor(node);
	            node.left = remove(node.left, substNode.interval);
	            node.interval.low = substNode.interval.low;
	            node.interval.high = substNode.interval.high;
		    }
	    } else if (src.compareTo(node.interval) >= 0) {
	    	node.right = remove(node.right, src);
	    } else {
	    	node.left = remove(node.left, src);
	    }
	    node.max = Math.max(node.interval.high, Math.max(node.left.max, node.right.max));

	    // Балансировка дерева. Уменьшаем значение level всех узлов этого уровня,
	    // если это нужно, затем выполняем skew и split для всех узлов нового уровня.
	    node = decreaseLevel(node);
	    node = skew(node);
	    node.right = skew(node.right);
	    if (!nil(node.right)) node.right.right = skew(node.right.right);
	    node = split(node);
	    node.right = split(node.right);
	    return node;
	}

	/**
	 * Ищет ближайший справа узел к заданному
	 * @param node Исходный узел
	 * @return Ссылка на ближайший справа узел.
	 */
	private Node successor(Node node) {
		node = node.right;
		while (!nil(node.left)) node = node.left;
		return node;
	}
	
	/**
	 * Ищет ближайший слева узел к заданному
	 * @param node Исходный узел
	 * @return Ссылка на ближайший слева узел.
	 */
	private Node predecessor(Node node) {
		node = node.left;
		while (!nil(node.right)) node = node.right;
		return node;
	}
	
	/**
	 * Уменьшает уровень узла, если у его потомков не хватает элементов
	 * @param node Исходный узел
	 * @return Тот же узел, возможно, с измененным уровнем
	 */
	private Node decreaseLevel(Node node) {
		int shouldBe = Math.min(node.left.level, node.right.level) + 1;
		if (shouldBe < node.level) {
			node.level = shouldBe;
			if (shouldBe < node.right.level) {
				node.right.level = shouldBe;
			}
		}
		return node;
	}
	
	/**
	 * "Красивая" печать дерева
	 */
	public void print() {
		print(root);
		System.out.println("-----------------------");
	}
	
	/**
	 * Вспомогательная рекурсивная функция "красивой" печати дерева
	 * @param node
	 */
	private static void print(Node node) {
		if (node.level != 0) {
			print(node.left);
			char[] fill = new char[2 * node.level];
			Arrays.fill(fill, ' ');
			System.out.print(String.valueOf(fill));
			System.out.println(node);
			print(node.right);
		}
	}
	
	/**
	 * Тестовая программа, выполняющая некоторые из операций с деревом промежутков
	 * @param args
	 */
	public static void main(String[] args) {
		// Строим дерево из 10 промежутков
		IntervalTree tree = new IntervalTree();
		Interval[] array = new Interval[] {
			new Interval(2,4), new Interval(5,8), new Interval(9,10),
			new Interval(13,14), new Interval(3,6), new Interval(9,12),
			new Interval(3,10), new Interval(1,4), new Interval(7,10),
			new Interval(11,14)
		};
		for (Interval src : array) {
			tree.put(src);
		}
		// Печатаем построенное дерево
		tree.print();
		
		// Теперь найдем в построенном дереве некоторые интервалы
		System.out.println(tree.get(new Interval(9,10)));
		
		List<Interval> all = tree.searchAll(new Interval(7,8));
		System.out.println();
		for (Interval interval : all) {
			System.out.println(interval);
		}
		System.out.println();
		
		// Удаляем один из интервалов и печатаем результат.
		tree.remove(new Interval(5,8));
		tree.print();
	}
}
