package prim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import prim.Graph.Arc;

/**
 * Алгоритм Прима вычисления минимального остовного дерева для
 * связного неориентированного нагруженного графа.
 */
public class Prim {
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
	
	private Set<Edge> edges = null;	// Список ребер, входящих в скелет
	
	// Рабочие (промежуточные) переменные, используемые при построении скелета
	int[] positions;	// Позиции вершин в очереди с приоритетом
	Arc[] binHeap;		// Очередь с приоритетами вершин (приоритет - расстояние)
	int heapSize;		// Размер очереди (число элементов)
	boolean[] passed;	// Список добавленных в скелет вершин

	public Prim(Graph g) {
		graph = g;
		nVert = g.getCount();
	}
	
	/**
	 * Выдает построенный скелет графа.
	 * Если скелет еще не строился, производится запуск алгоритма Прима.
	 * @return
	 */
	public Set<Edge> getSceleton() {
		if (edges == null) {
			prim();
		}
		return edges;
	}
	
	/**
	 * Реализация алгоритма Прима построения минимального скелета.
	 * По существу повторяет алгоритм Дейкстры, но вместо расстояния от
	 * начальной вершины при релаксации ребра использется длина ребра.
	 */
	public void prim() {
		edges = new HashSet<Edge>();		// Ребра скелета
		positions = new int[nVert];			// Индексы вершин в куче		
		int[] tree = new int[nVert];		// Номера начал ребер скелета		
		binHeap = new Arc[nVert];			// Двоичная куча
		heapSize = 0;						// Размер кучи
		passed = new boolean[nVert];		// Массив пройденных вершин
		for (int i = 0; i < nVert; ++i) {
			passed[i] = false;
			positions[i] = -1;
			tree[i] = -1;
		}
		
		// Инициализация кучи
		clearHeap();
		addToHeap(new Arc(0,0));
		
		while (!emptyHeap()) {
			// Жадный алгоритм выбирает ближайшую к скелету вершину
			Arc minPair = extractHeap();
			int vert = minPair.to;
			passed[vert] = true;
			if (tree[vert] != -1) {
				edges.add(new Edge(tree[vert], vert, minPair.weight));
			}
			
			// Производим релаксацию дуг, ведущих из выбранной вершины
			for (Iterator<Arc> iArc = graph.arcs(vert); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				int end = arc.to();
				if (!passed[end]) {
					if (positions[end] == -1) {
						// Новая вершина - добавляем в кучу
						addToHeap(new Arc(arc));
						tree[end] = vert;
					} else {
						// Вершина уже была в куче, производим ее релаксацию.
						Arc p = getFromHeap(positions[end]);
						if (arc.weight < p.weight) {
							changeHeap(positions[end], arc.weight);
							tree[end] = vert;
						}
					}
				}
			}
		}
	}
	
	//--------------------- PRIVATE ---------------------
	
	/**
	 * Сравнение дуг по длине
	 * @param arc1	Первая дуга
	 * @param arc2	Вторая дуга
	 * @return
	 */
	private static int compare(Arc arc1, Arc arc2) {
		return arc1.weight < arc2.weight ? -1 : arc1.weight == arc2.weight ? arc1.to - arc2.to : 1;
	}

	/**
	 * Изхменение позиции элемента в куче в соответствии с изменившимся
	 * (уменьшившимся) расстоянием до нее.
	 * @param i			Позиция элемента в куче
	 * @param newDist	Новое расстояние
	 */
	private void changeHeap(int i, double newDist) {
		binHeap[i].weight = newDist;
		heapUp(i);
	}

	/**
	 * Доступ к элементу кучи по индексу.
	 * @param i	Индекс элемента
	 * @return
	 */
	private Arc getFromHeap(int i) {
		return binHeap[i];
	}

	/**
	 * Извлечение из кучи элемента с минимальным расстоянием до него.
	 * @return	Элемент с наивысшим приоритетом (наименьшим расстоянием).
	 */
	private Arc extractHeap() {
		Arc minPair = binHeap[0];
		positions[minPair.to] = -1;
		if (--heapSize > 0) {
			binHeap[0] = binHeap[heapSize];
			binHeap[heapSize] = null;
			positions[binHeap[0].to] = 0;
			heapDown(0);
		}
		return minPair;
	}

	/**
	 * Добавление нового элемента в кучу.
	 * @param pair	Новый элемент
	 */
	private void addToHeap(Arc pair) {
		binHeap[positions[pair.to] = heapSize] = pair;
		heapUp(heapSize++);
	}

	/**
	 * Очистка кучи.
	 */
	private void clearHeap() {
		for (int i = 0; i < heapSize; ++i) binHeap[i] = null;
		heapSize = 0;
	}

	/**
	 * Проверка, пуста ли куча.
	 * @return
	 */
	private boolean emptyHeap() {
		return heapSize == 0;
	}

	/**
	 * Протаскивание элемента кучи с заданным индексом вверх по куче
	 * @param i	Индекс элемента
	 */
	private void heapUp(int i) {
		Arc pair = binHeap[i];
		int pred = (i - 1) / 2;
		while (pred >= 0 && compare(pair, binHeap[pred]) < 0) {
			positions[binHeap[pred].to] = i;
			binHeap[i] = binHeap[pred];
			i = pred;
			if (pred == 0) break;
			pred = (i - 1) / 2;
		}
		positions[pair.to] = i;
		binHeap[i] = pair;
	}

	/**
	 * Протаскивание элемента кучи с заданным индексом вниз по куче
	 * @param i	Индекс элемента
	 */
	private void heapDown(int i) {
		Arc pair = binHeap[i];
		int next = 2 * i + 1;
		while (next < heapSize) {
			if (next + 1 < heapSize && compare(binHeap[next+1], binHeap[next]) < 0) {
				next++;
			}
			if (compare(pair, binHeap[next]) <= 0) {
				break;
			}
			positions[binHeap[next].to] = i;
			binHeap[i] = binHeap[next];
			i = next;
			next = 2 * i + 1;
		}
		positions[pair.to] = i;
		binHeap[i] = pair;
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
		
		Prim prim = new Prim(g);
		Set<Edge> sceleton = prim.getSceleton();
		System.out.println(Arrays.toString(sceleton.toArray(new Edge[0])));
		double wholeWeight = 0;
		for (Edge edge : sceleton) {
			wholeWeight += edge.weight;
		}
		System.out.println("Sceleton weight = " + wholeWeight);
	}
}
