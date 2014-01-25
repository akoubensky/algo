package dag;

import java.util.Arrays;
import java.util.Iterator;

import dag.Topsort.NotADAGException;

public class Dag {
	final Graph graph;		// Граф, для которого производятся вычисления
	
	int src = -1;			// Начальная вершина, пути из которой анализируются
	int nVert;				// число вершин в графе
	
	double[] distances;		// Массив расстояний
	int[] tree;				// Дерево обхода по минимальным путям

	public Dag(Graph g) {
		graph = g;
		nVert = g.getCount();
		distances = new double[nVert];
		tree = new int[nVert];
	}
	
	/**
	 * Выдает дерево минимальных путей.
	 * Если дерево еще не построено, запускается алгоритм Дейкстры.
	 * @param u	Номер исходной вершины
	 * @return	Дерево в виде массива обратных дуг
	 */
	public int[] getTree(int u) {
		if (u < 0 || u >= nVert) return null;
		if (u != src) {
			try {
				dag(u);
			} catch (NotADAGException e) {
				return null;
			}
		}
		return tree;
	}
	
	/**
	 * Выдает длины минимальных путей.
	 * Если дерево еще не построено, запускается алгоритм Дейкстры.
	 * @param u	Номер исходной вершины
	 * @return	Массив расстояний до указанных вершин
	 */
	public double[] getDistances(int u) {
		if (u < 0 || u >= nVert) return null;
		if (u != src) {
			try {
				dag(u);
			} catch (NotADAGException e) {
				return null;
			}
		}
		return distances;
	}
	
	public void dag(int s) throws NotADAGException {
		Topsort topsort = new Topsort(graph);
		int[] labels = topsort.getLabels();
		int[] indices = new int[nVert];
		for (int i = 0; i < nVert; ++i) {
			indices[labels[i]] = i;
			distances[i] = Double.POSITIVE_INFINITY;
			tree[i] = -1;
		}
		distances[s] = 0;
		for (int index = labels[s]; index < nVert; ++index) {
			int from = indices[index];
			for (Iterator<Graph.Arc> iArc = graph.arcs(from); iArc.hasNext(); ) {
				Graph.Arc arc = iArc.next();
				int to = arc.to();
				double newDist = distances[from] + arc.weight();
				if (newDist < distances[to]) {
					distances[to] = newDist;
					tree[to] = from;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Graph g = new Graph(9);
		
		g.addArc(0, 2, 3);
		g.addArc(1, 0, 1);
		g.addArc(1, 4, 6);
		g.addArc(1, 6, 2);
		g.addArc(2, 5, 5);
		g.addArc(2, 7, 2);
		g.addArc(3, 1, 2);
		g.addArc(3, 6, 3);
		g.addArc(4, 2, 1);
		g.addArc(6, 4, 4);
		g.addArc(6, 8, 2);
		g.addArc(7, 5, 1);
		g.addArc(8, 4, 1);
		g.addArc(8, 7, 5);
		
		Dag dag = new Dag(g);
		System.out.println("Tree: " + Arrays.toString(dag.getTree(3)));
		System.out.println("Dist: " + Arrays.toString(dag.getDistances(3)));
	}
}
