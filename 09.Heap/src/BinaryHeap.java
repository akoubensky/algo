import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Организация двоичной кучи и реализация основных операций для работы с ней.
 */
public class BinaryHeap<P> {
	/**
	 * Компаратор для данной кучи. По умолчанию производит "естественное"
	 * сравнение элементов. Все элементы, помещаемые в эту кучу, размещаются в
	 * соответствии со сравнениями, производимыми данным компаратором.
	 */
	private Comparator<P> comparator = (o1, o2) -> {
		if (!(o1 instanceof Comparable && o2 instanceof Comparable)) {
			throw new IllegalArgumentException("Not comparable elements");
		}
		return ((Comparable)o1).compareTo(o2);
	};

	/**
	 * Хранилище элементов кучи - динамический массив элементов.
	 */
	private ArrayList<P> heap = new ArrayList<>();

	/**
	 * Конструктор пустой кучи.
	 */
	public BinaryHeap() {}

	/**
	 * Конструктор пустой кучи с заданным компаратором элементов.
	 * @param comparator    Компаратор для сравнения элементов
	 */
	public BinaryHeap(Comparator<P> comparator) { this.comparator = comparator; }

    /**
     * Конструктор кучи из набора заданных элементов.
     * @param collection    Заданное множество элементов
     */
    public BinaryHeap(Collection<P> collection) {
        heap = new ArrayList<>(collection);
        heapify();
    }

    /**
     * Конструктор кучи из набора заданных элементов с заданным компаратором элементов.
     * @param collection    Заданное множество элементов
     * @param comparator    Компаратор для сравнения элементов
     */
    public BinaryHeap(Collection<P> collection, Comparator<P> comparator) {
        this.comparator = comparator;
        heap = new ArrayList<>(collection);
        heapify();
    }

	/**
	 * Компаратор, используемый в данной куче.
	 * @return компаратор.
	 */
	public Comparator<P> getComparator() { return comparator; }

	/**
	 * Размер кучи - число элементов в ней.
	 * @return Число элементов кучи.
	 */
	public int size() {
		return heap.size();
	}

	/**
	 * Проверка пустоты кучи.
	 * @return true, если куча пуста, false в противном случае.
	 */
	public boolean isEmpty() {
		return heap.isEmpty();
	}

	/**
	 * Выдает элемент с максимальным приоритетом. Возбуждает
	 * прерывание IllegalStateException, если куча не содержит ни одного элемента.
	 * @return Элемент из кучи с максимальным приоритетом.
	 */
	public P peek() {
		if (heap.isEmpty()) throw new IllegalStateException();
		return heap.get(0);
	}

	/**
	 * Удаляет из кучи и выдает элемент с максимальным приоритетом.
	 * Возбуждает прерывание IllegalStateException, если в куче нет ни одного элемента.
	 * После удаления элемента куча реорганизуется так, чтобы на ее вершине
	 * по-прежнему был элемент с максимальным приоритетом.
	 * @return Элемент из кучи с максимальным приоритетом.
	 */
	public P poll() {
		if (heap.isEmpty()) throw new IllegalStateException();
		P retValue = heap.get(0);
		if (heap.size() > 1) {
			heap.set(0, heap.remove(heap.size() - 1));
			pushDown(0);
		} else {
			heap.remove(0);
		}
		return retValue;
	}

	/**
	 * Вспомогательныя функция добавления нового элемента. Предполагает, что элемент
	 * уже сформирован, осталось только добавить его в кучу.
	 * @param element Добавляемый элемент.
	 * @return Позиция добавленного элемента в куче (впоследствии может измениться).
	 */
	public int offer(P element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		heap.add(element);
		return pushUp(heap.size() - 1);
	}

