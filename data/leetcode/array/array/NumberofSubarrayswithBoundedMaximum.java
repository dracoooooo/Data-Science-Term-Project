public class NumberofSubarrayswithBoundedMaximum {
    public int numSubarrayBoundedMax(int[] A, int L, int R) {
        int n = A.length;
        int prev = 0, res = 0;
        for(int left = 0, right = 0; right < n; right++) {
            if(A[right] > R) {
                left = right + 1;
                prev = 0;
            } else {
                if(A[right] >= L) {
                    prev = right - left + 1;
                }
                res += prev;
            }
        }
        return res;
    }
}
