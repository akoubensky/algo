package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public interface Stack<T> {
    void push(T elem);
    T pop();
    boolean isEmpty();
    T top();
}
