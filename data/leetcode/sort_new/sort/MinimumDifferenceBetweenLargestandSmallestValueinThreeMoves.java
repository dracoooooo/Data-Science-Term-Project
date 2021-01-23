public class MinimumDifferenceBetweenLargestandSmallestValueinThreeMoves {
    public static int minDifference(int[] nums) {
        if (nums.length < 4) return 0;
        Arrays.sort(nums);
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int min = nums[0];
            int j = nums.length - 3 - 1;
            if (nums.length - 3 <= i) {
                j = 3 - (nums.length - 1 - i);
                min = nums[i];
            }
            int max = nums[j];
            res = Math.min(res, Math.abs(max - min));
        }
        return res;
    }
}
