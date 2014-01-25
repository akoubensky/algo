
public abstract class Heapsort {
	  /**
	   * Функция сортировки массива, основанная на алгоритмах работы с кучей.
	   * @param <T> Тип элементов массива.
	   * @param array Сортируемый массив.
	   */
	  public static <T extends Comparable<T>> void sort(T[] array) {
	    // Образование кучи из неупорядоченного массива.
	    for (int i = array.length / 2 - 1; i >= 0; --i) {
	      pushDown(array, i, array.length);
	    }
	    // Последовательное извлечение элементов массива из кучи.
	    for (int i = array.length - 1; i > 0; --i) {
	      T element = array[0];
	      array[0] = array[i];
	      array[i] = element;
	      pushDown(array, 0, i);
	    }
	  }
	  
	  /**
	   * Вспомогательная для функции сортировки массива функция. Проталкивает
	   * элемент массива вниз. Последовательное применение этой функции ко всем
	   * элементам первой половины массива приводит к образованию кучи.
	   * @param <T> Тип элементов массива.
	   * @param array Массив, подвергающийся преобразованию.
	   * @param i Индекс проталкиваемого элемента.
	   */
	  private static <T extends Comparable<T>> void pushDown(T[] array, int i, int heapSize) {
	    T element = array[i];
	    for (;;) {
	      int nextIndex = 2 * i + 1;
	      if (nextIndex >= heapSize) break;
	      if (nextIndex + 1 < heapSize && 
	          array[nextIndex + 1].compareTo(array[nextIndex]) > 0) {
	        nextIndex++;
	      }
	      if (element.compareTo(array[nextIndex]) >= 0) break;
	      array[i] = array[nextIndex];
	      i = nextIndex;
	    }
	    array[i] = element;
	  }

}
