public class WidestVerticalAreaBetweenTwoPointsContainingNoPoints {
    public int maxWidthOfVerticalArea(int[][] points) {
        
		int max = 0;   //to keep count of max width
        int[] sorted = new int[points.length];         //array to track points on X-axis

        for(int i = 0; i < points.length; i++){           //fill array with only x coordinate, since only width is concerning
            sorted[i] = points[i][0];
        }
		
        Arrays.sort(sorted);       //sort array
		
        for(int i = 1; i < points.length; i++){
            max = Math.max(max, Math.abs(sorted[i] - sorted[i-1]));   //find max difference between points
        }
        return max;
    }
}
