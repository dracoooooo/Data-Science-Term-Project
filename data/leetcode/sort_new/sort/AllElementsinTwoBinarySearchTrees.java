public class AllElementsinTwoBinarySearchTrees {
    public List<Integer> getAllElements(TreeNode root1, TreeNode root2) {

        List<Integer> result = new ArrayList<>();

        List<Integer> root1OrderedElements = new ArrayList<>();
        
        List<Integer> root2OrderedElements = new ArrayList<>();

        this.getOrderedElements(root1, root1OrderedElements);
        
        this.getOrderedElements(root2, root2OrderedElements);

        int i = 0, j = 0;

        while ((i < root1OrderedElements.size()) && (j < root2OrderedElements.size())){
            if (root1OrderedElements.get(i) < root2OrderedElements.get(j)){
                result.add(root1OrderedElements.get(i));
                i++;
            } else {
                result.add(root2OrderedElements.get(j));
                j++;
            }
        }

        if (i == root1OrderedElements.size()) {
            for (; j < root2OrderedElements.size(); j++){
                result.add(root2OrderedElements.get(j));
            }
        } else {
            for (; i < root1OrderedElements.size(); i++){
                result.add(root1OrderedElements.get(i));
            }
        }

        return result;

    }

    private void getOrderedElements(TreeNode root, List<Integer> orderedElements) {
        if (root == null) return;

        getOrderedElements(root.left, orderedElements);
        orderedElements.add(root.val);
        getOrderedElements(root.right, orderedElements);
    }
}
