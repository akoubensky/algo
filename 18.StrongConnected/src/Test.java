import java.util.Set;

/**
 * Проверка правильности работы алгоритма поиска сильно связных компонент ориентированного графа.
 */
public class Test {
    public static void main(String[] args) {
        // Граф с 6 вершинами и 11 дугами содержит три компоненты сильной связности.
        // Нарисуйте этот граф и попробуйте определить его компоненты сильной связности до запуска теста.
        Graph g = new Graph(6);
        g.addArc(5, 3);
        g.addArc(1, 2);
        g.addArc(1, 3);
        g.addArc(2, 3);
        g.addArc(2, 4);
        g.addArc(3, 5);
        g.addArc(4, 1);
        g.addArc(0, 2);
        g.addArc(0, 4);
        g.addArc(0, 0);
        g.addArc(0, 5);

        // Запуск теста и печать результатов.
        Set<Set<Integer>> components = g.getStronglyConnected();
        for (Set<Integer> component : components) {
            System.out.println(component);
        }
    }
}
