package transitive;

/**
 * Реализация некоторых операций над квадратными матрицами логических значений.
 */
public class Matrices {
	/**
	 * Добавляет (логически) к матрице другую матрицу
	 * @param m1	Исходная матрица
	 * @param m2	Добавляемая матрица
	 * @return		Исходная матрица с добавлением
	 */
	public static boolean[][] add(boolean[][] m1, boolean[][] m2) {
		int n = m1.length;	// Размеры матриц
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				m1[i][j] |= m2[i][j];
			}
		}
		return m1;
	}
	
	/**
	 * Логически перемножает две матрицы.
	 * @param m1	Одна из исходных матриц
	 * @param m2	Другая матрица
	 * @return		Результат умножения в новой памяти.
	 */
	public static boolean[][] mult(boolean[][] m1, boolean[][] m2) {
		int n = m1.length;	// Размеры матриц
		boolean[][] result = new boolean[n][n];
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				for (int k = 0; k < n; ++k) {
					result[i][j] |= m1[i][k] && m2[k][j];
				}
			}
		}
		return result;
	}
	
	/**
	 * Копирует матрицу.
	 * @param m	Исходная матрица
	 * @return	Ее копия
	 */
	public static boolean[][] copy(boolean[][] m) {
		int n = m.length;	// Размеры матриц
		boolean[][] result = new boolean[n][n];
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				result[i][j] = m[i][j];
			}
		}
		return result;
	}
}
