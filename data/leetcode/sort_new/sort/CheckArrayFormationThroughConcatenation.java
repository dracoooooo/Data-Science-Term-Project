public class CheckArrayFormationThroughConcatenation {
    public boolean canFormArray(int[] arr, int[][] pieces) {
        int i = 0;
        while (i < arr.length) {
            int v = arr[i];
            int[] p = find(v, pieces);
            if (p == null) return false;
            int k = 0;
            while (k < p.length && arr[i++] == p[k++]);
            if (k != p.length) return false;
        }
        return true;
    }
    
    private int[] find(int v, int[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            if (v == pieces[i][0]) return pieces[i];
        }
        return null;
    }
}
