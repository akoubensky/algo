import java.util.Comparator;
import java.util.Iterator;

/**
 * Реализация биномиальной кучи. Реализованы операции:
 * - size() Размер кучи (число элементов)
 * - getBest() Поиск "лучшего" элемента;
 * - retrieveBest() Извлечение "лучшего" элемента;
 * - add(key) Добавление нового элемента;
 * - add(heap) Добавление биномиальной кучи;
 * - find(key), findNode(key) Поиск узла по ключу;
 * - changeKey(node, key) Повышение приоритета узла;
 * - removeNode(node) Удаление узла.
 *
 * @param <K> Тип ключей.
 */
public class BinomHeap<K extends Comparable<K>> {
	/**
	 * Компаратор для сравнения ключей. По умолчанию - "естественное" сравнение.
	 */
	private Comparator<K> comparator = new Comparator<K>() {
		public int compare(K o1, K o2) {
			return o1.compareTo(o2);
		}
	};

	/**
	 * Класс, представляющий узел биномиального дерева.
	 *
	 * @param <K> Тип ключа.
	 */
	public static class Node<K> {
		/**
		 * Ссылка на родительский узел (более высокого уровня).
		 */
		private Node<K> parent = null;

		/**
		 * Ключ, хранящийся в узле.
		 */
		private K key;

		/**
		 * Уровень узла в дереве.
		 */
		private int degree = 0;

		/**
		 * Ссылка на старшего сына (узел предыдущего уровня).
		 */
		private Node<K> son = null;

		/**
		 * Ссылка на соседний узел (предыдущего уровня).
		 */
		private Node<K> brother = null;

		/**
		 * Конструктор узла - недоступен извне класса, так что
		 * создавать узлы можно только изнутри класса BinomHeap. 
		 * @param key Ключ нового узла.
		 */
		private Node(K key) { this.key = key; }

		/**
		 * Функция доступа к ключу узла.
		 * @return Ключ узла.
		 */
		public K key() { return key; }

		/**
		 * Функция соединения двух биномиальных деревьев одного уровня.
		 * Реализована в виде метода узла, к кторому в качестве потомка 
		 * присоединяется другой узел.
		 * @param node Присоединяемый узел
		 */
		private void addNode(Node<K> node) {
			if (node == null || node.degree != this.degree) {
				// Соединять можно только узлы одного уровня.
				throw new IllegalArgumentException("incompatible nodes");
			}
			// Соединение деревьев.
			node.parent = this;
			node.brother = son;
			son = node;
			// Уровень объединенного дерева повышается.
			++degree;
		}
	}

	/**
	 * Голова корневого списка биномиальных деревьев.
	 */
	private Node<K> head = null;
	private int size = 0;

	/**
	 * Конструктор пустого дерева со сравнением узлов "по умолчанию".
	 */
	public BinomHeap() {}

