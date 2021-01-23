public class MaximumPerformanceofaTeam {
    public int maxPerformance(int n, int[] speed, int[] efficiency, int k) {
        
    
        List<Engineer> engineers = new ArrayList<>();
        
        for(int i = 0; i < n; i++)
            engineers.add(new Engineer(speed[i], efficiency[i]));
        
        
        engineers.sort((a, b) -> b.efficiency - a.efficiency);
        
        //will keep slowest members at the top, fastest ones stay in the team longest
        PriorityQueue<Engineer> currentTeam = new PriorityQueue<>((a, b) -> a.speed - b.speed);
        
        //now iterate the engineers by most efficient to least, and add them to the currentTeam
        
        long teamSpeed = 0;
        long maxPerformance = Long.MIN_VALUE;
        
        for(Engineer newHire : engineers){
            
            
            if(currentTeam.size() == k){
                
                
                Engineer slowGuy = currentTeam.poll();
                
                //System.out.println("slowGuy: " + slowGuy.efficiency + " " + slowGuy.speed);
                //now we must remove the slow guys speed from our total speed
                
                teamSpeed -= slowGuy.speed;
            }
            
            currentTeam.add(newHire);
            
            teamSpeed += newHire.speed;
            

            long performanceWithNewGuy = teamSpeed * (long) newHire.efficiency;
            
            //check wether the performance has been imporved br bringing on the new hire
            maxPerformance = Math.max(maxPerformance, performanceWithNewGuy);
        }
        
       return (int) (maxPerformance % MOD);
    }
    
    int MOD = (int) (1e9 + 7);
    
    class Engineer{
        
        int speed;
        int efficiency;
        
        public Engineer(int speed, int efficiency){
            
            this.speed = speed;
            this.efficiency = efficiency;
        }
    }
}
