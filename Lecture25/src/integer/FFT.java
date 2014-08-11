package integer;

import java.util.Stack;

import calc.Modulo;

public class FFT {
	/**
	 * Нерекурсивная функция прямого быстрого преобразования Фурье
	 * @param a	Массив коэффициентов 
	 * @return	Массив значений многочлена в точках, равных корням из единицы.
	 */
	public static int[] dirFourier(int[] a) {
		return fourier(Modulo.complement(a), false);
	}
	
	/**
	 * Нерекурсивная функция обратного быстрого преобразования Фурье
	 * @param a	Массив значений многочлена в точках, равных корням из единицы 
	 * @return	Массив коэффициентов.
	 */
	public static int[] invFourier(int[] a) {
		return fourier(Modulo.complement(a), true);
	}
	
	/**
	 * Реализация "преобразования бабочки" над двумя элементами массива
	 * с заданным множителем - корнем из единицы
	 * @param a		Исходный массив
	 * @param i		Индекс первого элемента
	 * @param j		Индекс второго элемента
	 * @param root	Множитель
	 */
	private static void butterfly(int[] a, int i, int j, int root, int p) {
		int t = Modulo.mult(root, a[j], p);
		a[j] = Modulo.add(a[i], p-t, p);
		a[i] = Modulo.add(a[i], t, p);
	}
	
	/**
	 * Обращение двоичных битов в заданном числе.
	 * Например, число 010011 превращается в 110010.
	 * @param k	Исходное число
	 * @param n	Степень двойки, задающая число битов обращения
	 * @return	Двоично обращенное число
	 */
	private static int bitReverse(int k, int n) {
		int a = 0;
		int mask = 1;
		while (mask < n) {
			a <<= 1;
			a |= k & 1;
			k >>= 1;
			mask <<= 1;
		}
		
		return a;
	}
	
	/**
	 * Реализация нерекурсивного алгоритма быстрого преобразования Фурье.
	 * @param a			Массив точек для прямого или обратного преобразования
	 * @param invert	Это обратное преобразование?
	 * @return			Массив точек результата.
	 */
	private static int[] fourier(int[] a, boolean invert) {
		// Все значения a[i] должны быть неотрицательными!
		int n = a.length;						// Длина массива
		int maxn = 1 << 13;						// Максимально возможная длина
		assert maxn >= n;
		
		int s = Modulo.findMultiplier(maxn);	// Ищем простое число вида s*n + 1
		int p = s*maxn + 1;						// Модуль для вычислений
		assert p < (1 << 16);					// При больших значениях p вычисления
												// в целых становятся невозможными.
		// Все значения a[i] должны быть меньше p.
		int g = Modulo.generator(p);			// Образующая мультипликативной
												// группы вычетов по модулю p.
		int primRoot = Modulo.power(g, s, p);	// Примитивный корень порядка maxn из единицы
												// в группе вычетов по модулю p.
		if (invert) {
			// Инвертируем корень для обратного преобразования Фурье.
			primRoot = Modulo.genInverse(primRoot, p);
		}
		
		// Запомним значения примитивных корней из единицы степени 2, 4, 8,... n
		Stack<Integer> primRoots = new Stack<Integer>();
		for (int step = maxn; step != 1; step >>= 1) {
			if (step <= n) {
				primRoots.push(primRoot);
			}
			primRoot = Modulo.mult(primRoot, primRoot, p);
		}
		
		// Расстановка элементов в соответствии с "двоично обращенными" индексами
		int[] y = new int[n];
		for (int i = 0; i < n; ++i) {
			y[i] = a[bitReverse(i, n)];
		}
		
		// Цикл по (удвоенным) длинам обрабатываемых массивов
		for (int step = 2; step <= n; step <<= 1) {
			primRoot = primRoots.pop();	// корень из единицы
			for (int k = 0; k < n; k += step) {
				// Соединение "половинок"
				int w = 1;
				for (int j = 0; j < step / 2; ++j) {
					// Выполняем "преобразование бабочки"
					butterfly(y, k + j, k + j + step / 2, w, p);
					w = Modulo.mult(w, primRoot, p);
				}
			}
		}
		
		if (invert) {
			// Для обратного преобразования Фурье делим результаты на n.
			int multiplier = Modulo.genInverse(n, p);
			for (int i = 0; i < n; ++i) {
				y[i] = Modulo.mult(y[i], multiplier, p);
			}
		}
		return y;
	}
}
