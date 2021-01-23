public class GuessNumberHigherorLowerII {
    public int xxxgetMoneyAmount(int n) {
        int[][] f = new int[n + 1][n + 1];
        Deque<Integer[]> q; // item[]{index, value}
    
        int a, b, k0, v, f1, f2;
    
        for (b = 2; b <= n; b++) {
            k0 = b - 1;
            q = new LinkedList<Integer[]>();
    
            for (a = b - 1; a > 0; a--) {
                // find k0[a][b] by definition in O(1) time.
                while (f[a][k0 - 1] > f[k0 + 1][b])
                    k0--;
    
                // find f1[a][b] in O(1) time.
                while (!q.isEmpty() && q.peekFirst()[0] > k0)
                    q.pollFirst();
    
                v = f[a + 1][b] + a;
    
                while (!q.isEmpty() && v < q.peekLast()[1])
                    q.pollLast();
    
                q.offerLast(new Integer[] { a, v });
    
                f1 = q.peekFirst()[1];
                f2 = f[a][k0] + k0 + 1;
                f[a][b] = Math.min(f1, f2);
            }
        }
    
        return f[1][n];
    }
}
