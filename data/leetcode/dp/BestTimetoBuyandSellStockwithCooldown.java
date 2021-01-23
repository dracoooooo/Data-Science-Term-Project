public class BestTimetoBuyandSellStockwithCooldown {
    public int maxProfit(int[] prices) {
        if(prices.length<=1){
            return 0;
        }
        int no_stocks_profit = 0;
        int hold_profit = -prices[0];
        int sold_profit = 0;
        
        for(int i=1;i<prices.length;i++){
        
            hold_profit = Math.max(hold_profit, no_stocks_profit-prices[i]);
            no_stocks_profit= Math.max(no_stocks_profit,sold_profit);          
            sold_profit = hold_profit+prices[i];
        }
        return Math.max(no_stocks_profit,sold_profit);
       
    }
}