	/**
	 * Конструктор пустого дерева с заданным компаратором для сравнением узлов.
	 */
	public BinomHeap(Comparator<K> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * Количество элементов в куче
	 * @return
	 */
	public int size() { return size; }

	/**
	 * Выдает элемент кучи с максимальным приоритетом.
	 * Оценка времени работы - O(log N), где N - число узлов.
	 * @return Элемент с максимальным приоритетом.
	 */
	public K getBest() {
		// Если "лусший" узел отсутствует (дерево пустое),
		// то возникнет ситуация NullPointerException
		return getBestNode().key; 
	}

	/**
	 * Выдает ссылку на узел с максимальным приоритетом.
	 * Оценка времени работы - O(log N), где N - число узлов.
	 * @return Ссылка на узел с максимальным приоритетом или null, если дерево пусто.
	 */
	public Node<K> getBestNode() {
		if (head == null) {
			return null;
		}
		Node<K> minNode = head;
		// Поиск по списку биномиальных деревьев корня с максимальным приоритетом.
		for (Node<K> curr = head.brother; 
				curr != null; 
				curr = curr.brother) {
			if (comparator.compare(minNode.key, curr.key) < 0) {
				minNode = curr;
			}
		}
		return minNode; 
	}

	/**
	 * Вспомогательная функция - соединение двух корневых списков
	 * в один. Все деревья упорядочиваются по номеру уровня, но
	 * при этом допускается наличие в одном списков нескольких
	 * деревьев одного и того же уровня. Оценка времени работы -
	 * O(log N), где N - общее число узлов в обоих корневых списках.
	 * 
	 * @param rootList Добавляемый список деревьев.
	 */
	private void merge(Node<K> rootList) {
		// Второй корневой список предполагается непустым.
		assert rootList != null;
		
		Node<K> p1 = head;       // Указатель по первому корневому списку.
		Node<K> p2 = rootList;   // Указатель по второму корневому списку.
		Node<K> pm = null;       // Указатель по корневому списку объединенной кучи.
		while (p1 != null && p2 != null) {
			if (p1.degree < p2.degree) {
				if (pm == null) {
					head = p1;
				} else {
					pm.brother = p1;
				}
				pm = p1;
				p1 = p1.brother;
			} else {
				if (pm == null) {
					head = p2;
				} else {
					pm.brother = p2;
				}
				pm = p2;
				p2 = p2.brother;
			}
		}
		pm.brother = p1 == null ? p2 : p1;
	}

	/**
	 * Соединение двух куч с одним и тем же компаратором.
	 * Если компараторы двух куч дают разные результаты сравнения
	 * двух элементов, то результат работы непредсказуем!
	 * Оценка времени работы - O(log N), Где N - общее число узлов в кучах.
	 * @param heap Добавляемая куча. Разрушается после добавления.
	 */
	public void add(BinomHeap<K> heap) {
		if (heap.head == null) {
			return;
		}
		size += heap.size;
		if (head == null) { 
			head = heap.head; 
			return; 
		}
		// Обе кучи не пустые, сливаем корневые списки.
		merge(heap.head);
		// Теперь проходим по корневому списку и объединяем
		// деревья одного уровня ("сложение в столбик").
		consolidate();
	}
	
	/**
	 * Функция консолидации дерева. Проходит по корневому списку и
	 * объединяет деревья одного уровня так, чтобы в корневом списке
	 * не осталось больше деревьев одного уровня.
	 */
	private void consolidate() {
		Node<K> prev = null;
		Node<K> curr = head;
		Node<K> next = head.brother;
		while (next != null) {
			if (next.degree != curr.degree ||
					(next.brother != null &&
					curr.degree == next.brother.degree)) {
				// Случай 1. Одно дерево данного уровня или три. Пропускаем дерево.
				prev = curr; curr = next;
			} else if (comparator.compare(curr.key, next.key) > 0) {
				// Случай 2. Соединяем два соседних дерева одного уровня в одно.
				curr.brother = next.brother;
				curr.addNode(next);
			} else {
				// Случай 3. Симметричный случаю (2), но другой узел становится корневым.
				if (prev == null) head = next; else prev.brother = next;
				next.addNode(curr);
				curr = next;
			}
			next = curr.brother;
		}
	}

	/**
	 * Добавление одного узла сводится к добавлению узла в корневой список
	 * с последующей консолидацией дерева. Оценка времени работы - O(log N)
	 * @param key Ключ нового узла.
	 */
	public void add(K key) {
		Node<K> newNode = new Node<>(key);
		newNode.brother = head;
		head = newNode;
		size++;
		consolidate();
	}

	/**
	 * Удаление узла из корневого списка. Функция используется при извлечении
	 * самого приоритетного значения из кучи и при удалении заданного узла.
	 * Оценка времени работы - O(log N).
	 * @param node Удаляемый узел из корневого списка.
	 * @param predNode Предыдущий узел в корневом списке.
	 * @return Удаленный узел
	 */
	private Node<K> removeRootNode(Node<K> node) {
		assert node != null && node.parent == null;
		
		// 1. Поиск предыдущего узла в списке (если есть).
		Node<K> predNode = null;
		if (head != node) {
			predNode = head;
			while (predNode.brother != node) {
				predNode = predNode.brother;
			}
		}
		
		// 2. Удаление всего дерева из корневого списка.
		if (predNode == null) {
			head = node.brother; 
		} else { 
			predNode.brother = node.brother;
		}
		
		if (node.son != null) {
			// 3. Поддеревья удаляемого узла образуют новый корневой список.
			Node<K> rootList = null;
			for (Node<K> child = node.son; child != null; ) {
				Node<K> next = child.brother;
				child.brother = rootList;
				rootList = child;
				child = next;
			}
		
			// 4. Полученный корневой список добавляется к исходному.
			merge(rootList);
		}
		
		// Удаленный узел возвращается в качестве результата
		size--;
		return node;
	}

	/**
	 * Извлечение узла с самым высоким приоритетом.
	 * Оценка времени работы - O(log N).
	 * @return Удаленный узел.
	 */
	public Node<K> retrieveBestNode() { 
		if (head == null) return null;
		// Поиск узла с самым высоким приоритетом.
		Node<K> bestNode = head;
		for (Node<K> curr = head.brother; curr != null; curr = curr.brother) {
			if (comparator.compare(curr.key, bestNode.key) >= 0) {
				bestNode = curr;
			}
		}
		return removeRootNode(bestNode); 
	}

	/**
	 * Извлечение узла с самым высоким приоритетом.
	 * При попытке извлечения узла из пустого дерева возникает NullPointerException.
	 * Оценка времени работы - O(log N).
	 * @return Информационное поле удаленного узла.
	 */
	public K retrieveBest() {
		return retrieveBestNode().key;
	}

	/**
	 * Повышение приоритета заданного узла и перемещение его на
	 * более высокий уровень в биномиальном дереве.
	 * Оценка времени работы - O(log N).
	 * @param node Узел, у которого изменяется приоритет.
	 * @param newKey Новый ключ (приоритет).
	 * @return Ссылка на новую позицию узла с измененным приоритетом.
	 */
	public Node<K> changeKey(Node<K> node, K newKey) {
		if (node == null) throw new NullPointerException();
		if (comparator.compare(newKey, node.key) < 0) {
			throw new IllegalArgumentException("Понижение приоритета не допускается.");
		}
		// "Протаскивание" информации вверх по дереву.
		Node<K> current = node;
		while (current.parent != null && comparator.compare(newKey, current.parent.key) > 0) {
			current.key = current.parent.key;
			current = current.parent;
		}
		current.key = newKey;
		return current;
	}

	/**
	 * Удаление заданного узла из кучи.
	 * Оценка времени работы - O(log N).
	 * @param node Удаляемый узел.
	 */
	public Node<K> removeNode(Node<K> node) {
		if (node == null) throw new NullPointerException();
		// Сначала узел протаскивается в корень дерева...
		Node<K> current = node;
		while (current.parent != null) {
			current.key = current.parent.key;
			current = current.parent;
		}
		// ...и затем удаляется.
		return removeRootNode(current);
	}

	/**
	 * Поиск всех узлов в куче с заданным приоритетом (ключом).
	 * Оценка времени работы - O(N), где N - число узлов в куче.
	 * Некоторая оптимизация может быть достигнута, если
	 * в дереве много узлов с заведомо меньшим приоритетом.
	 * @param key Ключ поиска.
	 * @return Итератор всех узлов с заданным ключом поиска.
	 */
	public Iterator<Node<K>> findNode(final K key) {
		return new Iterator<Node<K>>() {
			Node<K> current = head;   // Текущий исследуемый узел.
			Node<K> toRemove = null;  // Последний пройденный узел.

			// При создании итератора сразу ищем первый узел с заданным ключом.
			{ findKey(); }

			/**
			 * Функция поиска очередного узла с заданным ключом.
			 */
			private void findKey() {
				while (current != null && !current.key.equals(key)) {
					if (comparator.compare(key, current.key) > 0 || current.degree == 0) {
						if (current.brother != null || current.parent == null) {
							current = current.brother;
						} else {
							current = current.parent.brother;
						}
					} else {
						current = current.son;
					}
				}
			}

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public Node<K> next() {
				if (current == null) throw new IllegalStateException();
				toRemove = current;
				// Делаем переход к очередному узлу.
				if (current.degree > 0) {
					current = current.son;
				} else if (current.parent == null) {
					current = current.brother;
				} else {
					current = current.parent.brother;
				}
				// Ищем узел с заданным ключом.
				findKey();
				return toRemove;
			}

			@Override
			public void remove() {
				if (toRemove == null) throw new IllegalStateException();
				removeNode(toRemove);
				toRemove = null;
			}
		};
	}

	/**
	 * Поиск всех узлов в куче с заданным приоритетом (ключом).
	 * Оценка времени работы - O(N), где N - число узлов в куче.
	 * Некоторая оптимизация может быть достигнута, если
	 * в дереве много узлов с заведомо меньшим приоритетом.
	 * @param key Ключ поиска.
	 * @return Итератор всех информационных значений в узлах с заданным ключом поиска.
	 */
	public Iterator<K> find(final K key) {
		return new Iterator<K>() {
			/**
			 * Итератор узлов.
			 */
			Iterator<Node<K>> internal = findNode(key);

			@Override
			public boolean hasNext() {
				return internal.hasNext();
			}

			@Override
			public K next() {
				return internal.next().key;
			}

			@Override
			public void remove() {
				internal.remove();
			}
		};
	}

	/**
	 * Отладочная функция (unit test).
	 * @param args Не используется.
	 */
	public static void main(String[] args) {
		// Заданный набор из 13 ключей.
		int[] keys = { 10, 1, 12, 25, 18, 6, 8, 14, 29, 11, 17, 38, 27 };
		// "Обратный" компаратор - приоритет будет тем выше, чем значение меньше.
		Comparator<Integer> reversedComparator = Comparator.reverseOrder();

		// Создаем кучу из первых 6 узлов.
		BinomHeap<Integer> t1 = new BinomHeap<>(reversedComparator);
		for (int i = 0; i < 6; ++i) {
			t1.add(keys[i]);
		}

		// Создаем кучу из следующих 7 узлов.
		BinomHeap<Integer> t2 = new BinomHeap<>(reversedComparator);
		for (int i = 6; i < 13; ++i) {
			t2.add(keys[i]);
		}

		// Соединяем обе кучи
		t1.add(t2);

		// Проходим объединенную кучу и печатаем число узлов в ней.
		System.out.println("size=" + t1.size());

		// Найдем в куче узел с заданным ключом и удалим его.
		Iterator<Integer> it = t1.find(14);
		if (it.hasNext()) {
			Integer found = it.next();
			System.out.println("Found: " + found);
			it.remove();
		} else {
			System.out.println("Not found");
		}

		// Удалим все узлы по очереди, используя функцию
		// удаления самого приоритетного узла.
		Integer min;
		while(t1.size() != 0) {
			min = t1.retrieveBest();
			System.out.println("number=" + t1.size() + ", min=" + min);
		}
	}
}
