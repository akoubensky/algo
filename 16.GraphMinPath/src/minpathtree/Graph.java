package minpathtree;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Представление нагруженного графа списками смежности.
 * Нагрузка на дуги - целые числа ("длина дуги").
 */
public class Graph implements Iterable<Integer> {
	/**
	 * Представление дуги графа
	 */
	public static class Arc {
		long info;		// Нагрузка на дугу
		int to;			// Номер вершины, в которую ведет дуга
		Arc next;		// Следующая дуга в списке

		public Arc(int to, long info, Arc next) {
			this.to = to; this.info = info; this.next = next;
		}
		
		public Arc(int to, long info) {
			this(to, info, null);
		}
		
		public long info() { return info; }
		
		public int to() { return to; }
	};

	private final Arc[] lGraph;		// Списки смежности
	private final int nVertex;		// Число вершин

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	public Graph(int nVert) {
		lGraph = new Arc[nVert];
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
	public void addArc(int from, int to, long info) {
		assert from < nVertex && from >= 0;
		assert to < nVertex && to >= 0;
		
		lGraph[from] = new Arc(to, info, lGraph[from]);
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new BreadthFirstIterator(this);
	}
	
	/**
	 * Итератор дуг, исходящих из заданной вершины.
	 * @param v	Начальная вершина
	 * @return	Итератор дуг
	 */
	public Iterator<Arc> arcsIterator(int v) {
		return new ArcsIterator(this, v);
	}
	
	/**
	 * Вычисление минимальных путей из заданной вершины методом "волны".
	 * @param start	Начальная вершина
	 * @return		Дерево минимальных путей
	 */
	public MinPathTree getMinPath(int start) {
		MinPathTree mpTree = new MinPathTree(this);
		Queue<Integer> queue = new LinkedList<Integer>();	// Контейнер-очередь
		boolean[] passed = new boolean[getCount()];			// Массив помещенных в очередь вершин

		// Сначала записываем начальную вершину в очередь
		queue.offer(start);
		passed[start] = true;
		mpTree.dist[start] = 0;
		
		// Просматриваем одну компоненту
		while (!queue.isEmpty()) {
			int u = queue.poll();
			for (Iterator<Arc> it = arcsIterator(u); it.hasNext(); ) {
				Arc arc = it.next();
				if (!passed[arc.to]) {
					queue.offer(arc.to);
					passed[arc.to] = true;
					mpTree.tree[arc.to] = u;
					mpTree.dist[arc.to] = mpTree.dist[u] + 1;
				}
			}
		}
		
		return mpTree;
	}

	//---------------------- PRIVATE -----------------------
	
	/**
	 * Итератор вершин графа в порядке обхода в ширину
	 *
	 * @param <W>
	 */
	private static class BreadthFirstIterator implements Iterator<Integer> {
		Queue<Integer> queue = new LinkedList<Integer>();	// Контейнер-очередь
		boolean[] marked;				// Массив пройденных вершин
		boolean[] passed;				// Массив помещенных в очередь вершин
		Graph m_graph;				// Граф
		
		/**
		 * Инициализация итератора
		 * @param graph
		 */
		BreadthFirstIterator(Graph graph) {
			m_graph = graph;
			marked = new boolean[graph.nVertex];
			passed = new boolean[graph.nVertex];
		}

		@Override
		public boolean hasNext() {
			if (queue.isEmpty()) {
				// Возможно, остались еще не пройденные вершины
				for (int i = 0; i < m_graph.nVertex; ++i) {
					if (!marked[i]) {
						queue.offer(i);
						passed[i] = true;
						break;
					}
				}
			}
			return !queue.isEmpty();
		}

		@Override
		public Integer next() {
			if (!hasNext()) throw new NoSuchElementException();
			// Выбираем вершину из очереди
			Integer next = queue.poll();
			marked[next] = true;
			// Помещаем в очередь новые вершины, в которые ведут дуги из выбранной
			for (Arc arc = m_graph.lGraph[next]; arc != null; arc = arc.next) {
				if (!passed[arc.to]) {
					queue.offer(arc.to);
					passed[arc.to] = true;
				}
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Реализация итератора дуг, ведущих из заданной вершины
	 */
	private static class ArcsIterator implements Iterator<Arc> {
		Arc currArc;

		/**
		 * Конструктор, запоминающий начало списка дуг
		 * @param graph	Граф
		 * @param v		Начальная вершина
		 */
		public ArcsIterator(Graph graph, int v) {
			currArc = graph.lGraph[v];
		}
		
		@Override
		public boolean hasNext() {
			return currArc != null;
		}

		@Override
		public Arc next() {
			if (currArc == null) throw new NoSuchElementException();
			Arc nextArc = currArc;
			currArc = currArc.next;
			return nextArc;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
