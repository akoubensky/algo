import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация индексного дерева (Trie, бор) с ключами-строками
 * @param <V>   Тип значений, хранящихся в дереве
 */
public class Trie<V> {
    /**
     * Дуга дерева, помеченная символом-меткой
     * @param <V>   Тип значений, хранящихся в узлах дерева
     */
    private static class Arc<V> {
        /** Метка на дуге */
        char symbol;
        /** Узел, в который ведет дуга */
        Node<V> node;

        /**
         * Создает дугу, помеченную заданной меткой и ведущую в заданный узел
         * @param symbol    Метка на дуге
         * @param node      Узел, в который ведет дуга
         */
        private Arc(char symbol, Node<V> node) {
            this.symbol = symbol;
            this.node = node;
        }

        /**
         * Создает дугу, помеченную заданной меткой и ведущую в новый узел с заданным значением
         * @param symbol    Метка на дуге
         * @param value     Значение, хранящееся в узле
         */
        public Arc(char symbol, V value) {
            this(symbol, new Node<>(value));
        }

        /**
         * Создает дугу, помеченную заданной меткой и ведущую в новый узел без хранимого значения
         * @param symbol    Метка на дуге
         */
        public Arc(char symbol) {
            this(symbol, new Node<>());
        }
    }

    /**
     * Узел дерева, возможно, хранящий значение и содержащий упорядоченный по меткам список дуг,
     * ведущих к продолжениям ключей
     * @param <V>   Тип значений, хранящихся в узлах дерева
     */
    private static class Node<V> {
        /** Признак хранимого значения */
        boolean hasValue = false;
        /** Хранимое значение (null, если такого значения нет) */
        V value;
        /** Упорядоченный по меткам список дуг, ведущих к продолжениям ключей */
        List<Arc<V>> arcs = new ArrayList<>();

        /**
         * Создание узла, содержащего значение
         * @param value Хранимое значение
         */
        Node(V value) {
            this.value = value;
        }

        /**
         * Создание пустого узла
         */
        Node() {}

        /**
         * Вставка новой дуги в упорядоченный список дуг заданного узла.
         * @param arc   Вставляемая дуга
         */
        void insert(Arc<V> arc) {
            int index = 0;
            for (ListIterator<Arc<V>> it = arcs.listIterator(); it.hasNext(); index++) {
                if (it.next().symbol >= arc.symbol) {
                    break;
                }
            }
            arcs.subList(0, index).add(arc);
        }
    }

    /**
     * Результат удаления постфикса
     * @param <V>   Тип значений, хранящихся в узлах дерева
     */
    private static class RemoveResult<V> {
        /** Надо ли удалять ветку? */
        boolean remove;
        /** Результат удаления постфикса */
        Optional<V> result;

        /**
         * Сщздает результат удаления постфикса
         * @param remove    Надо ли удалять ветку
         * @param result    Результат удаления постфикса
         */
        public RemoveResult(boolean remove, Optional<V> result) {
            this.remove = remove;
            this.result = result;
        }
    }

    /**
     * Пара из ключа и значения - элемент множкства пар, полученного в результате обхода дерева
     * @param <V>   Тип значений, хранящихся в узлах дерева
     */
    private static class TrieEntry<V> implements Map.Entry<String, V>, Comparable<TrieEntry<V>> {
        /** Ключ */
        private String key;
        /** Значение */
        private V value;

        /**
         * Создает пару из ключа и значения
         * @param key   Ключ
         * @param value Значение
         */
        TrieEntry(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V safe = this.value;
            this.value = value;
            return safe;
        }

        /**
         * Сравнение пар из ключа и значения по ключу и, если значение реализует интерфейс Comparable,
         * то затем и по значению.
         * @param o Пара для сравнения
         * @return  Результат сравнения,
         * @see Comparable#compareTo(Object)
         */
        @Override
        public int compareTo(TrieEntry<V> o) {
            int res =  key.compareTo(o.key);
            if (res == 0 && value.getClass().isInstance(Comparable.class)) {
                res = ((Comparable<V>)value).compareTo(o.value);
            }
            return res;
        }

        /**
         * Выдает текстовое представление пары из ключа и значения в виде <i>ключ: значение</i>
         * @return  Текстовое представление пары из ключа и значения
         */
        @Override
        public String toString() {
            return key + ": " + value;
        }
    }

    /** Корень дерева - узел с пустым префиксом */
    private Node<V> root = new Node<>();

    /**
     * Осуществляет поиск значения в дереве по ключу
     * @param key   Ключ поиска
     * @return      Найденное значение или Optional.empty, если значения по заданному ключу не найдено
     */
    public Optional<V> get(String key) {
        if (key == null) throw new IllegalArgumentException("null key");
        // Вызов вспомогательной рекурсивной функции
        return get(key, 0, root);
    }

    /**
     * Добавляет или заменяет значение в дереве по заданному ключу
     * @param key   Ключ
     * @param value Значение для добавления или замены
     * @return      Значение, которое ранее было связано с данным ключом в дереве,
     *              Optional.empty, если такого значения не было
     */
    public Optional<V> put(String key, V value) {
        if (key == null) throw new IllegalArgumentException("null key");
        // Вызов вспомогательной рекурсивной функции
        return put(key, 0, root, value);
    }

