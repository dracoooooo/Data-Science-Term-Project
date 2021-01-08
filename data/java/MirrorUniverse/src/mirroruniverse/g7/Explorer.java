package mirroruniverse.g7;

import java.util.HashSet;

public class Explorer {

	private static final int maxRows = 100;
	private static final int maxCols = 100;

	private final int fullMapRows = maxRows * 2 - 1;
	private final int fullMapCols = maxCols * 2 - 1;

	private byte[][] leftMap = Util.byteMap(fullMapRows, fullMapCols, (byte) -1);
	private byte[][] rightMap = Util.byteMap(fullMapRows, fullMapCols, (byte) -1);

	private int[][] leftVisited = Util.intMap(fullMapRows, fullMapCols, 0);
	private int[][] rightVisited = Util.intMap(fullMapRows, fullMapCols, 0);

	private HashSet <Integer> leftRowDis = new HashSet <Integer> ();
	private HashSet <Integer> leftColDis = new HashSet <Integer> ();

	private HashSet <Integer> rightRowDis = new HashSet <Integer> ();
	private HashSet <Integer> rightColDis = new HashSet <Integer> ();

	private Point left = new Point(fullMapRows / 2, fullMapCols / 2);
	private Point right = new Point(fullMapRows / 2, fullMapCols / 2);

	private Point leftExit = null;
	private Point rightExit = null;

	private int turns = 0;

	private int leftSight = 0;
	private int rightSight = 0;

	public final static byte known_and_reachable = 0;
	public final static byte known_but_unreachable = 1;
	public final static byte unknown_but_reachable = 2;
	public final static byte unknown_and_unreachable = 3;

	public final static boolean reachable(byte c) {
		return c == known_and_reachable || c == unknown_but_reachable;
	}

	private byte[][] leftCategory = Util.byteMap(fullMapRows, fullMapCols, unknown_and_unreachable);
	private byte[][] rightCategory = Util.byteMap(fullMapRows, fullMapCols, unknown_and_unreachable);

	private int[] leftCategoryCount = {0, 0, 0, fullMapRows * fullMapCols};
	private int[] rightCategoryCount = {0, 0, 0, fullMapRows * fullMapCols};

	private HashSet <Point> toFlood = new HashSet <Point> ();

	public void verbose() {
		System.out.println("Left:");
		verbose(leftMap, leftCategory, leftCategoryCount);
		System.out.println("Right:");
		verbose(rightMap, rightCategory, rightCategoryCount);
	}

	private void verbose(byte[][] map, byte[][] cat, int[] count) {
		int[] ncount = {0, 0, 0, 0};
		int rows = cat.length;
		int cols = cat[0].length;
		System.out.print("   ");
		for (int i = 0 ; i != cols ; ++i) {
			if (i < 10)
				System.out.print("|  " + i + " ");
			else
				System.out.print("| " + i + " ");
		}
		System.out.println("|");
		for (int i = 0 ; i != rows ; ++i) {
			if (i < 10)
				System.out.print(" " + i + " | ");
			else
				System.out.print(i + " | ");
			for (int j = 0 ; j != cols ; ++j) {
				ncount[cat[i][j]]++;
				if (map[i][j] == -1)
					System.out.print("?");
				else if (map[i][j] == 1)
					System.out.print("X");
				else if (map[i][j] == 2)
					System.out.print("-");
				else if (map[i][j] == 0)
					System.out.print("O");
				else
					throw new RuntimeException();
				switch (cat[i][j]) {
				case known_and_reachable:
					System.out.print("R | ");
					if (map[i][j] == -1)
						throw new RuntimeException();
					break;
				case known_but_unreachable:
					System.out.print("U | ");
					if (map[i][j] == -1)
						throw new RuntimeException();
					break;
				case unknown_and_unreachable:
					System.out.print("U | ");
					if (map[i][j] != -1)
						throw new RuntimeException();
					break;
				case unknown_but_reachable:
					System.out.print("R | ");
					if (map[i][j] != -1)
						throw new RuntimeException();
					break;
				default:
					throw new RuntimeException();
				}
			}
			System.out.println("");
		}
		for (int i = 0 ; i != 4 ; ++i)
			if (ncount[i] != count[i])
				throw new RuntimeException("" + i);
		System.out.println("  Known and reachable:     " + count[0]);
		System.out.println("  Known and unreachable:   " + count[1]);
		System.out.println("  Unknown and reachable:   " + count[2]);
		System.out.println("  Unknown and unreachable: " + count[3]);
	}

