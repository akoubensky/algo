package kruskal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Представление нагруженного графа списками смежности.
 * Нагрузка на дуги - вещественные числа ("длина дуги").
 */
public class Graph {
	/**
	 * Представление дуги графа
	 */
	public static class Arc {
		double weight;	// Нагрузка на дугу
		int to;			// Номер вершины, в которую ведет дуга

		public Arc(int to, double info) {
			this.to = to; this.weight = info;
		}
		
		public Arc(Arc arc) {
			this(arc.to, arc.weight);
		}
		
		public double weight() { return weight; }
		
		public int to() { return to; }
	};

	private final List<Arc>[] lGraph;	// Списки смежности
	private final int nVertex;			// Число вершин

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	public Graph(int nVert) {
		lGraph = new List[nVert];
		for (int i = 0; i < nVert; ++i) {
			lGraph[i] = new ArrayList<Arc>();
		}
		nVertex = nVert;
	}
	
	/**
	 * Число вершин графа
	 * @return
	 */
	public int getCount() { return nVertex; }

	/**
	 * Добавление дуги в граф. Предполагается, что ранее такой дуги в графе не было.
	 * @param from	Начало дуги (номер вершины)
	 * @param to	Конец дуги (номер вершины)
	 * @param info	Нагрузка на дугу
	 */
	public void addEdge(int from, int to, double info) {
		assert from < nVertex && from >= 0;
		assert to < nVertex && to >= 0;
		
		lGraph[from].add(new Arc(to, info));
		if (from != to) {
			lGraph[to].add(new Arc(from, info));
		}
	}
	
	/**
	 * Итератор дуг, ведущих из заданной вершины
	 * @param u	Исходная вершина
	 * @return
	 */
	public Iterator<Arc> arcs(int u) {
		return lGraph[u].iterator();
	}
}
