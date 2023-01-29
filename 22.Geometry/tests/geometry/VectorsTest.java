package geometry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверка некоторых операций вычислительной геометрии с векторами.
 */
public class VectorsTest {
    private List<Vector> vectorsSet1 = Arrays.asList(
            new Vector("1", new Point(1,4), new Point(4,7)),
            new Vector("2", new Point(11,5), new Point(5,6)),
            new Vector("3", new Point(1,1), new Point(5,7)),
            new Vector("4", new Point(3,5), new Point(6,5)),
            new Vector("5", new Point(7,5), new Point(5,4)),
            new Vector("6", new Point(5,3), new Point(8,5)),
            new Vector("7", new Point(4,2), new Point(4,4)),
            new Vector("8", new Point(7,3), new Point(3,1)),
            new Vector("9", new Point(10,1), new Point(8,2))
    );

    private List<Vector> vectorsSet2 = Arrays.asList(
            new Vector("1", new Point(7,4), new Point(3,10)),
            new Vector("2", new Point(7,3), new Point(4,8)),
            new Vector("3", new Point(5,4), new Point(6,0)),
            new Vector("4", new Point(3,2), new Point(5,5)),
            new Vector("5", new Point(2,1), new Point(4,7)),
            new Vector("6", new Point(3,6), new Point(2,9))
    );

    @Test
    public void getDirection() {
        Vector v1 = new Vector(new Point(4, 1));
        Vector v2 = new Vector(new Point(2, 3));
        Vector v3 = new Vector(new Point(8, 2));
        Vector v4 = new Vector(new Point(-2, -3));
        assertEquals(Direction.POSITIVE, Vectors.getDirection(v1, v2), "Wrong direction");
        assertEquals(Direction.NEGATIVE, Vectors.getDirection(v2, v1), "Wrong direction");
        assertEquals(Direction.COLLINEAR, Vectors.getDirection(v1, v3), "Wrong direction");
        assertEquals(Direction.ANTI_COLLINEAR, Vectors.getDirection(v2, v4), "Wrong direction");

        Vector v5 = new Vector(new Point(0, 1));
        Vector v6 = new Vector(new Point(0, 3));
        Vector v7 = new Vector(new Point(0, -2));
        assertEquals(Direction.POSITIVE, Vectors.getDirection(v2, v5), "Wrong direction");
        assertEquals(Direction.COLLINEAR, Vectors.getDirection(v5, v6), "Wrong direction");
        assertEquals(Direction.ANTI_COLLINEAR, Vectors.getDirection(v5, v7), "Wrong direction");
        assertEquals(Direction.NEGATIVE, Vectors.getDirection(v1, v7), "Wrong direction");
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
            assertEquals(Direction.POSITIVE, Vectors.getTurn(points[p], points[i], points [s]), "Wrong turn");
        }
    }

    @Test
    public void areRectanglesIntersect() {
        Vector v11 = new Vector(new Point(5, 3));
        Vector v12 = new Vector(new Point(1, 1), new Point(4, 2));
        assertTrue(Vectors.areRectanglesIntersect(v11, v12), "Intersection");
        Vector v21 = new Vector(new Point(5, -3));
        Vector v22 = new Vector(new Point(5, -4), new Point(7, -2));
        assertTrue(Vectors.areRectanglesIntersect(v21, v22), "Intersection");
        Vector v31 = new Vector(new Point(1, 3));
        Vector v32 = new Vector(new Point(-1, 4), new Point(2, 2));
        assertTrue(Vectors.areRectanglesIntersect(v31, v32), "Intersection");
        Vector v41 = new Vector(new Point(2, -3));
        Vector v42 = new Vector(new Point(-1, -1), new Point(-4, -2));
        assertFalse(Vectors.areRectanglesIntersect(v41, v42), "Intersection");
        Vector v51 = new Vector(new Point(1, 1));
        Vector v52 = new Vector(new Point(2, -1), new Point(3, -2));
        assertFalse(Vectors.areRectanglesIntersect(v51, v52), "Intersection");
        Vector v61 = new Vector(new Point(3, 1));
        Vector v62 = new Vector(new Point(1, -1), new Point(2, 2));
        assertTrue(Vectors.areRectanglesIntersect(v61, v62), "Intersection");
    }

    @Test
    public void areVectorsIntersect() {
        Vector v11 = new Vector(new Point(6, 3));
        Vector v12 = new Vector(new Point(3, 2), new Point(6, 4));
        assertFalse(Vectors.areVectorsIntersect(v11, v12), "Vectors intersection");
        Vector v22 = new Vector(new Point(6, 4));
        assertTrue(Vectors.areVectorsIntersect(v11, v22), "Vectors intersection");
        Vector v31 = new Vector(new Point(3, -2));
        Vector v32 = new Vector(new Point(1, -3), new Point(1, -1));
        assertFalse(Vectors.areVectorsIntersect(v31, v32), "Vectors intersection");
        Vector v41 = new Vector(new Point(0, -1), new Point(3, -2));
        assertTrue(Vectors.areVectorsIntersect(v41, v32), "Vectors intersection");
        Vector v51 = new Vector(new Point(2, 2));
        Vector v52 = new Vector(new Point(1, 1), new Point(2, 0));
        assertTrue(Vectors.areVectorsIntersect(v51, v52), "Vectors intersection");
        Vector v61 = new Vector(new Point(7, 4), new Point(3, 10));
        Vector v62 = new Vector(new Point(7, 3), new Point(4, 8));
        assertFalse(Vectors.areVectorsIntersect(v61, v62), "Vectors intersection");
    }

    @Test
    public void hasIntersections() {
    	Pair<Vector, Vector> pair = Vectors.hasIntersections(vectorsSet1);
        assertNotNull(pair, "has intersection");
        assertTrue((pair.first().getName().equals("3") && pair.second().getName().equals("4")) ||
        		   (pair.first().getName().equals("4") && pair.second().getName().equals("3")),
                   "wrong vectors intersection");
        pair = Vectors.hasIntersections(vectorsSet2);
        assertNull(pair, "has intersection");
    }
}