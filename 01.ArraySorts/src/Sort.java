import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 * Эффективность работы сортировок. Сравнение производительности
 * Двух методов: сортировки простыми вставками и сортировки слиянием.
 */
public class Sort {
	/**
	 * Сортировка простыми вставками.
	 * @param array - сортируемый массив.
	 */
	public static <T extends Comparable<T>> void insertSort(T[] array) {
		for (int j = 1; j < array.length; ++j) {
			T key = array[j];
			int i = j-1;
			while (i >= 0 && array[i].compareTo(key) > 0) {
				array[i+1] = array[i];
				--i;
			}
			array[i+1] = key;
		}
	}
	
	/**
	 * Сортировка участка массива методом слияния.
	 * Алгоритм описан в виде рекурсивной функции.
	 * @param array Исходный массив
	 * @param p Индекс начала сортируемого участка
	 * @param r Индекс конца сортируемого участка
	 */
	private static <T extends Comparable<T>> void mergeSort(
			T[] array, int p, int r) {
		if (r - p > 1) {
			int q = (p + r) / 2;
			mergeSort(array, p, q);
			mergeSort(array, q, r);
			merge(array, p, q, r);
		}
	}
	
	/**
	 * Слияние двух участков массива array[p:q] и array[q:r].
	 * @param array Массив, участки которого сливаются.
	 * @param p Индекс начала первого участвка слияния
	 * @param q Индекс конца первого участка и начала второго участка слияния
	 * @param r Индекс конца второго участка слияния.
	 */
	private static <T extends Comparable<T>> void merge(
			T[] array, int p, int q, int r) {
		// Создаем копию участков в промежуточном массиве.
		T[] arrayCopy = Arrays.copyOfRange(array, p, r);
		
		int i1 = 0;     // Индекс по первому участку в копии
		int i2 = q - p; // Индекс по второму учаску в копии
		int i = p;      // Индекс по исходному массиву, куда пересылается результат.
		
		while (i1 < q-p && i2 < r-p) {
			array[i++] = arrayCopy[i1].compareTo(arrayCopy[i2]) < 0 ?
					arrayCopy[i1++] : arrayCopy[i2++];
		}
		while (i1 < q-p) array[i++] = arrayCopy[i1++];
		while (i2 < r-p) array[i++] = arrayCopy[i2++];
	}

	/**
	 * Сортировка массива методом слияния.
	 * @param array
	 */
	public static <T extends Comparable<T>> void mergeSort(T[] array) {
		mergeSort(array, 0, array.length);
	}
	
	// Генератор случайных чисел
	private static Random rnd = new Random();
	
	/**
	 * Типы сортировок
	 */
	private static enum SortType {
		INSERT_SORT,
		MERGE_SORT
	}

	/**
	 * Функция тестирования времени работы сортировок.
	 * @param testType Какую из двух сортировок выбрать
	 * @param length
	 * @return
	 */
	public static long testSort(SortType testType, int length) {
		Integer[] array = new Integer[length];
		for (int i = 0; i < length; ++i) {
			array[i] = rnd.nextInt(10 * length);
		}
		long startTime = Calendar.getInstance().getTimeInMillis();
		if (testType == SortType.INSERT_SORT) insertSort(array); else mergeSort(array);
		long finishTime = Calendar.getInstance().getTimeInMillis();
		return finishTime - startTime;
	}
	
	/**
	 * Тестирует время работы двух методов сортировки с различным числом
	 * элементов в сортируемом массиве. 
	 * @param args
	 */
	public static void main(String[] args) {
		// Числа можно подобрать по своему вкусу.
		int count = 100;
		System.out.format("Повторяем сортировки %d раз%n", count);
		for (int test : new int[] { 50, 300, 10000 }) {
			long millisMerge = 0;
			long millisInsert = 0;
			for (int i = 0; i < count; i++) millisMerge += testSort(SortType.MERGE_SORT, test);
			for (int i = 0; i < count; i++) millisInsert += testSort(SortType.INSERT_SORT, test);
			System.out.format("Сортируем %d элементов%n  Вставками: %d ms, слиянием: %d ms%n",
					test, millisInsert, millisMerge);
		}
	}

}
