package kruskal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kruskal.Graph.Arc;

public class Kruskal {
	/**
	 * Этот класс представляет ребро, в котором помимо конца дуги
	 * и ее длины содержится еще и начало.
	 */
	public static class Edge extends Arc {
		final int from;	// Начало дуги
		
		public Edge(int from, int to, double weight) {
			super(to, weight);
			this.from = from;
		}
		
		@Override
		public String toString() {
			return "(" + from + "," + to + "," + weight + ")";
		}
	}
	
	final private Graph graph;	// Граф, для которого строится скелет
	final private int nVert;	// Число вершин в графе
	
	private Set<Edge> edges;			// Список ребер, входящих в скелет
	private SortedSet<Edge> allEdges;	// Список всех ребер графа
	
	public Kruskal(Graph g) {
		graph = g;
		nVert = g.getCount();
	}
	
	public Set<Edge> getSceleton() {
		if (edges == null) {
			kruskal();
		}
		return edges;
	}
	
	public void kruskal() {
		int[] forest = new int[nVert];
		Arrays.fill(forest, -1);
		fillEdges();
		edges = new HashSet<Edge>();
		for (Edge edge : allEdges) {
			testEdge(edge, forest);
		}
	}
	
	private void fillEdges() {
		allEdges = new TreeSet<Edge>(new Comparator<Edge>() {

			@Override
			public int compare(Edge edge1, Edge edge2) {
				int beg1 = Math.min(edge1.from, edge1.to);
				int end1 = Math.max(edge1.from, edge1.to);
				int beg2 = Math.min(edge2.from, edge2.to);
				int end2 = Math.max(edge2.from, edge2.to);
				if (edge1.weight < edge2.weight) return -1;
				if (edge1.weight > edge2.weight) return 1;
				if (beg1 != beg2) return beg1 - beg2;
				return end1 - end2;
			}
		});
		for (int u = 0; u < nVert; ++u) {
			for (Iterator<Arc> iArc = graph.arcs(u); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				if (arc.to >= u) {
					allEdges.add(new Edge(u, arc.to, arc.weight));
				}
			}
		}
	}
	
	private void testEdge(Edge edge, int[] forest) {
		int end1 = edge.from;
		int end2 = edge.to;
		int next;
		int root1 = end1;
		while (forest[root1] != -1) root1 = forest[root1];
		while ((next = forest[end1]) != -1) {
			forest[end1] = root1;
			end1 = next;
		}
		int root2 = end2;
		while (forest[root2] != -1) root2 = forest[root2];
		while ((next = forest[end2]) != -1) {
			forest[end2] = root2;
			end2 = next;
		}
		if (root1 != root2) {
			forest[root1] = root2;
			edges.add(edge);
		}
	}
	
	/**
	 * Тестовая функция, строящая связный граф и вычисляющая его скелет
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph(10);
		
		g.addEdge(0, 1, 1);
		g.addEdge(0, 3, 3);
		g.addEdge(1, 7, 4);
		g.addEdge(1, 4, 2);
		g.addEdge(1, 2, 5);
		g.addEdge(1, 9, 2);
		g.addEdge(2, 5, 1);
		g.addEdge(2, 9, 3);
		g.addEdge(3, 7, 5);
		g.addEdge(3, 8, 3);
		g.addEdge(4, 5, 4);
		g.addEdge(5, 6, 4);
		g.addEdge(5, 9, 5);
		g.addEdge(6, 7, 2);
		g.addEdge(7, 8, 1);
		
		Kruskal prim = new Kruskal(g);
		Set<Edge> sceleton = prim.getSceleton();
		System.out.println(Arrays.toString(sceleton.toArray(new Edge[0])));
		double wholeWeight = 0;
		for (Edge edge : sceleton) {
			wholeWeight += edge.weight;
		}
		System.out.println("Sceleton weight = " + wholeWeight);
	}
}
