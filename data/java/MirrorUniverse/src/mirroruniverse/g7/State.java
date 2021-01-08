package mirroruniverse.g7;

import java.util.Random;

public class State {

	/* State that works with 2-level BFS search
	 *
	 *  getNext:
	 *   Returns all possible next states from a state
	 *   if one player has reached his goal then he does
	 *   not move anymore while the other player moves
	 *
	 *  isFinal:
	 *   Returns if the state can progress to the next
	 *   level of search, that is if one of the two
	 *   players has reached his goal
	 *
	 *  isGoal:
	 *   Both players have reached their goal
	 *
	 *  isFinalOptimal:
	 *   The state is a final state and is also the
	 *   optimal final state, so the search can
	 *   discard all other final stages and keep
	 *   only this one to expand until the goal
	 */

	public static final int maxNextStates = 8;
	public static final byte unusedMoveCode = 30;
	public static final byte nullMoveCode = 0;

	private static byte[][] leftMap;
	private static byte[][] rightMap;

	private static int leftRows;
	private static int leftCols;

	private static int rightRows;
	private static int rightCols;

	private static int leftNorth;
	private static int leftSouth;
	private static int leftWest;
	private static int leftEast;

	private static int rightNorth;
	private static int rightSouth;
	private static int rightWest;
	private static int rightEast;

	private static Point leftExit;
	private static Point rightExit;

	private static boolean zeroPossible;

	private static int[][] permutations = allPermutations(8);
	private static Random random = new Random();

	public static boolean keepAlignment = true;
	public static boolean keepNonExit = true;

	private short i1, j1, i2, j2;

	public static int problemSize() {
		return leftRows * leftCols * rightRows * rightCols;
	}	

	public int i1() { return i1; }
	public int j1() { return j1; }
	public int i2() { return i2; }
	public int j2() { return j2; }

	public static void maps(byte[][] left, byte[][] right) {
		leftMap = left;
		rightMap = right;
	}

	public static void init(int[] leftBoundaries, int[] rightBoundaries,
	                        Point leftExitP, Point rightExitP) {
		/* Set left map */
		leftNorth = leftBoundaries[0];
		leftSouth = leftBoundaries[1];
		leftWest = leftBoundaries[2];
		leftEast = leftBoundaries[3];
		leftRows = leftSouth - leftNorth + 1;
		leftCols = leftEast - leftWest + 1;
		/* Set right map */
		rightNorth = rightBoundaries[0];
		rightSouth = rightBoundaries[1];
		rightWest = rightBoundaries[2];
		rightEast = rightBoundaries[3];
		rightRows = rightSouth - rightNorth + 1;
		rightCols = rightEast - rightWest + 1;
		/* Set exits */
		leftExit = leftExitP;
		rightExit = rightExitP;
		/* Check if zero solution is possible */
		if (leftExit != null && rightExit != null)
			zeroPossible = zeroPossible();
	}

	public static State dumpState() {
		return new State(-1, -1, -1, -1);
	}

	public static boolean zeroPossible() {
		for (int di = -1 ; di <= 1 ; ++di)
			for (int dj = -1 ; dj <= 1 ; ++dj)
				if (di != 0 || dj != 0) {
					int i1 = leftExit.i + di;
					int j1 = leftExit.j + dj;
					int i2 = rightExit.i + di;
					int j2 = rightExit.j + dj;
					if (inMapLeft(i1, j1) && leftMap[i1][j1] == 0 &&
					    inMapRight(i2, j2) && rightMap[i2][j2] == 0)
						return true;
				}
		return false;
	}

	public static boolean searchEarlyOptimal(State[] solution) {
		int len = solution.length;
		if (!solution[len - 1].searchGoal())
			return false;
		if (len == 1)
			return true;
		if (zeroPossible)
			return !solution[len - 2].searchFinal();
		if (len == 2)
			return true;
		return !solution[len - 3].searchFinal();
	}

	public State(int i1, int j1, int i2, int j2) {
		this.i1 = (short) i1;
		this.j1 = (short) j1;
		this.i2 = (short) i2;
		this.j2 = (short) j2;
	}

