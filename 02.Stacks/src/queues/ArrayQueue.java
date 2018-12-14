package queues;

public class ArrayQueue<T> implements Queue<T> {
    private final Object[] queue;
    private int head;
    private int count;

    public ArrayQueue(int maxSize) {
        queue = new Object[maxSize];
        head = 0;
        count = 0;
    }
    @Override
    public void enqueue(T e) {
        if (count == queue.length) throw new OverflowException();
        queue[(head + count) % queue.length] = e;
        count++;
    }

    @Override
    public T dequeue() {
        if (count == 0) throw new UnderflowException();
        count--;
        return (T)queue[(head++) % queue.length];
    }

    @Override
    public T pick() {
        if (count == 0) throw new UnderflowException();
        return (T)queue[head];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }
}
