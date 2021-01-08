package mirroruniverse.g7;

public class Util {

	public static final int[][] dirToCode = {
		{ 0, 0}, { 0, 1}, {-1, 1},
		{-1, 0}, {-1,-1}, { 0,-1},
		{ 1,-1}, { 1, 0}, { 1, 1}};

	public static final int[][] codeToDir = {
		{4, 3, 2},
		{5, 0, 1},
		{6, 7, 8}};

	public static void print(byte[][] array, int ti, int tj) {
		for (int i = 0 ; i != array.length ; ++i) {
			for (int j = 0 ; j != array[i].length ; ++j) {
				if (array[i][j] < 0)
					System.out.print("- ");
				else if (ti == i && tj == j)
					System.out.print("X ");
				else
					System.out.print(array[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public static boolean oneOf(int v, int ... x) {
		for (int i = 0 ; i != x.length ; ++i)
			if (v == x[i])
				return true;
		return false;
	}

	public static int abs(int x) {
		return x < 0 ? -x : x;
	}

	public static int square(int x) {
		return x * x;
	}

	public static int min(int x, int y) {
		return x < y ? x : y;
	}

	public static int max(int x, int y) {
		return x > y ? x : y;
	}

	public static byte[][] byteMap(int rows, int cols, byte code) {
		byte[][] a = new byte [rows][cols];
		for (int i = 0 ; i != rows ; ++i)
			for (int j = 0 ; j != cols ; ++j)
				a[i][j] = code;
		return a;
	}

	public static int[][] intMap(int rows, int cols, int code) {
		int[][] a = new int [rows][cols];
		for (int i = 0 ; i != rows ; ++i)
			for (int j = 0 ; j != cols ; ++j)
				a[i][j] = code;
		return a;
	}
}
