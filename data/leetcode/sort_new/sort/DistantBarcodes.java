public class DistantBarcodes {
    public int[] rearrangeBarcodes(int[] barcodes) {
        if(barcodes.length < 2){
            return barcodes;
        }
        
        PriorityQueue<int[]> pq = new PriorityQueue(new Comparator(){
            public int compare(Object obj1, Object obj2){
                int[] a = (int[])obj1;
                int[] b = (int[])obj2;
                return b[0] - a[0];
            }
        });
        
        // sort array to get the same number together
        Arrays.sort(barcodes);
        
        
        int[] tmp = new int[2];
        tmp[1] = barcodes[0];
        tmp[0] = 1;
        // get all information store at queue
        for(int i = 1; i < barcodes.length; ++i){
            if(tmp[1] == barcodes[i]){
                tmp[0]++;
            }
            else{
                pq.add(tmp);
                tmp = new int[2];
                tmp[1] = barcodes[i];
                tmp[0] = 1;
            }
        }
        pq.add(tmp);
        
        int start = pq.peek()[0];
        int index;
        //find the index based on case
        if(start > barcodes.length / 2){
            index = 0;
        }
        else{
            index = 1;
        }
        
        // iterate queue to get result
        while(!pq.isEmpty()){
            tmp = pq.poll();
            for(int i = 0; i < tmp[0]; ++i){
                barcodes[index] = tmp[1];
                index += 2;
                if(index >= barcodes.length && start > barcodes.length / 2){
                    index = 1;
                }
                else if(index >= barcodes.length){
                    index = 0;
                }
            }
        }
        
        return barcodes;
        
    }
}
