package geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Операции над векторами и другие сопутствующие операции.
 */
public class Vectors {
	/**
	 * Вычисление направления поворота от одного вектора к другому.
	 * Перед вычислениями вектора приводятся к одному началу (началу координат)
	 * @param v1	Вектор, который надо повернуть.
	 * @param v2	Вектор, к которому делается поворот.
	 * @return		Направление поворота
	 */
    public static Direction getDirection(Vector v1, Vector v2) {
        int x1 = v1.getFinish().x() - v1.getStart().x();
        int x2 = v2.getFinish().x() - v2.getStart().x();
        int y1 = v1.getFinish().y() - v1.getStart().y();
        int y2 = v2.getFinish().y() - v2.getStart().y();
        int determinant = x1 * y2 - x2 * y1;
        return determinant > 0 ? Direction.POSITIVE :
               determinant < 0 ? Direction.NEGATIVE :
               Math.signum(x1) == Math.signum(x2) && Math.signum(y1) == Math.signum(y2) ? Direction.COLLINEAR :
               Direction.ANTI_COLLINEAR;
    }

    /**
     * Вычисление направления поворота в точке ломаной при переходе через эту точку.
     * @param p1	Предыдущая точка.
     * @param p2	Точка поворота.
     * @param p3	Следующая точка.
     * @return		Направление поворота.
     */
    public static Direction getTurn(Point p1, Point p2, Point p3) {
        return getDirection(new Vector(p1, p2), new Vector(p1, p3));
    }

    /**
     * Проверка того, пересекаются ли "охватывающие прямоугольники" двух векторов.
     * @param v1	Первый вектор.
     * @param v2	Второй вектор.
     * @return		true, если прямоугольники имеют общие точки, false в противном случае.
     */
    public static boolean areRectanglesIntersect(Vector v1, Vector v2) {
        int x11 = v1.getStart().x();
        int x12 = v1.getFinish().x();
        int y11 = v1.getStart().y();
        int y12 = v1.getFinish().y();
        int x21 = v2.getStart().x();
        int x22 = v2.getFinish().x();
        int y21 = v2.getStart().y();
        int y22 = v2.getFinish().y();

        return !(Math.max(x11, x12) < Math.min(x21, x22) || Math.min(x11, x12) > Math.max(x21, x22) ||
                 Math.max(y11, y12) < Math.min(y21, y22) || Math.min(y11, y12) > Math.max(y21, y22));
    }

    /**
     * Проверка того, находятся ли две точки по одну сторону от прямой линии,
     * определенной направлением заданного вектора.
     * @param p1	Первая точка.
     * @param p2	Вторая точка.
     * @param v		Вектор, задающий прямую.
     * @return		true, если точки лежат строго по одну сторону от прямой.
     */
    public static boolean arePointsOnTheSameSide(Point p1, Point p2, Vector v) {
        Direction dir1 = getDirection(v, new Vector(v.getStart(), p1));
        Direction dir2 = getDirection(v, new Vector(v.getStart(), p2));
        if (dir1 == Direction.ANTI_COLLINEAR || dir1 == Direction.COLLINEAR ||
            dir2 == Direction.ANTI_COLLINEAR || dir2 == Direction.COLLINEAR) {
            return false;
        }
        return dir1 == dir2;
    }

    /**
     * Проверка того, пересекаются ли два отрезка, заданные векторами.
     * @param v1	Первый вектор.
     * @param v2	Второй вектор.
     * @return		true, если вектора имеют общие точки, false в противном случае.
     */
    public static boolean areVectorsIntersect(Vector v1, Vector v2) {
        if (!areRectanglesIntersect(v1, v2)) return false;
        return !arePointsOnTheSameSide(v1.getStart(), v1.getFinish(), v2) &&
               !arePointsOnTheSameSide(v2.getStart(), v2.getFinish(), v1);
    }

    /**
     * Реализация алгоритма проверки того, есть ли в заданном множестве отрезков
     * хотя бы два пересекающихся.
     * Алгоритм использует метод "движущейся прямой", параллельной оси абсцисс.
     * Исследуются "критические точки", являющиеся ординатами начал и концов отрезков 
     * из заданного множества. 
     * @param vectors	Множество отрезков, заданных векторами.
     * @return			Пара найденных пересекающихся отрезков, если таковые есть, 
     * 					в противном случае - null.
     */
    public static Pair<Vector, Vector> hasIntersections(Collection<Vector> vectors) {
    	// Упорядоченное множество критических точек, с каждой из которых связаны два
    	// множества векторов: множество векторов, имеющих начала в заданной критической точке,
    	// и множество векторов, имеющих конец в заданной критической точке.
        TreeMap<Integer, Pair<List<Vector>, List<Vector>>> pointsMap = new TreeMap<>();
        
        // Формируем множество критических точек и связываем их с векторами.
        // Каждый вектор попадает в множество два раза: с координатой начала и координатой конца.
        for (Vector v : vectors) {
        	int ys = v.getMin().y();
        	int yf = v.getMax().y();
        	Pair<List<Vector>, List<Vector>> lists = pointsMap.get(ys);
        	if (lists == null) pointsMap.put(ys, lists = new Pair<>(new ArrayList<>(), new ArrayList<>()));
        	lists.first().add(v);
        	lists = pointsMap.get(yf);
        	if (lists == null) pointsMap.put(yf, lists = new Pair<>(new ArrayList<>(), new ArrayList<>()));
        	lists.second().add(v);
        }

        // Множество векторов, пересекающих "движущуюся прямую" в текущей критической точке.
        VectorSet vectorsSet = new VectorSet();
        
        // Реализация движения прямой.
        for (int criticalPoint : pointsMap.keySet()) {
            Pair<List<Vector>, List<Vector>> pv = pointsMap.get(criticalPoint);
            
            // Добавляем те вектора, которые в данной критической точке имеют начала.
            for (Vector v : pv.first()) {
            	// При добавлении вектора вычисляем "соседние" отрезки и проверяем,
            	// не пересекается ли добавляемый вектор с одним из "соседей".
                Pair<Vector, Vector> siblings = vectorsSet.add(v);
                if (siblings.first() != null && areVectorsIntersect(v, siblings.first())) {
                    return new Pair<>(v, siblings.first());
                }
                if (siblings.second() != null && areVectorsIntersect(v, siblings.second())) {
                    return new Pair<>(v, siblings.second());
                }
            }
            
            // Удаляем те вектора, которые в данной критической точке имеют концы.
            for (Vector v : pv.second()) {
            	// При удалении вектора вычисляем "соседние" отрезки и проверяем,
            	// не пересекается ли друг с другом эти "соседи".
                Pair<Vector, Vector> siblings = vectorsSet.remove(v);
                if (siblings.first() != null && siblings.second() != null &&
                        areVectorsIntersect(siblings.first(), siblings.second())) {
                    return siblings;
                }
            }
        }
        
        // Пересечений не найдено.
        return null;
    }
}
