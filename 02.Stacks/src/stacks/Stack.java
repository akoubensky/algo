package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public interface Stack<T> {
    class StackOverflow extends RuntimeException {
        public StackOverflow() { super("Stack overflow"); }
        public StackOverflow(String message) { super(message); }
    }

    class StackUnderflow extends RuntimeException {
        public StackUnderflow() { super("Stack underflow"); }
        public StackUnderflow(String message) { super(message); }
    }

    void push(T elem);
    T pop();
    boolean isEmpty();
    T top();
}