	/**
	 * Простой линейный поиск первого заданного элемента в куче по его приоритету.
	 * @param element Искомый элемент.
	 * @return Позиция первого найденного элемента в куче или -1,
	 *         если такого элемента нет.
	 */
	public int search(P element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < heap.size(); ++i) {
			if (element.equals(heap.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Изменение приоритета элемента с заданной позицией в куче.
	 * @param i Позиция (индекс) элемента в куче.
	 * @param newElem Новый элемент (приоритет которого может быть как выше,
	 *                так и ниже исходного).
	 * @return Новая позиция элемента в куче.
	 */
	public int changePrio(int i, P newElem) {
		if (newElem == null) {
			throw new IllegalArgumentException();
		}
		P oldElem = heap.get(i);
		heap.set(i, newElem);
		if (comparator.compare(newElem, oldElem) < 0) {
			return pushDown(i);
		} else {
			return pushUp(i);
		}
	}

	/**
	 * Добавляет в кучу элементы из другой двоичной кучи. Добавление происходит
	 * в соответствии с компаратором из <i>этой</i> (this) кучи.
	 * @param other Куча, элементы которой добавляются в данную.
	 */
	public void add(BinaryHeap<P> other) {
		add(other.heap);
	}

	/**
	 * Добавляет в кучу элементы из некоторой коллекции элементов. Добавление
	 * происходит в соответствии с компаратором из <i>этой</i> (this) кучи.
	 * @param other Коллекция, элементы которой добавляются в данную.
	 */
	public void add(Collection<P> other) {
		heap.addAll(other);
		heapify();
	}

	/**
	 * Преобразует массив элементов, лежащих в heap, в даоичную кучу.
	 */
	private void heapify() {
		for (int i = (heap.size() - 1)/2; i>=0; i--) {
			pushDown(i);
		}
	}

	/**
	 * Функция протаскивания элемента вверх по куче (по направлению к вершине).
	 * @param index Индекс протаскиваемого элемента.
	 * @return Новая позиция элемента.
	 */
	private int pushUp(int index) {
		P element = heap.get(index);
		while (index > 0) {
			int parentIndex = (index - 1) / 2;
			P parentElement = heap.get(parentIndex);
			if (comparator.compare(parentElement, element) >= 0) {
				break;
			}
			heap.set(index, parentElement);
			index = parentIndex;
		}
		heap.set(index, element);
		return index;
	}

	/**
	 * Функция протаскивания элемента вниз по куче (по направлению от вершины).
	 * @param index Индекс протаскиваемого элемента.
	 * @return Новая позиция элемента.
	 */
	private int pushDown(int index) {
		P element = heap.get(index);
		int size = heap.size();
		while (2 * index + 1 < size) {
			int childIndex = 2 * index + 1;
			P childElement = heap.get(childIndex);
			if (childIndex + 1 < size) {
				P nextChildElement = heap.get(childIndex + 1);
				if (comparator.compare(nextChildElement, childElement) > 0) {
					childIndex++;
					childElement = nextChildElement;
				}
			}
			if (comparator.compare(childElement, element) <= 0) {
				break;
			}
			heap.set(index, childElement);
			index = childIndex;
		}
		heap.set(index, element);
		return index;
	}

	/**
	 * Тестовая функция, проверяющая работу основных алгоритмов работы с кучей.
	 * @param args Не используется.
	 */
	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(7, 5, 2, 8, 4, 9, 10, 1, 6, 3, 0);

		// Создаем двоичную кучу и помещаем в нее элементы заданного массива.
		BinaryHeap<Integer> heap = new BinaryHeap<>();
		heap.add(list);

		// Извлекаем элементы из кучи, начиная с максимального, пока не опустошим всю кучу.
		while (!heap.isEmpty()) {
			System.out.print(" " + heap.poll());
		}
		System.out.println();

		// Сортируем исходный массив методом двоичной кучи и печатаем результат.
		Integer[] array = list.toArray(new Integer[list.size()]);
		Heapsort.sort(array);
        for (Integer element : array) {
            System.out.print(" " + element);
        }
		System.out.println();
	}
}
