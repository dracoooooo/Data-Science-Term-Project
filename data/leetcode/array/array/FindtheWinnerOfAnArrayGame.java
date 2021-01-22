public class FindtheWinnerOfAnArrayGame {
    public static int getWinner(int[] arr, int k) {
        int n = arr.length;

        int curFirst = arr[0], curCount = 0;
        for (int i = 1; i < n; i++) {
            if (arr[i] < curFirst) {
                if (++curCount == k) return curFirst;
            } else {
                curFirst = arr[i];
                curCount = 1;
                // hande k = 1 case
                if (curCount == k) return curFirst;
            }
        }
        //curFirst is max(arr[i]), no need to test k - curCount times
        return curFirst;
    }
    
}
