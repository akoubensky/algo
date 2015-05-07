package geometry;

/**
 * Created by akubensk on 05.05.2015.
 */
public class Pair<V1, V2> {
    private V1 first;
    private V2 second;

    public Pair(V1 first, V2 second) {
        this.first = first;
        this.second = second;
    }

    public V1 getFirst() { return first; }
    public V2 getSecond() { return second; }
}
