package minpathtree;
import java.util.*;


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

		public Arc(int to, long info) {
			this.to = to;
			this.info = info;
		}
		
		public long info() { return info; }
		
		public int to() { return to; }
	};

	private final List<Arc>[] lGraph;		// Списки смежности
	private final int nVertex;		// Число вершин

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	public Graph(int nVert) {
		lGraph = new ArrayList[nVert];
		nVertex = nVert;
		for (int i = 0; i < nVert; i++) {
			lGraph[i] = new ArrayList<>();
		}
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
		
		lGraph[from].add(new Arc(to, info));
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new BreadthFirstIterator();
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
			for (Arc arc : lGraph[u]) {
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
	 */
	private class BreadthFirstIterator implements Iterator<Integer> {
		Queue<Integer> queue = new LinkedList<Integer>();	// Контейнер-очередь
		boolean[] marked;				// Массив пройденных вершин
		boolean[] passed;				// Массив помещенных в очередь вершин

		/**
		 * Инициализация итератора
		 */
		BreadthFirstIterator() {
			marked = new boolean[nVertex];
			passed = new boolean[nVertex];
		}

		@Override
		public boolean hasNext() {
			if (queue.isEmpty()) {
				// Возможно, остались еще не пройденные вершины
				for (int i = 0; i < nVertex; ++i) {
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
			for (Arc arc : lGraph[next]) {
				if (!passed[arc.to]) {
					queue.offer(arc.to);
					passed[arc.to] = true;
				}
			}
			return next;
		}
	}
	
}