	private void init(Point p, byte[][] map, byte[][] cat, int[] count) {
		map[p.i][p.j] = 0;
		count[unknown_and_unreachable]--;
		count[known_and_reachable]++;
		cat[p.i][p.j] = known_and_reachable;
		int rows = map.length;
		int cols = map[0].length;
		for (Point n : p.neighbors())
			if (n.i >= 0 && n.i < rows && n.j >= 0 && n.j < cols) {
				count[unknown_and_unreachable]--;
				count[unknown_but_reachable]++;
				cat[n.i][n.j] = unknown_but_reachable;
			}
	}

	private void update(Point p, byte[][] map, byte[][] cat, int[] count) {
		if (cat[p.i][p.j] == unknown_and_unreachable) {
			count[unknown_and_unreachable]--;
			count[known_but_unreachable]++;
			cat[p.i][p.j] = known_but_unreachable;
		} else if (cat[p.i][p.j] == unknown_but_reachable) {
			count[unknown_but_reachable]--;
			if (map[p.i][p.j] == 1) {
				count[known_but_unreachable]++;
				cat[p.i][p.j] = known_but_unreachable;
			} else {
				count[known_and_reachable]++;
				cat[p.i][p.j] = known_and_reachable;
				if (map[p.i][p.j] != 2)
					toFlood.add(new Point(p));
			}
		} else throw new RuntimeException();
	}

	private void finalize(byte[][] map, byte[][] cat, int[] count) {
		HashSet <Point> examined = new HashSet <Point> ();
		int rows = cat.length;
		int cols = cat[0].length;
		for (Point p : toFlood)
			examined.add(p);
		while (!toFlood.isEmpty()) {
			Point p = toFlood.iterator().next();
			toFlood.remove(p);
			for (Point n : p.neighbors())
				if (n.i >= 0 && n.i < rows && n.j >= 0 && n.j < cols)
					if (cat[n.i][n.j] == unknown_and_unreachable) {
						count[unknown_and_unreachable]--;
						count[unknown_but_reachable]++;
						cat[n.i][n.j] = unknown_but_reachable;
					} else if (cat[n.i][n.j] == known_but_unreachable && map[n.i][n.j] == 0 && !examined.contains(n)) {
						count[known_but_unreachable]--;
						count[known_and_reachable]++;
						cat[n.i][n.j] = known_and_reachable;
						Point c = new Point(n);
						toFlood.add(c);
						examined.add(c);
					}
		}
		toFlood.clear();
	}

	public Explorer() {
		leftMap[left.i][left.j] = 0;
		rightMap[right.i][right.j] = 0;
		leftVisited[left.i][left.j] = 1;
		rightVisited[right.i][right.j] = 1;
		State.maps(leftMap, rightMap);
	}

	public State state() {
		return new State(left.i, left.j, right.i, right.j);
	}

