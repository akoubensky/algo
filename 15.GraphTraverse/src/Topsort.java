import java.util.Arrays;

/**
 * Алгоритм топологической сортировки вершин графа
 */
public class Topsort {
	/**
	 * Реализация алгоритма топологической сортировки вершин графа.
	 * @param graph	Граф, вершины которого сортируются.
	 * @return		Массив меток, в котором каждой вершине графа сопоставлена метка.
	 */
	public static <W extends Number> int[] topsort(Graph<W> graph) {
		/**
		 * Реализация посетителя вершин и дуг графа
		 * для решения задачи топологической сортировки вершин.
		 *
		 * @param <W>	Тип нагрузки на дуги графа
		 */
		class TopsortVisitor extends GraphVisitor<W> {
			private int[] m_marks;	// Массив меток, назначаемых вершинам
			private int m_curMark;	// Текущая метка очередной вершины
			
			/**
			 * Конструктор посетителя определенного графа
			 * @param graph	Граф, подлежащий обходу
			 */
			public TopsortVisitor(Graph<W> graph) {
				m_curMark = graph.getCount();
				m_marks = new int[m_curMark];
			}
			
			/**
			 * Массив меток вершин графа
			 * @return
			 */
			public int[] getMarks() { return m_marks; }
			
			/**
			 * Реализация операции выхода из вершины.
			 * В этот момент вершина получает очередную метку.
			 */
			public void visitVertexOut(int v) {
				m_marks[v] = m_curMark--;
			}

			/**
			 * Реализация операции прохода по дуге содержит проверку
			 * применимости алгоритма топологической сортировки
			 */
			public void visitArcForward(int from, Graph.Arc<W> arc, boolean retArc) {
				if (retArc && m_marks[arc.to] == 0) {
					throw new InvalidGraphException();
				}
			}
		}

		/**
		 * Создаем специализированного посетителя для топологической сортировки.
		 */
		TopsortVisitor visitor = new TopsortVisitor(graph);
		graph.traverseDepthGraph(visitor);
		return visitor.getMarks();
	}
	
	/**
	 * Проверка работоспособности процедуры
	 * @param args
	 */
	public static void main(String[] args) {
		// Создаем DAG
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
		
		// Печатаем метки вершин после топологической сортировки
		System.out.println(Arrays.toString(topsort(graph)));
		
		// Добавляем дугу, замыкающую цикл
		graph.addArc(7, 4, 1);
		try {
			// Теперь топологическая сортировка должна показать ошибку
			System.out.println(Arrays.toString(topsort(graph)));
		} catch (InvalidGraphException ex) {
			// Выдаем сообщение о невозможности выполнить топологическую сортировку.
			System.out.println(ex.getMessage());
		}
	}
}
