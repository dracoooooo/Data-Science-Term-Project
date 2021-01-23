public class PalindromePartitioningII {
    public int minCut(String st) {
        char[] s = st.toCharArray();
        int n = s.length;
        int[] dp = new int[n];
        boolean[][] P = new boolean[n][n];
        for(int i=0;i<n;i++){
            int cut=i;
            for(int j=0;j<=i;j++){
                if(s[i]==s[j]&&(i-j<2||P[j+1][i-1])){
                    P[j][i]=true;
                    cut = Math.min(cut, j==0?0:(dp[j-1]+1));
                }
            }
            dp[i]=cut;
        }
        return dp[n-1];
    }
}
