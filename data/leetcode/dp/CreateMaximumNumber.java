public class CreateMaximumNumber {
    public int[] maxNumber(int[] A, int[] B, int k) {
        int m = A.length, n = B.length;
        if (m+n < k) return new int[0];

        String[][][] dp = new String[m+1][n+1][k+1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                for (int l = 0; l <= k; l++) {
                    if (i+j < l) dp[i][j][l] = "";
                    else {
                        String chooseAi = (i == 0 || l == 0) ? "" : dp[i-1][j][l-1] + A[i-1];
                        String chooseBj = (j == 0 || l == 0) ? "" : dp[i][j-1][l-1] + B[j-1];
                        String maxChoose = max(chooseAi, chooseBj);

                        String ignoreAi = (i == 0) ? "" : dp[i-1][j][l];
                        String ignoreBj = (j == 0) ? "" : dp[i][j-1][l];
                        String ignoreBoth = (i == 0 || j == 0) ? "" : dp[i-1][j-1][l];
                        String maxIgnore = max(max(ignoreAi, ignoreBj), ignoreBoth);

                        dp[i][j][l] = max(maxChoose, maxIgnore);
                    }
                }
            }
        }

        return convert(dp[m][n][k]);
    }

    private String max(String s1, String s2) {
        return (s1.compareTo(s2) < 0) ? s2 : s1;
    }

    private int[] convert(String s)  {
        int n = s.length(), res[] = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = s.charAt(i)-'0';
        }
        return res;
    }
}
