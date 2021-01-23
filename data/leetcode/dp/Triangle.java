public class Triangle {
    public int minimumTotal(List<List<Integer>> triangle) {
        int[][] dp = new int[triangle.size()][triangle.size()];
for(int i=0; i<dp[0].length;i++)
    Arrays.fill(dp[i], 0);
 dp[0][0] = triangle.get(0).get(0);

for(int i=1; i< triangle.size();i++) {
    List<Integer> row = triangle.get(i);
    for(int j=0;j<row.size();j++) {
         if(j==0) {
             dp[i][j] = row.get(0) + dp[i-1][j];
         } else if (j==row.size()-1) {
             dp[i][j] = row.get(row.size()-1) + dp[i-1][j-1];
         } else {
             dp[i][j] = row.get(j) + Math.min(dp[i-1][j-1], dp[i-1][j]);
         }
    }
}
return Arrays.stream(dp[triangle.size()-1]).min().getAsInt();
}
}
