public class SortArraybyIncreasingFrequency {
    public int[] frequencySort(int[] nums) {
        int[] counts = new int[201];
        PriorityQueue<int[]> pq = new PriorityQueue<>(new Comparator<int[]>(){
            @Override
            public int compare(int[] a, int[] b){
                if(a[1] == b[1]){
                    return b[0] - a[0];
                } else {
                    return a[1] - b[1];
                }
            }
        });
        for(int num : nums){
            counts[num+100]++;
        }
        for(int i=0; i<counts.length; i++){
            if(counts[i] > 0){
                pq.offer(new int[]{i-100, counts[i]});
            }
        }
        int idx = 0;
        while(!pq.isEmpty()){
            int[] info = pq.poll();
            Arrays.fill(nums, idx, idx+info[1], info[0]);
            idx += info[1];
        }
        return nums;
    }
}
