public class SortColors {
    public void sortColors(int[] nums) {
        int l=nums.length;
        int i=0,j=0,k=l-1,temp=0;
        while(j<=k){
            switch(nums[j]){
                case 0: temp=nums[i];
                        nums[i]=nums[j];
                        nums[j]=temp;
                        i++;
                        j++;
                        break;
                case 1: j++;
                        break;
                case 2: temp=nums[k];
                        nums[k]=nums[j];
                        nums[j]=temp;
                        k--; 
                        break;
                default: break;
                    
            }
        }
    }
}
