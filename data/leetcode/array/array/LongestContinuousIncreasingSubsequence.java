public class LongestContinuousIncreasingSubsequence {
    public int findLengthOfLCIS(int[] nums) {
        int n=0;
        int max=0;
        for(int i=0;i<nums.length;i++){
            int next=(i==nums.length-1)?Integer.MAX_VALUE:nums[i+1];
            if(nums[i]<next){
                n+=1;
            }
            else{
                n+=1;
                if(n>max){
                    max=n;
                }
                n=0;
            }
        }
        return (max>n)?max:n;
    }
}
