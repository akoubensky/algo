package geometry;

public record Point(int x, int y) implements Comparable<Point> {
    public static Point ORIGIN = new Point(0, 0);

    public static Point min(Point p1, Point p2) {
        return p1.compareTo(p2) < 0 ? p1 : p2;
    }

    public static Point max(Point p1, Point p2) {
        return p1.compareTo(p2) > 0 ? p1 : p2;
    }

    @Override
    public int compareTo(Point o) {
        return y == o.y ? x - o.x : y - o.y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
