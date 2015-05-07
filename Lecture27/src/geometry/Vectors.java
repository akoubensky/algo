package geometry;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by akubensk on 05.05.2015.
 */
public class Vectors {
    public static Direction getDirection(Vector v1, Vector v2) {
        int x1 = v1.getFinish().getX() - v1.getStart().getX();
        int x2 = v2.getFinish().getX() - v2.getStart().getX();
        int y1 = v1.getFinish().getY() - v1.getStart().getY();
        int y2 = v2.getFinish().getY() - v2.getStart().getY();
        int determinant = x1 * y2 - x2 * y1;
        return determinant > 0 ? Direction.POSITIVE :
               determinant < 0 ? Direction.NEGATIVE :
               Math.signum(x1) == Math.signum(x2) && Math.signum(y1) == Math.signum(y2) ? Direction.COLLINEAR :
               Direction.ANTI_COLLINEAR;
    }

    public static Direction getTurn(Point p1, Point p2, Point p3) {
        return getDirection(new Vector(p1, p2), new Vector(p1, p3));
    }

    public static boolean areRectanglesIntersect(Vector v1, Vector v2) {
        int x11 = v1.getStart().getX();
        int x12 = v1.getFinish().getX();
        int y11 = v1.getStart().getY();
        int y12 = v1.getFinish().getY();
        int x21 = v2.getStart().getX();
        int x22 = v2.getFinish().getX();
        int y21 = v2.getStart().getY();
        int y22 = v2.getFinish().getY();

        return !(Math.max(x11, x12) < Math.min(x21, x22) || Math.min(x11, x12) > Math.max(x21, x22) ||
                 Math.max(y11, y12) < Math.min(y21, y22) || Math.min(y11, y12) > Math.max(y21, y22));
    }

    public static boolean arePointsOnTheSameSide(Point p1, Point p2, Vector v) {
        Direction dir1 = getDirection(v, new Vector(v.getStart(), p1));
        Direction dir2 = getDirection(v, new Vector(v.getStart(), p2));
        if (dir1 == Direction.ANTI_COLLINEAR || dir1 == Direction.COLLINEAR ||
            dir2 == Direction.ANTI_COLLINEAR || dir2 == Direction.COLLINEAR) {
            return false;
        }
        return dir1 == dir2;
    }

    public static boolean areVectorsIntersect(Vector v1, Vector v2) {
        if (!areRectanglesIntersect(v1, v2)) return false;
        return !arePointsOnTheSameSide(v1.getStart(), v1.getFinish(), v2) &&
               !arePointsOnTheSameSide(v2.getStart(), v2.getFinish(), v1);
    }

    public static boolean hasIntersections(Collection<Vector> vectors) {
        TreeMap<Point, Pair<Vector, Boolean>> pointsMap = new TreeMap<>();
        VectorSet vectorsSet = new VectorSet();
        for (Vector v : vectors) {
            pointsMap.put(v.getStart(), new Pair<>(v, true));
            pointsMap.put(v.getFinish(), new Pair<>(v, false));
        }
        if (pointsMap.size() != 2*vectors.size()) return true;
        for (Point criticalPoint : pointsMap.keySet()) {
            Pair<Vector, Boolean> pv = pointsMap.get(criticalPoint);
            Vector v = pv.getFirst();
            boolean isStart = pv.getSecond();

            if (isStart) {
                Pair<Vector, Vector> siblings = vectorsSet.add(v);
                if (siblings.getFirst() != null && areVectorsIntersect(v, siblings.getFirst())) {
                    return true;
                }
                if (siblings.getSecond() != null && areVectorsIntersect(v, siblings.getSecond())) {
                    return true;
                }
            } else {
                Pair<Vector, Vector> siblings = vectorsSet.remove(v);
                if (siblings.getFirst() != null && siblings.getSecond() != null &&
                        areVectorsIntersect(siblings.getFirst(), siblings.getSecond())) {
                    return true;
                }
            }
        }
        return false;
    }
}
