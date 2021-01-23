public class SortIntegersByThePowerValue {
    public int getKth(int lo, int hi, int k) {
        Queue<int[]> queue = new PriorityQueue<>((a,b) -> (a[1] != b[1]) ? a[1]-b[1] : a[0]-b[0]); 
        for(int i=lo;i<=hi;i++){
            int curr = i;
            int count=0;
            while(curr != 1){
				  curr = (curr%2==0) ? curr/2 : curr*3+1;
                count++;
            }
            queue.add(new int[]{i,count});
        }
        for(int i=1;i<k;i++){
            queue.remove();
        }
        return queue.poll()[0];
    }
}
