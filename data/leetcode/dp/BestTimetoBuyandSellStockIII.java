public class BestTimetoBuyandSellStockIII {
    public int maxProfit(int[] prices) {


		//corner case 
		if(prices == null || prices.length == 0) return 0;

		int [][] dp = new int[prices.length + 1][5+1];

		for(int i = 1; i <= 5; i++){
			dp[0][i] = Integer.MIN_VALUE;
		}

		dp[0][1] = 0;

		for(int i = 1; i <= prices.length; i++){

			for(int j = 1; j <=5; j+=2){

				dp[i][j] = dp[i - 1][j];
				if(j > 1 && i > 1 && dp[i-1][j-1] != Integer.MIN_VALUE){
					dp[i][j] = Math.max(dp[i][j], dp[i-1][j-1] + prices[i-1] - prices[i-2]);
				}
			}

			for(int j = 2; j <= 5; j += 2){
				dp[i][j] = dp[i-1][j-1];
				if(j > 1 && i > 1 && dp[i-1][j] != Integer.MIN_VALUE){
					dp[i][j] = Math.max(dp[i][j], dp[i-1][j] + prices[i-1]- prices[i-2]);
				}
			}
		}

		int res = 0;
		for(int i = 1; i <= 5; i++){
			res = Math.max(res, dp[prices.length][i]);
		}

		return res;
	}
}
