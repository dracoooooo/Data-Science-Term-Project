public class ReversePairs {
    private int merge(int[] A, int l, int m, int r, int[] M) {
        int i = l, t = l, k = 0, res = 0;
        for(int j = m + 1; j <= r; j++) {
            while(t <= m && A[t] <= 2L * A[j]) t++;
            res += m - t + 1;
            while(i <= m && A[i] <= A[j]) M[k++] = A[i++];
            M[k++] = A[j];
        }
        while(i <= m) M[k++] = A[i++];
        for(i = l; i <= r; i++) A[i] = M[i - l];
        return res;
    }
    private int mergeSort(int[] A, int l, int r, int[] M) {
        if(l >= r) return 0;
        int m = l + ((r-l) >> 1);
        return mergeSort(A, l, m, M) + mergeSort(A, m + 1, r, M) + merge(A, l, m, r, M);
    }
    public int reversePairs(int[] nums) {
        return mergeSort(nums, 0, nums.length - 1, new int[nums.length]);
    }
}
