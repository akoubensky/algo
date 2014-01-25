package calc;

import java.util.Arrays;

/**
 * Комплексные числа и некоторые операции над ними.
 */
public class Complex {
	// Вещественная и мнимая части
	private double re, im;
	
	// Число i
	public static Complex I = new Complex (0, 1);
	
	/**
	 * Конструктор по вещественной и мнимой части
	 * @param re	вещественная часть
	 * @param im	мнимая часть
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Конструктор с нулевой мнимой частью.
	 * @param re	вещественная часть
	 */
	public Complex(double re) {
		this(re, 0);
	}
	
	/**
	 * Конструктор нуля
	 */
	public Complex() {
		this(0);
	}
	
	/**
	 * Конструктор копирования
	 * @param c	источник копии
	 */
	public Complex(Complex c) {
		re = c.re();
		im = c.im();
	}
	
	/**
	 * Вещественная часть
	 * @return
	 */
	public double re() { return re; }
	
	/**
	 * Мнимая часть
	 * @return
	 */
	public double im() { return im; }
	
	/**
	 * Сложение комплексных чисел.
	 * @param c1	первое слашаемое
	 * @param c2	второе слагаемое
	 * @return		сумма
	 */
	public static Complex plus(Complex c1, Complex c2) {
		return new Complex (c1.re() + c2.re(), c1.im() + c2.im());
	}
	
	/**
	 * Вычитание комплексных чисел
	 * @param c1	уменьшаемое
	 * @param c2	вычитаемое
	 * @return		разность
	 */
	public static Complex minus(Complex c1, Complex c2) {
		return new Complex (c1.re() - c2.re(), c1.im() - c2.im());
	}
	
	/**
	 * Умножение комплексных чисел
	 * @param c1	первый сомножитель
	 * @param c2	второй сомножитель
	 * @return		произведение
	 */
	public static Complex mult(Complex c1, Complex c2) {
		double re1 = c1.re();
		double im1 = c1.im();
		double re2 = c2.re();
		double im2 = c2.im();
		return new Complex (re1*re2 - im1*im2, re1*im2 + re2*im1);
	}
	
	/**
	 * Сложение комплексного числа с заданным
	 * @param c	слагаемое
	 */
	public void add(Complex c) {
		re += c.re();
		im += c.im();
	}
	
	public void negate() {
		re = -re;
		im = -im;
	}
	
	public void complement() {
		im = -im;
	}
	
	/**
	 * Умножение комплексного числа на заданное
	 * @param c	сомножитель
	 */
	public void multiply(Complex c) {
		double re1 = re*c.re - im*c.im;
		double im1 = re*c.im + im*c.re;
		re = re1;
		im = im1;
	}
	
	/**
	 * Вывод в привычной математической нотации
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String format = "%5.3f";
		builder.append(String.format("%5.3f", re));
		String imPart = String.format("%5.3f", im);
		String zero = String.format(format, 0.0);
		if (!(zero.equals(imPart) || ('-' + zero).equals(imPart))) {
			builder.append(" + ").append(String.format("%5.3f", im)).append(" i");
		}
		return builder.toString();
	}
	
	/**
	 * Генерация комплексного числа по его полярным координатам
	 * @param ro	радиус
	 * @param phi	угол
	 * @return
	 */
	public static Complex fromPolar(double ro, double phi) {
		return new Complex(ro * Math.cos(phi), ro * Math.sin(phi));
	}
	
	/**
	 * Генерация комплексного числа с заданным углом и радиусом, равным единице.
	 * @param angle	угол
	 * @return
	 */
	public static Complex fromAngle(double angle) {
		return fromPolar(1, angle);
	}
	
	/**
	 * Один из корней заданной степени из единицы
	 * @param n	степень
	 * @param k	номер корня
	 * @return	корень
	 */
	public static Complex rootOne(int n, int k) {
		return fromAngle(2*Math.PI*k / n);
	}
	
	/**
	 * Главный корень заданной степени из единицы
	 * @param n	степень
	 * @return	корень
	 */
	public static Complex rootOne(int n) {
		return rootOne(n, 1);
	}
	
	/**
	 * Деление на вещественный делитель
	 * @param c	Исходное комплексное число
	 * @param d	Делитель
	 * @return
	 */
	public void divide(double d) {
		re /= d;
		im /= d;
	}
	
	/**
	 * Деление пополам
	 * @param c	исходное число
	 * @return	его половина
	 */
	public void half() {
		re /= 2;
		im /= 2;
	}
	
	/**
	 * Дополнение массива нулями до длины, равной степени двойки
	 * @param a	Исходный массив
	 * @return	Дополненный массив (если длина уже есть степень двойки -
	 * 			то тот же самый исходный массив)
	 */
	public static Complex[] complement(Complex[] a) {
		int n = a.length;
		int n2 = Integer.highestOneBit(n);
		if (n == n2) {
			return a;
		}
		return complement(a, n2 << 1);
	}
	
	/**
	 * Дополнение массива нулями до заданной длины
	 * @param a		Исходный массив
	 * @param nn	Длина, до которой необходимо дополнить массив
	 * @return		Дополненный нулями массив
	 */
	public static Complex[] complement(Complex[] a, int nn) {
		int n = a.length;
		if (n == nn) {
			return a;
		}
		Complex[] aa = Arrays.copyOf(a, nn);
		Arrays.fill(aa, n, nn, new Complex());
		return aa;
	}
	
}
