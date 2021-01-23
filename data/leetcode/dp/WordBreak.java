public class WordBreak {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> set=new HashSet<>(wordDict);
        int maxLength=0;
        
        for(String word:wordDict){
            maxLength=Math.max(maxLength,word.length());
        }
    
        boolean[] dp=new boolean[s.length()+1];
        dp[0]=true;
        
        for(int i=0;i<s.length();i++){
            int j=i;
            while(j>=0 && j>i-maxLength){
                if(set.contains(s.substring(j,i+1)) && dp[j]){
                    dp[i+1]=true;
                }
                j--;
            }
        }
        
        return dp[s.length()];
        
    }
}
