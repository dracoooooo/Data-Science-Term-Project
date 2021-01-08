package mirroruniverse.g7_old;

import java.util.LinkedList;

public class Player implements mirroruniverse.sim.Player {

	private int[] solution;

	private int position = -1;

	public void init(int[][] leftMap, int[][] rightMap) {
		State start = State.init(core(leftMap), core(rightMap));
		BFS solver = new BFS(start);
		LinkedList <State> path = solver.solve();
		if (path == null) {
			System.err.println("Not easy peasy lemon squeezy");
			return;
		}
		// add the ending state when only one moves
		if (!path.getLast().equals(State.end()));
			path.offerLast(State.end());
		for (State s : path)
			System.out.print(s + "  ");
		System.out.println("");
		int size = path.size() - 1;
		solution = new int [size];
		for (int i = 0 ; i != size ; ++i) {
			State from = path.pollFirst();
			State to = path.getFirst();
			solution[i] = State.direction(from, to);
		}
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
