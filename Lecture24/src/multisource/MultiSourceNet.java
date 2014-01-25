package multisource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Реализация сети с несколькими истоками и стоками на основе графа
 * с дугами, несущими поток.
 */
public class MultiSourceNet extends Graph {
	private Set<Integer> sources = new HashSet<Integer>();	// Множество вершин истоков
	private Set<Integer> sinks = new HashSet<Integer>();	// Множество вершин стоков
	
	/**
	 * Конструктор для создания пустой сети с заданными истоками и стоками.
	 * @param count		Число вершин в сети
	 * @param sources	Истоки
	 * @param sinks		Стоки
	 */
	public MultiSourceNet(int count, Integer[] sources, Integer[] sinks) {
		super(count);
		this.sources.addAll(Arrays.asList(sources));
		this.sinks.addAll(Arrays.asList(sinks));
	}
	
	/**
	 * Конструктор для создания сети из другой сети, которая была получена 
	 * искусственным добавлением истока и стока в исходную сеть.
	 * @param net	Сеть с одним истоком и стоком
	 */
	public MultiSourceNet(Net net) {
		super(net.getCount() - 2);
		int n = net.getCount() - 2;
		int src = net.getSource();
		int sink = net.getSink();
		
		// Полагаем, что исток и сток находятся в конце списка вершин,
		// как это делается при искусственном добавлении истока и стока в сеть.
		assert src == n && sink == n + 1;
		
		// Истоки - это вершины, в которые ведут дуги из добавленного искусственно истока
		for (Iterator<Arc> iSrcArc = net.arcs(src); iSrcArc.hasNext(); ) {
			sources.add(iSrcArc.next().to());
		}
		
		// Копируем все дуги из исходной сети, попутно регистрируя стоки.
		for (int u = 0; u < n; ++u) {
			for (Iterator<Arc> iArc = net.arcs(u); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				if (arc.to() == sink) {
					sinks.add(u);
				} else {
					addArc(u, new Arc(arc));
				}
			}
		}
	}
	
	/**
	 * Создание сети путем добавления искусственно новых истока и стока.
	 * @return
	 */
	public Net createSimpleNet() {
		// Новая сеть будет иметь на две вершины больше;
		// исток и сток - две последние вершины.
		int n = getCount();
		Net net = new Net(n + 2, n, n+1);
		
		// Копируем дуги из исходной сети
		for (int u = 0; u < n; ++u) {
			for (Iterator<Arc> iArc = arcs(u); iArc.hasNext(); ) {
				Arc arc = iArc.next();
				net.addArc(u, arc);
			}
		}
		
		// Добавляем дуги, ведущие из нового истока в исходные истоки.
		for (int s : sources) {
			net.addArc(n, s, Integer.MAX_VALUE);
		}
		
		// Добавляем дуги, ведущие из исходных стоков в новый
		for (int s : sinks) {
			net.addArc(s, n + 1, Integer.MAX_VALUE);
		}
		
		return net;
	}

	/**
	 * Проверочная функция организует граф, представляющий некоторую
	 * сеть из 12 узлов (истоки - вершины № 0,8,9, стоки - вершины № 10,11)
	 * @param args
	 */
	public static void main(String[] args) {
		Integer[] sources = { 0, 8, 9 };
		Integer[] sinks = { 10, 11 }; 
		MultiSourceNet net = new MultiSourceNet(12, sources, sinks);

		net.addArc(0, 1, 8);
		net.addArc(0, 2, 12);
		net.addArc(1, 2, 10);
		net.addArc(1, 5, 3);
		net.addArc(2, 5, 21);
		net.addArc(2, 6, 13);
		net.addArc(3, 2, 12);
		net.addArc(3, 6, 27);
		net.addArc(3, 7, 17);
		net.addArc(3, 4, 4);
		net.addArc(4, 7, 17);
		net.addArc(5, 6, 11);
		net.addArc(5, 10, 15);
		net.addArc(6, 10, 17);
		net.addArc(6, 11, 18);
		net.addArc(7, 6, 9);
		net.addArc(7, 11, 20);
		net.addArc(8, 2, 18);
		net.addArc(8, 3, 13);
		net.addArc(9, 3, 15);
		net.addArc(9, 4, 22);

		// Мскусственно добавляем новые исток и сток
		Net singleSourceNet = net.createSimpleNet();
		// Запускаем алгоритм Эдмондса - Карпа на новой сети
		EdmondsKarp ek = new EdmondsKarp(singleSourceNet);
		int maxFlow = ek.edmondsKarp();
		// Возвращаемся обратно к исходной сети, сохраняя поток
		MultiSourceNet netResult = new MultiSourceNet(singleSourceNet);
		
		// Печатаем результат
		netResult.printNet();
		System.out.println("Maximum flow = " + maxFlow);
	}
}
