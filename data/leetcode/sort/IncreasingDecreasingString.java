public class IncreasingDecreasingString {
    public String sortString(String s) {
        int[] alphabet = new int[26];
        
        for(char c: s.toCharArray()) {
            alphabet[c - 'a']++;
        }
        
        int i = 0;
        boolean plus = true;
        int len = s.length();
        StringBuilder sb = new StringBuilder();
        
        while(len > 0) {
            if (alphabet[i] > 0) {
                sb.append((char) (i + 'a'));
                alphabet[i]--;
                len--;
            }
            
            if (((plus) && (i == 25)) || ((!plus) && (i == 0))) {
                plus = !plus;
            } else {
                i += plus? 1 : -1;
            }
        }
        
        return sb.toString(); 
    }
}
