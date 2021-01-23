public class OnesandZeroes {
    public int findMaxForm(String[] strs, int m, int n) {
        int[] zero=new int[strs.length];
        int[] one=new int[strs.length];
        
        for(int i=0;i<strs.length;i++){
            one[i]=0;
            zero[i]=0;
            for(int j=0;j<strs[i].length();j++){
                if(strs[i].charAt(j)=='1')one[i]++;
                else {
                    zero[i]++;
                }
            }
        }
        
        int[][] dp=new int[m+1][n+1];

        for(int i=0;i<strs.length;i++){
            for(int j=m;j>=zero[i];j--){
                for(int k=n;k>=one[i];k--){
                    dp[j][k]=Math.max(dp[j][k],1+dp[j-zero[i]][k-one[i]]);
                }
            }
        }
        
        return dp[m][n];
    }
}
