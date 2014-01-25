package dag;

import java.util.Iterator;

/**
 * Реализация простого алгоритма топологической сортировки вершин графа
 */
public class Topsort {
	@SuppressWarnings("serial")
	public static class NotADAGException extends Exception {
		public NotADAGException() {
			super("Not a DAG");
		}
	}
	
	Graph graph;
	int nVert;
	
	int[] marks;
	boolean[] passed;
	
	int curMark;
	boolean sorted = false;
	
	public Topsort(Graph g) {
		graph = g;
		nVert = g.getCount();
		marks = new int[nVert];
		passed = new boolean[nVert];
	}
	
	public int[] getLabels() throws NotADAGException {
		if (!sorted) {
			topsort();
			sorted = true;
		}
		return marks;
	}
	
	private void topsort() throws NotADAGException {
		curMark = nVert;
		for (int i = 0; i < nVert; ++i) {
			passed[i] = false;
			marks[i] = -1;
		}
		for (int start = 0; start < nVert; ++start) {
			if (!passed[start]) {
				traverseComp(start);
			}
		}
	}
	
	private void traverseComp(int s) throws NotADAGException {
		passed[s] = true;
		for (Iterator<Graph.Arc> iArc = graph.arcs(s); iArc.hasNext(); ) {
			Graph.Arc arc = iArc.next();
			int to = arc.to();
			if (!passed[to]) {
				if (marks[to] != -1) {
					throw new NotADAGException();
				}
				traverseComp(to);
			}
		}
		marks[s] = --curMark;
	}
}
