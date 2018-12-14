/**
 * Интерфейс, определяющий процедуры обработки вершин и дуг при обходе дерева
 */
public interface Action {
    /**
     * Обработка вершины в момент ее посещения.
     * @param n Номер вершины
     */
    void processVertex(int n);

    /**
     * Обработка дуги в момент прохода по дуге.
     * @param from
     * @param to
     */
    void processArc(int from, int to);
}
