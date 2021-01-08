package mirroruniverse.g7_generic;

public class Player implements mirroruniverse.sim.Player {

	private int[] solution;

	private int position = -1;

	public void init(int[][] leftMap, int[][] rightMap) {
		State start = State.init(core(leftMap), core(rightMap));
		long time = System.currentTimeMillis();
		State[] path = Search.solve(start);
		time = System.currentTimeMillis() - time;
		System.out.println("Search time: " + time + " ms");
		if (path == null)
			throw new RuntimeException("Cannot see the exit!");
		int size = path.length - 1;
		solution = new int [size];
		for (int i = 0 ; i != size ; ++i)
			solution[i] = State.direction(path[i], path[i+1]);
	}

	public static void print(int[][] array) {
		for (int i = 0 ; i != array.length ; ++i) {
			for (int j = 0 ; j != array[i].length ; ++j)
				System.out.print(array[i][j] + " ");
			System.out.println("");
		}
	}

	public int lookAndMove(int[][] leftMap, int[][] rightMap) {
		if (position < 0) {
			init(leftMap, rightMap);
			position = 0;
		}
		return solution[position++];
	}

	public static int[][] core(int[][] data) {
		int north = Integer.MAX_VALUE;
		int south = -1;
		int west = Integer.MAX_VALUE;
		int east = -1;
		int rows = data.length;
		int cols = data[0].length;
		for (int i = 0 ; i != rows ; ++i)
			for (int j = 0 ; j != cols ; ++j) {
				if (data[i][j] == 1)
					continue;
				if (i < north)
					north = i;
				if (i > south)
					south = i;
				if (j < west)
					west = j;
				if (j > east)
					east = j;
			}
		int midRow = rows / 2;
		int midCol = cols / 2;
		rows = south - north + 1;
		cols = east - west + 1;
		int[][] res = new int [rows][cols];
		for (int i = north ; i <= south ; ++i)
			for (int j = west ; j <= east ; ++j)
				res[i-north][j-west] = data[i][j];
		res[midRow-north][midCol-west] = 3;
		return res;
	}
}
