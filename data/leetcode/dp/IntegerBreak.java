public class IntegerBreak {
    int dp[];
    public int integerBreak(int n) {
        dp=new int[n+1];
        return breaker(n,n);
    }
    
    public int breaker(int n,int real){
    	if(n<0) {
    		return Integer.MIN_VALUE;
    	}
    	if(dp[n]!=0)return dp[n];
        if(n==0){
            return 1;
        }
        int prod=0;
        for(int i=1;i<=n;i++){
            if(i!=real)
            prod=Math.max(breaker(n-i,real)*i,prod);
        }
        dp[n]=prod;
        return prod;
    }
}
