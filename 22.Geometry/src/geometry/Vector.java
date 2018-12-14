package geometry;

/**
 * Вектор - пара точек. Вектор - неизменяемое значение, поэтому побочные эффекты
 * при работе с векторами исключены.
 */
public class Vector {
    private Point start;		// Начальная точка
    private Point finish;		// Конечная точка
    private String name = null;	// Идентификатор вектора (не обязательное значение)

    public Vector(Point start, Point finish) {
        this.start = start;
        this.finish = finish;
    }

    /**
     * Задает вектор с началом в начале координат.
     * @param end
     */
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
    
    /**
     * Конец вектора с меньшими координатами
     * @return
     */
    public Point getMin() { return Point.min(start,  finish); }
    
    /**
     * Конец вектора с бОльшими координатами.
     * @return
     */
    public Point getMax() { return Point.max(start, finish); }
    
    /**
     * Идентификатор вектора.
     * @return
     */
    public String getName() { return name; } 

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Vector)) return false;
        return start.equals(((Vector)o).start) && finish.equals(((Vector)o).finish);
    }

    @Override
    public int hashCode() {
        return start.hashCode() ^ finish.hashCode();
    }

    /**
     * При отсутствии идентификатора выдает координаты концов вектора
     */
    @Override
    public String toString() {
        return name == null ? start.toString() + " - " + finish.toString() : name;
    }
}
