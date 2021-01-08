package mirroruniverse.g7;

public class IntQueue {

	private static class Node {

		public static final int limit = 1024;

		public int[] data = new int [limit];

		public int front = 0;
		public int back = 0;

		public Node next = null;
	}

	private Node start;
	private Node end;

	public IntQueue() {
		start = new Node();
		end = start;
	}

	public void push(int x) {
		if (end.back == Node.limit) {
			end.next = new Node();
			end = end.next;
		}
		end.data[end.back++] = x;
	}

	public int pop() {
		if (start.front == Node.limit)
			start = start.next;
		return start.data[start.front++];
	}

	public int last() {
		return end.data[end.back - 1];
	}

	public void clear() {
		start = end;
		start.front = 0;
		start.back = 0;
	}

	public boolean isEmpty() {
		return start == end && start.front == start.back;
	}
}
