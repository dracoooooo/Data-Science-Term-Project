public class FrogJump {
    public boolean canCross(int[] stones) {
        Map<Integer, Set<Integer>> posWithJumps = new HashMap<>();
        // hashmap entry {5, <2, 3>} means the position x=5 can be reached on a jump of stretch 2 or 3
        
        posWithJumps.computeIfAbsent(0, k -> new HashSet<Integer>(){{add(0);}});
        
        for(int i = 1; i < stones.length; i++) {
            // jump onto this stone from the previous stones
            posWithJumps.computeIfAbsent(stones[i], k -> new HashSet<Integer>());
            for(int j = 0; j < i; j++) {
                int requiredJump = stones[i] - stones[j];
                for(int jump: new int[]{requiredJump - 1, requiredJump, requiredJump + 1}) {                    
                    if(posWithJumps.get(stones[j]).contains(jump)) {
                        posWithJumps.get(stones[i]).add(requiredJump);
                    }
                }
            }
        }
        
        // whether the last stone's x position can be reached on a jump of any stretch
        return !posWithJumps.get(stones[stones.length - 1]).isEmpty();
    }
}
