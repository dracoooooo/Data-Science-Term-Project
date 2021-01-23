public class MagneticForceBetweenTwoBalls {
    public int maxDistance(int[] p, int k) {
        Arrays.sort(p);
        int lo=1;int hi=p[p.length-1]-p[0];
        int mid=0;
        int ans=0;
        while(hi-lo>1){
            mid=lo+(hi-lo)/2;
            if(isPos(mid,p,k)){
                ans=Math.max(ans,mid);
                lo=mid;
            }
            else
                hi=mid;
        }
        if(isPos(hi,p,k))
            ans=hi;
        else if(isPos(lo,p,k))
            ans=lo;
        return ans;
    }
    public static boolean isPos(int dis,int p[],int balls){
        int bCount=1;int lstPlacem=p[0];
        int curDis=0;
        for(int i=1;i<p.length;i++){
            curDis=p[i]-lstPlacem;
            if(curDis>=dis){
                curDis=0;
                bCount++;
                lstPlacem=p[i];
            }
        }
        if(balls<=bCount){
            return true;
        }
        else
            return false;
    }
}
