package warshall;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Представление ненагруженного графа матрицей смежности.
 * Биты представления упаковываются по 32 разряда в целые значения.
 * Алгоритм Уоршалла представлен методом вычисления транзитивного замыкания графа.
 */
public class Graph {
	private final int[][] matrix;	// Матрица смежности
	private final int nVertex;		// Число вершин
	private final int strLength;	// Количество элементов в строке упакованной матрицы
	private final int PACKAGE = 32;	// Количество битов в упакованном элементе
	private final int POWER = 5;	// Степень, в которую нужно возвести двойку, чтобы получить PACKAGE

	/**
	 * Конструктор пустого графа с заданным числом вершин
	 * @param nVert Число вершин
	 */
	public Graph(int nVert) {
		if (nVert <= 0) {
			throw new IllegalArgumentException();
		}
		strLength = (nVert + (PACKAGE-1)) >> POWER;
		matrix = new int[nVertex = nVert][strLength]; 
	}
	
	/**
	 * Число вершин графа
	 * @return
	 */
	public int getCount() { return nVertex; }
	
	/**
	 * Проверяет, соединены ли две вершины дугой.
	 * @param u	Первая вершина
	 * @param v	Вторая вершина
	 * @return	true, если соединены, false в противном случае.
	 */
	public boolean isConnected(int u, int v) {
		if (u < 0 || v < 0 || u >= nVertex || v >= nVertex) {
			throw new IllegalArgumentException();
		}
		return (matrix[u][v >> POWER] & (1 << (v & (PACKAGE-1)))) != 0;
	}
	
	/**
	 * Добавляет дугу в граф, если ее еще там не было.
	 * @param u	Начало дуги
	 * @param v	Конец дуги
	 */
	public void setArc(int u, int v) {
		if (u < 0 || v < 0 || u >= nVertex || v >= nVertex) {
			throw new IllegalArgumentException();
		}
		matrix[u][v >> POWER] |= (1 << (v & (PACKAGE-1)));
	}

	/**
	 * Убирает дугу из графа, если она там была.
	 * @param u	Начало дуги
	 * @param v	Конец дуги
	 */
	public void clearArc(int u, int v) {
		if (u < 0 || v < 0 || u >= nVertex || v >= nVertex) {
			throw new IllegalArgumentException();
		}
		matrix[u][v >> POWER] &= ~(1 << (v & (PACKAGE-1)));
	}
	
	/**
	 * Выдает множество вершин, смежных с заданной, в виде итерируемой последовательности.
	 * @param u	Номер исходной вершины.
	 * @return	Последовательность смежных с заданной вершин.
	 */
	public Iterable<Integer> adjacent(int u) {
		if (u < 0 || u >= nVertex) {
			throw new IllegalArgumentException();
		}
		// Возвращаемая функция представляет интерфейс Iterable.
		return () -> new Iterator<Integer>() {
			int next = -1;
			
			{ setNext(); }
			
			/**
			 * Сдвигает указателдь next на следующую из существующих дуг.
			 */
			private void setNext() {
				int[] line = matrix[u];
				do {
					next++;
				} while ((line[next >> POWER] & (1 << (next & (PACKAGE-1)))) == 0);
			}

			@Override
			public boolean hasNext() {
				return next < nVertex;
			}

			@Override
			public Integer next() {
				int res = next;
				setNext();
				return res;
			}
		};
	}

	/**
	 * Строит транзитивное замыкание графа с помощью алгоритма Уоршалла.
	 */
	public void closeTransitive() {
		for (int k = 0; k < nVertex; k++) {
			for (int i = 0; i < nVertex; i++) {
				if (isConnected(i, k)) {
					int[] lineI = matrix[i];
					int[] lineK = matrix[k];
					for (int j = 0; j < strLength; j++) {
						lineI[j] |= lineK[j];
					}
				}
			}
		}
	}
	
	/**
	 * Печатает граф в виде матрицы из нулей и единиц.
	 * @param out	Поток печати.
	 */
	public void printMatrix(PrintStream out) {
		for (int i = 0; i < nVertex; i++) {
			int[] line = matrix[i];
			int elem = 0;
			for (int j = 0; j < nVertex; j++) {
				int jMod = j & (PACKAGE-1);
				if (jMod == 0) elem = line[j >> POWER];
				out.print((char)('0' + ((elem >>> jMod) & 1))); 
			}
			out.println();
		}
	}
	
	/**
	 * Проверка правильности работы алгоритма Уоршалла на примере графа из
	 * 40 узлов, связанных в два простых цикла.
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph(40);
		for (int i = 0; i < 19; i++)
			g.setArc(i, i+1);
		g.setArc(19, 0);
		for (int i = 20; i < 39; i++)
			g.setArc(i, i+1);
		g.setArc(39, 20);
		
		g.printMatrix(System.out);
		System.out.println("----------------");
		
		g.closeTransitive();
		g.printMatrix(System.out);
		System.out.println("----------------");
	}
}
