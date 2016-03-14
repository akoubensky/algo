package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class StackOverflow extends RuntimeException {
    public StackOverflow() { super(); }
    public StackOverflow(String message) { super(message); }
}
