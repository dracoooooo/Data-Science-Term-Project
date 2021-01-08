package mirroruniverse.g7;

public class Point {

	private static Point[] neigs = init();

	private static Point[] init() {
		Point[] arr = new Point[8];
		for (int i = 0 ; i != 8 ; ++i)
			arr[i] = new Point(-1, -1);
		return arr;
	}

	public int i, j;

	public Point(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Point(Point p) {
		i = p.i;
		j = p.j;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Point))
			return false;
		Point p = (Point) obj;
		return i == p.i && j == p.j;
	}

	public int hashCode() {
		return i ^ j;
	}

	public Point[] neighbors() {
		for (int d = 1 ; d != 9 ; ++d) {
			int di = Util.dirToCode[d][0];
			int dj = Util.dirToCode[d][1];
			neigs[d-1].i = i + di;
			neigs[d-1].j = j + dj;
		}
		return neigs;
	}

	public String toString() {
		return "(" + i + "," + j + ")";
	}
}
