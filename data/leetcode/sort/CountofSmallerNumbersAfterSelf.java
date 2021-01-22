public class CountofSmallerNumbersAfterSelf {
    public List<Integer> countSmaller(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new ArrayList<>();
        }
        
        // indexArray contains indices in original array and
        // it will be sorted by corresponding number in the original array
        int[] indexArray = initializeArray(nums);
    
        // countArray is actually the return array
        int[] countArray = new int[nums.length];
        
        // helperArray is the helper for merge sort
        int[] helper = new int[nums.length];
        
        mergeSort(nums, 0, nums.length - 1, indexArray, countArray, helper);
        
        return convertToList(countArray);
    }
    
    private List<Integer> convertToList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int ele : array) {
            list.add(Integer.valueOf(ele));
        }
        return list;
    }
    
    private int[] initializeArray(int[] nums) {
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = i;
        }
        return res;
    }
    
    private void mergeSort(int[] nums, int left, int right, int[] indexArray,
                           int[] countArray, int[] helper) {
        if (left >= right) return;
        
        int mid = left + (right - left) / 2;
        mergeSort(nums, left, mid, indexArray, countArray, helper);
        mergeSort(nums, mid + 1, right, indexArray, countArray, helper);
        
        merge(nums, left, mid, right, indexArray, countArray, helper);
    }
    
    private void merge(int[] nums, int left, int mid, int right, int[] indexArray, 
                      int[] countArray, int[] helper) {
        copyIndex(indexArray, left, right, helper);
        int ls = left, rs = mid + 1, cur = left;
        
        while (ls < mid + 1) {
            if (rs > right || nums[helper[ls]] <= nums[helper[rs]]) {
                countArray[helper[ls]] += (rs - (mid + 1));
				
                indexArray[cur++] = helper[ls++];
            } else {
                indexArray[cur++] = helper[rs++];
            }
        }
    }
    
    private void copyIndex(int[] indexArray, int left, int right, int[] helper) {
        for (int i = left; i <= right; i++) {
            helper[i] = indexArray[i];
        }
    }
}
