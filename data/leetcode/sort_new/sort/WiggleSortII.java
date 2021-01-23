public class WiggleSortII {
    public void wiggleSort(int[] nums) {
        Arrays.sort(nums);
        
        // find mid point of array
        int mid = (nums.length - 1 ) / 2;
        int right = nums.length - 1;
        
        int[] result = new int[right + 1];
        int counter = 0;
        
        // select values from two parts of the array and arrange them in aux array
        while(mid >= 0 || right > (nums.length - 1 ) / 2 ){
            
            if(counter % 2 == 0){
                result[counter] = nums[mid];
                mid--;
            } else {
                result[counter] = nums[right];
                right--;
            }
            
            counter++;
        }
        
        for(int i = 0; i < nums.length; i++){
            nums[i] = result[i];
        }
    }
}
