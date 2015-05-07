package geometry;

/**
 * Created by akubensk on 05.05.2015.
 */
public class Point implements Comparable<Point> {
    public static Point ORIGIN = new Point(0, 0);

    private int x;
    private int y;

    public Point() { this(0, 0); }

    public Point(int x, int y) { this.x = x; this.y = y; }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) ^ Integer.hashCode(y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Point)) return false;
        Point other = (Point)o;
        return x == other.x && y == other.y;
    }

    @Override
    public int compareTo(Point other) {
        if (y == other.y) return x - other.x;
        return y - other.y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public static Point min(Point p1, Point p2) {
        return p1.compareTo(p2) < 0 ? p1 : p2;
    }

    public static Point max(Point p1, Point p2) {
        return p1.compareTo(p2) > 0 ? p1 : p2;
    }
}
