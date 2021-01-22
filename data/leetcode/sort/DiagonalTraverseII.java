public class DiagonalTraverseII {
    public int[] findDiagonalOrder(List<List<Integer>> nums) {
      
        int totalRowNum = nums.size();
        List<Value> values = new ArrayList<Value>();

        for(int i = 0; i < totalRowNum; i++) {
            if(nums.get(i).size() != 0){
                for(int j = 0; j < nums.get(i).size(); j++)
                    values.add(new Value(nums.get(i).get(j), i , j));
            }
        }
        Collections.sort(values);
        int[] result = new int[values.size()];
        
        for(int i = 0 ; i < values.size(); i++)
            result[i] = values.get(i).getValue();
        
        return result;
    }
    
    public class Value implements Comparable<Value>{
        int val;
        int column;
        int row;
        int sum;
        
        public Value(int val, int row, int column){
            this.sum = column + row;
            this.val = val;
            this.row = row;
            this.column = column;
        }
        
        public int getValue(){
            return this.val;
        }
        
        public int getSum(){
            return this.sum;
        }
        
        @Override
        public int compareTo(Value v) {
            if(this.sum < v.sum)
                return -1;
            else if(this.sum > v.sum)
                return 1;
            else {
                if(this.row > v.row)
                    return -1;
                else if(this.row < v.row)
                    return 1;
                else
                    return 0;
            }
        }
    }
}
