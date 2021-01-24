import java.util.ArrayList;
import java.util.Comparator;

public class solution {
    public int[] visited;
    public int[] visiting;
    public int[][] AdjacencyMatrix;
    public ArrayList<ArrayList<Integer>> save;

    public boolean isSame(ArrayList<Integer> A, ArrayList<Integer> B){
        if(A.size() != B.size()) return false;
        for(int i = 0; i < A.size(); ++i){
            if(!A.get(i).equals(B.get(i))) return false;
        }
        return true;
    }

    public boolean isNovel(ArrayList<Integer> queue){
        ArrayList<Integer> tmp = (ArrayList<Integer>) queue.clone();
        tmp.remove(tmp.size() - 1);
        tmp.sort(Comparator.naturalOrder());
        for(int i = 0; i < save.size(); ++i){
            if(isSame(tmp, save.get(i))) return false;
        }
        save.add(tmp);
        return true;
    }

    public void dfsWrapper(int[][] AdjacencyMatrix){
        int len = AdjacencyMatrix.length;
        this.AdjacencyMatrix = AdjacencyMatrix;
        this.visited = new int[len];
        this.visiting = new int[len];
        this.save = new ArrayList<>();
        for(int i = 0; i < len; ++i){
            dfs(i, new ArrayList<Integer>(0));
            visiting = new int[len];
        }
    }

    public void dfs(int current, ArrayList<Integer> queue){
        queue.add(current);
        if(visiting[current] == 1){
            String res = "";
//            判断环的起点
            boolean isStart = false;
            ArrayList<Integer> Circle = new ArrayList<>();
            for(int i = 0; i < queue.size(); ++i){
                if(queue.get(i) == current) isStart = true;
                if(isStart) {
                    res += queue.get(i) + " ";
                    Circle.add(queue.get(i));
                }
            }
            if(isNovel(Circle)) {
                System.out.println(res);
            }
            queue.remove(queue.size() - 1);

            return;
        }

        visiting[current] = 1;
        for(int i = 0; i < AdjacencyMatrix[current].length; ++i){
            if(AdjacencyMatrix[current][i] == 1 && i != current) dfs(i, queue);
        }
        visiting[current] = 0;
        queue.remove(queue.size() - 1);
        return;
    }

    public static void main(String[] args){
        solution s = new solution();
        int[][] adj = new int[][]{{1,1,0,1},{0,1,1,0},{1,0,1,0},{1,0,0,1}};

        s.dfsWrapper(adj);
    }
}
