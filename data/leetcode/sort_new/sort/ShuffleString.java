public class ShuffleString {
    public String restoreString(String s, int[] indices) {
        char[] arr = new char[s.length()];
        int idx = 0;

        for (Character ch : s.toCharArray()) {
            arr[indices[idx++]] = ch;
        }

        return new String(arr);
    }
}
