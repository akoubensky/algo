package testing;

import java.util.Arrays;

import calc.IntPolynom;

public class TestInt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] coeff = new int[3000];
		Arrays.fill(coeff, 1);
		IntPolynom p = new IntPolynom(coeff);
		
		System.out.println(p);
		long startTime = System.currentTimeMillis();
		IntPolynom p1 = IntPolynom.multiplyFFT(p, p);
		long midTime1 = System.currentTimeMillis();
		IntPolynom p2 = IntPolynom.multiply(p, p);
		long finishTime = System.currentTimeMillis();
		System.out.println("Быстрое преобразование Фурье (нерекурсивное): " + (midTime1 - startTime));
		System.out.println(p1);
		System.out.println("Обычное умножение: " + (finishTime - midTime1));
		System.out.println(p2);
	}
}
