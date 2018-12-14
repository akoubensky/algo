package transitive;

import java.util.Arrays;

/**
 * Реализация вычисления транзитивного замыкания графа методом Уоршалла.
 */
public class Warshall {
	/**
	 * Алгоритм Уоршалла вычисления транзитивного замыкания графа
	 * @param srcGraph
	 * @return
	 */
	public static Graph transitive(Graph srcGraph) {
		int n = srcGraph.getCount();
		Graph result = new Graph(n);
		boolean[][] matrix = srcGraph.buildMatrix();
		for (int k = 0; k < n; ++k) {
			for (int i = 0; i < n; ++i) {
				if (i != k && matrix[i][k]) {
					for (int j = 0; j < n; ++j) {
						matrix[i][j] |= matrix[k][j];
					}
				}
			}
		}
		result.fromMatrix(matrix);
		return result;
	}

	/**
	 * Тестовая функция, вычисляющая транзивное замыкание простого несвязного графа.
	 * @param args
	 */
	public static void main(String[] args) {
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
		
		Graph tg = Warshall.transitive(g);
		for (boolean[] line : tg.buildMatrix()) {
			System.out.println(Arrays.toString(line));
		}
	}
}
