package multisource;

/**
 * Расширение графа с помощью добавления информации об истоке и стоке
 */
public class Net extends Graph {
	private int source;					// Исток
	private int sink;					// Сток

	/**
	 * Конструктор добавляет информацию об истоке и стоке.
	 * @param count		Число вершин сети
	 * @param source	Номер вершины-истока
	 * @param sink		Номер вершины-стока
	 */
	public Net(int count, int source, int sink) {
		super(count);
		this.source = source;
		this.sink = sink;
	}
	
	/**
	 * Исток в сети
	 * @return	Номер вершины истока
	 */
	public int getSource() { return source; }
	
	/**
	 * Сток в сети
	 * @return	Номер вершины стока
	 */
	public int getSink() { return sink; }
}
