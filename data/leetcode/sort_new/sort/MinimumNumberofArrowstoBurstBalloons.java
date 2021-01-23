public class MinimumNumberofArrowstoBurstBalloons {
    public int findMinArrowShots(int[][] points) {
        if (points.length == 0)
            return 0;
   
   // method to sort the 2d array using Integer.compare()
   
      Arrays.sort(points, (a, b) -> Integer.compare(a[1] , b[1]));
   
       int arrow = 1;
       int end = points[0][1];
       for (int i = 1; i < points.length; i++) {
           if (end < points[i][0]) {
               arrow++;
               end = points[i][1];
           }
       }
       return arrow;
  }
}
