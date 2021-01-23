public class HouseRobber {
    public int rob(int[] nums) {
        int n = nums.length;
        if(n==0) return 0;
        
        // i,0 : Not including the ith home.
        // i,1 : Including the ith home.
        int[][] dp = new int[n][2];
        
        // Base case.
        dp[0][0] = 0;
        dp[0][1] = nums[0];
        
        for(int i = 1; i < n; i++) {
            dp[i][0] = dp[i-1][1];
            dp[i][1] = Math.max(dp[i-1][0] + nums[i], dp[i-1][1]);
        }
        
        return Math.max(dp[n-1][0], dp[n-1][1]);
    }
}
