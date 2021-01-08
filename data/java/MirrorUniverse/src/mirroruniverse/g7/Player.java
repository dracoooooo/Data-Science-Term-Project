package mirroruniverse.g7;

import java.util.LinkedList;

public class Player implements mirroruniverse.sim.Player {

	// Information about the surroundings
	private Explorer exp = new Explorer();

	// Evaluator
	private Evaluate eval = new Evaluate(exp);

	// Current movement information
	private int pathPos = 0;
	private int[] path = new int [0];

	// Final solution is found
	private boolean solutionFound = false;

	// Turn when the first one stepped on goal
	private int oneOnGoal = 0;

	// Turns in the game
	private int turns = 0;

	// History of movements
	private LinkedList <Integer> history = new LinkedList <Integer> ();

	// Starting time
	private long startTime;

	// Time for next search
	private int nextSearch = 0;

	// Time limit in milliseconds
	private final long timeLimit = 300000;

	// Full turns we want to support
	private final int maxTurns = 60000;

	// Total time spent in searching
	private long timeSearching = 0;

	public int lookAndMove(int[][] left, int[][] right) {
		int dir = -1;
		try {
			// Start clock
			if (turns == 0)
				startTime = System.currentTimeMillis();
			// Update turns
			turns++;
			// Add information to the map
			boolean update = exp.dump(left, right);
			// Decide next direction
			dir = pickDirection(update);
			// Move the player to that direction and return it
			exp.move(dir);
			// Check if one moved on goal
			if (oneOnGoal == 0 && (exp.leftOnGoal() || exp.rightOnGoal()))
				oneOnGoal = turns;
			// Add to history
			history.addLast(dir);
			// If both in place game is finished
			if (exp.leftOnGoal() && exp.rightOnGoal()) {
				// Print some statistics
				long elapsedTime = System.currentTimeMillis() - startTime;
				System.out.println("Turns:     " + turns);
				System.out.println("Interval:  " + (turns - oneOnGoal));
				System.out.println("Time (ms): " + elapsedTime);
			}
		} catch (Exception e) {
			boolean first = true;
			for (int d : history) {
				if (first)
					first = false;
				else
					System.out.print(", ");
				System.out.print(d);
			}
			System.out.println("\n-1, " + history.size());
			System.out.flush();
			e.printStackTrace(System.err);
			System.err.println("Terminating...");
			System.exit(1);
		}
		return dir;
	}

	private int pickDirection(boolean update) {
		System.out.print("Turn " + turns + ": ");
		// We already know the solution
		if (solutionFound) {
			System.out.println("Follow solution path.");
			return path[pathPos++];
		}
		// If we see everything we search
		if (exp.leftExplored() && exp.rightExplored()) {
			System.out.println("Explored everything: ");
			exp.initStateGlobals();
			long searchTime = System.currentTimeMillis();
			// We are sure this search will find the solution
			path = decodePath(Search.terminate(exp.state()));
			searchTime = System.currentTimeMillis() - searchTime;
			System.out.println("Solution path found! (" + searchTime + " ms)");
			solutionFound = true;
			timeSearching += searchTime;
			pathPos = 1;
			return path[0];
		}
		// Update time for next search
		nextSearch--;
		// Now we see both exits
		if (update && exp.leftExitReachable() && exp.rightExitReachable() && nextSearch <= 0) {
			// Solve the problem with current information
			System.out.print("Trying search: ");
			exp.initStateGlobals();
			long searchTime = System.currentTimeMillis();
			State[] encodedPath = Search.terminate(exp.state());
			long currentTime = System.currentTimeMillis();
			searchTime = currentTime - searchTime;
			timeSearching += searchTime;
			if (encodedPath != null && State.searchEarlyOptimal(encodedPath)) {
				// Set the solution and return first move
				System.out.println("Solution path found! (" + searchTime + " ms)");
				solutionFound = true;
				path = decodePath(encodedPath);
				pathPos = 1;
				return path[0];
			}
			// Search failed compute time for next search
			System.out.println("Optimal solution not found... (" + searchTime + " ms)");
			long remTime = timeLimit - currentTime;
			// If we have exceeded 50% of total time
			if (remTime + remTime >= timeLimit) {
				// Compute percentage of time spent in searches
				long elapsedTime = currentTime - startTime;
				double searchRatio = (double) timeSearching / (double) elapsedTime;
				// Compute remaining searches
				long remSearches = (long) (remTime * searchRatio) / searchTime;
				// Spread out searches to cover remaining rounds
				if (remSearches <= 0)
					nextSearch = Integer.MAX_VALUE;
				else {
					int remTurns = maxTurns - turns;
					nextSearch = (int) (remTurns / remSearches);
				}
			}
		}
		// Pre-determined path to follow
		if (pathPos != path.length && !exp.willMoveOnGoal(path[pathPos])) {
			System.out.println("Follow exploration path.");
			return path[pathPos++];
		}
		// Search for new exploration path
		exp.initStateGlobals();
		long evalTime = System.currentTimeMillis();
		eval.update();
		long exploreTime = System.currentTimeMillis();
		evalTime = exploreTime - evalTime;
		State[] explorePath = Search.explore(exp.state(), eval);
		// If aligned fails discard it
		if (State.keepAlignment && explorePath == null) {
			State.keepAlignment = false;
			System.out.println("Turn off aligned moves: " + turns);
			explorePath = Search.explore(exp.state(), eval);
		}
		// If one must exit do it
		if (State.keepNonExit && explorePath == null) {
			State.keepNonExit = false;
			System.out.println("Turn off early termination: " + turns);
			explorePath = Search.explore(exp.state(), eval);
		}
		// Sanity check
		if (explorePath == null)
			throw new RuntimeException("No map exit");
		exploreTime = System.currentTimeMillis() - exploreTime;
		System.out.println("Compute new exploration path. (" + evalTime + " + " + exploreTime + " ms)");
		// Decode and cut last position
		path = decodePath(explorePath);
		pathPos = 1;
		return path[0];
	}

	private static int[] decodePath(State[] path) {
		int size = path.length - 1;
		int[] solution = new int [size];
		for (int i = 0 ; i != size ; ++i)
			solution[i] = State.direction(path[i], path[i+1]);
		return solution;
	}
}
