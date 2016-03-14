package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class DoubleStack<T> {
    Object[] stacks;
    int top, bottom;

    public DoubleStack(int size) {
        stacks = new Object[size];
        top = 0;
        bottom = size;
    }

    private final Stack<T> upStack = new Stack<T>() {

        @Override
        public void push(T elem) {

        }

        @Override
        public T pop() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public T top() {
            return null;
        }
    };
    private final Stack<T> downStack = new Stack<T>() {
        @Override
        public void push(T elem) {

        }

        @Override
        public T pop() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public T top() {
            return null;
        }
    };

    public Stack<T> upStack() { return upStack; }
    public Stack<T> downStack() { return downStack; }

}
