package nonrec;
import java.util.Arrays;

import calc.Complex;

/**
 * Функции, реализующие быстрое преобразование Фурье
 */
public class FFT {
	/**
	 * Нерекурсивная функция прямого быстрого преобразования Фурье
	 * @param a	Массив коэффициентов 
	 * @return	Массив значений многочлена в точках, равных корням из единицы.
	 */
	public static Complex[] FFT(Complex[] a) {
		return FFT(Complex.complement(a), false);
	}
	
	/**
	 * Нерекурсивная функция обратного быстрого преобразования Фурье
	 * @param a	Массив значений многочлена в точках, равных корням из единицы 
	 * @return	Массив коэффициентов.
	 */
	public static Complex[] invFFT(Complex[] a) {
		return FFT(Complex.complement(a), true);
	}
	
	/**
	 * Реализация "преобразования бабочки" над двумя элементами массива
	 * с заданным множителем - корнем из единицы
	 * @param a		Исходный массив
	 * @param i		Индекс первого элемента
	 * @param j		Индекс второго элемента
	 * @param root	Множитель
	 */
	private static void butterfly(Complex[] a, int i, int j, Complex root) {
		Complex t = Complex.mult(root, a[j]);
		a[j] = Complex.minus(a[i], t);
		a[i].add(t);
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
	private static Complex[] FFT(Complex[] a, boolean invert) {
		int n = a.length;
		
		// Расстановка элементов в соответствии с "двоично обращенными" индексами
		Complex[] y = new Complex[n];
		for (int i = 0; i < n; ++i) {
			y[i] = new Complex(a[bitReverse(i, n)]);
		}
		
		// Цикл по (удвоенным) длинам обрабатываемых массивов
		for (int step = 2; step <= n; step <<= 1) {
			Complex w1 = Complex.rootOne(step);	// корень из единицы
			if (invert) w1.complement();		// комплексно сопряженный, если
												// преобразование обратное
			for (int k = 0; k < n; k += step) {
				// Соединение "половинок"
				Complex w = new Complex(1);
				for (int j = 0; j < step / 2; ++j) {
					butterfly(y, k + j, k + j + step / 2, w);
					w.multiply(w1);
				}
			}
		}
		
		if (invert) {
			for (int i = 0; i < n; ++i) {
				y[i].divide(n);
			}
		}
		return y;
	}
}
