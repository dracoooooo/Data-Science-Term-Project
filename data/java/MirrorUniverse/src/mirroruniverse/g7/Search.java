package mirroruniverse.g7;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;

/* Includes two types of searches
 * One is searching for the goal
 * Second is to cover an unknown position
 *
 * To search the next exploration point we a use a simple BFS search
 * that stops as soon as it encounters a state where one of the players
 * is on an unknown spot. It will not expand such a state.
 *
 * To search for the final goal state we use a 2-level BFS search.
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

public class Search {

	/* Keep a mapping between child and parent state
	 * We need this for 2 reasons:
	 *   1) To know the states we do not
	 *      want to place in open set again
	 *   2) To re-create the traversed path
	 *      once we reach the bottom final state
	 */
	private static byte[] closedSet = new byte [Explorer.maxSize()];
	private static TreeSet <Integer> dirtyLocations = new TreeSet <Integer> ();
	private static boolean tooDirty = true;

	/* Reset the closed set */
	private static void reset(int size) {
		byte nullCode = State.nullMoveCode;
		if (tooDirty)
			for (int i = 0 ; i != size ; ++i)
				closedSet[i] = nullCode;
		else {
			for (int i : dirtyLocations) {
				if (i >= size) break;
				closedSet[i] = nullCode;
			}
			dirtyLocations.clear();
		}
		for (int i = 0 ; i != size ; ++i)
			if (closedSet[i] != nullCode)
				throw new RuntimeException();
	}

	/* Update the closed set */
	private static void update(int index, byte value) {
		closedSet[index] = value;
		if (!tooDirty) {
			dirtyLocations.add(index);
			if (dirtyLocations.size() == 100000) {
				dirtyLocations.clear();
				tooDirty = true;
			}
		}
	}

	/* Solve the problem */
	public static State[] terminate(State startState) {
		/* Set of states to be expanded */
		IntQueue openSet = new IntQueue();
		/* Reset the closed set */
		reset(State.problemSize());
		/* Keep all states when one has reached partial goal
		 * We will not expand these states until we have
		 * exhausted the search space to find them all
		 * We will terminate the search if during the
		 * first level of search we find the optimal
		 */
		Queue <State> finalSet = new LinkedList <State> ();
		/* Allocate space for next states and directions */
		byte[] moves = new byte [State.maxNextStates];
		State[] nextStates = new State [State.maxNextStates];
		byte nullCode = State.nullMoveCode;
		for (int i = 0 ; i != nextStates.length ; ++i) {
			moves[i] = nullCode;
			nextStates[i] = State.dumpState();
		}
		/* Add first state in the open and closed set */
		openSet.push(startState.id());
		closedSet[startState.id()] = State.unusedMoveCode;
		State currentState = State.dumpState();
		do {
			/* Get state from open set */
			State.byId(currentState, openSet.pop());
			/* Check if one of final state */
			if (currentState.searchFinal()) {
				finalSet.offer(new State(currentState));
				/* If you find the optimal final state */
				if (currentState.searchOptimalFinal()) {
					/* Clear the whole final set
					 * and add only this element
					 */
					finalSet.clear();
					finalSet.offer(new State(currentState));
					break;
				}
			} else {
				/* Expand state and examine its children */
				int validNextStates = currentState.terminateNext(moves, nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					int id = nextStates[i].id();
					if (closedSet[id] == nullCode) {
						openSet.push(id);
						update(id, moves[i]);
					}
				}
			}
			/* Terminate when the open set is empty
			 * This indicates we failed to solve it
			 */
		} while (!openSet.isEmpty());
		/* The one with the least moves to optimal wins */
		State[] bestPartialPath = null;
		/* To keep track of changes in the closed set */
		HashSet <Integer> closedSetChanges = new HashSet <Integer> ();
		/* Check all final states of the set in FIFO order
		 * FIFO ensures that ties take the fastest one
		 */
		while (!finalSet.isEmpty()) {
			/* Clear from closed set all modifications
			 * from previous second level BFS searches
			 */
			for (int id : closedSetChanges)
				closedSet[id] = nullCode;
			closedSetChanges.clear();
			/* Start from partial goal */
			State partialGoal = finalSet.poll();
			if (closedSet[partialGoal.id()] == nullCode)
				throw new RuntimeException();
			/* Start from the final state */
			openSet.clear();
			openSet.push(partialGoal.id());
			do {
				/* Get state from open set */
				State.byId(currentState, openSet.pop());
				/* If state if final */
				if (currentState.searchGoal()) {
					/* Keep the smallest partial path */
					State[] partialPath = path(partialGoal, currentState);
					if (bestPartialPath == null ||
					    partialPath.length < bestPartialPath.length)
						bestPartialPath = partialPath;
					break;
				}
				/* Expand state and examine its children
				 * Same as first level search
				 */
				int validNextStates = currentState.terminateNext(moves, nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					int id = nextStates[i].id();
					if (closedSet[id] == nullCode) {
						openSet.push(id);
						update(id, moves[i]);
						closedSetChanges.add(id);
					}
				}
			/* Normally the open set will never be emptied */
			} while (!openSet.isEmpty());
		}
		/* If no solution found */
		if (bestPartialPath == null)
			return null;
		/* Re-create the path from the mid to the start */
		State[] half = path(startState, bestPartialPath[0]);
		/* Connect the two paths */
		State[] result = new State[half.length + bestPartialPath.length - 1];
		for (int i = 0 ; i != half.length - 1 ; ++i)
			result[i] = half[i];
		for (int i = 0 ; i != bestPartialPath.length ; ++i)
			result[i + half.length - 1] = bestPartialPath[i];
		return result;
	}

	/* Return best possible solution */
	public static State[] explore(State startState, Evaluate eval) {
		/* Set of states to be expanded */
		IntQueue openSet = new IntQueue();
		/* Best solution so far */
		State bestState = null;
		int bestCost = Integer.MAX_VALUE;
		/* Reset the closed set */
		reset(State.problemSize());
		/* Allocate space for next states and directions */
		byte[] moves = new byte [State.maxNextStates];
		State[] nextStates = new State [State.maxNextStates];
		byte nullCode = State.nullMoveCode;
		for (int i = 0 ; i != nextStates.length ; ++i) {
			moves[i] = nullCode;
			nextStates[i] = State.dumpState();
		}
		/* Add first state in the open and closed set */
		openSet.push(startState.id());
		closedSet[startState.id()] = State.unusedMoveCode;
		State currentState = State.dumpState();
		State limitState = new State(startState);
		int depth = 0;
		do {
			/* Get state from open set */
			State.byId(currentState, openSet.pop());
			/* Check if exploration goal */
			if (eval.goal(currentState)) {
				/* Store possible destination */
				int cost = eval.cost(currentState, depth);
				if (cost < bestCost) {
					bestState = new State(currentState);
					bestCost = cost;
					if (eval.bestPossibleCost(depth) >= bestCost)
						break;
				}
			} else {
				/* Expand state and examine its children */
				int validNextStates = currentState.exploreNext(moves, nextStates);
				for (int i = 0 ; i != validNextStates ; ++i) {
					int id = nextStates[i].id();
					if (closedSet[id] == nullCode) {
						openSet.push(id);
						update(id, moves[i]);
					}
				}
			}
			/* Check if next depth states */
			if (currentState.equals(limitState)) {
				State.byId(limitState, openSet.last());
				if (eval.bestPossibleCost(++depth) >= bestCost)
					break;
			}
			/* Terminate when the open set is empty
			 * This indicates we failed to solve it
			 * Also if we have a solution and we have
			 * passed the depth limit of the search
			 */
		} while (!openSet.isEmpty());
		if (bestState == null) {
			System.out.println("No solution");
			return null;
		}
		return path(startState, bestState);
	}

	/* Re-create the solution path */
	private static State[] path(State startState, State endState) {
		/* A stack to reverse the path */
		Stack <State> pathStack = new Stack <State> ();
		/* Get first point starting from the end */
		State current = new State(endState);
		pathStack.push(current);
		/* Traverse the search space up by using the closed set */
		while (!startState.equals(current)) {
			if (closedSet[current.id()] == 0) {
				System.out.println(current);
				throw new RuntimeException();
			}
			current = State.trace(current, closedSet[current.id()]);
			pathStack.push(current);
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
