public class CheckingExistenceofEdgeLengthLimitedPaths {
    int curr = 0;
    
    public boolean[] distanceLimitedPathsExist(int n, int[][] edges, int[][] queries) {
        Arrays.sort(edges, (a, b) -> (a[2] - b[2]));
		// Add another column to the queries so that we know the index after sorting.
        int[][] qs = new int[queries.length][4];
        for(int i = 0; i < queries.length; i++) {
            qs[i] = new int[]{queries[i][0], queries[i][1], queries[i][2], i};
        }
        Arrays.sort(qs, (a, b) -> (a[2] - b[2]));
        boolean[] res = new boolean[queries.length];
        int[] roots = new int[n];
        for(int i = 0; i < n; i++) roots[i] = i;
        for(int i = 0; i < queries.length; i++) {
            int[] q = qs[i];
            res[q[3]] = helper(edges, q[0], q[1], q[2], roots);
        }
        return res;
    }
    
    private boolean helper(int[][] edges, int start, int end, int limit, int[] roots) {
        for(int i = curr; i < edges.length; i++) {
            int[] e = edges[i];
            if(e[2] >= limit) {
				// 'curr' is to mark until which step have we finished the union-find.
                curr = i;
                break;
            }
            int root1 = find(roots, e[0]);
            int root2 = find(roots, e[1]);
            if(root1 != root2) {
                roots[root1] = root2;
            }
        }
        return find(roots, start) == find(roots, end);
    }
    
    private int find(int[] roots, int i) {
        int j = i;
        while(roots[i] != i) {
            i = roots[i];
        }
        roots[j] = i;
        return i;
    }
}
