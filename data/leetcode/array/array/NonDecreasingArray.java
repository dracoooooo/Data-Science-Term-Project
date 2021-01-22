public class NonDecreasingArray {
    public boolean checkPossibility(int[] nums) {
        // Array of 2 is always true
        if (nums.length <= 2) {
            return true;
        }

        boolean beenModified = false;

        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                if (beenModified) {
                    return false;
                }
                beenModified = true;
                if (i == 0) {
                    nums[i] = nums[i + 1];
                } else {
                    if (nums[i - 1] <= nums[i + 1]) {
                        nums[i] = nums[i - 1];
                    } else {
                        nums[i + 1] = nums[i];
                    }
                }
            }
        }
        return true;
    }
}
