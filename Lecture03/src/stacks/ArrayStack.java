package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class ArrayStack<T> implements Stack<T> {
    private final Object[] stack;
    private int top = 0;

    public ArrayStack(int size) {
        stack = new Object[size];
    }

    @Override
    public void push(T elem) {
        if (top == stack.length) throw new StackOverflow("Stack overflow: " + top);
        stack[top++] = elem;
    }

    @Override
    public T pop() {
        if (top == 0) throw new StackUnderflow();
        T elem = (T)stack[--top];
        stack[top] = null;
        return elem;
    }

    @Override
    public boolean isEmpty() {
        return top == 0;
    }

    @Override
    public T top() {
        if (top == 0) throw new StackUnderflow();
        return (T)stack[top-1];
    }

    public static void main(String[] args) {
        Stack<String> stack = new ArrayStack<>(5);
        stack.push("one");
        stack.push("two");
        System.out.println(stack.top());
        System.out.println(stack.pop());
        stack.push("three");
        stack.push("four");
        stack.push("five");
        stack.push("six");
        System.out.println(stack.top());
        stack.push("seven");
    }
}
