public class BurstBalloons {
    public int maxCoins(int[] nums) {
		return get_best_ans(nums);
	}
	private int get_best_ans(int []nums){
		int [][]dp=new int[nums.length+2][nums.length+2];

		for(int len=1;len<=nums.length;len++){
			for(int start=1;start<=nums.length-len+1;start++){
				for(int end=start;end<start+len;end++){
					int left=dp[start][end-1], right=dp[end+1][start+len-1];
					int last=nums[end-1];
					if(start-2>=0)  last*=nums[start-2];
					if(start+len-1<nums.length)   last*=nums[start+len-1];
					dp[start][start+len-1]=Math.max(dp[start][start+len-1],left+last+right);
				}
			}
		}
		return dp[1][nums.length];
	}
}
