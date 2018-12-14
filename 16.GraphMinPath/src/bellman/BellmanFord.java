package bellman;

import java.util.Arrays;
import java.util.Iterator;

public class BellmanFord {
	final Graph graph;		// Граф, для которого производятся вычисления
	
	int src = -1;			// Начальная вершина, пути из которой анализируются
	int nVert;				// число вершин в графе
	
	double[] distances;		// Массив расстояний
	int[] tree;				// Дерево обхода по минимальным путям

	public BellmanFord(Graph g) {
		graph = g;
		nVert = g.getCount();
		distances = new double[nVert];
		tree = new int[nVert];
	}
	
	/**
	 * Выдает дерево минимальных путей.
	 * Если дерево еще не построено, запускается алгоритм Беллмана - Форда.
	 * @param u	Номер исходной вершины
	 * @return	Дерево в виде массива обратных дуг
	 */
	public int[] getTree(int u) {
		if (u < 0 || u >= nVert) return null;
		if (u != src) {
			bellmanFord(u);
		}
		return tree;
	}
	
	/**
	 * Выдает длины минимальных путей.
	 * Если дерево еще не построено, запускается алгоритм Беллмана - Форда.
	 * @param u	Номер исходной вершины
	 * @return	Массив расстояний до указанных вершин
	 */
	public double[] getDistances(int u) {
		if (u < 0 || u >= nVert) return null;
		if (u != src) {
			bellmanFord(u);
		}
		return distances;
	}
	
	/**
	 * Релаксация дуги
	 * @param from		Номер начальной вершины
	 * @param fromTo	Дуга (нагрузка и конечная вершина)
	 * @return			True, если релаксация привела к изменению расстояния,
	 * 					иначе False.
	 */
	private boolean relax(int from, Graph.Arc fromTo) {
		int to = fromTo.to();
		double newDist = distances[from] + fromTo.weight();
		if (newDist < distances[to]) {
			distances[to] = newDist;
			tree[to] = from;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Реализация алгоритма Беллмана - Форда по нахождению кратчайших путей
	 * из заданной вершины в произвольном графе без циклов отрицательной длины.
	 * @param s
	 * @return
	 */
	private boolean bellmanFord(int s) {
		src = s;
		// Инициализация массивов
		for (int i = 0; i < nVert; ++i) {
			distances[i] = Double.POSITIVE_INFINITY;
			tree[i] = -1;
		}
		distances[s] = 0;
		
		// Были изменения?
		boolean changed = true;
		// Если после (N+1)-го шага изменения все еще происходят,
		// значит, в графе имеется цикл с отрицательным весом.
		for (int step = 0; step <= nVert && changed; ++step) {
			changed = false;
			// Цикл по всем дугам из всех достигнутых когда-либо вершин
			for (int u = 0; u < nVert; ++u) {
				if (distances[u] != Double.POSITIVE_INFINITY) {
					for (Iterator<Graph.Arc> iArc = graph.arcs(u); iArc.hasNext(); ) {
						Graph.Arc arc = iArc.next();
						// Релаксация дуги
						if (relax(u, arc)) {
							changed = true;
						}
					}
				}
			}
		}
		return !changed;
	}
	
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
		
		BellmanFord bellmanFord = new BellmanFord(g);
		System.out.println("Tree: " + Arrays.toString(bellmanFord.getTree(3)));
		System.out.println("Dist: " + Arrays.toString(bellmanFord.getDistances(3)));
	}
}
