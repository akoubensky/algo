package queues;

public class LinkedQueue<T> implements Queue<T> {
    private static class Node<T> {
        T info;
        Node<T> next;
        Node(T info, Node<T> next) {
            this.info = info;
            this.next = next;
        }
        Node (T info) { this(info, null); }
    }

    Node<T> first = null;
    Node<T> last = null;

    @Override
    public void enqueue(T e) {
        Node<T> newNode = new Node<>(e);
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }

    @Override
    public T dequeue() {
        if (first == null) throw new UnderflowException();
        Node<T> ret = first;
        first = first.next;
        ret.next = null;
        return ret.info;
    }

    @Override
    public T pick() {
        if (first == null) throw new UnderflowException();
        return first.info;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }
}
