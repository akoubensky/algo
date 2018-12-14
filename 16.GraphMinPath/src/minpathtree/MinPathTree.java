package minpathtree;

import java.util.Arrays;

/**
 * Представление дерева минимальных путей из некоторой вершины 
 */
public class MinPathTree {
	public final int[] tree;	// Дерево путей
	public final long[] dist;	// Минимальные расстояния
	
	public MinPathTree(Graph graph) {
		int n = graph.getCount();
		tree = new int[n];
		dist = new long[n];
		// Все начальные расстояния - бесконечно большие.
		for (int i = 0; i < n; ++i) dist[i] = Long.MAX_VALUE;
	}
	
	// Проверяем правильность алгоритма вычисления минимальных расстояний
	public static void main(String[] args) {
		Graph graph = new Graph(8);
		
		// Задаем неориентированный граф
		graph.addArc(0, 1, 1);
		graph.addArc(1, 0, 1);
		graph.addArc(0, 3, 1);
		graph.addArc(3, 0, 1);
		graph.addArc(0, 4, 1);
		graph.addArc(4, 0, 1);
		graph.addArc(1, 2, 1);
		graph.addArc(2, 1, 1);
		graph.addArc(1, 3, 1);
		graph.addArc(3, 1, 1);
		graph.addArc(2, 3, 1);
		graph.addArc(3, 2, 1);
		graph.addArc(3, 5, 1);
		graph.addArc(5, 3, 1);
		graph.addArc(4, 6, 1);
		graph.addArc(6, 4, 1);
		graph.addArc(4, 7, 1);
		graph.addArc(7, 4, 1);
		graph.addArc(5, 6, 1);
		graph.addArc(6, 5, 1);
		
		MinPathTree mpTree = graph.getMinPath(0);
		System.out.println("Tree: " + Arrays.toString(mpTree.tree));
		System.out.println("Dist: " + Arrays.toString(mpTree.dist));
	}
}
