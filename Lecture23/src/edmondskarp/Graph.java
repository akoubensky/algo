package edmondskarp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Решение задачи нахождения максимального потока в сети
 * методом Фалкерсона - Форда (реализация алгоритма Эдмондса - Карпа).
 * 
 * Класс представляет собой описание сети, нагруженной целыми значениями.
 * Представление сети - в виде списков смежности.
 * 
 */
public class Graph {
	/**
	 * Дуга содержит как максимальную нагрузку, так и нагрузку
	 * текущего потока в сети.
	 */
	private static class Arc {
		final int from, to;	// Концы дуги
		final int capacity;	// Пропускная способность дуги
		int flow = 0;		// Величина потока по дуге
		
		Arc(int from, int to, int capacity) {
			this.from = from;
			this.to = to;
			this.capacity = capacity;
		}
	}
	
	/**
	 * Количество вершин графа.
	 */
	private final int count;
	
	/**
	 * Источник и сток сети.
	 */
	final int source, sink;
	
	/**
	 * Все дуги графа.
	 */
	private final List<Arc> arcs = new ArrayList<>();
	
	/**
	 * Списки смежности графа.
	 */
	private final List<Arc>[] graph;
	
	/**
	 * Конструктор пустой сети.
	 * @param n			Число вершин графа.
	 * @param source	Источник.
	 * @param sink		Сток.
	 */
	@SuppressWarnings("unchecked")
	public Graph(int n, int source, int sink) {
		if (n < 2) throw new IllegalArgumentException("There should exist at least 2 nodes");
		// Инициализация списков смежности.
		graph = new List[count = n];
		for (int i = 0; i < n; i++) graph[i] = new ArrayList<Arc>();
		// Инициализация истока и стока.
		this.source = source;
		this.sink = sink;
	}
	
	/**
	 * Добавляет дугу в сеть. Дуга попадает в списки смежности дважды,
	 * один раз как прямая дуга, и другой раз - как обратная.
	 * @param from		Начало дуги.
	 * @param to		Конец дуги.
	 * @param capacity	Пропускная способность дуги.
	 */
	public void addArc(int from, int to, int capacity) {
		if (from < 0 || to < 0 || from >= count || to >= count || capacity < 0) {
			throw new IllegalArgumentException("Invalid arc parameters");
		}
		Arc arc = new Arc(from, to, capacity);
		arcs.add(arc);
		graph[from].add(arc);
		graph[to].add(arc);
	}
	
	/**
	 * Удаляет дугу из графа. Дуга должна быть удалена из двух списков смежности.
	 * @param from	Начало дуги.
	 * @param to	Конец дуги.
	 */
	public void removeArc(int from, int to) {
		// Поиск дуги в списке дуг
		Arc arcToRemove = null;
		for(Arc arc : graph[from]) {
			if (arc.from == from && arc.to == to) {
				arcToRemove = arc;
				break;
			}
		}
		if (null == arcToRemove) return;
		// Удаление дуги из списков смежности.
		graph[from].remove(arcToRemove);
		graph[to].remove(arcToRemove);
		arcs.remove(arcToRemove);
	}
	
	/**
	 * Находит максимальный поток в сети по методу Форда - Фалкерсона.
	 * Реализует алгоритм Эдмондса - Карпа.
	 * @return	Величина максимального потока в сети
	 */
	public int edmondsKarp() {
		// Существующий поток удаляется.
		clearFlow();
		// Цикл по увеличению потока в сети, пока имеется путь
		// с положительной нагрузкой от истока к стоку в остаточной сети.
		while(searchPath() > 0) ;
		// Величина максимального потока - сумма величин потока на дугах,
		// исходящих из истока.
		int maxFlow = 0;
		for (Arc arc : graph[source]) {
			maxFlow += arc.flow;
		}
		return maxFlow;
	}
	
	/**
	 * Обнуляет поток по всем дугам.
	 */
	private void clearFlow() {
		arcs.forEach(arc -> arc.flow = 0);
	}
	
	/**
	 * Ищет дополняющий путь в остаточной сети графа с положительной нагрузкой.
	 * Поиск ведется обходом графа в ширину и пркращается в тот момент, когда
	 * "волна" захватывает сток.
	 * @return	Величина дополнительной нагрузки.
	 */
	private int searchPath() {
		// Дерево обхода в ширину. В дереве хранятся дуги, а не вершины,
		// что облегчает последующий анализ и модификацию потока вдоль пути.
		Arc[] path = new Arc[count];
		// Отметка пройденных вершин. Вершина считается пройденной в тот момент,
		// Когда ее впервые захватывает "волна".
		boolean[] passed = new boolean[count];
		// Очередь вершин для обхода.
		LinkedList<Integer> queue = new LinkedList<>();
		
		// Обход по остаточной сети.
		queue.addLast(source);
		passed[source] = true;
		while (!queue.isEmpty()) {
			int u = queue.removeFirst();
			for (Arc arc : graph[u]) {
				int end = -1;
				if (arc.from == u && !passed[arc.to] && arc.flow < arc.capacity) {
					// Прямая дуга, можно увеличить поток вдоль дуги.
					end = arc.to;
				} else if (arc.to == u && !passed[arc.from] && arc.flow > 0) {
					// Обратная дуга, можно уменьшить поток вдоль дуги.
					end = arc.from;
				}
				if (end >= 0) {
					// Найдена новая вершина. Пополняем дерево обхода.
					path[end] = arc;
					if (end == sink) {
						// Найден путь в остаточной сети!
						return modifyPath(path);
					} else {
						passed[end] = true;
						queue.add(end);
					}
				}
			}
		}
		// Путь не найден.
		return 0;
	}
	
	/**
	 * Анализирует путь, найденный в остаточной сети, и подифицирует поток вдоль пути.
	 * @param p
	 * @return
	 */
	private int modifyPath(Arc[] p) {
		// Вычисляем максимально возможное увеличение потока вдоль пути.
		int addition = Integer.MAX_VALUE;
		int next = sink, pred;
		while (next != source) {
			Arc arc = p[next];
			if (arc.to == next) {
				pred = arc.from;
				if (addition > arc.capacity - arc.flow) {
					addition = arc.capacity - arc.flow;
				}
			} else {
				pred = arc.to;
				if (addition > arc.flow) {
					addition = arc.flow;
				}
			}
			next = pred;
		}
		// Производим изменение потока вдоль пути.
		next = sink;
		while (next != source) {
			Arc arc = p[next];
			if (arc.to == next) {
				pred = arc.from;
				arc.flow += addition;
			} else {
				pred = arc.to;
				arc.flow -= addition;
			}
			next = pred;
		}
		return addition;
	}
	
	/**
	 * Читает строки из потока. Каждая строка представляет собой тройку чисел,
	 * характеризующую дугу графа: номера вершин концов дуги и ее пропускную способность.
	 * Строит сеть из всех заданных дуг.
	 * @param n			// Количество вершин графа
	 * @param arcs		// Поток строк, представляющих дуги
	 * @param source	// Номер вершины истока
	 * @param sink		// Номер вершины стока
	 * @return			// Сеть, построенная по заданным параметрам.
	 */
	public static Graph build(int n, Stream<String> arcs, int source, int sink) {
		Graph g = new Graph(n, source, sink);
		arcs
			.filter(s -> s.matches("\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*"))
			.map(String::trim)
			.map((s -> s.split("\\s*,\\s*")))
			.forEach(triple -> g.addArc(Integer.parseInt(triple[0]),
					                    Integer.parseInt(triple[1]),
					                    Integer.parseInt(triple[2])));
		return g;
	}
}
