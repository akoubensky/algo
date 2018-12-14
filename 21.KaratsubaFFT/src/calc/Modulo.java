package calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Вычисления по заданному модулю и некоторые специальные функции
 */
public class Modulo {
	/**
	 * Сложение по заданному модулю
	 * @param a		Первое слагаемое
	 * @param b		Второе слагаемое
	 * @param mod	Модуль
	 * @return		Сумма
	 */
	public static int add(int a, int b, int mod) {
		return (a + b) % mod;
	}
	
	/**
	 * Умножение по заданному модулю
	 * @param a		Первый сомножитель
	 * @param b		Второй сомножитель
	 * @param mod	Модуль
	 * @return		Произведение
	 */
	public static int mult(int a, int b, int mod) {
		return (int)((a * (long)b) % mod);
	}
	
	/**
	 * "Быстрое" возведение в натуральную степень по заданному модулю.
	 * @param x		Основание
	 * @param a		Показатель
	 * @param mod	Модуль
	 * @return		Степень
	 */
	public static int power(int x, int a, int mod) {
		int z = 1;
		// inv: x ** a * z = X ** A
		while (a != 0) {
			if ((a & 1) == 0) {
				x = mult(x, x, mod);
				a >>= 1;
			} else {
				z = mult(z, x, mod);
				a--;
			}
		}
		return z;
	}
	
	/**
	 * Проверка простоты числа
	 * @param p	Тестируемое число
	 * @return	true, если число простое, иначе false
	 */
	public static boolean isPrime(int p) {
		// assume p > 2
		if ((p & 1) == 0) return false;
		for (int d = 3; d * d <= p; d+= 2) {
			if (p % d == 0) return false;
		}
		return true;
	}
	
	/**
	 * Для заданного числа (степни двойки) подбирает множитель k такой, что n*k + 1 - простое.
	 * Эвристика: проверке подлежит не более log n множителей.
	 * @param n	Исходная степень двойки
	 * @return	Множитель k такой, что k*n + 1 - простое
	 */
	public static int findMultiplier(int n) {
		// Полагаем, что n - степень двойки
		assert n == Integer.highestOneBit(n);
		for (int k = 1; ; ++k) {
			if (isPrime(n*k + 1)) return k;
		}
	}
	
	/**
	 * Находит генератор группы по простому модулю.
	 * @param p	Модуль
	 * @return	Генератор
	 */
	public static int generator(int p) {
		List<Integer> fact = new ArrayList<Integer>();
		int phi = p - 1;	// Функция Эйлера
		
		// Факторизация числа phi
		int n = phi;
		for (int i = 2; i * i <= n; ++i) {
			if (n % i == 0) {
				fact.add(i);
				while (n % i == 0)
					n /= i;
			}
		}
		if (n > 1)
			fact.add(n);

		// Ищем генератор
		genloop:
		for (int gen = 2; gen <= p; ++gen) {
			for (int factor : fact) {
				if (power(gen, phi / factor, p) == 1) {
					continue genloop;
				}
			}
			return gen;
		}
		return -1;
	}
	
	/**
	 * Дополнение массива нулями до длины, равной степени двойки
	 * @param a	Исходный массив
	 * @return	Дополненный массив (если длина уже есть степень двойки -
	 * 			то тот же самый исходный массив)
	 */
	public static int[] complement(int[] a) {
		int nn = Integer.highestOneBit(a.length);
		return a.length == nn ? a : Arrays.copyOf(a, 2*nn);
	}
	
	/**
	 * Дополнение массива нулями до заданной длины
	 * @param a		Исходный массив
	 * @param nn	Длина, до которой необходимо дополнить массив
	 * @return		Дополненный нулями массив
	 */
	public static int[] complement(int[] a, int nn) {
		return a.length == nn ? a : Arrays.copyOf(a, nn);
	}
	
	/**
	 * Обращение ненулевого числа в поле по простому модулю.
	 * @param a	Исходное число.
	 * @param p	Модуль
	 * @return	Обратное к заданному число
	 */
	public static int genInverse(int a, int p) {
		// a^(p-1) = 1 mod p, поэтому g^(p-2)*g = 1
		return power(a, p-2, p);
	}
	
	public static void main(String[] args) {
		int n = 32;					// Степень двойки
		int k = findMultiplier(n);	// Множитель, дающий простое k*n + 1
		System.out.println(k);
		int p = n * k + 1;			// Простой модуль
		System.out.println(p);
		int g = generator(p);		// Генератор группы чисел по простому модулю
		System.out.println(g);
		
		// Проверяем, что найден генератор группы
		int[] all = new int[p-1];
		for (int j = 1; j < p; ++j) {
			all[j-1] = power(g, j, p);
		}
		Arrays.sort(all);
		// Должны быть все числа от 1 до p-1
		System.out.println(Arrays.toString(all));
		int w = power(g, k, p);		// Корень степени n из единицы
		System.out.println(w);
		int inv = genInverse(w, p);
		System.out.println(inv);
		System.out.println(mult(w, inv, p));
		int[] roots = new int[n];	// Все n корней степени n из единицы
		for (int j = 1; j <= n; ++j) {
			roots[j-1] = power(w, j, p);
		}
		System.out.println(Arrays.toString(roots));
		// Отсортируем для проверки, что все корни различны.
		Arrays.sort(roots);
		System.out.println(Arrays.toString(roots));
		
	}
}
