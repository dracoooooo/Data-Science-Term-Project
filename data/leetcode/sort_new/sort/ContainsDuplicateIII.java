public class ContainsDuplicateIII {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        int size=nums.length;
        Node[] arr=new Node[size];
        for(int i=0;i<size;i++){
            arr[i]=new Node(nums[i],i);
        }
        Arrays.sort(arr,(n1,n2)->Integer.compare(n1.value,n2.value));
        for(int i=0;i<size;i++){
            long num=arr[i].value;            
            for(int j=i+1;j<size;j++){
                long temp=Math.abs(num-arr[j].value);
                if(temp>t)break;
                if(Math.abs(arr[i].index-arr[j].index)<=k)
                    return true;
        }}
        return false;
    }
    private class Node{
        int index;
        int value;
        public Node(int v,int i){
            index=i;
            value=v;
        }
    }
}
