public class MaximumHeightbyStackingCuboids {
    public int maxHeight(int[][] cs) {
        int n = cs.length;
        for (int i = 0; i < n; i++) {
            Arrays.sort(cs[i]);
        }
        Arrays.sort(cs, (a, b) -> a[0] != b[0] ? b[0] - a[0] : (a[1] != b[1] ? b[1] - a[1] : b[2] - a[2]));
        int[] dp = new int[n];
        dp[0] = cs[0][2];
        int res = dp[0];
        for (int i = 1; i < n; i++) {
            dp[i] = 0;
            for (int j = i - 1; j >= 0; j--) {
                if (cs[i][0] <= cs[j][0] && cs[i][1] <= cs[j][1] && cs[i][2] <= cs[j][2])
                    dp[i] = Math.max(dp[i], dp[j]);
            }
            dp[i] += cs[i][2];
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}
