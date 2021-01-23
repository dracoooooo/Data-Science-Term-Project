public class DungeonGame {
    public int calculateMinimumHP(int[][] dungeon) {

        int[][] dp = new int[dungeon.length][dungeon[0].length];
        for (int[] row : dp) // populate dp array
            Arrays.fill(row, Integer.MAX_VALUE);
        
        return findPath(dungeon, dp, 0, 0); // begin recursive calls
        
    }
    
    private int findPath(int[][] dungeon, int[][] dp, int r, int c) {
        
        int R = dungeon.length;
        int C = dungeon[0].length;
        
        if (r >= R || c >= C) return Integer.MAX_VALUE; // boundary check
        if (r == R-1 && c == C-1) return ( dungeon[r][c] <= 0 ) ? 1-dungeon[r][c] : 1; // if we reached the princess
        if (dp[r][c] != Integer.MAX_VALUE) return dp[r][c]; // if the path value is already stored in dp
        
        int ifRight = findPath(dungeon, dp, r, c+1); // if the min valued path is to the right
        int ifDown = findPath(dungeon, dp, r+1, c); // if the min valued path is down
        int minHealth = Math.min(ifRight, ifDown) - dungeon[r][c]; // min health required for health to be 1 (> 0)
        dp[r][c] = ( minHealth <= 0 ) ? 1 : minHealth; // assign to dp
        return dp[r][c]; 
        
    }
}
