package queues;

public class CircleQueue<T> implements Queue<T> {
    private static class Node<T> {
        T info;
        Node<T> next;
        Node(T info, Node<T> next) {
            this.info = info;
            this.next = next;
        }
        Node(T info) { this(info, null); }
    }

    Node<T> last = null;

    @Override
    public void enqueue(T e) {
        Node<T> newNode = new Node<>(e);
        newNode.next = last == null ? newNode : last.next;
        if (last != null) last.next = newNode;
        last = newNode;
    }

    @Override
    public T dequeue() {
        if (last == null) throw new UnderflowException();
        Node<T> ret = last.next;
        if (ret == last) {
            last = null;
        } else {
            last.next = ret.next;
        }
        ret.next = null;
        return ret.info;
    }

    @Override
    public T pick() {
        if (last == null) throw new UnderflowException();
        return last.next.info;
    }

    @Override
    public boolean isEmpty() {
        return last == null;
    }
}
