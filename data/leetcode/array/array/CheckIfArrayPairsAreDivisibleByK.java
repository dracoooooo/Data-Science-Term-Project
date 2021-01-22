public class CheckIfArrayPairsAreDivisibleByK {
    public boolean canArrange(int[] arr, int k) {
        int[] freq = new int[k];
        
        for(int num : arr) {
            
            num%=k;
            if(num<0)
                num+=k;
            freq[num]++;
        }
        if(freq[0]%2!=0)
            return false;
        for(int i=1;i<=k/2;i++) {
            if(freq[i] != freq[k-i])
                return false;
        }
        return true;
    }
    
}
