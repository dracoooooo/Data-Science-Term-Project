package cpu.alu;


/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class myALU {

        // 模拟寄存器中的进位标志位
        private String CF = "0";

        // 模拟寄存器中的溢出标志位
        private String OF = "0";

        //add two integer
        String add(String src, String dest) {
            // TODO
            int length = src.length();
            char[] result = new char[length];
            for(int i = length-1; i >=0; --i){
                String X = String.valueOf(src.charAt(i));
                String Y = String.valueOf(dest.charAt(i));
                result[i] = xor(xor(X, Y), CF).toCharArray()[0];
                CF = and(and(or(X, CF),or(X, Y)),or(Y, CF));
            }
            OF = CF;
            CF = "0";
            return String.valueOf(result);
        }

        String[] addWithOF(String src, String dest, String cf) {
            if(cf.equals("1")) CF="1";
            else CF="0";
            String[] ret = new String[2];
            String result = add(src, dest);
            ret[0] = OF;
            ret[1] = result;
            return ret;
        }
        //sub two integer
        // dest - src
        String sub(String src, String dest) {
            // TODO
            src = reverseAndPlusOne(src);
            return add(src, dest);
        }

        public String reverseAndPlusOne(String sbin){
            char[] bin = sbin.toCharArray();
            for(int i = bin.length-1; i >= 0; --i){
                if(bin[i]=='1') bin[i]='0';
                else bin[i]='1';
            }
            for(int i = bin.length-1; i >= 0; --i){
                if(bin[i]=='0'){
                    bin[i]='1';
                    break;
                }
                else if(bin[i]=='1'){
                    bin[i]='0';
                    continue;
                }
            }
            return String.valueOf(bin);
        }

        String and(String src, String dest) {
            // TODO
            int length = dest.length();
            char[] result = new char[length];
            for(int i = 0; i < length; ++i){
                if(src.charAt(i) == '1' && dest.charAt(i) == '1') result[i] = '1';
                else result[i] = '0';
            }
            return String.valueOf(result);
        }

        String or(String src, String dest) {
            // TODO
            int length = dest.length();
            char[] result = new char[length];
            for(int i = 0; i < length; ++i){
                if(src.charAt(i) == '0' && dest.charAt(i) == '0') result[i] = '0';
                else result[i] = '1';
            }
            return String.valueOf(result);
        }

        String xor(String src, String dest) {
            // TODO
            int length = dest.length();
            char[] result = new char[length];
            for(int i = 0; i < length; ++i){
                if(src.charAt(i) != dest.charAt(i)) result[i] = '1';
                else result[i] = '0';
            }
            return String.valueOf(result);
    }
    public static void main(String[] args){
    }

}
