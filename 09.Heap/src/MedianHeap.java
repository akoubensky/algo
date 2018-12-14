import java.util.Comparator;

/**
 * Реализация структуры данных, представляющей упорядоченный набор элементов, 
 * в которой за логарифмическое время выполняются две операции: 
 * добавление элемента и извлечение медианы.
 * @param <P> Тип элементов структуры.
 */
public class MedianHeap<P extends Comparable<P>> {
	// Две двоичные кучи содержат элементы меньшие и большие медианы соответственно.
	// В "левой" куче на вершине находится максимальный элемент,
	// в правой - на вершине находится минимальный элемент.
	private final BinaryHeap<P> left;
	private final BinaryHeap<P> right;
	
	/**
	 * Конструктор, создающий две кучи с нужными компараторами.
	 */
	public MedianHeap() {
		// Компаратор, задающий "естественный" порядок элементов, создает кучу,
		// в которой на вершине находится максимальный элемент.
		left = new BinaryHeap<P>(new Comparator<P>() {
			@Override
			public int compare(P p0, P p1) {
				return p0.compareTo(p1);
			}
		});
		// Компаратор, задающий "обратный" порядок элементов, создает кучу,
		// в которой на вершине находится минимальный элемент.
		right = new BinaryHeap<P>(new Comparator<P>() {
			@Override
			public int compare(P p0, P p1) {
				return p1.compareTo(p0);
			}
		});
	}
	
	/**
	 * Размер (количество элементов) структуры данных
	 * @return Количество элементов в структуре.
	 */
	public int size() { 
		return left.size() + right.size(); 
	}
	
	/**
	 * Проверяет структуру на пустоту (отсутствие элементов)
	 * @return True, если в структуре нет элементов, false в противном случае.
	 */
	public boolean isEmpty() { 
		return left.isEmpty() && right.isEmpty(); 
	}
	
	/**
	 * Выдает медиану.
	 * @return Медиана
	 */
	public P getMedian() {
		if (isEmpty()) {
			throw new IllegalStateException("Cannot extract median from an empty heap");
		}
		// Медиана находится на вершине левой кучи.
		return left.peek();
	}
	
	/**
	 * Удаляет медиану и выдает ее в качестве результата.
	 * @return Медиана
	 */
	public P retreiveMedian() { 
		if (isEmpty()) {
			throw new IllegalStateException("Cannot extract median from an empty heap");
		}
		// Медиана находится на вершине левой кучи.
		P result = left.poll();
		// Балансировка размеров куч, если нужно.
		if (left.size() < right.size()) {
			right2Left();
		}
		return result; 
	}
	
	/**
	 * Добавляет новый элемент в структуру.
	 * @param item Добавляемый элемент.
	 */
	public void add(P item) {
		Comparator<P> comparator = left.getComparator();
		// Сначала элемент добавляем в одну из двух куч
		// в соответствии с его величиной. Потом, возможно,
		// необходимо провести балансировку размеров куч. 
		if (isEmpty() || comparator.compare(item, left.peek()) <= 0) {
			left.offer(item);
			// Балансировка размеров, если нужно.
			if (left.size() > right.size() + 1) {
				left2Right();
			}
		} else {
			right.offer(item);
			// Балансировка размеров, если нужно.
			if (left.size() < right.size()) {
				right2Left();
			}
		}
	}
	
	/**
	 * Перенос "лучшего" элемента из левой кучи в правую.
	 */
	private void left2Right() {
		P best = left.poll();
		right.offer(best);
	}
	
	/**
	 * Перенос "лучшего" элемента из правой кучи в левую.
	 */
	private void right2Left() {
		P best = right.poll();
		left.offer(best);
	}
	
	/**
	 * Проверка работоспособности некоторых операций с определяемой структурой.
	 * @param args Не используется.
	 */
	public static void main(String[] args) {
		MedianHeap<Integer> mHeap = new MedianHeap<Integer>();
		// Добавляем в структуру 25 последовательных целых чисел.
		for (int i = 1; i <= 25; ++i) {
			mHeap.add(i);
		}
		// Извлекаем все числа по очереди, начиная с середины.
		while (!mHeap.isEmpty()) {
			System.out.print(mHeap.retreiveMedian() + " ");
		}
		System.out.println();
	}
}
