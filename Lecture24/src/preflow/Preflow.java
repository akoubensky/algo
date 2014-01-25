package preflow;

import java.util.Iterator;

import preflow.Graph.Arc;

/**
 * Реализация вычисления максимального потока в вети методом
 * проталкивания предпотока.
 */
public class Preflow {
	/**
	 * Фиктивная дуга, несущая обратный поток.
	 */
	private static class ExtArc extends Arc {
		public ExtArc(int to) {
			super(to, 0);
		}

	}
	
	Graph graph;	// Сеть
	int nVertex;	// Количество вершин в сети
	
	/**
	 * Конструктор запоминает сеть, с которой будет вестись работа.
	 * @param g	Сеть
	 */
	public Preflow(Graph g) {
		graph = g;
		nVertex = g.getCount();
	}
	
	/**
	 * Собственно вычисление максимального потока в сети
	 * @return	Величина максимального потока
	 */
	public int maxFlow() {
		int source = graph.getSource();		// Исток
		int sink = graph.getSink();			// Сток
		int[] excess = new int[nVertex];	// Избытки в вершинах
		int[] height = new int[nVertex];	// Высоты вершин
		
		// 1. Инициализация
		for (int i = 0; i < nVertex; ++i) {
			for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				if (graph.getArc(arc.to(), i) == null) {
					graph.addArc(arc.to(), new ExtArc(i));
				}
				if (i == source) {
					// Пускаем максимальный поток по выходящим из истока дугам.
					arc.setFlow(arc.capacity());
					graph.getArc(arc.to(), i).setFlow(-arc.capacity());
					excess[arc.to()] = arc.capacity();
				} else if (arc.to() != source) {
					// Все остальные дуги имеют нулевой начальный поток.
					arc.setFlow(0);
				}
			}
		}
		// Высоты всех вершин кроме истока равны нулю.
		height[source] = nVertex;
		
		// 2. Цикл проверки вершин
		boolean excessFound;	// Есть вершина с положительным избытком?
		do {
			excessFound = false;
			for (int i = 0; i < nVertex; ++i) {
				if (i == source || i == sink) {
					continue;
				}
				if (excess[i] > 0) {
					// Нашли вершину с положительным избытком;
					// Исследуем ее соседей.
					excessFound = true;
					
					int minHeight = 2*nVertex;	// Минимальная из высот соседей
					int maxFlow = 0;			// Максимум потока по дугам
					Arc workArc = null;			// Дуга, по которой можно протолкнуть дополнительный поток.
					for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
						Arc arc = iArc.next();
						int to = arc.to();
						if (arc.capacity() - arc.flow() > 0) {
							if (height[to] == height[i] - 1) {
								// Можно выполнить проталкивание
								if (arc.capacity() - arc.flow() > maxFlow) {
									maxFlow = arc.capacity() - arc.flow();
									workArc = arc;
								}
							}
							// Оцениваем высоту вершины для подъема.
							if (height[to] < minHeight) {
								minHeight = height[to];
							}
						}
					}
					
					if (workArc != null) {
						// Выполняем проталкивание предпотока по дуге
						int flow = Math.min(excess[i], maxFlow);
						excess[i] -= flow;
						excess[workArc.to()] += flow;
						workArc.changeFlow(flow);
						graph.getArc(workArc.to(), i).changeFlow(-flow);
						System.out.println("Push " + i + " -> " + workArc.to() + " (" + flow + ")");
					} else {
						// Выполняем подъём
						height[i] = minHeight + 1;
						System.out.println("Lift " + i + " to " + height[i]);
					}
				}
			}
		} while (excessFound);
		
		// 3. Убираем фиктивные дуги
		for (int i = 0; i < nVertex; ++i) {
			for (Iterator<Arc> iArc = graph.arcs(i); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				if (arc instanceof ExtArc) {
					iArc.remove();
				}
			}
		}
		
		// 4. Вычисление величины потока
		int maxFlow = 0;
		for (Iterator<Arc> iArc = graph.arcs(source); iArc.hasNext(); ) {
			maxFlow += iArc.next().flow();
		}
		
		return maxFlow;
	}

	/**
	 * Проверочная функция организует граф, представляющий некоторую
	 * сеть из 6 узлов (исток - вершина № 0, сток - вершина № 5)
	 * @param args
	 */
	public static void main(String[] args) {
		Graph net = new Graph(6, 0, 5);

		net.addArc(0, 1, 16);
		net.addArc(0, 2, 13);
		net.addArc(1, 2, 10);
		net.addArc(1, 3, 12);
		net.addArc(2, 1, 4);
		net.addArc(2, 4, 14);
		net.addArc(3, 2, 9);
		net.addArc(3, 5, 20);
		net.addArc(4, 3, 7);
		net.addArc(4, 5, 4);

		Preflow pf = new Preflow(net);
		int maxFlow = pf.maxFlow();
		net.printNet();
		System.out.println("Maximum flow = " + maxFlow);
	}
}
