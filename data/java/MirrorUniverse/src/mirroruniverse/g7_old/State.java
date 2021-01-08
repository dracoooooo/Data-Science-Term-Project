package mirroruniverse.g7_old;

public class State {

	private static boolean[][] leftMap;
	private static boolean[][] rightMap;

	private static int leftRows;
	private static int leftCols;

	private static int rightRows;
	private static int rightCols;

	private static State end;

	private int i1, j1, i2, j2;

	public static State init(int[][] left, int[][] right) {
		/* Keep starting state */
		State start = new State();
		State end = new State();
		/* Set left map */
		leftRows = left.length;
		leftCols = left[0].length;
		leftMap = new boolean [leftRows][leftCols];
		for (int i = 0 ; i != leftRows ; ++i)
			for (int j = 0 ; j != leftCols ; ++j)
				if (left[i][j] == 1)
					leftMap[i][j] = false;
				else {
					leftMap[i][j] = true;
					if (left[i][j] == 2) {
						end.i1 = i;
						end.j1 = j;
					} else if (left[i][j] == 3) {
						start.i1 = i;
						start.j1 = j;
					}
				}
		/* Set right map */
		rightRows = right.length;
		rightCols = right[0].length;
		rightMap = new boolean [rightRows][rightCols];
		for (int i = 0 ; i != rightRows ; ++i)
			for (int j = 0 ; j != rightCols ; ++j)
				if (right[i][j] == 1)
					rightMap[i][j] = false;
				else {
					rightMap[i][j] = true;
					if (right[i][j] == 2) {
						end.i2 = i;
						end.j2 = j;
					} else if (right[i][j] == 3) {
						start.i2 = i;
						start.j2 = j;
					}
				}
		/* Store the goal and return the start */
		State.end = end;
		return start;
	}

	public static State end() {
		return new State(end);
	}

	public State() {
		this(-1, -1, -1, -1);
	}

	public State(int i1, int j1, int i2, int j2) {
		this.i1 = i1;
		this.j1 = j1;
		this.i2 = i2;
		this.j2 = j2;
	}

	public State(State par) {
		this.i1 = par.i1;
		this.j1 = par.j1;
		this.i2 = par.i2;
		this.j2 = par.j2;
	}

	public boolean isGoal() {
		return equals(end);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof State))
			return false;
		State par = (State) obj;
		return i1 == par.i1 && j1 == par.j1 && i2 == par.i2 && j2 == par.j2;
	}

	public int hashCode() {
		return Math.abs(i1 * 123456789 + j1 * 987654321 + i2 * 135792468 + j2 * 975318642);
	}

	private static final int[][] dirToCode = { { 0, 0 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 },  { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };

	private static boolean isValidLeft(int i, int j) {
		if (i < 0 || i >= leftRows || j < 0 || j >= leftCols)
			return false;
		return leftMap[i][j];
	}

	private static boolean isValidRight(int i, int j) {
		if (i < 0 || i >= rightRows || j < 0 || j >= rightCols)
			return false;
		return rightMap[i][j];
	}

	public int getNext(State[] next) {
		int c = 0;
		for (int d = 1 ; d != 9 ; ++d) {
			int di = dirToCode[d][0];
			int dj = dirToCode[d][1];
			int g = 0;
			boolean m = false;
			if (isValidLeft(i1 + di, j1 + dj)) {
				next[c].i1 = i1 + di;
				next[c].j1 = j1 + dj;
				if (next[c].i1 == end.i1 && next[c].j1 == end.j1) g++;
				m = true;
			} else {
				next[c].i1 = i1;
				next[c].j1 = j1;
			}
			if (isValidRight(i2 + di, j2 + dj)) {
				next[c].i2 = i2 + di;
				next[c].j2 = j2 + dj;
				if (next[c].i2 == end.i2 && next[c].j2 == end.j2) g++;
				m = true;
			} else {
				next[c].i2 = i2;
				next[c].j2 = j2;
			}
			if (m && (g == 0 || g == 2)) c++;
		}
		return c;
	}

	private static final int[][] codeToDir = { { 4, 3, 2 }, { 5, 0, 1 }, { 6, 7, 8 } };

	public static int direction(State from, State to) {
		int di = to.i1 - from.i1;
		int dj = to.j1 - from.j1;
		if (di != 0 || dj != 0)
			return codeToDir[di+1][dj+1];
		di = to.i2 - from.i2;
		dj = to.j2 - from.j2;
		return codeToDir[di+1][dj+1];
	}

	public String toString() {
		return "[" + i1 + "," + j1 + "][" + i2 + "," + j2 + "]";
	}
}
