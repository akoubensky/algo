package recursive;
import java.util.Arrays;

import calc.Complex;

/**
 * Функции, реализующие быстрое преобразование Фурье
 */
public class FFT {
	/**
	 * Рекурсивная функция прямого быстрого преобразования Фурье
	 * @param a	Массив коэффициентов 
	 * @return	Массив значений многочлена в точках, равных корням из единицы.
	 */
	public static Complex[] FFT(Complex[] a) {
		return FFT(Complex.complement(a), false);
	}
	
	/**
	 * Рекурсивная функция обратного быстрого преобразования Фурье
	 * @param a	Массив значений многочлена в точках, равных корням из единицы 
	 * @return	Массив коэффициентов.
	 */
	public static Complex[] InvFFT(Complex[] a) {
		return FFT(Complex.complement(a), true);
	}
	
	/**
	 * Реализация рекурсивного алгоритма быстрого преобразования Фурье.
	 * @param a			Массив точек для прямого или обратного преобразования
	 * @param invert	Это обратное преобразование?
	 * @return			Массив точек результата.
	 */
	private static Complex[] FFT(Complex[] a, boolean invert) {
		int n = a.length;
		if (n == 1) {
			return a;
		}
		int halfN = n / 2;
		
		// Сомножитель
		Complex w = new Complex(1);
		// Главный корень из единицы со знаком
		Complex wn = Complex.fromAngle((invert ? -Math.PI : Math.PI) * 2 / n);
		// половинные массивы коэффициентов с четными и нечетными номерами
		Complex[] a0 = new Complex[halfN];
		Complex[] a1 = new Complex[halfN];
		for (int i = 0; i < halfN; ++i) {
			a0[i] = a[2*i];
			a1[i] = a[2*i+1];
		}
		
		// Рекурсивный вызов алгоритма
		Complex[] y0 = FFT(a0, invert);
		Complex[] y1 = FFT(a1, invert);
		
		// Результат преобразования Фурье
		Complex[] y = new Complex[n];
		// Генерация результата
		for (int i = 0; i < halfN; ++i) {
			Complex t = Complex.mult(w, y1[i]);
			y[i] = Complex.plus(y0[i], t);
			y[i + halfN] = Complex.minus(y0[i], t);
			w = Complex.mult(w, wn);
			if (invert) {
				// На каждом шаге рекурсии результат делится пополам,
				// так что в конце работы все элементы массива разделятся на n.
				y[i].half();
				y[i+halfN].half();
			}
		}
		
		return y;
	}
}
