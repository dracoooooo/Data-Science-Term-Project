public class ContinuousSubarraySum {
    public boolean checkSubarraySum(int[] nums, int k) {
        Map<Integer, Boolean> map = new HashMap<>(); // memoization
        List<Integer> numbers = new ArrayList<Integer>();
		// accumulate the lat element of the array already
        numbers.add(nums[nums.length - 1]);
		// start at  the element before the last element because we are already using the last element.
        return check(nums, nums.length - 2, map, k, numbers);
    }

    boolean check(int[] nums, int ix, Map<Integer, Boolean> map, int k, List<Integer> sub) {
        if (ix < 0 ) {
            return false;
        }
		// calculate the accumulated result
        int result = 0;
        sub.add(nums[ix]);
        int n = sub.size();
        for (int i=0; i<n &&  n>=2;result += sub.get(i++));
        if (n >=2 && ((k != 0 && result % k ==0) || (k==0 && result == 0))) {
            map.put(result, true);
            return true;
        }
		
        if (map.containsKey(result)) {
            return map.get(result);
        }
		// no result so far so we have 3 options: 
		// 1) start with an empty array
		// 2) start from this element
		// 3) use the accumulated array
		// so there are 3 calls
        List<Integer> newList = new ArrayList<>();
        newList.add(nums[ix]);
        boolean found = check(nums, ix - 1, map, k, sub)
		        || check(nums, ix - 1, map, k, newList)
                || check(nums, ix - 1, map, k, new ArrayList<>());
        map.put(result, found);

        return map.get(result);
    }
}
