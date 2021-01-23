public class DistinctSubsequences {
    public int numDistinct(String s, String t) {
        int n = s.length();       // length of String(s) in which sequence of String(t) is tobe counted
        int m = t.length();      //  length of target String(t) 
        
        long dp[][] = new long[n+1][1+m];
        
        for(int i = 0 ; i<=n ; i++)
            dp[i][0] = 1;           //base case: fisrt i chars in S will always match first 0 chars in t(target)
        
        for(int i = 1 ; i<n+1 ;i++)
        {
            for(int j = 1 ; j<m+1 ;j++)
            {
                if(s.charAt(i-1)==t.charAt(j-1))
                    dp[i][j] = dp[i-1][j]+dp[i-1][j-1];//not match+match
                else
                    dp[i][j] = dp[i-1][j];                   //not match
               
            }
           
        }
        return (int)dp[n][m];
    }
}
