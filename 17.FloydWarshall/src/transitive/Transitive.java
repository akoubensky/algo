package transitive;

import java.util.Arrays;

/**
 * Реализация вычисления транзитивного замыкания графа методом "перемножения матриц".
 */
public class Transitive {
	/**
	 * Алгоритм вычисления транзитивного замыкания графа
	 * @param srcGraph
	 * @return
	 */
	public static Graph transitive(Graph srcGraph) {
		int n = srcGraph.getCount();
		Graph result = new Graph(n);
		boolean[][] srcMatrix = srcGraph.buildMatrix();
		boolean[][] resMatrix = Matrices.copy(srcMatrix);
		for (int k = 2; k <= n; ++k) {
			Matrices.add(resMatrix, Matrices.mult(resMatrix, srcMatrix));
		}
		result.fromMatrix(resMatrix);
		return result;
	}
	
	/**
	 * Тестовая функция, вычисляющая транзивное замыкание простого несвязного графа.
	 * @param args
	 */
	public static void main(String[] args) {
		// Задаем граф с помощью матрицы смежности
		boolean[][] matrix = {
				{ false, false, false,  true, false, false, false },
				{ false, false, false, false, false, false,  true },
				{  true, false, false, false, false,  true, false },
				{ false, false, false, false, false, false, false },
				{ false,  true, false, false, false, false, false },
				{ false, false, false,  true, false, false, false },
				{ false, false, false, false,  true, false, false }
		};
		Graph g = new Graph(7);
		g.fromMatrix(matrix);
		
		Graph tg = Transitive.transitive(g);
		for (boolean[] line : tg.buildMatrix()) {
			System.out.println(Arrays.toString(line));
		}
	}
}
