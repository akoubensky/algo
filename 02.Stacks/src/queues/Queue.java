package queues;

public interface Queue<T> {
    class OverflowException extends RuntimeException {
        public OverflowException() { super("Queue overflow"); }
        public OverflowException(String message) { super(message); }
    }

    class UnderflowException extends RuntimeException {
        public UnderflowException() { super("Queue underflow"); }
        public UnderflowException(String message) { super(message); }
    }

    void enqueue(T e);
    T dequeue();
    T pick();
    boolean isEmpty();
}
