public class RelativeSortArray {
    public int[] relativeSortArray(int[] arr1, int[] arr2) {
        
        //declaring the frequencyArr and result for storing the final result 
        int frequencyArr[]=new int[1001];
        int result[]=new int[arr1.length];
        
        //calculating the frequency of every element in arr1
        for(int e:arr1){
            frequencyArr[e]++;
        }
        //declaring the counter as j
        int j=0;
        
        //storing the value into result array according to arr2
        for(int e:arr2){
            while(frequencyArr[e]>0){
                result[j++]=e;
                frequencyArr[e]--;
            }
        }
        
        //storing the remaining element 
        for(int i=0;i<frequencyArr.length;i++){
            while(frequencyArr[i]>0){
                result[j++]=i;
                frequencyArr[i]--;
            }
            
        }
        
        //returning the result
        return result;
        
    }
}
