package floyd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Реализация алгоритма Флойда - Уоршалла по вычислению минимальных путей
 * между всеми парами вершин в ориентированном нагруженном графе.
 */
public class MinPaths {
	private double[][] paths;	// Матрица расстояний
	private int[][] directions;	// Матрица направлений
	
	/**
	 * Конструктор запускает алгоритм Флойда - Уоршалла
	 * @param g
	 */
	public MinPaths(Graph g) {
		floyd(g);
	}
	
	/**
	 * Длина минимального пути берется из вычисленной матрицы путей
	 * @param from	Исходная вершина
	 * @param to	Целевая вершина
	 * @return		Длина минимального пути между исходной и целевой вершиной.
	 */
	public double getPathLength(int from, int to) {
		return paths[from][to];
	}
	
	/**
	 * Строит путь минимальной длины между заданными двумя вершинами.
	 * @param from	Исходная вершина
	 * @param to	Целевая вершина
	 * @return		Массив номеров вершин, задающих путь от исходной до целевой вершины
	 */
	public List<Integer> getPath(int from, int to) {
		List<Integer> path = new ArrayList<Integer>();
		do {
			path.add(0, to);
		} while ((to = directions[from][to]) != -1);
		return path;
	}
	
	/**
	 * Реализация алгоритма Флойда - Уоршалла по вычислению минимальных путей
	 * между всеми парами вершин в графе
	 * @param g
	 */
	private void floyd(Graph g) {
		int nVert = g.getCount();
		// Начальное состояние матрицы путей - это матрица смежности,
		// в которой несуществующие дуги имеют длину "бесконечность",
		// кроме "дуг" (u, u), которые имеют "длину" 0.
		paths = g.buildMatrix();
		// Построение начальной матрицы направлений
		directions = new int[nVert][nVert];
		for (int i = 0; i < nVert; ++i) {
			int[] line = directions[i];
			for (int j = 0; j < nVert; ++j) {
				line[j] = i == j || paths[i][j] == Double.POSITIVE_INFINITY ? -1 : i;
			}
		}
		
		// Собственно алгоритм Флойда - Уоршалла
		for (int k = 0; k < nVert; ++k) {
			for (int i = 0; i < nVert; ++i) {
				if (i != k && paths[i][k] < Double.POSITIVE_INFINITY) {
					for (int j = 0; j < nVert; ++j) {
						double newPath = paths[i][k] + paths[k][j];
						if (newPath < paths[i][j]) {
							paths[i][j] = newPath;
							directions[i][j] = directions[k][j];
						}
					}
				}
			}
		}
	}
	
	/**
	 * Функция проверки работоспособности алгоритма на примере
	 * небольшого связного ориентированного графа из 9 вершин
	 * (некоторые дуги имеют отрицательную длину). 
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph(9);
		
		g.addArc(0, 5, 3);
		g.addArc(0, 2, 2);
		g.addArc(0, 4, -3);
		g.addArc(1, 0, 2);
		g.addArc(2, 0, -1);
		g.addArc(2, 4, 2);
		g.addArc(3, 0, 3);
		g.addArc(3, 1, 2);
		g.addArc(3, 4, -2);
		g.addArc(3, 8, 2);
		g.addArc(4, 6, 1);
		g.addArc(4, 7, 4);
		g.addArc(5, 7, 2);
		g.addArc(6, 3, 3);
		g.addArc(6, 8, 2);
		g.addArc(7, 2, 2);
		g.addArc(7, 5, -1);
		g.addArc(8, 7, -1);
		g.addArc(8, 4, 2);
		
		// Запускаем алгоритм Флойда - Уоршалла
		MinPaths floyd = new MinPaths(g);
		// По результату выводим два пути между некоторыми парами вершин
		System.out.print("Path (2 - 5): " + Arrays.toString(floyd.getPath(2, 5).toArray(new Integer[0])));
		System.out.println("; Distance: " + floyd.getPathLength(2, 5));
		System.out.print("Path (1 - 3): " + Arrays.toString(floyd.getPath(1, 3).toArray(new Integer[0])));
		System.out.println("; Distance: " + floyd.getPathLength(1, 3));
	}
}
