public class IntersectionOfTwoArraysII {
    public int[] intersect(int[] nums1, int[] nums2) {
        HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        ArrayList<Integer> arr = new ArrayList<Integer>();
        
        for(int itr = 0; itr < nums1.length; itr++){
            if (temp.containsKey(nums1[itr]))              { 
                temp.put(nums1[itr], temp.get(nums1[itr]) + 1); 
            }  
            else{ 
                temp.put(nums1[itr], 1); 
            } 
        }
        
        for(int itr = 0; itr < nums2.length; itr++){
            if(temp.containsKey(nums2[itr])){
                int n = temp.get(nums2[itr]) - 1;
                if(n >= 0){
                    temp.put(nums2[itr], temp.get(nums2[itr]) - 1); 
                     arr.add(nums2[itr]);
                }
            }
        }
        
        int[] res = new int[arr.size()];
        for(int itr = 0; itr < arr.size(); itr++){
            res[itr] = arr.get(itr);
        }
        return res;       
    }   
}
