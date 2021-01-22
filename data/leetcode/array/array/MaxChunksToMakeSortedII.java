public class MaxChunksToMakeSortedII {
    public int maxChunksToSorted(int[] arr) {
        Map<Integer, Integer> count = new HashMap();
        List<Pair> counted = new ArrayList();
        for (int x: arr) {
            count.put(x, count.getOrDefault(x, 0) + 1);
            counted.add(new Pair(x, count.get(x)));
        }

        List<Pair> expect = new ArrayList(counted);
        Collections.sort(expect, (a, b) -> a.compare(b));

        Pair cur = counted.get(0);
        int ans = 0;
        for (int i = 0; i < arr.length; ++i) {
            Pair X = counted.get(i), Y = expect.get(i);
            if (X.compare(cur) > 0) cur = X;
            if (cur.compare(Y) == 0) ans++;
        }

        return ans;
    }
    class Pair {
        int val, count;
        Pair(int v, int c) {
            val = v; count = c;
        }
        int compare(Pair that) {
            return this.val != that.val ? this.val - that.val : this.count - that.count;
        }
    }
}
