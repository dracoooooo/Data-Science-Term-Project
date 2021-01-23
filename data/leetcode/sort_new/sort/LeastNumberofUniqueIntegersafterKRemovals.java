public class LeastNumberofUniqueIntegersafterKRemovals {
    public int findLeastNumOfUniqueInts(int[] arr, int k) {
        if (arr.length < k)
            return 0;
        int count = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int number: arr) {
            if (map.containsKey(number))                     // whole statement similar to map.getOrDefault(n, 0) + 1;
                map.replace(number, map.get(number) + 1);
            else 
                map.put(number, 1);
        }
        List<Integer> list = new ArrayList<>(map.keySet());
        Collections.sort(list, (a, b) -> map.get(a) - map.get(b));
        int i=0;
        while (k>0 && i<map.size()) {
            k -= map.get(list.get(i));
            if (k >= 0)
                count += 1;
            i++;
        }
        
        
        return map.size() - count;
    }
}
