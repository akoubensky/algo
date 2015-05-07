package geometry;

/**
 * Created by akubensk on 05.05.2015.
 */
public class Vector {
    private Point start;
    private Point finish;
    private String name = null;

    public Vector(Point start, Point finish) {
        this.start = start;
        this.finish = finish;
    }

    public Vector(Point end) { this(Point.ORIGIN, end); }

    public Vector(String name, Point end) {
        this(end);
        this.name = name;
    }

    public Vector(String name, Point start, Point finish) {
        this(start, finish);
        this.name = name;
    }

    public Point getStart() { return start; }

    public Point getFinish() { return finish; }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Vector)) return false;
        return start.equals(((Vector)o).start) && finish.equals(((Vector)o).finish);
    }

    @Override
    public int hashCode() {
        return start.hashCode() ^ finish.hashCode();
    }

    @Override
    public String toString() {
        return name == null ? start.toString() + " - " + finish.toString() : name;
    }
}
