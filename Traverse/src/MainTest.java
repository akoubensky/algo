/**
 * Проверка работоспособности процедуры обхода графа "в глубину".
 */
public class MainTest {
    /**
     * Класс, представляющий простое действие при обходе вершин графа -
     * печать номера вершиныи и номеров вершин на концах дуги.
     */
    private static class MyAction implements Action {

        @Override
        public void processVertex(int n) {
            System.out.format("(%d) ", n);
        }

        @Override
        public void processArc(int from, int to) {
            System.out.format("(%d -> %d) ", from, to);
        }
    }

    public static void main(String[] args) {
        // Простой неориентированный граф с пятью вершинами и шестью ребрами.
        Graph g = new Graph(5);
        g.addArc(0, 2); g.addArc(0, 3);
        g.addArc(1, 3);
        g.addArc(2, 3); g.addArc(2, 4);
        g.addArc(2, 4);

        // Обход графа с печатью протокола.
        g.traverse(new MyAction());
        System.out.println();
    }
}
