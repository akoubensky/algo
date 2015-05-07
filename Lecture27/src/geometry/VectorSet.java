package geometry;

import java.util.function.Consumer;

/**
 * Created by akubensk on 05.05.2015.
 */
public class VectorSet {
    private static class Node {
        Node left;
        Node right;
        Vector info;

        Node(Vector v) { info = v; }
    }

    private Node root;

    private static boolean isLess(Point p, Vector v) {
        return Vectors.getDirection(new Vector(p, v.getStart()), new Vector(p, v.getFinish())) == Direction.POSITIVE;
    }

    private Node add(Vector v, Node root) {
        if (root == null) {
            return new Node(v);
        } else if (isLess(v.getStart(), root.info)) {
            root.left = add(v, root.left);
        } else {
            root.right = add(v, root.right);
        }
        return root;
    }

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
        } else if (isLess(v.getStart(), root.info)) {
            root.left = remove(v, root.left);
        } else {
            root.right = remove(v, root.right);
        }
        return root;
    }

    private Vector findMin(Node root) {
        while (root.left != null) root = root.left;
        return root.info;
    }

    private Vector findMax(Node root) {
        while (root.right != null) root = root.right;
        return root.info;
    }

    private Node deleteMin(Node root) {
        if (root.left == null) return root.right;
        root.left = deleteMin(root.left);
        return root;
    }

    private Pair<Vector, Vector> findSiblings(Vector v, Node root, boolean start) {
        if (root.info == v) {
            return new Pair<>(
                    root.left == null ? null : findMax(root.left),
                    root.right == null ? null : findMin(root.right));
        }
        if (isLess(start ? v.getStart() : v.getFinish(), root.info)) {
            Pair<Vector, Vector> siblings = findSiblings(v, root.left, start);
            if (siblings.getSecond() == null) {
                return new Pair<>(siblings.getFirst(), root.info);
            } else {
                return siblings;
            }
        } else {
            Pair<Vector, Vector> siblings = findSiblings(v, root.right, start);
            if (siblings.getFirst() == null) {
                return new Pair<>(root.info, siblings.getSecond());
            } else {
                return siblings;
            }
        }
    }

    private void traverse(Consumer<Vector> consumer) {
        traverse(consumer, root);
    }

    private void traverse(Consumer<Vector> consumer, Node root) {
        if (root != null) {
            traverse(consumer, root.left);
            consumer.accept(root.info);
            traverse(consumer, root.right);
        }
    }

    public Pair<Vector, Vector> add(Vector v) {
        root = add(v, root);
        return findSiblings(v, root, true);
    }

    public Pair<Vector, Vector> remove(Vector v) {
        Pair<Vector, Vector> siblings = findSiblings(v, root, false);
        root = remove(v, root);
        return siblings;
    }

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
