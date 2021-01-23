public class SortIntegersbyTheNumberof1Bits {
    public int countBits(int n) {
        int sum = 0;
        while(n != 0) {
            if(n % 2 == 1) {
                sum++;
            }
            n /= 2;
        }
        return sum;
    }
    public int[] sortByBits(int[] arr) {
        Arrays.sort(arr);
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < arr.length - 1; j++) {
                if(countBits(arr[j]) > countBits(arr[j + 1])) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
                
            }
        }
        return arr;
    }
}
