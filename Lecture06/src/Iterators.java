import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Некоторые утилиты по работе с итераторами.
 * - nullIterator() выдает итератор пустого множества
 * - singleElement(item) выдает итератор одноэлементного множества
 * - join(it1, it2) выдает последовательное соединение двух итераторов 
 */
public abstract class Iterators {
	/**
	 * Функция, выдающая пустой итератор
	 * @param <T> Тип элементов итератора
	 * @return Итератор
	 */
	public static <T> Iterator<T> nullIterator() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() { return false; }

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
	 * Функция, выдающая одноэлементный итератор.
	 * @param <T>
	 * @param item
	 * @return
	 */
	public static <T> Iterator<T> singleElement(final T item) {
		return new Iterator<T>() {
			boolean hasItem = true;

			@Override
			public boolean hasNext() { 
				return hasItem; 
			}

			@Override
			public T next() {
				if (hasItem) {
					hasItem = false;
					return item;
				} else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Функция последовательного соединения двух итераторов.
	 * @param <T> - Тип элементов итераторов
	 * @param it1 - Первый итератор
	 * @param it2 - Второй итератор
	 * @return - Соединенный итератор.
	 */
	public static <T> Iterator<T>join(final Iterator<T> it1, final Iterator<T> it2) {
		return new Iterator<T>() {
			Iterator<T> current = it1;

			@Override
			public boolean hasNext() {
				return it1.hasNext() || it2.hasNext();
			}

			@Override
			public T next() {
				if (it1.hasNext()) {
					return it1.next();
				} else {
					current = it2;
					return it2.next();
				}
			}

			@Override
			public void remove() {
				current.remove();
			}
		};
	}
}
