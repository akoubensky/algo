import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Реализация некоторых операций над итераторами:
 * - join(it1, it2) - соединение двух итераторов
 * - empty() - итератор, не содержащий элементов
 * - singleItem(item) - итератор из одного элемента.
 */
public abstract class Iterators {
	/**
	 * Функция соединения двух итераторов.
	 * @param iter1 Первый итератор
	 * @param iter2 Второй итератор
	 * @return Итератор, выдающий сначала все элементы первого итератора, 
	 *         а потом все элементы второго.
	 */
	public static <T> Iterator<T> join(final Iterator<T> iter1, final Iterator<T> iter2) {
		return new Iterator<T> () {
			Iterator<T> current = iter1;

			@Override
			public boolean hasNext() {
				return iter1.hasNext() || iter2.hasNext();
			}

			@Override
			public T next() {
				if (iter1.hasNext()) {
					return iter1.next(); 
				} else {
					current = iter2;
					return iter2.next();
				}
			}

			@Override
			public void remove() {
				current.remove();
			}
		};
	}

	/**
	 * Функция, выдающая "пустой" итератор
	 * @return Итератор, не содержащий элементов
	 */
	public static <T> Iterator<T> empty() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public T next() {
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}
		};
	}

	/**
	 * Функция, выдающая итератор одноэлементного множества.
	 * @param item Элемент
	 * @return Итератор
	 */
	public static <T> Iterator<T> singleItem(final T item) {
		return new Iterator<T>() {
			// Признак того, что элемент еще не пройден в итерации.
			boolean hasItem = true;

			@Override
			public boolean hasNext() {
				return hasItem;
			}

			@Override
			public T next() {
				if (!hasItem) throw new NoSuchElementException();
				hasItem = false;
				return item;
			}

			/**
			 * Реализация удаления из структуры здесь не поддерживается,
			 * поскольку отсутствует сама структура.
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
}
