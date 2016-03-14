package stacks;

/**
 * Created by Home on 14.03.2016.
 */
public class StackUnderflow extends RuntimeException {
    public StackUnderflow() { super(); }
    public StackUnderflow(String message) { super(message); }
}
