public class CanIWin {
    int maxChoosableInteger;
    int desiredTotal;
    HashMap<Integer, Boolean> hashMap;
    int OFFSET = 9; // 2^9 = 512 >= 300 
    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if (desiredTotal == 0)
            return true;
        if (maxChoosableInteger*(maxChoosableInteger+1)/2<desiredTotal)
            return false;
        
        hashMap = new HashMap<Integer, Boolean>();
        this.maxChoosableInteger = maxChoosableInteger;
        this.desiredTotal = desiredTotal;
        
        return dp(0, 0);
    }
    
    // you can also derive sum from profile but cba
    public boolean dp(int sum, int profile) {
        if (hashMap.get(profile)  != null)
            return hashMap.get(profile);
        
        boolean res = false;
        
        for (int i = OFFSET; i < OFFSET + maxChoosableInteger; i++) {
            if ((profile & (1 << i) ) != 0)
                continue;
            if (sum + i - OFFSET + 1 >= desiredTotal) {
                hashMap.put(profile, true);
                return true;
            }
            res = res || !dp(sum +i - OFFSET + 1, (profile | (1 << i)) + i - OFFSET + 1);
        }
        
        hashMap.put(profile, res);
        return res;
    }
}
