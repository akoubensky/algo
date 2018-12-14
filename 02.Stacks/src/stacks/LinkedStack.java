package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class LinkedStack<T> implements Stack<T> {
    private static class Node<T> {
        T info;
        Node<T> next;
        Node(T info, Node<T> next) {
            this.info = info;
            this.next = next;
        }
        Node (T info) { this(info, null); }
    }

    Node<T> top = null;

    @Override
    public void push(T elem) {
        Node<T> newElem = new Node<>(elem, top);
        top = newElem;
    }

    @Override
    public T pop() {
        if (top == null) throw new StackUnderflow();
        T elem = top.info;
        top = top.next;
        return elem;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public T top() {
        if (top == null) throw new StackUnderflow();
        return top.info;
    }

    public static void main(String[] args) {
        Stack<String> stack = new LinkedStack<>();
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
