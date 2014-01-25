/**
 * Абстрактный посетитель вершин и дуг графа. Все операции посещения - пустые.
 *
 * @param <W> Тип нагрузки на дуги
 */
public abstract class GraphVisitor<W extends Number> {
	/**
	 * Прохождение по дуге "вперед"
	 * @param from		Начало дуги (номер вершины)
	 * @param arc		Дуга, по которой проходим (конец дуги и нагрузка)
	 * @param retArc	Признак обратной дуги (ведет в уже посещенную вершину)
	 */
	public void visitArcForward(int from, Graph.Arc<W> arc, boolean retArc) {}
	
	/**
	 * Прохождение по дуге назад
	 * @param from		Начало дуги (номер вершины)
	 * @param arc		Дуга, по которой проходим (конец дуги и нагрузка)
	 */
	public void visitArcBackward(int from, Graph.Arc<W> arc) {}
	
	/**
	 * Первый заход в вершину
	 * @param v	Номер вершины
	 */
	public void visitVertexIn(int v) {}
	
	/**
	 * Окончательный выход из вершины
	 * @param v	Номер вершины
	 */
	public void visitVertexOut(int v) {}
	
	/**
	 * Начало обхода новой компоненты связности
	 * @param start	Номер исходной вершины компоненты
	 */
	public void visitComponentStart(int start) {}
}
