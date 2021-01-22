public class CanMakeArithmeticProgressionFromSequence {
    public boolean canMakeArithmeticProgression(int[] arr) {
        Arrays.sort(arr);
        
        int diff = Math.abs(arr[1]-arr[0]);
        for(int i = 2; i < arr.length; i++){
            if(diff != Math.abs(arr[i]-arr[i-1])){
                return false;
            }
        }
               
        return true;
    }
}
