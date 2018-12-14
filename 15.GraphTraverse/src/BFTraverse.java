import java.util.Arrays;
import java.util.Iterator;

public class BFTraverse {
    public static void main(String[] args) {
        // Создаем граф с циклами
        Graph<Integer> graph = new Graph<Integer>(9);

        graph.addEdge(3, 1, 1);
        graph.addEdge(3, 6, 1);
        graph.addEdge(1, 6, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 4, 1);
        graph.addEdge(6, 4, 1);
        graph.addEdge(6, 8, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(4, 2, 1);
        graph.addEdge(8, 7, 1);
        graph.addEdge(2, 7, 1);
        graph.addEdge(2, 5, 1);
        graph.addEdge(7, 5, 1);
        graph.addEdge(8, 4, 1);
        graph.addEdge(7, 4, 1);

        // Обходим граф в глубину и запоминаем вершины в порядке обхода
        int[] vertices = new int[graph.getCount()];
        int index = 0;
        for (Iterator<Integer> it = graph.bfIterator(); it.hasNext(); ) {
            int v = it.next();
            vertices[index++] = v;
        }

        System.out.println(Arrays.toString(vertices));
    }
}
