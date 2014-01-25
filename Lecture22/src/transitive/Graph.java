package transitive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Представление структуры графа в виде матрицы смежности.
 */
public class Graph {
	private final List<Integer>[] lGraph;	// Списки смежности
	private final int nVertex;				// Число вершин

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	public Graph(int nVert) {
		lGraph = new List[nVert];
		for (int i = 0; i < nVert; ++i) {
			lGraph[i] = new ArrayList<Integer>();
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
	 */
	public void addArc(int from, int to) {
		assert from < nVertex && from >= 0;
		assert to < nVertex && to >= 0;
		
		lGraph[from].add(to);
	}
	
	/**
	 * Итератор концов дуг, ведущих из заданной вершины
	 * @param u	Исходная вершина
	 * @return
	 */
	public Iterator<Integer> arcs(int u) {
		return lGraph[u].iterator();
	}
	
	/**
	 * Строит матрицу смежности заданного графа
	 * @return
	 */
	public boolean[][] buildMatrix() {
		boolean[][] matrix = new boolean[nVertex][nVertex];
		for (int u = 0; u < nVertex; ++u) {
			for (Iterator<Integer> iArc = arcs(u); iArc.hasNext(); ) {
				int to = iArc.next();
				matrix[u][to] = true;
			}
		}
		return matrix;
	}
	
	/**
	 * Заменяет полность структуру графа по заданной матрице смежности
	 * @param matrix Матрица смежности
	 */
	public void fromMatrix(boolean[][] matrix) {
		for (int i = 0; i < matrix.length; ++i) {
			boolean[] line = matrix[i];
			List<Integer> list = new ArrayList<Integer>();
			for (int j = 0; j < line.length; ++j) {
				if (line[j]) {
					list.add(j);
				}
			}
			lGraph[i] = list;
		}
	}
}
