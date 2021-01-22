public class NumberofMatchingSubsequences {
    public int numMatchingSubseq(String S, String[] words) {
        int result = 0;
        Map<String, Boolean> memo = new HashMap<>();
        boolean checkTF = false;
        // go through all elements of string array
        for(String tmp: words) {
            // if HashMap already store the specific string
            if(memo.containsKey(tmp) == true) {
                // if that string is True, then ans++, otherwise go to next loop.
                if( memo.get(tmp) == true) {
                    result++;
                }
                continue;
            }
            checkTF = check(S, tmp);
            // if element length is longer than original string, it must be false
            if(S.length() >= tmp.length() && checkTF == true) {
                result++;
            }
            // add new string pattern in the HashMap
            memo.put(tmp, checkTF);
        }
        return result;
    }
    // two pointer method to check whether the target string is subsequence of original string.
    public boolean check(String S, String target) {
        int cnt_1 = 0;
        int cnt_2 = 0;
        while(cnt_1 != S.length() && cnt_2 != target.length()) {
            if(target.charAt(cnt_2) == S.charAt(cnt_1)) {
                cnt_1++;
                cnt_2++;
            }
            else {
                cnt_1++;
            }
        }
        if(cnt_2 == target.length()) {
            return true;
        }
        return false;
    }
}
