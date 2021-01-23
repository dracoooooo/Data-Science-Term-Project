public class CountofRangeSum {
    public int countRangeSum(int[] nums, int lower, int upper) {
        if (nums == null || nums.length == 0) return 0;
    
        long[] preSums = new long[nums.length + 1];  
        for (int i = 1; i < preSums.length; ++i) preSums[i] = preSums[i - 1] + nums[i - 1]; 
        
        int[] result = new int[1];  
        mergeSort(result, preSums, 0, preSums.length - 1, lower, upper);
        return result[0];
    }
    
    private long[] mergeSort(int[] result, long[] preSums, int l, int r, int lower, int upper) {
        if (l == r) return new long[]{preSums[l]};
        
        int mid = l + (r - l) / 2;
        long[] left = mergeSort(result, preSums, l, mid, lower, upper);
        long[] right = mergeSort(result, preSums, mid + 1, r, lower, upper);
    
        long[] merged = new long[left.length + right.length];
        for (int i = 0, j = 0, start = 0, end = 0; i < left.length || j < right.length;) {
            if (j == right.length || (i < left.length && left[i] <= right[j])) {
                while (start < right.length && right[start] - left[i] < lower) start++;
                while (end < right.length && right[end] - left[i] <= upper) end++;    
                result[0] += end - start;  
                merged[i + j] = left[i++];
            } else {
                merged[i + j] = right[j++];
            }
        }
        return merged;
    }
}
