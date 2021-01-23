public class WordBreakII {
    public List<String> wordBreak(String s, Set<String> wordDict) {
        //dp array only save the index
        ArrayList<ArrayList<Integer>> dp = new ArrayList<ArrayList<Integer>>();
        //initialize dp array, give it first value not null.
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(-1);
        dp.add(list);
        
        for(int j = 1; j<=s.length(); j++) {
            list = new ArrayList<Integer>();
            for(int i= 0; i<j; i++) {
                if(dp.get(i).size() != 0 && wordDict.contains(s.substring(i, j))) {
                    list.add(i);
                } 
            }
            dp.add(list);
        }
        
        List<String> result = generateSegments(dp, dp.size() -1, s);
        return result;
    }
    
    //use the saved index to construct the sentence
    public List<String> generateSegments(ArrayList<ArrayList<Integer>> segArr, int index, String s){
        List<String> result = new ArrayList<String>();
        if(index == 0) {
            result.add("");
            return result;
        }
                
        ArrayList<Integer> arr = segArr.get(index);
        for(Integer i: arr){
            List<String> prevStrings = generateSegments(segArr, i, s);
            for(String string: prevStrings){
                
                string = (string.length() == 0? s.substring(i,index) : string + " " + s.substring(i,index));
                result.add(string);
            }
        }
        return result;
    }
}
