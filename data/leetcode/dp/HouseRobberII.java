public class HouseRobberII {
    public int rob(int[] nums, int start, int end) {
        int included = 0, excluded = 0;
        
        for(int idx = start; idx <= end; idx++) {
            int i = included;
            int e = excluded;
            
            included = nums[idx] + e;
            excluded = Math.max(i, e);
        }
        
        return Math.max(included, excluded);
    }
    
    public int rob(int[] nums) {
        if(nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        if(n == 1) {
            return nums[0];
        }
        else if(n == 2) {
            return Math.max(nums[0], nums[1]);
        }
        
        return Math.max(
            rob(nums, 0, n-2),
            rob(nums, 1, n-1)
        );
    }
}