	public boolean dump(int[][] visionLeft, int[][] visionRight) {
		// update the turns
		if (turns++ == 0) {
			// get the sight radius
			leftSight = visionLeft.length / 2;
			rightSight = visionRight.length / 2;
			init(left, leftMap, leftCategory, leftCategoryCount);
			init(right, rightMap, rightCategory, rightCategoryCount);
		}
		// dump the left map
		boolean leftUpdated = false;
		int visionLeftRows = visionLeft.length;
		int visionLeftCols = visionLeft[0].length;
		int vi1 = visionLeftRows / 2;
		int vj1 = visionLeftCols / 2;
		for (int i = 0 ; i != visionLeftRows ; ++i)
			for (int j = 0 ; j != visionLeftCols ; ++j) {
				int mi = i - vi1 + left.i;
				int mj = j - vj1 + left.j;
				if (inMap(mi, mj)) {
					int before = leftMap[mi][mj];
					int after = visionLeft[i][j];
					if (!consistent(before, after))
						throw new RuntimeException("Inconsistent vision");
					if (before == -1) {
						leftUpdated = true;
						if (after == 2) {
							leftExit = new Point(mi, mj);
							System.out.println("Left exit found.");
						}
						leftMap[mi][mj] = (byte) after;
						update(new Point(mi, mj), leftMap, leftCategory, leftCategoryCount);
					}
				} else if (visionLeft[i][j] != 1)
					throw new RuntimeException("Inconsistent vision");
			}
		// cut parts of left map
		if (leftUpdated) {
			int[] boundaries = boundaries(leftMap, 0, 2);
			for (int i = 0 ; i != fullMapRows ; ++i)
				if (Util.abs(i - boundaries[0]) >= maxRows || Util.abs(i - boundaries[1]) >= maxRows)
					if (!leftRowDis.contains(i)) {
						leftRowDis.add(i);
						for (int j = 0 ; j != fullMapCols ; ++j) {
							if (!Util.oneOf(leftMap[i][j], 1, -1))
								throw new RuntimeException("Inconsistent vision");
							if (leftMap[i][j] != 1) {
								leftMap[i][j] = 1;
								update(new Point(i, j), leftMap, leftCategory, leftCategoryCount);
							}
						}
					}
			for (int j = 0 ; j != fullMapCols ; ++j)
				if (Util.abs(j - boundaries[2]) >= maxCols || Util.abs(j - boundaries[3]) >= maxCols)
					if (!leftColDis.contains(j)) {
					leftColDis.add(j);
						for (int i = 0 ; i != fullMapRows ; ++i) {
							if (!Util.oneOf(leftMap[i][j], 1, -1))
								throw new RuntimeException("Inconsistent vision");
							if (leftMap[i][j] != 1) {
								leftMap[i][j] = 1;
								update(new Point(i, j), leftMap, leftCategory, leftCategoryCount);
							}
						}
					}
			finalize(leftMap, leftCategory, leftCategoryCount);
		}
		// dump the right map
		boolean rightUpdated = false;
		int visionRightRows = visionRight.length;
		int visionRightCols = visionRight[0].length;
		int vi2 = visionRightRows / 2;
		int vj2 = visionRightCols / 2;
		for (int i = 0 ; i != visionRightRows ; ++i)
			for (int j = 0 ; j != visionRightCols ; ++j) {
				int mi = i - vi2 + right.i;
				int mj = j - vj2 + right.j;
				if (inMap(mi, mj)) {
					int before = rightMap[mi][mj];
					int after = visionRight[i][j];
					if (!consistent(before, after))
						throw new RuntimeException("Inconsistent vision");
					if (before == -1) {
						rightUpdated = true;
						if (after == 2) {
							rightExit = new Point(mi, mj);
							System.out.println("Right exit found.");
						}
						rightMap[mi][mj] = (byte) after;
						update(new Point(mi, mj), rightMap, rightCategory, rightCategoryCount);
					}
				} else if (visionRight[i][j] != 1)
					throw new RuntimeException("Inconsistent vision");
			}
		if (rightUpdated) {
			// cut parts of right map
			int[] boundaries = boundaries(rightMap, 0, 2);
			for (int i = 0 ; i != fullMapRows ; ++i)
				if (Util.abs(i - boundaries[0]) >= maxRows || Util.abs(i - boundaries[1]) >= maxRows)
					if (!rightRowDis.contains(i)) {
						rightRowDis.add(i);
						for (int j = 0 ; j != fullMapCols ; ++j) {
							if (!Util.oneOf(rightMap[i][j], 1, -1))
								throw new RuntimeException("Inconsistent vision");
							if (rightMap[i][j] != 1) {
								rightMap[i][j] = 1;
								update(new Point(i, j), rightMap, rightCategory, rightCategoryCount);
							}
						}
					}
			for (int j = 0 ; j != fullMapCols ; ++j)
				if (Util.abs(j - boundaries[2]) >= maxCols || Util.abs(j - boundaries[3]) >= maxCols)
					if (!rightColDis.contains(j)) {
						rightColDis.add(j);
						for (int i = 0 ; i != fullMapRows ; ++i) {
							if (!Util.oneOf(rightMap[i][j], 1, -1))
								throw new RuntimeException("Inconsistent vision");
							if (rightMap[i][j] != 1) {
								rightMap[i][j] = 1;
								update(new Point(i, j), rightMap, rightCategory, rightCategoryCount);
							}
						}
					}
			finalize(rightMap, rightCategory, rightCategoryCount);
		}
		return leftUpdated || rightUpdated;
	}

	private static boolean consistent(int before, int after) {
		return after != -1 && (before == -1 || after == before);
	}

	private boolean inMap(int i, int j) {
		return i >=0 && j >= 0 && i < fullMapRows && j < fullMapCols;
	}

