package mirroruniverse.g7_old;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BFS {

	/* Keep yet-to-be-expanded states */
	private Queue <State> open_set;

	/* Keep a mapping between child and parent state
	 * We need this for 2 reasons:
	 *   1) To know the states we do not
	 *      want to place in open set again
	 *   2) To re-create the traversed path
	 *      once we reach the bottom final state
	 */
	private HashMap <State, State> closed_set;

	public BFS(State start) {
		/* Clear out possible previous versions */
		open_set = null;
		closed_set = null;
		/* Set of states to be expanded */
		open_set = new LinkedList <State> ();
		/* Set of states examined or set to be
		 * examined soon through the open set
		 */
		closed_set = new HashMap <State, State> ();
		/* Add first state in the open set */
		open_set.offer(start);
		/* Add first state mapping to self in closed set
		 * We will use this small cycle to terminate
		 * while traversing the path in reverse order
		 */
		closed_set.put(start, start);
	}

	/* Search for solution or return null if none exists */
	public LinkedList <State> solve() {
		State[] nextStates = new State [8];
		for (int i = 0 ; i != nextStates.length ; ++i)
			nextStates[i] = new State();
		do {
			/* Get state from open set */
			State current = open_set.poll();
			/* Check if final state */
			if (current.isGoal())
				/* If final re-create path and terminate */
				return path(current);
			/* Expand state and examine its children */
			int valid = current.getNext(nextStates);
			for (int i = 0 ; i != valid ; ++i) {
				State next = nextStates[i];
				if (!closed_set.containsKey(next)) {
					State copy = new State(next);
					open_set.offer(copy);
					closed_set.put(copy, current);
				}
			}
		/* Terminate when the open set is empty
		 * This indicates we failed to solve it
		 */
		} while (!open_set.isEmpty());
		return null;
	}

	/* Re-create the solution path */
	private LinkedList <State> path(State current) {
		LinkedList <State> path = new LinkedList <State> ();
		State previous;
		/* Get first point */
		path.offerFirst(current);
		/* Traverse the search space up by using the closed set
		 * We know we reached start because it will be mapped to itself
		 */
		while ((previous = closed_set.get(current)) != current) {
			path.offerFirst(previous);
			current = previous;
		}
		return path;
	}
}
