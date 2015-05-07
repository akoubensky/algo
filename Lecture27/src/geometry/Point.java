package geometry;

/**
 * Точка с целочисленными координатами
 */
public class Point implements Comparable<Point> {
	/**
	 * Начало координат
	 */
    public static Point ORIGIN = new Point(0, 0);

    private int x;	// абсцисса
    private int y;	// ордината

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

    /**
     * Сравнение двух точек происходит в "обратном лексикографическом порядке".
     * Меньшей считается точка с меньшей ординатой, а при равенстве ординат - с меньшей абсциссой.
     */
    @Override
    public int compareTo(Point other) {
        if (y == other.y) return x - other.x;
        return y - other.y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Выбор точки с "меньшими" координатами.
     * @param p1
     * @param p2
     * @return
     */
    public static Point min(Point p1, Point p2) {
        return p1.compareTo(p2) < 0 ? p1 : p2;
    }

    /**
     * Выбор точки с "бОльшими" координатами
     * @param p1
     * @param p2
     * @return
     */
    public static Point max(Point p1, Point p2) {
        return p1.compareTo(p2) > 0 ? p1 : p2;
    }
}
