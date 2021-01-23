public class MinimumDeletionstoMakeCharacterFrequenciesUnique {
    public int minDeletions(String s) {
        int freq[] = new int[26];
        for(char ch: s.toCharArray()) {
            freq[ch - 'a']++;
        }
        PriorityQueue<Integer> q = new PriorityQueue<>(new Comparator<>(){
            public int compare(Integer x, Integer y) {
                return y.compareTo(x);
            }
        });
        for(int i = 0; i < 26; i++) {
            if(freq[i] == 0) continue;
            q.add(freq[i]);
        }
        int ans = 0;
        while(!q.isEmpty()) {
            int now = q.poll();
            if(q.peek() != null && q.peek() == now) {
                ans++;
                if(now > 1) q.add(now - 1);
            }
        }
        return ans;
    }
}
