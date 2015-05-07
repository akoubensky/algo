package geometry;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class VectorsTest {
    private List<Vector> vectors = Arrays.asList(
            new Vector("1", new Point(1,4), new Point(4,7)),
            new Vector("2", new Point(11,5), new Point(5,6)),
            new Vector("3", new Point(1,1), new Point(5,7)),
            new Vector("4", new Point(3,5), new Point(6,5)),
            new Vector("5", new Point(5,4), new Point(7,5)),
            new Vector("6", new Point(5,3), new Point(8,5)),
            new Vector("7", new Point(4,2), new Point(4,4)),
            new Vector("8", new Point(3,1), new Point(7,3)),
            new Vector("9", new Point(10,1), new Point(8,2))
    );

    @Test
    public void getDirection() {
        Vector v1 = new Vector(new Point(4, 1));
        Vector v2 = new Vector(new Point(2, 3));
        Vector v3 = new Vector(new Point(8, 2));
        Vector v4 = new Vector(new Point(-2, -3));
        assertEquals("Wrong direction", Direction.POSITIVE, Vectors.getDirection(v1, v2));
        assertEquals("Wrong direction", Direction.NEGATIVE, Vectors.getDirection(v2, v1));
        assertEquals("Wrong direction", Direction.COLLINEAR, Vectors.getDirection(v1, v3));
        assertEquals("Wrong direction", Direction.ANTI_COLLINEAR, Vectors.getDirection(v2, v4));

        Vector v5 = new Vector(new Point(0, 1));
        Vector v6 = new Vector(new Point(0, 3));
        Vector v7 = new Vector(new Point(0, -2));
        assertEquals("Wrong direction", Direction.POSITIVE, Vectors.getDirection(v2, v5));
        assertEquals("Wrong direction", Direction.COLLINEAR, Vectors.getDirection(v5, v6));
        assertEquals("Wrong direction", Direction.ANTI_COLLINEAR, Vectors.getDirection(v5, v7));
        assertEquals("Wrong direction", Direction.NEGATIVE, Vectors.getDirection(v1, v7));
    }

    @Test
    public void getTurn() {
        Point[] points = new Point[]
                { new Point(-2, 1), new Point(-3, -1), new Point(-2, -2),
                  new Point(1, -1), new Point(4, 1), new Point(1, 2)
                };
        for (int i = 0; i < points.length; i++) {
            int p = i==0 ? points.length - 1 : i-1;
            int s = i==points.length-1 ? 0 : i+1;
            assertEquals("Wrong turn", Direction.POSITIVE, Vectors.getTurn(points[p], points[i], points [s]));
        }
    }

    @Test
    public void areRectanglesIntersect() {
        Vector v11 = new Vector(new Point(5, 3));
        Vector v12 = new Vector(new Point(1, 1), new Point(4, 2));
        assertTrue("Intersection", Vectors.areRectanglesIntersect(v11, v12));
        Vector v21 = new Vector(new Point(5, -3));
        Vector v22 = new Vector(new Point(5, -4), new Point(7, -2));
        assertTrue("Intersection", Vectors.areRectanglesIntersect(v21, v22));
        Vector v31 = new Vector(new Point(1, 3));
        Vector v32 = new Vector(new Point(-1, 4), new Point(2, 2));
        assertTrue("Intersection", Vectors.areRectanglesIntersect(v31, v32));
        Vector v41 = new Vector(new Point(2, -3));
        Vector v42 = new Vector(new Point(-1, -1), new Point(-4, -2));
        assertFalse("Intersection", Vectors.areRectanglesIntersect(v41, v42));
        Vector v51 = new Vector(new Point(1, 1));
        Vector v52 = new Vector(new Point(2, -1), new Point(3, -2));
        assertFalse("Intersection", Vectors.areRectanglesIntersect(v51, v52));
        Vector v61 = new Vector(new Point(3, 1));
        Vector v62 = new Vector(new Point(1, -1), new Point(2, 2));
        assertTrue("Intersection", Vectors.areRectanglesIntersect(v61, v62));
    }

    @Test
    public void areVectorsIntersect() {
        Vector v11 = new Vector(new Point(6, 3));
        Vector v12 = new Vector(new Point(3, 2), new Point(6, 4));
        assertFalse("Vectors intersection", Vectors.areVectorsIntersect(v11, v12));
        Vector v22 = new Vector(new Point(6, 4));
        assertTrue("Vectors intersection", Vectors.areVectorsIntersect(v11, v22));
        Vector v31 = new Vector(new Point(3, -2));
        Vector v32 = new Vector(new Point(1, -3), new Point(1, -1));
        assertFalse("Vectors intersection", Vectors.areVectorsIntersect(v31, v32));
        Vector v41 = new Vector(new Point(0, -1), new Point(3, -2));
        assertTrue("Vectors intersection", Vectors.areVectorsIntersect(v41, v32));
        Vector v51 = new Vector(new Point(2, 2));
        Vector v52 = new Vector(new Point(1, 1), new Point(2, 0));
        assertTrue("Vectors intersection", Vectors.areVectorsIntersect(v51, v52));
    }

    @Test
    public void hasIntersections() {
        assertTrue("has intersection", Vectors.hasIntersections(vectors));
    }
}