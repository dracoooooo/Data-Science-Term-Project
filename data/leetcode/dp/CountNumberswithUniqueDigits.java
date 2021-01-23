public class CountNumberswithUniqueDigits {
    public int countNumbersWithUniqueDigits(int n) {
		if(n==0)    return 1;
		int ans=10,t=9;
		for(int i=0;i<n-1;i++){
			t*=(9-i);
			ans+=t;
		}
		return ans;
	}
}
