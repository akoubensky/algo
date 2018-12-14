package testing;

import java.util.Arrays;

import calc.Polynom;

public class TestRecursive {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] coeff = new double[3000];
		Arrays.fill(coeff, 1);
		Polynom p = new Polynom(coeff);
		
		System.out.println(p);
		long startTime = System.currentTimeMillis();
		Polynom p1 = Polynom.multiplyRecFFT(p, p);
		long midTime1 = System.currentTimeMillis();
		Polynom p2 = Polynom.multiply(p, p);
		long finishTime = System.currentTimeMillis();
		System.out.println("Быстрое преобразование Фурье (рекурсивное): " + (midTime1 - startTime));
		System.out.println(p1);
		System.out.println("Обычное умножение: " + (finishTime - midTime1));
		System.out.println(p2);
	}

}