	public boolean leftOnGoal() {
		return leftMap[left.i][left.j] == 2;
	}

	public boolean rightOnGoal() {
		return rightMap[right.i][right.j] == 2;
	}

	public boolean leftExitFound() {
		return leftExit != null;
	}

	public boolean rightExitFound() {
		return rightExit != null;
	}

	public boolean leftExitReachable() {
		return leftExit != null && leftCategory[leftExit.i][leftExit.j] == known_and_reachable;
	}

	public boolean rightExitReachable() {
		return rightExit != null && rightCategory[rightExit.i][rightExit.j] == known_and_reachable;
	}

	public boolean leftExplored() {
		return leftCategoryCount[unknown_but_reachable] == 0;
	}

	public boolean rightExplored() {
		return rightCategoryCount[unknown_but_reachable] == 0;
	}

	public int leftSight() {
		return leftSight;
	}

	public int rightSight() {
		return rightSight;
	}

	public int mapRows() {
		return fullMapRows;
	}

	public int mapCols() {
		return fullMapCols;
	}

	private boolean canLeftMove(int dir) {
		if (leftOnGoal())
			return false;
		int di = Util.dirToCode[dir][0];
		int dj = Util.dirToCode[dir][1];
		int ni = left.i + di;
		int nj = left.j + dj;
		if (!inMap(ni, nj))
			return false;
		return Util.oneOf(leftMap[ni][nj], 0, 2);
	}

	private boolean canRightMove(int dir) {
		if (rightOnGoal())
			return false;
		int di = Util.dirToCode[dir][0];
		int dj = Util.dirToCode[dir][1];
		int ni = right.i + di;
		int nj = right.j + dj;
		if (!inMap(ni, nj))
			return false;
		return Util.oneOf(rightMap[ni][nj], 0, 2);
	}

	public void move(int dir) {
		int di = Util.dirToCode[dir][0];
		int dj = Util.dirToCode[dir][1];
		if (canLeftMove(dir)) {
			left.i += di;
			left.j += dj;
		}
		if (canRightMove(dir)) {
			right.i += di;
			right.j += dj;
		}
		leftVisited[left.i][left.j]++;
		rightVisited[right.i][right.j]++;
	}

	public boolean willMoveOnGoal(int dir) {
		int di = Util.dirToCode[dir][0];
		int dj = Util.dirToCode[dir][1];
		if (canLeftMove(dir)) {
			int i = left.i + di;
			int j = left.j + dj;
			if (leftMap[i][j] == 2)
				return true;
		}
		if (canRightMove(dir)) {
			int i = right.i + di;
			int j = right.j + dj;
			if (rightMap[i][j] == 2)
				return true;
		}
		return false;
	}

	public void initStateGlobals() {
		// Get left core of the map
		int[] leftBounds = boundaries(leftMap, 0, 2);
		leftBounds = expand(leftMap, leftBounds);
		// Get right core of the map
		int[] rightBounds = boundaries(rightMap, 0, 2);
		rightBounds = expand(rightMap, rightBounds);
		State.init(leftBounds, rightBounds, leftExit, rightExit);
	}

	private static int[] boundaries(byte[][] data, int ... codes) {
		int north = Integer.MAX_VALUE;
		int south = -1;
		int west = Integer.MAX_VALUE;
		int east = -1;
		for (int i = 0 ; i != data.length ; ++i)
			for (int j = 0 ; j != data[i].length ; ++j)
				if (Util.oneOf(data[i][j], codes)) {
					if (i < north) north = i;
					if (i > south) south = i;
					if (j < west)  west = j;
					if (j > east)  east = j;
				}
		int[] ret = {north, south, west, east};
		return ret;
	}

	private static int[] expand(byte[][] data, int[] boundaries) {
		int rows = data.length;
		int cols = data[0].length;
		int[] res = new int [4];
		res[0] = Util.max(0, boundaries[0]-1);
		res[1] = Util.min(rows-1, boundaries[1]+1);
		res[2] = Util.max(0, boundaries[2]-1);
		res[3] = Util.min(cols-1, boundaries[3]+1);
		return res;
	}

	public static int maxSize() {
		return Util.square((maxRows + 2) * (maxCols + 2));
	}

	public void leftDistMap(int[][] level) {
		Evaluate.distMap(leftMap, leftCategory, level);
	}

	public void rightDistMap(int[][] level) {
		Evaluate.distMap(rightMap, rightCategory, level);
	}
}
