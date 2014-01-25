import java.util.Arrays;

/**
 * Реализация алгоритма Хаффмена с построением дерева кодов.
 */
public class HuffmanTree {
	/**
	 * Основная функция построения кодов согласно алгоритму Хаффмена.
	 * @param p Массив частот символов
	 * @return Массив соответствующих кодов
	 */
	public static String[] huffman(double[] p) {
		// Создаем лес, в котором каждое дерево представляет один символ алфавита.
		HuffmanTree huffman = new HuffmanTree(p);
		// Следующая функция осуществляет слияние деревьев.
		huffman.huffman();
		// Кода символов извлекаются из результирующего дерева.
		return extractCodes(getCodes(huffman.extractMin()));
	}
	
	/**
	 * Элемент дерева
	 */
	private abstract static class TreeElement {
		// Вероятность, приписанная элементу (приоритет в куче)
		final double m_prob;
		
		/**
		 * Абстрактный конструктор
		 * @param prob Вероятность появления элемента
		 */
		TreeElement(double prob) {
			m_prob = prob;
		}
		
		/**
		 * Функция доступа к вероятности.
		 * @return
		 */
		double getProb() { return m_prob; }
	}
	
	/**
	 * Простой символ как элемент дерева.
	 * Содержит номер этого символа в алфавите.
	 */
	private static class Symbol extends TreeElement {
		// Номер символа
		final int m_number;
		
		/**
		 * Конструктор символа
		 * @param number Номер символа
		 * @param prob Вероятность (частота) появления символа.
		 */
		Symbol(int number, double prob) {
			super(prob);
			m_number = number;
		}
		
		/**
		 * Функция доступа к номеру символа
		 * @return
		 */
		int getNumber() { return m_number; }
	}
	
	/**
	 * Составной символ состоит из двух половин.
	 */
	private static class SuperSymbol extends TreeElement {
		// Элементы, составляющие левую и правую половины.
		final TreeElement m_left, m_right;
		
		/**
		 * Конструктор
		 * @param left Левое поддерево (или простой символ)
		 * @param right Правое поддерево
		 */
		SuperSymbol(TreeElement left, TreeElement right) {
			super(left.getProb() + right.getProb());
			m_left = left;
			m_right = right;
		}
		
		/**
		 * Функция доступа к левой половине
		 * @return
		 */
		TreeElement getLeft() { return m_left; }
		
		/**
		 * Функция доступа к правой половине
		 * @return
		 */
		TreeElement getRight() { return m_right; }
	}
	
	// Лес из деревьев организован в двоичную кучу, представленную массивом.
	private TreeElement[] m_heap;
	// Текущий размер кучи
	private int m_size;
	
	/**
	 * Конструктор строит начальную кучу, упорядочивая
	 * заданный список вероятностей символов алфавита.
	 * @param p Список вероятностей символов
	 */
	private HuffmanTree(double[] p) {
		m_size = p.length;
		m_heap = new TreeElement[m_size];
		for (int i = 0; i < m_size; ++i) {
			m_heap[i] = new Symbol(i, p[i]);
		}
		buildHeap();
	}
	
	/**
	 * Выстраивание массива элементов в кучу
	 */
	private void buildHeap() {
		for (int i = m_size / 2; i >= 0; --i) {
			heapify(i);
		}
	}
	
	/**
	 * Функция проталкивания элемента вниз по куче
	 * @param index Индекс элемента с вершины кучи.
	 */
	private void heapify(int index) {
		TreeElement e = m_heap[index];
		int son;
		while ((son = 2*index + 1) < m_size) {
			if (son + 1 < m_size && m_heap[son+1].getProb() < m_heap[son].getProb()) {
				son++;
			}
			if (m_heap[son].getProb() < e.getProb()) {
				m_heap[index] = m_heap[son];
				index = son;
			} else {
				break;
			}
		}
		m_heap[index] = e;
	}
	
	/**
	 * Извлечение самого приоритетного элемента (с минимальной вероятностью).
	 * Элемент удаляется из кучи.
	 * @return
	 */
	private TreeElement extractMin() {
		TreeElement e = m_heap[0];
		m_heap[0] = m_heap[--m_size];
		heapify(0);
		return e;
	}
	
	/**
	 * Добавление нового элемента в кучу.
	 * @param e
	 */
	private void add(TreeElement e) {
		double prob = e.getProb();
		// Элемент e проталкивается в направлении вершины кучи.
		int index = m_size++;
		while (index > 0) {
			int parent = (index - 1) / 2;
			if (m_heap[parent].getProb() > prob) {
				m_heap[index] = m_heap[parent];
				index = parent;
			} else {
				break;
			}
		}
		m_heap[index] = e;
	}
	
	/**
	 * Реализацтя алгоритма Хаффмена: из кучи берутся два элемента,
	 * соединяются в один "суперэлемент" и он записывается в кучу.
	 * Процедура продолжается, пока в куче есть хотя бы два элемента.
	 */
	private void huffman() {
		while(m_size > 1) {
			add(new SuperSymbol(extractMin(), extractMin()));
		}
	}
	
	/**
	 * Пара из номера символа и его кода.
	 */
	private static class Code {
		// Номер символа
		int m_numb;
		// Код символа
		StringBuilder m_code;
		
		/**
		 * Конструктор пары: в начале каждому символу приписывается пустой код.
		 * @param num
		 */
		Code(int num) {
			m_numb = num;
			m_code = new StringBuilder();
		}
		
		/**
		 * Добавление нуля или единицы к коду
		 * @param right
		 * @return
		 */
		Code join(boolean right) {
			m_code.append(right ? '1' : '0');
			return this;
		}
	}
	
	/**
	 * Соединение двух списков кодов, при котором к первой половине кодов
	 * добавляется символ "0", а ко второй половине = "1".
	 * @param a1 Первая половина списка кодов
	 * @param a2 Вторая половина списка кодов
	 * @return
	 */
	private static Code[] join(Code[] a1, Code[] a2) {
		Code[] joint = new Code[a1.length + a2.length];
		int j = 0;
		for (int i = 0; i < a1.length; ++i) {
			joint[j++] = a1[i].join(false);
		}
		for (int i = 0; i < a2.length; ++i) {
			joint[j++] = a2[i].join(true);
		}
		return joint;
	}
	
	/**
	 * Извлечение кодов из дерева.
	 * @param e
	 * @return
	 */
	private static Code[] getCodes(TreeElement e) {
		if (e instanceof SuperSymbol) {
			SuperSymbol ee = (SuperSymbol)e;
			return join(getCodes(ee.getLeft()), getCodes(ee.getRight()));
		} else {
			return new Code[] { new Code(((Symbol)e).getNumber()) };
		}
	}
	
	/**
	 * Преобразование списка кодов в простой массив
	 * в соответствии с номерами символов.
	 * @param codes
	 * @return
	 */
	private static String[] extractCodes(Code[] codes) {
		String[] res = new String[codes.length];
		for (int i = 0; i < codes.length; ++i) {
			Code code = codes[i];
			res[code.m_numb] = code.m_code.reverse().toString();
		}
		return res;
	}
	
	/**
	 * Проверка на простом примере.
	 * @param args
	 */
	public static void main(String[] args) {
		double[] p = {0.12, 0.11, 0.20, 0.20, 0.19, 0.09, 0.09};
		System.out.println(Arrays.toString(HuffmanTree.huffman(p)));
	}
}
