public class FindInMountainArray {
    public int findInMountainArray(int t, MountainArray a) {
        int peak=findPeak(a);//Devide array in two parts from Peak element and check them both 
       int result= binarySearch(t,a,0,peak,true);
         if(result==-1){
             result=binarySearch(t,a,peak+1,a.length()-1,false);
         }
        return result; 
     }
     
     public static int findPeak(MountainArray a){
         int l=0,r=a.length()-1;
         while(l<r){
             int mid=l+(r-l)/2;
             if(a.get(mid)<a.get(mid+1))
                 l=mid+1;
             else r=mid;
         }
         return l;
         
     }
     public static  int binarySearch(int t ,MountainArray a,int l,int r,boolean increasing){
         while(l<=r){
             int mid=l+(r-l)/2;
              if(t==a.get(mid))return mid;
 
             if(increasing){
                  if(a.get(mid)<t)
                     l=mid+1;
                 else r=mid-1;
                     
             }else {
                  if(a.get(mid)<t)
                     r=mid-1;
                 else l=mid+1;
 
             }
         }
         return -1;
     }
}
