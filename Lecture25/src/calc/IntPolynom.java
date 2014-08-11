package calc;

import java.util.Arrays;

public class IntPolynom {
	// Полином представлен своими коэффициентами
	private int[] a;
	
	/**
	 * Конструктор по заданным коэффициентам
	 * @param a
	 */
	public IntPolynom(int[] a) {
		this.a = Arrays.copyOf(a, a.length);
	}
	
	/**
	 * Конструктор копирования
	 * @param p
	 */
	public IntPolynom(IntPolynom p) {
		this(p.a);
	}
	
	/**
	 * Представление полинома в стандартном виде (но не более, чем до 20-й степени)
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < a.length; ++i) {
			if (i > 0) builder.append(" + ");
			builder.append(a[i]);
			if (i > 0) {
				builder.append(" x^").append(i);
			}
			if (i >= 20) {
				builder.append("...");
				break;
			}
		}
		return builder.toString();
	}
	
	/**
	 * Коэффициенты полинома
	 * @return
	 */
	public int[] coef() { return a; }
	
	/**
	 * Традиционный алгоритм умножения многочленов.
	 * @param p1	Первый многочлен
	 * @param p2	Второй многочлен
	 * @return		Их произведение
	 */
	public static IntPolynom multiply(IntPolynom p1, IntPolynom p2) {
		int[] a = p1.coef();
		int[] b = p2.coef();
		int n = a.length;
		int m = b.length;
		int[] c = new int[n+m-1];
		for (int k = 0; k < n+m-1; ++k) {
			int s = 0;
			for (int i = 0; i <= k; ++i) {
				if (i >= n || k-i >= m) {
					continue;
				}
				s += a[i] * b[k-i];
			}
			c[k] = s;
		}
		
		return new IntPolynom(c);
	}
	
	/**
	 * Ближайшая сверху степень двойки к заданному числу.
	 * @param n	Исходное число
	 * @return	Ближайшая степень двойки.
	 */
	private static int power2(int n) {
		int nn = Integer.highestOneBit(n);
		return (n == nn ? n : nn << 1);
	}
	
	/**
	 * Умножение полиномов с использованием нерекурсивного алгоритма
	 * быстрого преобразования Фурье
	 * @return
	 */
	public static IntPolynom multiplyFFT(IntPolynom p1, IntPolynom p2) {
		int[] a = p1.coef();
		int[] b = p2.coef();
		int length = power2(2 * Math.max(a.length, b.length));
		a = Modulo.complement(a, length);
		b = Modulo.complement(b, length);
		
		// Прямое преобразование Фурье
		int[] ya = integer.FFT.dirFourier(a);
		int[] yb = integer.FFT.dirFourier(b);
		// Перемножение
		for (int i = 0; i < ya.length; ++i) {
			ya[i] *= yb[i];
		}
		// Обратное преобразование Фурье
		return new IntPolynom(integer.FFT.invFourier(ya));
	}

}
