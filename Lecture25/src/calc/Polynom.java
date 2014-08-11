package calc;
import java.util.Arrays;


/**
 * Работа с полиномами заданной степени.
 */
public class Polynom {
	// Полином представлен своими коэффициентами
	private Complex[] a;
	
	/**
	 * Конструктор по заданным вещественным коэффициентам
	 * @param a
	 */
	public Polynom(double[] a) {
		this.a = new Complex[a.length];
		for (int i = 0; i < a.length; ++i) {
			this.a[i] = new Complex(a[i]);
		}
	}
	
	/**
	 * Конструктор по заданным комплексным коэффициентам
	 * @param a
	 */
	public Polynom(Complex[] a) {
		this.a = Arrays.copyOf(a, a.length);
	}
	
	/**
	 * Конструктор копирования
	 * @param p
	 */
	public Polynom(Polynom p) {
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
			builder.append('(').append(a[i]).append(')');
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
	public Complex[] coef() { return a; }
	
	/**
	 * Традиционный алгоритм умножения многочленов.
	 * @param p1	Первый многочлен
	 * @param p2	Второй многочлен
	 * @return		Их произведение
	 */
	public static Polynom multiply(Polynom p1, Polynom p2) {
		Complex[] a = p1.coef();
		Complex[] b = p2.coef();
		int n = a.length;
		int m = b.length;
		Complex[] c = new Complex[n+m-1];
		for (int k = 0; k < n+m-1; ++k) {
			Complex s = new Complex();
			for (int i = 0; i <= k; ++i) {
				if (i >= n || k-i >= m) {
					continue;
				}
				s.add(Complex.mult(a[i], b[k-i]));
			}
			c[k] = s;
		}
		
		return new Polynom(c);
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
	 * Умножение полиномов с использованием рекурсивного алгоритма
	 * быстрого преобразования Фурье
	 * @return
	 */
	public static Polynom multiplyRecFFT(Polynom p1, Polynom p2) {
		Complex[] a = p1.coef();
		Complex[] b = p2.coef();
		int length = power2(2 * Math.max(a.length, b.length));
		a = Complex.complement(a, length);
		b = Complex.complement(b, length);
		
		// Прямое преобразование Фурье
		Complex[] ya = recursive.FFT.dirFourier(a);
		Complex[] yb = recursive.FFT.dirFourier(b);
		// Перемножение
		for (int i = 0; i < ya.length; ++i) {
			ya[i].multiply(yb[i]);
		}
		// Обратное преобразование Фурье
		return new Polynom(recursive.FFT.invFourier(ya));
	}
	
	/**
	 * Умножение полиномов с использованием нерекурсивного алгоритма
	 * быстрого преобразования Фурье
	 * @return
	 */
	public static Polynom multiplyFFT(Polynom p1, Polynom p2) {
		Complex[] a = p1.coef();
		Complex[] b = p2.coef();
		int length = power2(2 * Math.max(a.length, b.length));
		a = Complex.complement(a, length);
		b = Complex.complement(b, length);
		
		// Прямое преобразование Фурье
		Complex[] ya = nonrec.FFT.dirFourier(a);
		Complex[] yb = nonrec.FFT.dirFourier(b);
		// Перемножение
		for (int i = 0; i < ya.length; ++i) {
			ya[i].multiply(yb[i]);
		}
		// Обратное преобразование Фурье
		return new Polynom(nonrec.FFT.invFourier(ya));
	}
}
