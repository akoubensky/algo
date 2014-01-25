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
			for (int i = from; i < m; ++i) codes[i].append('0');
			for (int i = m; i < to; ++i) codes[i].append('1');
			fano(p, codes, from, m);
			fano(p, codes, m, to);
		}
	}
	
	/**
	 * Проверка на простом примере.
	 * @param args
	 */
	public static void main(String[] args) {
		double[] p = {0.20, 0.20, 0.19, 0.12, 0.11, 0.09, 0.09};
		System.out.println(Arrays.toString(fano(p)));
	}
}
