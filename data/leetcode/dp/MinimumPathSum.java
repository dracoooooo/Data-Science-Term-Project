public class MinimumPathSum {
    public int minPathSum(int[][] arr) {
        int m = arr.length;
        int n = arr[0].length;
        return dy(arr);
    }
    
    private int dy(int arr[][]) {
        int m = arr.length;
        int n = arr[0].length;
        int dp[][] = new int[m][n];
        for(int i=0;i<m;i++) {
            for(int j=0;j<n;j++) {
                if( i == 0 && j == 0) {
                    dp[i][j] = arr[i][j];
                    continue;
                }
                dp[i][j] = arr[i][j] + Math.min(j > 0 ? dp[i][j-1] : Integer.MAX_VALUE, i > 0 ? dp[i-1][j] : Integer.MAX_VALUE);
            }
        }
        return dp[m-1][n-1];
    }
}
