public class SellDiminishingValuedColoredBalls {
    public int maxProfit(int[] inventory, int orders) {
        int mod = (int) 1e9+7;
        Arrays.sort(inventory);
        List<int[]> ranges = new ArrayList<>();
        int left = 1;
        int index = 0;
        while (index < inventory.length) { // create range entry [left, right, size]
            ranges.add(new int[] {left, inventory[index], inventory.length - index});
            left = inventory[index] + 1;
            index++;
        }
        index--; // move last index pointer to last index in array
        long res = 0;
        while (orders > 0 && index >= 0) {
            int[] range = ranges.get(index);
            int size = range[2];
            int num = (size * (range[1] - range[0] + 1));
            long val = 0l;
            if (num >= orders) { // if we can find the finish the order in this iteration
                int newLeft = range[1] - (orders/size) + 1; // finding the left bound of this new range [newLeft, right] where newLeft > left
                long extra = (((long) (orders%size) * (newLeft-1)) % mod); // sum of the values not include in the above range
                long s = (sum(newLeft, range[1]) % mod);
                s = ((s * size) % mod);
                s = ((s + extra) % mod);
                val = s;
            } else { // satisfy as much order as we can
                val = sum(range[0], range[1]) % mod; 
                val = (val * size) % mod;
            }

            
            res = (res + val) % mod;
            orders -= num;
            index--;
        }
        return (int) res % mod;
    }
    
    
    private long sum(int left, int right) {
        long sum = left + right;
        int size = right - left + 1;
        long res = sum * (size/2);
        if (size % 2 == 1) {
            res += (sum/2);
        }
        return res;
    }
    
}
