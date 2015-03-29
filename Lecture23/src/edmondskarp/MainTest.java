package edmondskarp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Проверка правильности работы реализации алгоритма Эдмондса - Карпа 
 */
public class MainTest {

	public static void main(String[] args) {
		Graph g;
		try (BufferedReader br = new BufferedReader(new FileReader("graph.txt"))) {
			g = Graph.build(6, br.lines(), 0, 5);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println(g.edmondsKarp());
	}

}
