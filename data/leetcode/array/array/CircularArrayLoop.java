public class CircularArrayLoop {
    public boolean circularArrayLoop(int[] arr) {
        HashMap<Integer,Integer>map=new HashMap<Integer,Integer>();
        for(int i=0;i<arr.length;i++){
            int h=arr[i];
            int si=i;
            if(!map.containsKey(si)){//not previously visited
                HashMap<Integer,Integer> cmap=new HashMap<Integer,Integer>();
                boolean ans=true;
                int ci=si; //current index
                int previ=si;//prev index
                while(!map.containsKey(ci)&&!cmap.containsKey(ci)){
                   if(h*arr[ci]<=0)
                       break;
                    cmap.put(ci,1);
                   previ=ci;//mark previous index
                    ci=ci+arr[ci];
                    if(ci>=0)
                        ci=ci%arr.length;
                    else{
                        int steps=(ci*-1)-1;
                        ci=(arr.length-1)-(steps%arr.length);
                    }
                }
                if(cmap.containsKey(ci)){
                    if(previ!=ci) //cycle found
                        return(true);
                }
                else { //put all keys in the map
                   for(Integer key:cmap.keySet()){
                       map.put(key,1);
                   } 
                }
            }
        }
        return(false);//no cycle
    }//circularArrayLoop
 
}
