package mirroruniverse.g7_generic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/* 2-level BFS search
 * The algorithm works on two levels. First it searches for final states.
 * The final states are not expanded but are kept in a separate queue.
 * When the 1st level search space is exhausted  then the final states are
 * expanded to reach the final state. Returns the one with the smallest path.
 * This guarantees for our problem that we will get an optimal solution.
 * For our problem a final state is defined to be when at least one player
 * has reached the exit point. The path length of final states directly
 * corresponds to the difference at which the players exit the board.
 * So when one reaches his exit he is not expanded anymore until we get
 * all those states than one has exited. Then the second one moves to reach
 * his destination and the path length is the difference between terminations.
 * If two final states have same length in the 2nd level of the search, the
 * first one in the queue is the winner as it was produced earlier from the
 * 1st level search therefore it took less moves to get there.
 * A small optimization is that we may immediately terminate if we know
 * that a path state is optimal. When an optimal path state is encountered
 * during the first level of the search, we keep only that as the final set.
 */

// TODO unfixed bug in this version

public class Search {

	/* Solve the problem */
	public static State[] solve(State startState) {
		/* Set of states to be expanded */
		Queue <State> openSet = new LinkedList <State> ();
		/* Keep a mapping between child and parent state
		 * We need this for 2 reasons:
		 *   1) To know the states we do not
		 *      want to place in open set again
		 *   2) To re-create the traversed path
		 *      once we reach the bottom final state
		 */
		HashMap <State, State> closedSet = new HashMap <State, State> ();
		/* Keep all states when one has reached partial goal
		 * We will not expand these states until we have
		 * exhausted the search space to find them all
		 * We will terminate the search if during the
		 * first level of search we find the optimal
		 */
		Queue <State> finalSet = new LinkedList <State> ();
		/* Allocate space for next states */
		State[] nextStates = new State [State.maxNextStates];
		for (int i = 0 ; i != nextStates.length ; ++i)
			nextStates[i] = new State();
		/* Add first state in the open and closed set */
		openSet.offer(startState);
		closedSet.put(startState, null);
		do {
			/* Get state from open set */
			State currentState = openSet.poll();
			/* Check if one of final state */
			if (currentState.isFinal()) {
				finalSet.offer(currentState);
				/* If you find the optimal final state */
				if (currentState.isOptimalFinal()) {
					/* Clear the whole final set
					 * and add only this element
					 */
					finalSet.clear();
					finalSet.offer(currentState);
					break;
				}
			} else {
				/* Expand state and examine its children */
				int validNextStates = currentState.getNext(nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					State next = nextStates[i];
					if (!closedSet.containsKey(next)) {
						State copy = new State(next);
						openSet.offer(copy);
						closedSet.put(copy, currentState);
					}
				}
			}
			/* Terminate when the open set is empty
			 * This indicates we failed to solve it
			 */
		} while (!openSet.isEmpty());
		/* The one with the least moves to optimal wins */
		State[] bestPartialPath = null;
		/* Search for each final move the path to the goal */
		do {
			State partialGoal = finalSet.poll();
			/* Start from the final state */
			openSet.add(partialGoal);
			do {
				/* Get state from open set */
				State currentState = openSet.poll();
				/* If state if final */
				if (currentState.isGoal()) {
					/* Keep the smallest partial path */
					State[] partialPath = path(partialGoal, currentState,
					                           closedSet);
					if (bestPartialPath == null ||
					    partialPath.length < bestPartialPath.length)
						bestPartialPath = partialPath;
					break;
				}
				/* Expand state and examine its children
				 * Same as first level search
				 */
				int validNextStates = currentState.getNext(nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					State nextState = nextStates[i];
					if (!closedSet.containsKey(nextState)) {
						State copy = new State(nextState);
						openSet.offer(copy);
						closedSet.put(copy, currentState);
					}
				}
			/* Normally the open set will never be emptied */
			} while (!openSet.isEmpty());
		/* Check all final states of the set in FIFO order
		 * FIFO ensures that ties take the fastest one
		 */
		} while (!finalSet.isEmpty());
		/* If no solution found */
		if (bestPartialPath == null)
			return null;
		/* Keep last state and re-create whole path */
		State lastState = bestPartialPath[bestPartialPath.length-1];
		return path(startState, lastState, closedSet);
	}

	/* Re-create the solution path */
	private static State[] path(State start, State end,
	                            HashMap <State, State> closedSet) {
		/* A stack to reverse the path */
		Stack <State> pathStack = new Stack <State> ();
		/* Get first point */
		pathStack.push(end);
		/* Traverse the search space up by using the closed set */
		while (!end.equals(start)) {
			end = closedSet.get(end);
			pathStack.push(end);
		}
		/* Empty the stack and create an array of states */
		int i = 0;
		State[] path = new State [pathStack.size()];
		do {
			path[i++] = pathStack.pop();
		} while (!pathStack.isEmpty());
		return path;
	}
}
