package cpu.alu;


import transformer.Transformer;


import java.util.Arrays;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减乘除
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    private Transformer transformer = new Transformer();

    //add two integer
    public String add(String src, String dest) {
        // add two integer in 2's complement code
        String result = adder(src, dest, '0', 32);  //注意有进位不等于溢出，溢出要另外判断。已经被封装在上一步

        return result;
    }

    //sub two integer
    // dest - src
    public String sub(String src, String dest) {
        return adder(dest, negation(src), '1', 32);  //不能直接取反加一（有可能取反加一溢出），必须这一步改初始carry位‘0’为‘1’。这样写完全模拟ppt上面的做法，但是还是可能取反的时候就溢出，先加一反而不溢出，所以其实还是不完美。注意有进位不等于溢出，溢出要另外判断。
    }

    //signed integer mod
    String imod(String src, String dest) {
        String temp = dest;
        if (src.charAt(0) == '0' && dest.charAt(0) == '0') {
            while (isLarger(temp, src) || temp.equals(src)) {
                temp = sub(src, temp);
            }
        }
        if (src.charAt(0) == '0' && dest.charAt(0) == '1') {
            while (isLarger("00000000000000000000000000000000", temp) || temp.equals("00000000000000000000000000000000")) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }

        if (src.charAt(0) == '1' && dest.charAt(0) == '0') {
            while (isLarger(temp, "00000000000000000000000000000000") || temp.equals("00000000000000000000000000000000")) {
                temp = add(src, temp);
            }
            temp = sub(src, temp);
        }
        if (src.charAt(0) == '1' && dest.charAt(0) == '1') {
            while (isLarger(src, temp) || temp.equals(src)) {
                temp = sub(src, temp);

            }
        }

        return temp;
    }

    /**
     * fix bug(2019-10-13):
     * 根据标准i386手册(p384)，所有shift操作的移位操作数仅使用后5位，其他位数直接舍去
     * 对应测试用例已修复(testShift2 testShift6)
     * @param src
     * @param dest
     * @return
     */
    String shl(String src, String dest) {
        return sal(src, dest);
    }

    String shr(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        char[] fill = new char[32];
        Arrays.fill(fill, '0');
        return new String(fill).concat(dest).substring(32 - shift, 64 - shift);
    }

    String sal(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        char[] fill = new char[32];
        Arrays.fill(fill, '0');
        return dest.substring(shift).concat(new String(fill)).substring(0, 32);
    }

    String sar(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        char[] fill = new char[32];
        Arrays.fill(fill, dest.charAt(0));
        return new String(fill).concat(dest).substring(32 - shift, 64 - shift);
    }

    String rol(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        return dest.substring(shift).concat(dest.substring(0, shift));
    }

    String ror(String src, String dest) {
        int shift = Math.abs(Integer.parseInt(transformer.binaryToInt("0" + src.substring(src.length()-5))));
        return dest.substring(32 - shift).concat(dest.substring(0, 32 - shift));
    }


    private String adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = carry_adder(operand1, operand2, c, length);
        OF = addOverFlow(operand1, operand2, res);
        return res;  //注意有进位不等于溢出，溢出要另外判断
    }
    /**
     * given a length, make operand to that digits considering the sign
     *
     * @param operand given num
     * @param length  make complete
     * @return completed string
     */
    private String impleDigits(String operand, int length) {
        int len = length - operand.length();
        char imple = operand.charAt(0);
        StringBuffer res = new StringBuffer(new StringBuffer(operand).reverse());
        for (int i = 0; i < len; i++) {
            res = res.append(imple);
        }
        return res.reverse().toString();
    }

    /**
     * test if add given two nums overflow
     *
     * @param operand1 first
     * @param operand2 second
     * @param result   result after the adding
     * @return 1 means overflow, 0 means not
     */
    private String addOverFlow(String operand1, String operand2, String result) {
        int X = operand1.charAt(0) - '0';
        int Y = operand2.charAt(0) - '0';
        int S = result.charAt(0) - '0';
        return "" + ((~X & ~Y & S) | (X & Y & ~S));  //两个操作数符号相同，和结果符号不同，则溢出
    }

    /**
     * add two nums with the length of given length
     * @param operand1 first
     * @param operand2 second
     * @param c        original carray
     * @param length   given length
     * @return result
     */
    private String carry_adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = "";
        char carry = c;
        for (int i = length - 1; i >= 0; i--) {  //这里length不一定是4的倍数，采用更加通用的加法算法
            String temp = fullAdder(operand1.charAt(i), operand2.charAt(i), carry);
            carry = temp.charAt(0);
            res = temp.charAt(1) + res;
        }
        CF = String.valueOf(carry);
        return res;  //注意这个方法里面溢出即有进位
    }

    /**
     * the 2 bits' full adder
     *
     * @param x first char
     * @param y second char
     * @param c carry from the former bit
     * @return result after adding, the first position means the carry to the next and second means the result in this position
     */
    private String fullAdder(char x, char y, char c) {
        int bit = (x - '0') ^ (y - '0') ^ (c - '0');  //三位异或
        int carry = ((x - '0') & (y - '0')) | ((y - '0') & (c - '0')) | ((x - '0') & (c - '0'));  //有两位为1则产生进位
        return "" + carry + bit;  //第一个空串让后面的加法都变为字符串加法
    }

    private boolean isLarger(String a, String b) {
        if (a.charAt(0) == '0' && b.charAt(0) == '1') {
            return true;
        }
        if (a.charAt(0) == '1' && b.charAt(0) == '0') {
            return false;
        }
        for (int i = 1; i < a.length(); i++) {
            int diff = a.charAt(i) - b.charAt(i);
            if (diff > 0) {
                return true;
            } else if (diff < 0) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * convert the string's 0 and 1.
     * e.g 00000 to 11111
     *
     * @param operand string to convert (by default, it is 32 bits long)
     * @return string after converting
     */
    public String negation(String operand) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < operand.length(); i++) {
            result = operand.charAt(i) == '1' ? result.append("0") : result.append("1");
        }
        return result.toString();
    }


}