	public State(State par) {
		i1 = par.i1;
		j1 = par.j1;
		i2 = par.i2;
		j2 = par.j2;
	}

	private boolean leftOnGoal() {
		return leftMap[i1][j1] == 2;
	}

	private boolean rightOnGoal() {
		return rightMap[i2][j2] == 2;
	}

	public boolean searchGoal() {
		return leftOnGoal() && rightOnGoal();
	}

	public boolean searchFinal() {
		return leftOnGoal() || rightOnGoal();
	}

	public boolean searchOptimalFinal() {
		if (zeroPossible)
			return searchGoal();
		if (!searchFinal())
			return false;
		int dist1 = distanceSquared(i1, j1, leftExit.i, leftExit.j);
		int dist2 = distanceSquared(i2, j2, rightExit.i, rightExit.j);
		return dist1 <= 2 && dist2 <= 2;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof State))
			return false;
		State p = (State) obj;
		return i1 == p.i1 && j1 == p.j1 && i2 == p.i2 && j2 == p.j2;
	}

	public int id() {
		int rightSize = rightCols * rightRows;
		int lid = (i1 - leftNorth) * leftCols + j1 - leftWest;
		int rid = (i2 - rightNorth) * rightCols + j2 - rightWest;
		return lid * rightSize + rid;
	}

	public static void byId(State s, int id) {
		int rightSize = rightCols * rightRows;
		int lp = id / rightSize;
		int rp = id % rightSize;
		s.i1 = (short) (lp / leftCols + leftNorth);
		s.j1 = (short) (lp % leftCols + leftWest);
		s.i2 = (short) (rp / rightCols + rightNorth);
		s.j2 = (short) (rp % rightCols + rightWest);
	}

	private static boolean inMapLeft(int i, int j) {
		return i >= leftNorth && i <= leftSouth && j >= leftWest && j <= leftEast;
	}

	private static boolean inMapRight(int i, int j) {
		return i >= rightNorth && i <= rightSouth && j >= rightWest && j <= rightEast;
	}

	public int terminateNext(byte[] move, State[] nextState) {
		// if one has reached his goal he will not move
		boolean finLeft = leftMap[i1][j1] == 2;
		boolean finRight = rightMap[i2][j2] == 2;
		boolean immLeft, immRight;
		int c = 0;
		for (int d = 1 ; d != 9 ; ++d) {
			int ed = d;
			int di = Util.dirToCode[d][0];
			int dj = Util.dirToCode[d][1];
			if (finLeft || !inMapLeft(i1 + di, j1 + dj))
				immLeft = true;
			else {
				int m = leftMap[i1 + di][j1 + dj];
				// cannot move to unknown territory
				if (m == -1) continue;
				immLeft = (m == 1);
			}
			if (finRight || !inMapRight(i2 + di, j2 + dj))
				immRight = true;
			else {
				int m = rightMap[i2 + di][j2 + dj];
				// cannot move to unknown territory
				if (m == -1) continue;
				immRight = (m == 1);
			}
			// one at least must move
			if (immLeft && immRight)
				continue;
			if (!immLeft) {
				nextState[c].i1 = (short) (i1 + di);
				nextState[c].j1 = (short) (j1 + dj);
			} else {
				nextState[c].i1 = i1;
				nextState[c].j1 = j1;
				ed += 10;
			}
			if (!immRight) {
				nextState[c].i2 = (short) (i2 + di);
				nextState[c].j2 = (short) (j2 + dj);
			} else {
				nextState[c].i2 = i2;
				nextState[c].j2 = j2;
				ed += 20;
			}
			move[c++] = (byte) ed;
		}
		return c;
	}

	public int exploreNext(byte[] move, State[] nextState) {
		// if one has reached his goal he will not move
		boolean finLeft = leftMap[i1][j1] == 2;
		boolean finRight = rightMap[i2][j2] == 2;
		// no point to keep alignment if one has finished
		boolean aligned = !finLeft && !finRight && keepAlignment;
		boolean immLeft, immRight;
		int c = 0;
		int[] dirs = permutations[random.nextInt(permutations.length)];
		for (int k =  0 ; k != 8 ; ++k) {
			boolean leftNowOnGoal = false;
			boolean rightNowOnGoal = false;
			int d = dirs[k];
			int ed = d;
			int di = Util.dirToCode[d][0];
			int dj = Util.dirToCode[d][1];
			if (finLeft || !inMapLeft(i1 + di, j1 + dj))
				immLeft = true;
			else
				immLeft = Util.oneOf(leftMap[i1 + di][j1 + dj], 1, -1);
			if (finRight || !inMapRight(i2 + di, j2 + dj))
				immRight = true;
			else
				immRight = Util.oneOf(rightMap[i2 + di][j2 + dj], 1, -1);
			// one at least must move
			if (immLeft && immRight)
				continue;
			// both must move is alignment must be kept
			if (aligned && (immLeft || immRight))
				continue;
			if (!immLeft) {
				nextState[c].i1 = (short) (i1 + di);
				nextState[c].j1 = (short) (j1 + dj);
				if (leftMap[nextState[c].i1][nextState[c].j1] == 2)
					leftNowOnGoal = true;
			} else {
				nextState[c].i1 = i1;
				nextState[c].j1 = j1;
				ed += 10;
			}
			if (!immRight) {
				nextState[c].i2 = (short) (i2 + di);
				nextState[c].j2 = (short) (j2 + dj);
				if (rightMap[nextState[c].i2][nextState[c].j2] == 2)
					rightNowOnGoal = true;
			} else {
				nextState[c].i2 = i2;
				nextState[c].j2 = j2;
				ed += 20;
			}
			// we do no let anyone terminate if he has not already
			if (keepNonExit && (leftNowOnGoal || rightNowOnGoal))
				continue;
			move[c++] = (byte) ed;
		}
		return c;
	}

	public static int direction(State from, State to) {
		int di = to.i1 - from.i1;
		int dj = to.j1 - from.j1;
		if (di != 0 || dj != 0)
			return Util.codeToDir[di+1][dj+1];
		di = to.i2 - from.i2;
		dj = to.j2 - from.j2;
		return Util.codeToDir[di+1][dj+1];
	}

	private static boolean isValidLeft(int i, int j) {
		return inMapLeft(i, j) && leftMap[i][j] == 0;
	}

	private static boolean isValidRight(int i, int j) {
		return inMapRight(i, j) && rightMap[i][j] == 0;
	}

	public static State trace(State to, int dir) {
		boolean im1 = dir > 10 && dir < 20;
		boolean im2 = dir > 20;
		dir %= 10;
		int di = Util.dirToCode[dir][0];
		int dj = Util.dirToCode[dir][1];
		State from = new State(to);
		if (!im1 && isValidLeft(to.i1 - di, to.j1 - dj)) {
			from.i1 -= di;
			from.j1 -= dj;
		}
		if (!im2 && isValidRight(to.i2 - di, to.j2 - dj)) {
			from.i2 -= di;
			from.j2 -= dj;
		}
		return from;
	}

	public String toString() {
		return "[" + i1 + "," + j1 + "][" + i2 + "," + j2 + "]";
	}

	private static int distanceSquared(int x, int y, int tx, int ty) {
		int dx = x - tx;
		int dy = y - ty;
		return dx * dx + dy * dy;
	}

	private static int factorial(int n) {
		int r = 1;
		while (n != 0)
			r *= n--;
		return r;
	}

	private static void swap(int[] a, int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	private static int[][] allPermutations(int n) {
		int[][] res = new int [factorial(n)][n];
		int[] array = new int [n];
		int[] index = new int [1];
		index[0] = 0;
		for (int i = 0 ; i != n ; ++i)
			array[i] = i + 1;
		permute(0, res, array, index);
		return res;
	}

	private static void permute(int pos, int[][] res, int[] array, int[] index) {
		if (pos == array.length) {
			int i = index[0]++;
			for (int j = 0 ; j != array.length ; ++j)
				res[i][j] = array[j];
		}
		for (int i = pos ; i != array.length ; ++i) {
			swap(array, pos, i);
			permute(pos + 1, res, array, index);
			swap(array, pos, i);
		}
	}
}
