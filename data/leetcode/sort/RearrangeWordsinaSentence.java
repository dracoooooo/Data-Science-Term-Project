public class RearrangeWordsinaSentence {
    public String arrangeWords(String text) {
        String[] arr = text.split(" ");
        arr[0] = arr[0].substring(0,1).toLowerCase() + arr[0].substring(1);
        Arrays.sort(arr, (a,b)->(a.length()-b.length()));
        arr[0] = arr[0].substring(0,1).toUpperCase() + arr[0].substring(1);
        
        StringBuilder result = new StringBuilder(arr[0]);
        for(int i = 1; i < arr.length; i++) {
            result.append(" " + arr[i]);
        }
        
        return result.toString();
    }
}
