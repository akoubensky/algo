package johnson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import johnson.Graph.Arc;

/**
 * Реализует алгоритм Джонсона вычисления минимальных путей между
 * всеми парами вершин в ориентированном нагруженном графе.
 */
public class Johnson {
	private double[][] paths;	// Матрица расстояний
	private int[][] directions;	// Матрица направлений

	/**
	 * Конструктор запускает алгоритм Джонсона на заданном графе
	 * @param g
	 */
	public Johnson(Graph g) {
		johnson(g);
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
	 * Реализация алгоритма Джонсона вычисления минимальных путей между
	 * всеми парами вершин в ориентированном нагруженном графе.
	 * @param g	Исходный граф
	 */
	private void johnson(Graph g) {
		int nVert = g.getCount();
		
		// 1. Добавляем новую вершину в граф и проводим дуги
		//    нулевой длины из нее во все остальные вершины - O(n).
		int newVertex = g.addVertex();
		for (int u = 0; u < nVert; ++u) {
			g.addArc(newVertex, u, 0);
		}
		
		// 2. Запускаем алгоритм Беллмана - Форда для вычисления длин
		//    минимальных путей из этой вершины во все прочие - O(n*(n+m)).
		BellmanFord bf = new BellmanFord(g);
		double[] f = bf.getDistances(newVertex);
		
		// 3. Удаляем добавленную вершину и корректируем длины всех дуг с учетом
		//    найденных длин путей так, чтобы все длины стали неотрицательными - O(n+m).
		g.removeVertex(newVertex);
		for (int u = 0; u < nVert; ++u) {
			for (Iterator<Arc> iArc = g.arcs(u); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				arc.addWeight(f[u] - f[arc.to()]);
			}
		}
		
		// 4. Теперь для каждой вершины запускаем алгоритм Дейкстры - O(n*(n+m)*log n)
		Dijkstra dijkstra = new Dijkstra(g);
		paths = new double[nVert][];
		directions = new int[nVert][];
		for (int u = 0; u < nVert; ++u) {
			paths[u] = dijkstra.getDistances(u);
			directions[u] = dijkstra.getTree(u);
		}
		
		// 5. Восстанавливаем исходные длины дуг - O(n+m).
		for (int u = 0; u < nVert; ++u) {
			for (Iterator<Arc> iArc = g.arcs(u); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				arc.addWeight(f[arc.to()] - f[u]);
			}
		}
		
		// 6. Корректируем матрицу длин путей с учетом реальных длин дуг - O(n*n).
		for (int i = 0; i < nVert; ++i) {
			for (int j = 0; j < nVert; ++j) {
				paths[i][j] += f[j] - f[i];
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
		
		// Запускаем алгоритм Джонсона
		Johnson johnson = new Johnson(g);
		// По результату выводим два пути между некоторыми парами вершин
		System.out.print("Path (2 - 5): " + Arrays.toString(johnson.getPath(2, 5).toArray(new Integer[0])));
		System.out.println("; Distance: " + johnson.getPathLength(2, 5));
		System.out.print("Path (1 - 3): " + Arrays.toString(johnson.getPath(1, 3).toArray(new Integer[0])));
		System.out.println("; Distance: " + johnson.getPathLength(1, 3));
	}
}
