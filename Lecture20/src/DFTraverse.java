import java.util.Arrays;


public class DFTraverse {
	public static void main(String[] args) {
		// Создаем граф с циклами
		Graph<Integer> graph = new Graph<Integer>(9);
		
		graph.addArc(3, 1, 1);
		graph.addArc(3, 6, 1);
		graph.addArc(1, 6, 1);
		graph.addArc(1, 0, 1);
		graph.addArc(1, 4, 1);
		graph.addArc(6, 4, 1);
		graph.addArc(6, 8, 1);
		graph.addArc(0, 2, 1);
		graph.addArc(4, 2, 1);
		graph.addArc(8, 7, 1);
		graph.addArc(2, 7, 1);
		graph.addArc(2, 5, 1);
		graph.addArc(7, 5, 1);
		graph.addArc(8, 4, 1);
		graph.addArc(7, 4, 1);

		// Обходим граф в глубину и запоминаем вершины в порядке обхода
		int[] vertices = new int[graph.getCount()];
		int index = 0;
		for (int v : graph) {
			vertices[index++] = v;
		}
		
		System.out.println(Arrays.toString(vertices));
	}
}
