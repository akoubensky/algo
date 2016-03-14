package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class StacksSystem<T> {
    private final short NIL = -1;

    T[] info;
    short[] next;

    private class InternalStack implements Stack<T> {
        short top = NIL;

        @Override
        public void push(T elem) {
            short index = getElement();
            info[index] = elem;
            next[index] = top;
            top = index;
        }

        @Override
        public T pop() {
            T result = info[top];
            short free = top;
            top = next[top];
            freeElement(free);
            return result;
        }

        @Override
        public boolean isEmpty() {
            return top == NIL;
        }

        @Override
        public T top() {
            return info[top];
        }
    }

    short fill = 0;
    short free = NIL;

    public StacksSystem(short size) {
        info = (T[])new Object[size];
        next = new short[size];
    }

    public Stack<T> getStack() { return new InternalStack(); }

    private short getElement() {
        if (free != NIL) {
            short result = free;
            free = next[free];
            return result;
        } else {
            return fill++;
        }
    }

    private void freeElement(short index) {
        info[index] = null;
        next[index] = free;
        free = index;
    }

    public static void main(String[] args) {
        StacksSystem<String> system = new StacksSystem<>((short)25);
        Stack<String> stack1 = system.getStack();
        Stack<String> stack2 = system.getStack();
        stack1.push("one-1");
        stack1.push("two-1");
        stack2.push("one-2");
        stack2.push("two-2");
        System.out.println(stack1.top());
        System.out.println(stack1.pop());
        System.out.println(stack2.top());
        System.out.println(stack2.pop());
        stack1.push("three-1");
        stack1.push("four-1");
        stack2.push("three-2");
        stack2.push("four-2");
        stack1.push("five-1");
        stack1.push("six-1");
        stack2.push("five-2");
        stack2.push("six-2");
        System.out.println(stack1.top());
        System.out.println(stack2.top());
        stack1.push("seven-1");
        stack2.push("seven-2");
    }
}
