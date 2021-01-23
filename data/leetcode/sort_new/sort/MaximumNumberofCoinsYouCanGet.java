public class MaximumNumberofCoinsYouCanGet {
    public int maxCoins(int[] piles) {
        // sort the given array 
        Arrays.sort(piles); 
     
      
        int sum = 0;
        for(int i = 1; i <= piles.length/3; i++){
      
          sum += piles[piles.length - 2*i ];
        }
      
        return sum;
        
        
    }
}
