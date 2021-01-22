public class ThekStrongestValuesinanArray {
    public int[] getStrongest(int[] arr, int k) {
        Arrays.sort(arr);
        final int L = arr.length;
        
        final int median = arr[(L - 1) / 2];
        
        return Arrays.copyOfRange(Arrays.stream(arr).boxed().sorted((a, b) ->{
            int x = Math.abs(a - median), y = Math.abs(b - median);
            return x == y ? b - a : y - x;
        }).mapToInt(i -> i).toArray(), 0, k);
    }
}
