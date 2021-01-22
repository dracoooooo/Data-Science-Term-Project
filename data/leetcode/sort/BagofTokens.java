public class BagofTokens {
    public int bagOfTokensScore(int[] tokens, int P) {
        if(tokens == null || tokens.length == 0) return 0;
        Arrays.sort(tokens);
        int ans = 0;
        for(int i = 0, j = tokens.length -1;i <= j;){
            if(ans == 0 && P < tokens[i]) return 0;
            //If you are powerful, get whatever you can. :-)
            while(i< tokens.length && P >= tokens[i]){ 
                ans++;
                P -= tokens[i];
                i++;
            }
            //Don't spend to the last penny. Keep it. 
            if(i >= j) return ans;
            //Be more powerful.            
            if(i < j && ans > 0){                     
                P += tokens[j];
                j--;
                ans--;
            }
        }
        return ans;
    }
}