    /**
     * Удаляет значение из дерева по заданному ключу. Если такого значения в дереве не было, то дерево не изменяется.
     * @param key   Ключ
     * @return      Значение, которое ранее было связано с данным ключом в дереве,
     *              Optional.empty, если такого значения не было
     */
    public Optional<V> remove(String key) {
        if (key == null) throw new IllegalArgumentException("null key");
        // Отдельно рассматривается случай пустого ключа
        if (key.isEmpty()) {
            Optional<V> result = root.hasValue ? Optional.of(root.value) : Optional.empty();
            root.hasValue = false;
            root.value = null;
            return result;
        }
        // Вызов вспомогательной рекурсивной функции
        RemoveResult<V> rr = remove(key, 0, root);
        if (rr.remove) {
            // Удаление "висячей" ветки
            remove(root.arcs, key.charAt(0));
        }
        return rr.result;
    }

    /**
     * Выдает упорядоченный по ключу набор пар из ключа и значения, хранящихся в дереве.
     * @return  Набор пар из ключа и значения, хранящихся в дереве
     */
    public List<Map.Entry<String, V>> entryList() {
        return entryList("", root);
    }

    //=============================================================

    /**
     * Поиск дуги в упорядоченном списке по заданному ключу-метке дуги
     * @param arcs  Список дуг
     * @param c     Метка-ключ поиска
     * @param <V>   Тип значений, хранящихся в узлах дерева
     * @return
     */
    private static <V> Node<V> find(List<Arc<V>> arcs, char c) {
        for (Arc<V> arc : arcs) {
            if (arc.symbol == c) {
                return arc.node;
            } else if (arc.symbol > c) {
                break;
            }
        }
        return null;
    }

    /**
     * Удаление дуги из (упорядоченного) списка дуг по заданному ключу-метке дуги.
     * assert: удаляемая дуга непременно есть в списке
     * @param arcs  Список дуг
     * @param c     Метка-ключ удаляемой дуги
     * @param <V>   Тип значений, хранящихся в узлах дерева
     */
    private static <V> void remove(List<Arc<V>> arcs, char c) {
        for (Iterator<Arc<V>> it = arcs.iterator(); it.hasNext(); ) {
            if (it.next().symbol == c) {
                it.remove();
                return;
            }
        }
    }

    /**
     * Вспомогательная рекурсивная функция поиска в дереве по заданному ключу.
     * @param key   Ключ поиска
     * @param i     Индекс символа в ключе, показывающий позицию очередного еще не просмотренного символа
     * @param node  Корневой узел для поиска
     * @param <V>   Тип значений, хранящихся в узлах дерева
     * @return      Значение, связанное с заданным ключом, если оно есть в дереве, иначе - Optional.empty
     */
    private static <V> Optional<V> get(String key, int i, Node<V> node) {
        if (i == key.length()) {
            return node.hasValue ? Optional.of(node.value) : Optional.empty();
        }
        Node<V> next = find(node.arcs, key.charAt(i));
        return next == null ? Optional.empty() : get(key, i+1, next);
    }

    /**
     * Вспомогательная рекурсивная функция добавления или замены значения в дереве по заданному ключу
     * @param key   Ключ для поиска или вставки
     * @param i     Индекс символа в ключе, показывающий позицию очередного еще не просмотренного символа
     * @param node  Корневой узел для поиска
     * @param value Вставляемое значение
     * @return      Значение, ранее связанное с заданным ключом в дереве, если таковое было.
     *              В противном случае - Optional.empty
     */
    private Optional<V> put(String key, int i, Node<V> node, V value) {
        if (i == key.length()) {
            // Вставка или замена значения
            Optional<V> result = node.hasValue ? Optional.of(node.value) : Optional.empty();
            node.hasValue = true;
            node.value = value;
            return result;
        } else {
            Node<V> next = find(node.arcs, key.charAt(i));
            if (next == null) {
                // Добавление ранее отсутствующей дуги
                node.insert(new Arc<V>(key.charAt(i), next = new Node<>()));
            }
            return put(key, i+1, next, value);
        }
    }

    /**
     * Вспомогательная рекурсивная функция, выдающая упорядоченный по ключу набор пар из ключа и значения,
     * хранящихся в дереве.
     * @param prefix    Префикс ключей, имеющихся в поддереве с заданным корнем
     * @param node      Корень для поиска
     * @return          Упорядоченный по ключу набор пар из ключа и значения
     */
    private List<Map.Entry<String, V>> entryList(String prefix, Node<V> node) {
        List<Map.Entry<String, V>> result = new ArrayList<>();
        if (node.hasValue) result.add(new TrieEntry<>(prefix, node.value));
        // Перебор в порядке возрастания меток дуг
        for (Arc<V> arc : node.arcs) {
            result.addAll(entryList(prefix + arc.symbol, arc.node));
        }
        return result;
    }

    /**
     * Вспомогательная рекурсивная функция удаления значения с заданным ключом из дерева
     * @param key   Ключ для удаления значения
     * @param i     Индекс в строке-ключе для поиска суффикса
     * @param node  Корневой узел для поиска по дереву
     * @return      Результат удаления значения.
     */
    private RemoveResult<V> remove(String key, int i, Node<V> node) {
        if (i == key.length()) {
            // Удаление значения, если оно было связано с ключом
            Optional<V> result = node.hasValue ? Optional.of(node.value) : Optional.empty();
            node.hasValue = false;
            node.value = null;
            // Ветку надо будет удалить, если из ее конечного узла уже нет никаких дуг
            return new RemoveResult<>(node.arcs.isEmpty(), result);
        } else {
            Node<V> next = find(node.arcs, key.charAt(i));
            if (next == null) {
                // Ключ в дереве не найден
                return new RemoveResult<>(false, Optional.empty());
            }
            RemoveResult<V> rr = remove(key, i+1, next);
            if (rr.remove) {
                // Удаление "висячей" ветки
                remove(node.arcs, key.charAt(i));
            }
            return new RemoveResult<>(rr.remove && node.arcs.isEmpty() && !node.hasValue, rr.result);
        }
    }
}
