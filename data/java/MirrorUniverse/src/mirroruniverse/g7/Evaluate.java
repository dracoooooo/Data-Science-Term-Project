package mirroruniverse.g7;

import java.util.HashSet;

public class Evaluate {

	private Explorer exp;

	private int[][] leftDistMap;
	private int[][] rightDistMap;

	private boolean leftOff;
	private boolean rightOff;

	public Evaluate(Explorer exp) {
		this.exp = exp;
		leftDistMap = new int [exp.mapRows()][exp.mapCols()];
		rightDistMap = new int [exp.mapRows()][exp.mapCols()];
	}

	public static void distMap(byte[][] map, byte[][] cat, int[][] level) {
		int rows = map.length;
		int cols = map[0].length;
		/* Sets that will swap in second phase */
		HashSet <Point> set1 = new HashSet <Point> ();
		HashSet <Point> set2 = new HashSet <Point> ();
		/* Initialize structures */
		for (int i = 0 ; i != rows ; ++i)
			for (int j = 0 ; j != cols ; ++j)
				if (cat[i][j] != Explorer.unknown_but_reachable)
					level[i][j] = Integer.MAX_VALUE;
				else {
					level[i][j] = 0;
					set1.add(new Point(i, j));
				}
		/* Compute the distances */
		int cur_level = 1;
		while (!set1.isEmpty()) {
			do {
				Point p = set1.iterator().next();
				set1.remove(p);
				for (Point n : p.neighbors())
					if (n.i >= 0 && n.i < rows && n.j >= 0 && n.j < cols &&
					    cat[n.i][n.j] == Explorer.known_and_reachable &&
					    cur_level < level[n.i][n.j]) {
						level[n.i][n.j] = cur_level;
						set2.add(new Point(n));
					}
			} while (!set1.isEmpty());
			HashSet <Point> temp;
			temp = set1;
			set1 = set2;
			set2 = temp;
			cur_level++;
		}
	}

	public void update() {
		exp.leftDistMap(leftDistMap);
		exp.rightDistMap(rightDistMap);
		leftOff = exp.leftExitReachable() && !exp.rightExitReachable();
		rightOff = exp.rightExitReachable() && !exp.leftExitReachable();
	}

	private int leftDist(State state) {
		return leftDistMap[state.i1()][state.j1()];
	}

	private int rightDist(State state) {
		return rightDistMap[state.i2()][state.j2()];
	}

	public boolean goal(State state) {
		return leftDist(state) == 1 || rightDist(state) == 1;
	}

	public int cost(State state, int depth) {
		int left = leftOff ? 0 : leftDist(state);
		int right = rightOff ? 0 : rightDist(state);
		return left + right + depth;
	}

	public int bestPossibleCost(int depth) {
		int left = leftOff ? 0 : 1;
		int right = rightOff ? 0 : 1;
		return left + right + depth;
	}
}
