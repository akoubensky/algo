import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Структура данных, поддерживающая быстрое (за O(log N)) вычисление
 * суммы значений на заданном отрезке массива.
 * Тип элементов массива предполагается целым.
 */
public class SummIntervals implements Iterable<Integer>{
	// Структура в виде дерева
	private Integer[] tree;
	// Размер исходного массива
	private int size;
	// Длина массива, "подравненная" до ближайшей степени двойки
	private int length;
	
	//-----------------------------------------------------
	// Публичные функции (интерфейс)
	//-----------------------------------------------------
	
	/**
	 * Конструктор получает исходный массив. Нейтральное значение
	 * полагается равным нулю.
	 * @param array Исходный массив
	 */
	public SummIntervals(Integer[] array) {
		size = array.length;
		// Вычисляем "подравненную" длину.
		length = Integer.highestOneBit(array.length);
		if (length < size) {
			length <<= 1;
		}
		
		// Массив хранимых значений будет иметь длину от 2*size до почти 4*size. 
		tree = new Integer[2 * length];
		
		// Заполнение второй половины массива элементами 
		// (в конец записываем "нейтральные" элементы).
		for (int i = 0; i < size; ++i) {
			tree[i + length] = array[i];
		}
		for (int i = size; i < length; ++i) {
			tree[i + length] = 0;
		}
		
		// Заполнение первой половины массива (построение дерева).
		for (int i = length - 1; i > 0; --i) {
			tree[i] = tree[2 * i] + tree[2 * i + 1];
		}
	}
	
	/**
	 * Длина исходного массива
	 * @return
	 */
	public int size() { return size; }
	
	/**
	 * Доступ к элементу исходного массива по индексу
	 * @param i Индекс
	 * @return Элемент
	 */
	public Integer get(int i) {
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
		return tree[i + length];
	}
	
	/**
	 * Изменение элемента с заданным индексом
	 * @param i Индекс
	 * @param item Новое значение элемента
	 */
	public void set(int i, Integer item) {
		int ind = i + length;
		tree[ind] = item;
		// Продвигаемся вверх по дереву от измененного элемента
		for (int p = ind / 2; p > 0; p /= 2) {
			tree[p] = tree[2*p] + tree[2*p + 1];
		}
	}
	
	/**
	 * Вычисление суммы значений на отрезке [a, b)
	 * @param a Левая граница отрезка (включается)
	 * @param b Правая граница отрезка (исключается)
	 * @return Максимальное значение
	 */
	public Integer sum(int a, int b) {
		// Проверяем правильность индексов
		if (a < 0 || a >= b || b > size) {
			throw new IndexOutOfBoundsException();
		}
		// Вызываем вспомогательную рекурсивную функцию
		return sum(a, b, 1, 0, length);
	}

	/**
	 * Итератор, выдающий по очереди все элементы исходного массива
	 * @return
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int i = 0;
			
			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public Integer next() {
				if (!hasNext()) throw new NoSuchElementException();
				return get(i++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	/**
	 * Функция находит минимальное число первых элементов массива,
	 * сумма которых больше или равна заданного значения.
	 * Если сумма всех элементов все еще меньше заданного значения,
	 * то выдается число элементов, на единицу большее размера массива.
	 * 
	 * Предполагается, что все элементы массива неотрицательны.
	 * 
	 * @param value Заданная сумма префиксных элементов
	 * @return Число элементов в префиксе, сумма элементов которого впервые
	 *         превосходит заданное.
	 */
	public int findPrefix(Integer value) {
		int node = 1;		// Рассматриваемый узел дерева
		int low = 0;		// Левая граница рассматриваемого интервала
		int high = length;	// Правая граница рассматриваемого интервала
		
		while (true) {
			if (node >= length || value >= tree[node]) {
				// Дошли до листа дерева или нашли сумму, удовлетворяющую условию.
				return Math.min(size + 1, high);
			}
			if (tree[2*node] >= value) {
				// Продолжаем поиск в левом поддереве
				node = 2*node;
				high = (low + high) / 2;
			} else {
				// Продолжаем поиск в правом поддереве, соответственно уменьшив
				// искомую сумму
				value -= tree[2*node];
				node = 2*node + 1;
				low = (low + high) / 2;
			}
		}
	}
	
	//-----------------------------------------------------
	// Скрытые вспомогательные функции
	//-----------------------------------------------------
	
	/**
	 * Рекурсивная функция вычисления суммы значений на отрезке [a, b)
	 * @param a Левая граница отрезка (включается)
	 * @param b Правая граница отрезка (исключается)
	 * @param root Индекс корневого элемента дерева с интервалом [min, max)
	 * @param min Левая граница покрываемого корнем интервала (включается)
	 * @param max Правая граница покрываемого корнем интервала (исключается
	 * @return Сумма на отрезке
	 */
	private Integer sum(int a, int b, int root, int min, int max) {
		if (a == min && b == max) {
			// Интервал корня совпадает с интересующим нас отрезком
			return tree[root];
		}
		// Делим интервал корня на две половины индексом middle.
		int middle = (min + max) / 2;
		if (b <= middle) {
			// Отрезок целиком лежит в левом интервале
			return sum(a, b, 2 * root, min, middle); 
		}
		if (a >= middle) {
			// Отрезок целиком лежит в правом интервале
			return sum(a, b, 2 * root + 1, middle, max);
		}
		// Суммы считаются отдельно по левой и правой половинам интервала,
		// затем берется максимум из вычисленных значений.
		return sum(a, middle, 2 * root, min, middle) + 
			   sum(middle, b, 2 * root + 1, middle, max);
	}
	
	/**
	 * Проверка правильности работы основных операций
	 * @param args
	 */
	public static void main(String[] args) {
		Random rnd = new Random();
		Integer[] array = new Integer[25];
		for (int i = 0; i < 25; ++i) {
			array[i] = rnd.nextInt(50);
		}
		System.out.println(Arrays.toString(array));
		SummIntervals intervalStructure = new SummIntervals(array);
		System.out.println("[0, 8): " + intervalStructure.sum(0, 8));
		System.out.println("[3, 11): " + intervalStructure.sum(3, 11));
		
		intervalStructure.set(13, rnd.nextInt(50));
		intervalStructure.set(22, rnd.nextInt(50));
		boolean first = true;
		for (Integer item : intervalStructure) {
			System.out.print(first ? "[" : ", ");
			System.out.print(item);
			first = false;
		}
		System.out.println("]");
		
		System.out.println("[8, 16): " + intervalStructure.sum(8, 16));
		System.out.println("[20, 25): " + intervalStructure.sum(20, 25));
		System.out.println("[24, 25): " + intervalStructure.sum(24, 25));
		
		System.out.println("Prefix sums:");
		int sum = 0;
		int i = 1;
		first = true;
		for (Integer item : intervalStructure) {
			System.out.print(first ? "[" : ", ");
			System.out.print((i++) + ":" + (sum += item));
			first = false;
		}
		System.out.println("]");
		System.out.println("summa = 300; prefix = " + intervalStructure.findPrefix(300) + " elements");
		System.out.println("summa = 600; prefix = " + intervalStructure.findPrefix(600) + " elements");
		System.out.println("summa = 0; prefix = " + intervalStructure.findPrefix(0) + " elements");
		System.out.println("summa = 1000; prefix = " + intervalStructure.findPrefix(1000) + " elements");
	}
}
