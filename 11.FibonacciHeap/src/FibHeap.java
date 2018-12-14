import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Реализация представления и основных операций с Фибоначчиевой кучей.
 * Реализованы следующие операции:
 * -   size()  - число элементов в куче;
 * -   isEmpty()  - провека наличия элементов;
 * -   add(key, value)  - добавление новой ассоциативной пары;
 * -   add(heap)  - слияние с другой Фибоначчиевой кучей;
 * -   getBest()  - доступ к наиболее приоритетному элементу;
 * -   getBestNode()  - доступ к наиболее приоритетному узлу;
 * -   extractBest()  - извлечение наиболее приоритетного узла;
 * -   changeKey(node, newKey)  - изменение приоритета заданного узла;
 * -   find(key)  - поиск значения по ключу;
 * -   findNode(key)  - поиск узла по ключу;
 * -   removeNode(node)  - удаление узла.
 *
 * @param <K>
 * @param <V>
 */
public class FibHeap<K extends Comparable<K>, V> {
	/**
	 * Компаратор, использующийся для сравнения ключей
	 */
	private Comparator<K> comparator = Comparator.naturalOrder();

	/**
	 * Класс, представляющий структуру узла кучи. Класс доступен
	 * извне охватывающего класса. Экземпляр этого класса может быть
	 * получен при поиске узлов и затем использован для операций
	 * по изменению приоритета или удалению.
	 *
	 * @param <K> Тип ключа
	 * @param <V> Тип ассоциированного значения
	 */
	public static class Node<K, V> {
		private K key;						// ключ
		private V info;						// значение
		private int degree = 0;				// степень узла - число потомков
		private boolean mark = false;		// пометка (был удален потомок)
		private Node<K,V> parent = null;	// ссылка на родительский узел 
		private Node<K,V> son = null; 		// Ссылка на список потомков
		private Node<K,V> next = null; 		// Ссылка на следующий узел в списке
		private Node<K,V> prev = null;		// Ссылка на предыдущий узел в списке.

		/**
		 * Конструктор используется только внутри кучи, создать узел извне нельзя.
		 * @param key	Ключ
		 * @param info	Значение
		 */
		private Node(K key, V info) { 
			this.key = key; 
			this.info = info;
			// При создании узел прдставляет собой одноэлементный циклический список
			next = prev = this;
		}
		
		/**
		 * Доступ к ключу, хранящемуся в узле.
		 * @return	Ключ
		 */
		public K getKey() { return key; }
		
		/**
		 * Доступ к значению, хранящемуся в узле
		 * @return	Значение
		 */
		public V getValue() { return info; }
		
		@Override
		public String toString() {
			return "<" + key.toString() + "," + info.toString() + ">";
		}
	}

	/**
	 * Ссылка на наиболее приоритетный элемент в корневом списке
	 */
	private Node<K,V> root = null;
	
	/**
	 * Счетчик числа элементов
	 */
	private int count = 0;
	
	/**
	 * Конструктор с компаратором "по умолчанию".
	 */
	public FibHeap() {}
	
