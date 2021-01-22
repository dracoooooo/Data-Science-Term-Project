public class RemoveCoveredIntervals {
    public int removeCoveredIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a,b)->
            (a[0]==b[0] ? b[1]-a[1] : a[0]-b[0])
        );
        
        int count = 1;
        int a = intervals[0][0], b = intervals[0][1];
        for(int i = 1; i < intervals.length; i++) {
            if(a <= intervals[i][0] && intervals[i][1] <= b) {
                continue;
            } else {
                a = intervals[i][0];
                b = intervals[i][1];
                count++;
            }
        }
        
        return count;
    }
}
