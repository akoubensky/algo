import java.util.Arrays;


public class Fano {
	/**
	 * В массиве неотрицательных значений, упорядоченных по убыванию,
	 * находит индекс элемента так, чтобы элементы, находящиеся слева от
	 * найденного индекса давали в сумме приблизительно ту же величину,
	 * что и сумма остальных элементов.
	 * 
	 * @param p 	Исходный массив
	 * @param from 	Начальный индекс
	 * @param to 	Конечный индекс
	 * @return		Найденный индекс
	 */
	public static int median(double[] p, int from, int to) {
		int med = to - 1;
		double sEnd = p[med];
		double sBeg = p[from];
		for (int i = from + 1; i < med; ++i) sBeg += p[i];
		double d;
		do {
			d = sBeg - sEnd;
			med--; sBeg -= p[med]; sEnd += p[med];
		} while (Math.abs(sBeg - sEnd) < d);
		return med + 1;
	}
	
	/**
	 * Реализация алгоритма кодирования по Фано.
	 * 
	 * @param p Исходный массив вероятностей, упорядоченный по убыванию.
	 * @return	Массив двоичных кодов префиксной схемы кодирования.
	 */
	public static String[] fano(double[] p) {
		int n = p.length;
		
		// Построение начального массива (пустых) кодов.
		StringBuilder[] codes = new StringBuilder[n];
		for (int i = 0; i < n; ++i) codes[i] = new StringBuilder();
		
		// Вызов основной рекурсивной функции.
		fano(p, codes, 0, n);
		
		// Выдача результата
		String[] c = new String[n];
		for (int i = 0; i < n; ++i) c[i] = codes[i].toString();
		return c;
	}
	
	/**
	 * Основная рекурсивная функция, реализующая алгоритм кодирования
	 * по Фано. Осуществляет наращивание кодов в заданной части массива.
	 * 
	 * @param p		Исходный массив вероятностей
	 * @param codes	Имеющийся массив кодов
	 * @param from	Нижняя граница индексов
	 * @param to	Верхняя граница индексов
	 */
	private static void fano(double[] p, StringBuilder[] codes, int from, int to) {
		if (to - from > 1) {
			int m = median(p, from, to);
			assert m > from && m < to;
			for (int i = from; i < m; ++i) codes[i].append('1');
			for (int i = m; i < to; ++i) codes[i].append('0');
			fano(p, codes, from, m);
			fano(p, codes, m, to);
		}
	}
	
	/**
	 * Проверка на простом примере.
	 * @param args
	 */
	public static void main(String[] args) {
		double[] p = {
				0.170724,
				0.078881,
				0.070544,
				0.060655,
				0.059827,
				0.051694,
				0.049189,
				0.042626,
				0.040116,
				0.033812,
				0.031054,
				0.026809,
				0.026179,
				0.024958,
				0.024288,
				0.017939,
				0.016610,
				0.016492,
				0.013447,
				0.013363,
				0.011635,
				0.011089,
				0.011010,
				0.009451,
				0.008511,
				0.007441,
				0.007317,
				0.007182,
				0.006496,
				0.005404,
				0.005302,
				0.005201,
				0.005038,
				0.004525,
				0.004373,
				0.003805,
				0.003692,
				0.002550,
				0.002510,
				0.002336,
				0.002100,
				0.001030,
				0.000642,
				0.000557,
				0.000411,
				0.000411,
				0.000388,
				0.000265,
				0.000124,
				0.000000
		};
		String[] res = fano(p);
        for (String s : res) {
            System.out.println(s);
        }
	}
}
