package cpu.alu;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class ALU {

    private static int BIT_LEN = 32;
    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //add two integer
    String add(String src, String dest) {
        // TODO
        // src + dest;
        char[] srcBits = src.toCharArray();
        char[] destBits = dest.toCharArray();
        char[] ansBits = new char[BIT_LEN];
        char Ci = '0';
        for(int i = src.length()-1; i >=0; i--){
            ansBits[i] = xorBit(Ci, xorBit(srcBits[i], destBits[i]));
            Ci = orBit(andBit(srcBits[i], destBits[i]), orBit(andBit(srcBits[i],Ci), andBit(destBits[i], Ci)));
        }
        CF = String.valueOf(Ci);
		return new String(ansBits);
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        // TODO
        // dest - src;
		return add(toComplement(src), dest);
	}

    String and(String src, String dest) {
        // TODO
        StringBuilder ans= new StringBuilder(BIT_LEN);
        char[] srcBits = src.toCharArray();
        char[] destBits = dest.toCharArray();
        for(int i = 0; i < srcBits.length; ++i){
            ans.append(andBit(srcBits[i], destBits[i]));
        }
		return ans.toString();
    }

    String or(String src, String dest) {
        // TODO
        StringBuilder ans= new StringBuilder(BIT_LEN);
        char[] srcBits = src.toCharArray();
        char[] destBits = dest.toCharArray();
        for(int i = 0; i < srcBits.length; ++i){
            ans.append(orBit(srcBits[i], destBits[i]));
        }
        return ans.toString();
    }

    String xor(String src, String dest) {
        // TODO
        StringBuilder ans= new StringBuilder(BIT_LEN);
        char[] srcBits = src.toCharArray();
        char[] destBits = dest.toCharArray();
        for(int i = 0; i < srcBits.length; ++i){
            ans.append(xorBit(srcBits[i], destBits[i]));
        }
        return ans.toString();
    }

    public static char andBit(char bit1, char bit2)
    {
        if(bit1 == '1' && bit2 == '1') return '1';
        else return '0';
    }
    public static char orBit(char bit1, char bit2)
    {
        if(bit1 == '1' || bit2 == '1') return '1';
        else return '0';
    }
    public static char xorBit(char bit1, char bit2){
        if(bit1 == bit2) return '0';
        else return '1';
    }
    private String toComplement(String bits){
        char[] ans = bits.toCharArray();
        boolean initOneFound = false;
        for(int i = bits.length()-1; i >=0; i--){
            if(initOneFound) ans[i] = ans[i] == '1' ? '0': '1' ;
            if(ans[i] == '1') initOneFound = true;
        }
        return new String(ans);
    }
}
