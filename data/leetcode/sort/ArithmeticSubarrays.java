public class ArithmeticSubarrays {
    public List<Boolean> checkArithmeticSubarrays(int[] nums, int[] l, int[] r) {
        List<Boolean> result = new ArrayList<>();
        
        for(int i = 0; i < l.length; i++) {
            if(isArithmetic(Arrays.copyOfRange(nums, l[i], r[i]+1))) { 
                result.add(true);
            } else {
                result.add(false);
            }
        }
        
        return result;
    }
    
    private boolean isArithmetic(int[] nums) {
        Arrays.sort(nums);
        
        int diff = nums[1] - nums[0];
        for(int i = 2; i < nums.length; i++) {
            if(nums[i]-nums[i-1] != diff) {
                return false;
            }
        }
        
        return true;
    }
}
