public class MinimumSubsequenceinNonIncreasingOrder {
    public List<Integer> minSubsequence(int[] nums) {
        List<Integer>list=new ArrayList<Integer>();
        Arrays.sort(nums);
        int sum=0,sum1=0;
        for(int a:nums)
        {
            sum+=a;
        }
        int i=nums.length-1;
        while(i>=0)
        {
            sum=sum-nums[i];
            sum1+=nums[i];
            list.add(nums[i]);
            
            if(sum1>sum)
                return list;            
            i--;
        }
        return list;
    }
}
