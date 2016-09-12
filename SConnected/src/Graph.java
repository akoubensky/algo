import java.util.*;

/**
 * Представление ориентированного ненагруженного графа в виде списков смежности.
 * В данном классе определена функция поиска списка компонент сильной связности графа.
 * Для решения этой задачи используется также транспонирование графа - построение графа с обращенными ребрами.
 */
public class Graph {
    /**
     * Списки смежности. Предполагается, что в графе нет кратных ребер, хотя работу алгоритма это и не ограничивает.
     */
    private final Set<Integer>[] graph;

    /**
     * Количество вершин в графе.
     */
    private final int n;

    /**
     * Конструктор "пустого" графа, не содержащего дуг.
     * @param n
     */
    public Graph(int n) {
        graph = new Set[this.n = n];
        for (int i = 0; i < n; i++) graph[i] = new HashSet<>();
    }

    /**
     * Добавление дуги в граф. Правильность задания аргументов не проверяется.
     * @param from  Номер вершины, из которой выходит дуга.
     * @param to    Номер вершины, в которую входит дуга.
     */
    public void addArc(int from, int to) {
        graph[from].add(to);
    }

    /**
     * Транспонирование графа - построение графа на основе исходного с обращенными дугами.
     * @return  Транспонированный граф.
     */
    public Graph transpose() {
        Graph transposed = new Graph(n);
        for (int i = 0; i < n; i++) {
            Set<Integer> set = graph[i];
            for (Integer j : set) {
                transposed.addArc(j, i);
            }
        }
        return transposed;
    }

    /**
     * Обход графа в глубину (dfs). Вершины для начала обхода каждой компоненты берутся в порядке,
     * определяемом первым аргументом. Действия, выполняемые при обходе, определяются вторым аргументом.
     * @param vertices  Упорядоченный набор вершин, из которого берутся стартовые вершины для обхода.
     * @param action    Действия по входу и выходу из очередной компоненты обхода и по выходу из вершины.
     */
    public void traverse(List<Integer> vertices, Action action) {
        boolean[] passed = new boolean[n];
        for (Integer u : vertices) {
            if (!passed[u]) {
                // Входим в новую компоненту обхода
                action.startComponent();
                traverse(u, passed, action);
                // Завершаем обработку очередной компоненты
                action.finishComponent();
            }
        }
    }

    /**
     * Рекурсивная функция, которая определяет основной алгоритм обхода компоненты графа.
     * @param u         Начальная вершина для обхода компоненты
     * @param passed    Массив пройденных вершин
     * @param action    Действия, осуществляемые при обходе.
     */
    private void traverse(int u, boolean[] passed, Action action) {
        passed[u] = true;
        for (Integer v : graph[u]) {
            if (!passed[v]) traverse(v, passed, action);
        }
        // В данном случае проходится только одна компонента,
        // так что исполняется только действие, связанное с выходом из вершины.
        action.passOut(u);
    }

    /**
     * Основной алгоритм поиска сильно связных компонент графа.
     * @return  Множество, элементами которого явлются наборы номеров вершин,
     *          составляющих одну компоненту сильной связности.
     */
    public Set<Set<Integer>> getStronglyConnected() {
        // Сначала проходим вершины графа в "естественном" порядке
        List<Integer> naturalOrder = new ArrayList<>(n);
        for (int i = 0; i < n; i++) naturalOrder.add(i);

        // При обходе помечаем вершины метками в соответствии с временем выхода из вершины
        Marker marker = new Marker(n);
        traverse(naturalOrder, marker);

        // Транспонируем граф и обходим его снова, теперь уже в порядке вершин, определяемом метками вершин.
        Graph transposed = transpose();
        ComponentsCollector collector = new ComponentsCollector();
        transposed.traverse(Sorter.getSorted(marker.marked), collector);

        // Результат накоплен в коллекторе компонент.
        return collector.getComponents();
    }

    /**
     * Этот класс определяет действие по выходу из вершины при обходе графа.
     * Это действие представляет собой присваивание метки вершине в обратном порядке, то есть вершина,
     * из которой выход осуществляется в первую очередь, получает максимальную метку.
     */
    private static class Marker extends ActionAdapter {
        private final int[] marked;
        private int nextMark;

        Marker(int n) {
            marked = new int[nextMark = n];
        }

        @Override
        public void passOut(int u) {
            marked[u] = nextMark--;
        }
    }

    /**
     * Этот класс определяет действия по входу и выходу из компоненты обхода и выходу из вершины при обходе.
     * Действие заклчается в том, что вершины собираются при обходе в компоненты,
     * и компоненты собираются в набор компонент.
     */
    private static class ComponentsCollector extends ActionAdapter {
        // Собираемый набор компонент
        Set<Set<Integer>> allComponents = new HashSet<>();
        // Очередная собираемая компонента
        Set<Integer> nextComponent;

        public Set<Set<Integer>> getComponents() {
            return allComponents;
        }

        /**
         * В начале обхода компоненты формируется новое множество вершин.
         */
        @Override
        public void startComponent() {
            nextComponent = new HashSet<>();
        }

        /**
         * При выходе из вершины ее номер фиксируется в очередной компоненте
         * @param u Номер покидаемой вершины.
         */
        @Override
        public void passOut(int u) {
            nextComponent.add(u);
        }

        /**
         * В конце обхода компоненты эта компонента добавляется в формируемый набор компонент.
         */
        @Override
        public void finishComponent() {
            allComponents.add(nextComponent);
        }
    }

    /**
     * Вспомогательный класс, осуществляющий сортировку номеров вершин в соответствии с полученными ими метками.
     */
    private static class Sorter {
        /**
         * Пара из номера вершины и ее метки. Упорядочивается по меткам.
         * Никакие две вершины не будут иметь одинаковых меток.
         */
        private static class MarkedVertex implements Comparable<MarkedVertex> {
            int num;
            int mark;

            MarkedVertex(int num, int mark) { this.num = num; this.mark = mark; }

            @Override
            public int compareTo(MarkedVertex o) {
                return mark - o.mark;
            }
        }

        /**
         * Функция сортировки номеров вершин в соответствии с их метками.
         * @param marked    Массив меток вершин
         * @return          Массив номеров вершин, упорядоченных в соответствии с их метками.
         */
        public static List<Integer> getSorted(final int[] marked) {
            // Формируем массив пар номеров вершин с их метками.
            MarkedVertex[] array = new MarkedVertex[marked.length];
            for (int i = 0; i < marked.length; i++) array[i] = new MarkedVertex(i, marked[i]);

            // Сортируем массив
            Arrays.sort(array);

            // Извлекаем результат - список номеров вершин
            List<Integer> result = new ArrayList<>(marked.length);
            for (MarkedVertex next : array) {
                result.add(next.num);
            }
            return result;
        }
    }
}
