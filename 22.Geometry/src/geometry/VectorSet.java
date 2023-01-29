package geometry;

import java.util.function.Consumer;

/**
 * Реализация упорядоченного множества векторов. Для простоты реализации используется
 * несбалансированное двоичное дерево. На самом деле в реализацию надо бы добавить
 * балансировку (например, реализовать красно-черное дерево). Стандартная структура
 * данных TreeSet не подходит, поскольку упорядочение веторов зависит от того,
 * в какой точке происходит сравнение.
 */
public class VectorSet {
	/**
	 * Узел двоичного дерева
	 */
    private static class Node {
        Node left;
        Node right;
        Vector info;

        Node(Vector v) { info = v; }
    }

    /**
     * Корень дерева.
     */
    private Node root;

    /**
     * Проверяет, лежит ли заданная точка "левее" заданного вектора
     * @param p
     * @param v
     * @return
     */
    private static boolean isLess(Point p, Vector v) {
        return Vectors.getDirection(new Vector(p, v.getMin()), new Vector(p, v.getMax())) == Direction.POSITIVE;
    }

    /**
     * Добавление вектора в заданное поддерево.
     * @param v
     * @param root
     * @return
     */
    private Node add(Vector v, Node root) {
        if (root == null) {
            return new Node(v);
        } else if (isLess(v.getMin(), root.info)) {
            root.left = add(v, root.left);
        } else {
            root.right = add(v, root.right);
        }
        return root;
    }

    /**
     * Удаление вектора из заданного поддерева.
     * @param v
     * @param root
     * @return
     */
    private Node remove(Vector v, Node root) {
        if (root == null) return null;
        if (root.info == v) {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                root.info = findMin(root.right);
                root.right = deleteMin(root.right);
            }
        } else if (isLess(v.getMin(), root.info)) {
            root.left = remove(v, root.left);
        } else {
            root.right = remove(v, root.right);
        }
        return root;
    }

    /**
     * Наименьший узел в заданном поддереве
     * @param root
     * @return
     */
    private Vector findMin(Node root) {
        while (root.left != null) root = root.left;
        return root.info;
    }

    /**
     * Наибольший узел в заданном поддереве.
     * @param root
     * @return
     */
    private Vector findMax(Node root) {
        while (root.right != null) root = root.right;
        return root.info;
    }

    /**
     * Удаление наименьшего узла в заданном поддереве.
     * @param root
     * @return
     */
    private Node deleteMin(Node root) {
        if (root.left == null) return root.right;
        root.left = deleteMin(root.left);
        return root;
    }

    /**
     * Нахождение соседних узлов для заданного вектора в заданном поддереве.
     * @param v
     * @param root
     * @return
     */
    private Pair<Vector, Vector> findSiblings(Vector v, Node root) {
        if (root.info == v) {
        	// Вектор лежит в корне.
            return new Pair<>(
                    root.left == null ? null : findMax(root.left),
                    root.right == null ? null : findMin(root.right));
        }
        
        if (isLess(v.getStart(), root.info)) {
        	// Вектор лежит в левом поддереве.
            Pair<Vector, Vector> siblings = findSiblings(v, root.left);
            if (siblings.second() == null) {
                return new Pair<>(siblings.first(), root.info);
            } else {
                return siblings;
            }
        } else {
        	// Вектор лежит в правом поддереве
            Pair<Vector, Vector> siblings = findSiblings(v, root.right);
            if (siblings.first() == null) {
                return new Pair<>(root.info, siblings.second());
            } else {
                return siblings;
            }
        }
    }

    /**
     * Обход дерева в левостороннем порядке (порядке возрастания элементов).
     * @param consumer	Операция, выполняемая с узлами дерева при обходе.
     */
    private void traverse(Consumer<Vector> consumer) {
        traverse(consumer, root);
    }

    /**
     * Обход поддерева в левостороннем порядке (порядке возрастания элементов).
     * @param consumer	Операция, выполняемая с узлами дерева при обходе.
     * @param root		Корень исследуемого поддерева
     */
    private void traverse(Consumer<Vector> consumer, Node root) {
        if (root != null) {
            traverse(consumer, root.left);
            consumer.accept(root.info);
            traverse(consumer, root.right);
        }
    }

    /**
     * Добавление нового вектора в дерево с вычислением его "соседей".
     * @param v
     * @return
     */
    public Pair<Vector, Vector> add(Vector v) {
        root = add(v, root);
        return findSiblings(v, root);
    }

    /**
     * Удаление вектора из дерева с вычислением его соседей.
     * @param v
     * @return
     */
    public Pair<Vector, Vector> remove(Vector v) {
        Pair<Vector, Vector> siblings = findSiblings(v, root);
        root = remove(v, root);
        return siblings;
    }

    /**
     * Множество векторов пусто?
     * @return
     */
    public boolean isEmpty() { return root == null; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        traverse(v -> builder.append(v.toString()).append(", "));
        if (builder.length() > 1) {
            builder.delete(builder.length() - 2, builder.length()).append("]");
        } else {
            builder.append("]");
        }
        return builder.toString();
    }
}