	/**
	 * Конструктор с заданным компаратором ключей.
	 * @param comparator	Компаратор ключей
	 */
	public FibHeap(Comparator<K> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * Размер кучи (число элементов)
	 * @return	Размер кучи
	 */
	public int size() { return count; }
	
	/**
	 * Проверка пустоты кучи
	 * @return True, если куча пуста, False в противном случае.
	 */
	public boolean isEmpty() { return count == 0; }

	/**
	 * Добавление одного узла в кучу. 
	 * Узел просто добавляется в корневой список.
	 * @param key	Ключ
	 * @param info	Значение
	 */
	public void add(K key, V info) {
		Node<K, V> newNode = new Node<K, V>(key, info);
		count++;
		if (root == null) {
			root = newNode;
		} else {
			joinLists(newNode, root);
			if (comparator.compare(root.key, key) < 0) {
				root = newNode;
			}
		}
	}

	/**
	 * Соединение двух Фибоначчиевых куч в одну. Просто соединяет два списка в один.
	 * @param otherHeap	Присоединяемая куча
	 */
	public void add(FibHeap<K,V> otherHeap) {
		root = joinLists(root, otherHeap.root);
		count += otherHeap.count;
	}

	/**
	 * Взятие наиболее приоритетного значения
	 * @return	Наиболее приоритетное значение
	 */
	public V getBest() { 
		return root == null ? null : root.info; 
	}

	/**
	 * Взятие ключа с наивысшим приоритом
	 * @return	Ключ с наивысшим приоритом
	 */
	public K getBestKey() { 
		return root == null ? null : root.key; 
	}

	/**
	 * Взятие наиболее приоритетного узла.
	 * @return	Наиболее приоритетный узел.
	 */
	public Node<K,V> getBestNode() { 
		return root; 
	}

	/**
	 * Извлечение самого приоритетного узла из кучи.
	 * @return	Извлеченное значение
	 */
	public V extractBest() {
		if (root == null) {
			throw new NullPointerException("Извлечение узла из пустой кучи");
		}
		Node<K,V> bestNode = root;
		// Список потомков узла переносим в корневой список.
		// Отношение "предок - потомок" пока сохраняется
		root = joinLists(root, root.son).next;
		// отрезаем ссылку на потомка
		bestNode.son = null;
		count--;
		if (root.next == root) {
			// Удаляется последний элемент в куче
			root = null;
		} else {
			// Наиболее приоритетный узел исключается из корневого списка
			cut(bestNode);
			// Производится консолидация кучи. При этом поомки узла,
			// перемещенные в корневой список, получают пустую ссылку на предка.
			consolidate();
		}
		return bestNode.info; 
	}
	
	/**
	 * Увеличение приоритета заданного узла.
	 * @param node Узел, приоритет которого изменяется
	 * @param newKey Новый приоритет
	 * @return Узел с измененным приоритетом
	 */
	public Node<K,V> changeKey(Node<K,V> node, K newKey) {
		// Мы можем только увеличить, но не уменьшить приоритет
		if (node == null || newKey.compareTo(node.key) < 0) {
			throw new IllegalArgumentException();
		}
		node.key = newKey;
		
		// Узел с уменьшенным приоритетом перемещается в корневой список.
		// Если удаление узла привело к нарушению структуры кучи
		// (у некоторого узла удаляется второй потомок), то возможно
		// перемещение в корневой список и других узлов (предков данного).
		Node<K,V> parent = node.parent;
		if (parent != null && newKey.compareTo(parent.key) >= 0) {
			joinLists(root, cut(node));
			cascadingCut(parent);
		}
		
		// Ссылка на корень, возможно, тоже изменится, если новый
		// приоритет выше максимального.
		if (newKey.compareTo(root.key) >= 0) {
			root = node;
		}
		
		return node; 
	}

	/**
	 * Удаление заданного узла.
	 * @param node Удаляемый узел.
	 */
	public void removeNode(Node<K,V> node) {
		if (node == null) throw new IllegalArgumentException();
		// Перемещаем удаляемый узел в корневой список увеличением
		// его приоритета до максимального.
		changeKey(node, root.key);
		// Удаляем наиболее приоритетный узел - это будет только что
		// перемещенный узел.
		extractBest();
	}

	/**
	 * Ищет первый элемент кучи с заданным ключом.
	 * @param key Ключ
	 * @return Найденное значение
	 */
	public V find(final K key) { 
		Node<K,V> node = findNode(key);
		return node == null ? null : node.info;
	}

	/**
	 * Ищет первый узел кучи с заданным ключом.
	 * @param key Ключ
	 * @return Найденный узел или null, если такого ключа нет в куче.
	 */
	public Node<K,V> findNode(final K key) {
		return listFind(root, key); 
	}
	
	//=================================================================
	// Скрытые (вспомогательные) функции и структуры данных.
	//=================================================================
	
	/**
	 * Вспомогательный класс, необходимый для построения нового корневого
	 * списка при консолидации узлов. Содержит индексированный список
	 * Фибоначчиевых деревьев.
	 */
	private class RootList {
		// Список деревьев
		private List<Node<K,V>> rootList = new ArrayList<Node<K,V>>();
		
		/**
		 * @see {@link #java.util.List.size()}
		 * @return Размер списка
		 */
		private int size() { return rootList.size(); }
		
		/**
		 * @see {@link #java.util.List.get()}
		 * @param i Индекс
		 * @return Элемент списка с заданным индексом
		 */
		private Node<K,V> get(int i) { return rootList.get(i); }
		
		/**
		 * Добавление узла в корневой список с возможным соединением узлов.
		 * @param node Добавляемый узел
		 */
		private void addNode(Node<K,V> node) {
			if (rootList.size() <= node.degree) {
				// Длина списка меньше, чем степень добавляемого узла.
				// Расширяем список, при этом новый узел добавляется в конец списка.
				for (int i = rootList.size(); i < node.degree; ++i) {
					rootList.add(null);
				}
				rootList.add(node);
			} else if (rootList.get(node.degree) == null) {
				// Узла с такой степенью еще не было в списке. Добавляем его.
				rootList.set(node.degree, node);
			} else {
				// Узел с такой степенью уже был в списке. Соединяем его
				// с новым узлом и добавляем теперь уже узел со степенью,
				// на единицу большей (рекурсивный вызов).
				Node<K,V> newNode = join(node, removeNode(node.degree));
				addNode(newNode);
			}
		}
		
		/**
		 * Удаление узла из списка
		 * @param index Индекс удаляемого узла
		 * @return Удаленный узел.
		 */
		private Node<K, V> removeNode(int index) {
			Node<K,V> node = rootList.get(index);
			rootList.set(index, null);
			return node;
		}
	}

	/**
	 * Вспомогательная функция поиска узла с заданным ключом в списке
	 * деревьев кучи.
	 * @param list Список деревьев (кольцевой, двунаправленный)
	 * @param key Ключ поиска
	 * @return Найденный узел или null, если узла с заданным ключом нет в списке.
	 */
	private Node<K,V> listFind(Node<K, V> list, K key) {
		if (list == null) return null;
		Node<K,V> current = list;
		do {
			if (current.key.equals(key)) return current;
			Node<K, V> found = listFind(current.son, key);
			if (found != null) return found;
			current = current.next;
		} while (current != list);
		return null;
	}

	/**
	 * Соединение двух узлов одного уровня в один на единицу большего уровня.
	 * @param node1
	 * @param node2
	 * @return
	 */
	private Node<K, V> join(Node<K, V> node1, Node<K, V> node2) {
		assert node1 != null && node2 != null;
		if (comparator.compare(node1.key, node2.key) < 0) {
			Node<K, V> save = node1; node1 = node2; node2 = save;
		}
		node1.son = joinLists(node2, node1.son);
		node2.parent = node1;
		node1.degree++;
		return node1;
	}

	/**
	 * Соединяет два списка узлов в один (присоединяет второй к концу первого)
	 * и выдает укатель на объединенный список. Оба списка могут быть пустыми. 
	 * @param list1	Первый список
	 * @param list2	Второй список
	 * @return Голова объединенного списка (обычно тот же элемент, что и голова 
	 *         первого списка)
	 */
	private Node<K,V> joinLists(Node<K,V> list1, Node<K,V> list2) {
		if (list1 == null) return list2;
		if (list2 == null) return list1;
		Node<K,V> list1Prev = list1.prev;
		list1.prev = list2.prev;
		list2.prev = list1Prev;
		list1Prev.next = list2;
		list1.prev.next = list1;
		return list1;
	}

	/**
	 * Узел node исключается из списка. При этом корректируется 
	 * родительский узел, если он есть. Сыновья узла остаются.
	 * @param node
	 */
	private Node<K,V> cut(Node<K, V> node) {
		assert node != null;
		if (node.parent != null) {
			node.parent.degree--;
			if (node.parent.son == node) {
				node.parent.son = node.next;
			}
			if (node.parent.son == node) {
				node.parent.son = null;
			}
		}
		node.next.prev = node.prev;
		node.prev.next = node.next;
		node.next = node.prev = node;
		node.parent = null;
		node.mark = false;
		return node;
	}
	
	/**
	 * Консолидация узлов корневого списка.
	 */
	private void consolidate() {
		if (root == null) return;
		
		// Создаем новый корневой список и добавляем в него все узлы
		// из имеющегося корневого списка (с консолидацией).
		RootList newRootList = new RootList();
		Node<K,V> node = root;
		while (true) {
			// В корневом списке после переноса с более низких уровней
			// могут оказаться помеченные узлы или узлы с непустой ссылкой на
			// (уже удаленного) родителя. Исправляем это.
			node.parent = null;
			node.mark = false;
			Node<K,V> next = node.next;
			newRootList.addNode(cut(node));
			if (node == next) break;
			node = next;
		}
		
		// Переносим деревья из нового построенного корневого списка,
		// соединяя их в структуру двунаправленного кольцевого списка,
		// И находя по дороге самый приоритетный элемент.
		root = null;
		for (int i = newRootList.size() - 1; i >= 0; --i) {
			node = newRootList.get(i);
			if (node != null) {
				node.next = root == null ? node : root;
				node.prev = root == null ? node : root.prev;
				root = node;
				root.next.prev = root;
				root.prev.next = root;
				if (comparator.compare(root.next.key, root.key) > 0) {
					root = root.next;
				}
			}
		}
	}
	
	private void cascadingCut(Node<K,V> node) {
		Node<K,V> parent = node.parent;
		if (parent != null) {
			if (!node.mark) {
				node.mark = true;
			} else {
				joinLists(root, cut(node));
				cascadingCut(parent);
			}
		}
	}
	
	/**
	 * Проверка правильности работы некоторых основных операций с кучей.
	 * @param args
	 */
	public static void main(String[] args) {
		FibHeap<Integer, String> fh = new FibHeap<Integer, String>();
		fh.add(1, "one");
		fh.add(3, "three");
		fh.add(2, "two");
		System.out.println(fh.extractBest());
		fh.add(6, "six");
		fh.add(5, "five");
		fh.add(4, "four");
		System.out.println(fh.extractBest());
		fh.add(8, "eight");
		fh.add(7, "seven");
		fh.add(9, "nine");
		Node<Integer, String> one = fh.findNode(1);
		System.out.println(fh.changeKey(one, 6));
		while (!fh.isEmpty()) {
			System.out.println(fh.extractBest());
		}
		
		// Построение вырожденной кучи
		FibHeap<Integer, Integer> linearHeap = new FibHeap<Integer, Integer>();
		linearHeap.add(0, 0);
		linearHeap.add(1, 0);
		linearHeap.add(2, 0);
		linearHeap.extractBest();
		for (int x = 1; x <= 11; x+=2) {
			linearHeap.add(x+1, 0);
			linearHeap.add(x+2, 0);
			linearHeap.add(x+3, 0);
			linearHeap.extractBest();
			linearHeap.removeNode(linearHeap.findNode(x+1));
		}
		
		// В результате выполнения предыдущего цикла построена куча
		// из нечетных элементов с единственным вырожденным в цепочку деревом.
		System.out.println("Finished");
	}
}
