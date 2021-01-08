package cpu.alu;

import transformer.Transformer;
import util.IEEE754Float;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };
    /**
     * compute the float add of (a + b)
     * */
    String add(String a,String b){
        if (a.matches(IEEE754Float.NaN) || b.matches(IEEE754Float.NaN)) {
            return IEEE754Float.NaN;
        }
        String cornerCondition = cornerCheck(addCorner, a, b);
        if (null != cornerCondition) return cornerCondition;
        String total = new FPUHelper().floatAddition(a, b, 8, 23, 4);
        return total.substring(1);
    }

    /**
     * compute the float add of (a - b)
     * */
    String sub(String a,String b){
        if (a.matches(IEEE754Float.NaN) || b.matches(IEEE754Float.NaN)) {
            return IEEE754Float.NaN;
        }
        String cornerCondition = cornerCheck(subCorner, a, b);
        if (null != cornerCondition) return cornerCondition;
        b = (b.charAt(0)=='0' ? "1" : "0") + b.substring(1);
        String total = new FPUHelper().floatAddition( a,b,8, 23, 4 );
        return total.substring(1);
    }

    String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) &&
                    oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        return null;
    }

    private class FPUHelper {

        /**
         * add two float number
         *
         * @param operand1 first in binary format
         * @param operand2 second in binary format
         * @param eLength  exponent's length
         * @param sLength  significand's length
         * @param gLength  guard's bits
         * @return exponent overflow + sign + eLength + sLength
         */
        public String floatAddition(String operand1, String operand2, int eLength, int sLength, int gLength) {
            if (allZero(operand1.substring(1))) {
                return "0" + operand2;
            }
            if (allZero(operand2.substring(1))) {
                return "0" + operand1;
            }
            int Xexponent = Integer.valueOf(operand1.substring(1, 1 + eLength), 2);  //注意这里要加上第二个参数2，不然默认十进制转化
            int Yexponent = Integer.valueOf(operand2.substring(1, 1 + eLength), 2);
            if (Xexponent == 0) Xexponent++;  //如果指数是全0，那么指数的真实值为1，因为阶码已经考虑了隐藏位
            if (Yexponent == 0) Yexponent++;
            String Xsig = getSignificand(operand1, eLength, sLength);  //get the two significands including the implicit bit
            String Ysig = getSignificand(operand2, eLength, sLength);
            for (int i = 0; i < gLength; i++) {  //add the guard bits
                Xsig += "0";
                Ysig += "0";
            }
            //现在Xsig组成为 隐藏位+sLength+保护位
            int exponent = Math.max(Xexponent, Yexponent);  //choose the bigger exponent and right shift the smaller one
            if (Xexponent != Yexponent) {
                if (Xexponent > Yexponent) {
                    Ysig = logRightShift(Ysig, String.valueOf(Xexponent - Yexponent));
                } else {
                    Xsig = logRightShift(Xsig, String.valueOf(Yexponent - Xexponent));
                }
            }
            if (allZero(Xsig)) {
                return "0" + operand2;
            }
            if (allZero(Ysig)) {
                return "0" + operand1;
            }
            String temp = signedAddition(operand1.charAt(0) + Xsig, operand2.charAt(0) + Ysig, Xsig.length());  //需要传入符号。这里直接设置寄存器长度为 Xsig.length() 就可以检测是否溢出，传入参数结构为 符号位+隐藏位+sLength+保护位
            //temp结构为 溢出位+符号位+隐藏位+sLength+保护位
            boolean overflow = temp.charAt(0) == '0' ? false : true;
            char sign = temp.charAt(1);
            String sig = temp.substring(2);  //sig结构为 隐藏位+sLength+保护位
            StringBuilder answer = new StringBuilder();
            if (allZero(sig)) {
                answer.append("0" + sign);
                for (int i = 0; i < eLength + sLength; i++) answer.append("0");
                return answer.toString();
            }
            if (overflow) {
                sig = "1" + sig.substring(0, sig.length() - 1);  //右移一位（不可能要移动两次）
                exponent++;
                if (exponent == maxValue(eLength)) {  //指数上溢
                    answer.append("1" + sign);
                    answer.append(Integer.toBinaryString(exponent));
                    for (int i = 0; i < sLength; i++) answer.append("0");  //无穷要求阶码全为0
                    return answer.toString();
                }
            }
            while (sig.charAt(0) != '1' && exponent > 0) {  //规格化
                sig = leftShift(sig, "1");
                exponent--;
            }
            if (exponent == 0) {  //非规格化数
                sig = logRightShift(sig, "1");
            }
            answer.append("0" + sign);
            String ans_exponent = Integer.toBinaryString(exponent);
            int len = ans_exponent.length();  //注意要先把长度单独写出来，不能写在循环里面
            for (int i = 0; i < eLength - len; i++) ans_exponent = "0" + ans_exponent;  //补齐到eLength长度
            answer.append(ans_exponent);
            answer.append(sig.substring(1, 1 + sLength));  //round策略为向0舍入，故直接舍去保护位
            return answer.toString();
        }
        /**
         * left shift a num using its string format
         * e.g. "00001001" left shift 2 bits -> "00100100"
         *
         * @param operand to be moved
         * @param n       moving nums of bits
         * @return after moving
         */
        public String leftShift(String operand, String n) {
            StringBuffer result = new StringBuffer(operand.substring(Integer.parseInt(n)));  //保证位数不变
            for (int i = 0; i < Integer.parseInt(n); i++) {
                result = result.append("0");
            }
            return result.toString();
        }

        /**
         * right shitf a num without considering its sign using its string format
         *
         * @param operand to be moved
         * @param n       moving nums of bits
         * @return after moving
         */
        public String logRightShift(String operand, String n) {
            StringBuffer result = new StringBuffer(operand);  //保证位数不变
            result = result.reverse();
            for (int i = 0; i < Integer.parseInt(n); i++) {
                result = result.append("0");
            }
            return result.reverse().toString().substring(0, operand.length());
        }

        /**
         * check if the given string is full of '0'
         *
         * @param string given string
         * @return result
         */
        public boolean allZero(String string) {
            for (char c : string.toCharArray()) {
                if (c != '0') return false;
            }
            return true;
        }

        /**
         * calculate the max value (true value) with the given length of bits
         *
         * @param length given length
         * @return result
         */
        public double maxValue(int length) {
            //不能使用移位操作
            return Math.pow(2, length) - 1;
        }

        /**
         * get the significand bits includes the implicit bit considering the subnormal number
         *
         * @param operand number string in the binary format
         * @param eLength exponent's length
         * @param sLength significand's length
         * @return result string including the implicit bit
         */
        public String getSignificand(String operand, int eLength, int sLength) {
            String exponent = operand.substring(1, 1 + eLength);
            if (Integer.valueOf(exponent) == 0) return "0" + operand.substring(1 + eLength);  //subnormal number
            else return "1" + operand.substring(1 + eLength);
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
         * sign-magnitude representation add
         *
         * @param operand1 sign-magnitude
         * @param operand2 sign-magnitude
         * @param length   length of the rigister(larger than the number's length without the sign)
         * @return 2+length, first presents overflow, second presents the sign and remain means result(length equal to the given length)
         */
        public String signedAddition(String operand1, String operand2, int length) {  //溢出规则和补码不一样，因此这里用了另外一种adder，可以判断是否有进位
            String a = operand1.substring(1);  //取出绝对值
            if (a.length() < length) a = impleDigits("0" + operand1.substring(1), length);  //扩展到寄存器长度
            String b = operand2.substring(1);
            if (b.length() < length) b = impleDigits("0" + operand2.substring(1), length);
            if (allZero(a)) return operand2;  //0的情况单独判断，不然下面减法会出错
            if (allZero(b)) return operand1;
            if (operand1.charAt(0) == operand2.charAt(0)) {  //同号做加法
                String temp = carry_adder(a, b, '0', length);
                return "" + temp.charAt(0) + operand1.charAt(0) + temp.substring(1);  //有进位即溢出
            } else {  //异号做减法，此时不可能溢出
                b = oneAdder(negation(b)).substring(1);  //取反加一，0的补没有意义，先前已经被排除
                String temp = carry_adder(a, b, '0', length);
                if (temp.charAt(0) == '1') return "0" + operand1.charAt(0) + temp.substring(1);  //如果有进位则正常，符号和被减数一样
                return "0" + negation("" + operand1.charAt(0)) + oneAdder(negation(temp.substring(1))).substring(1);  //没有进位就取反加一，并且符号和被减数相反
            }
        }

        /**
         * add two nums with the length of given length which could be divided by 4, and the result's first bit presents the overflow
         * different from the {@code adder} method, the result's first bit presents whether it generates the carry
         *
         * @param operand1 first
         * @param operand2 second
         * @param c        original carray
         * @param length   given length
         * @return result, and the result's first bit presents the carry
         */
        public String carry_adder(String operand1, String operand2, char c, int length) {
            operand1 = impleDigits(operand1, length);
            operand2 = impleDigits(operand2, length);
            String res = "";
            char carry = c;
            for (int i = length - 1; i >= 0; i--) {  //这里length不一定是4的倍数，采用更加通用的加法算法
                String temp = fullAdder(operand1.charAt(i), operand2.charAt(i), carry);
                carry = temp.charAt(0);
                res = temp.charAt(1) + res;
//            res = (char)('0'+((carry-'0')^(operand1.charAt(i)-'0')^(operand2.charAt(i)-'0')))+res;
//            carry = (char)('0'+((carry-'0')&(operand1.charAt(i)-'0') | (carry-'0')&(operand2.charAt(i)-'0') | (operand1.charAt(i)-'0')&(operand2.charAt(i)-'0')));
            }
            return carry + res;  //注意这个方法里面溢出即有进位
        }

        /**
         * add one to the operand
         *
         * @param operand the operand
         * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
         */
        public String oneAdder(String operand) {
            int len = operand.length();
            StringBuffer temp = new StringBuffer(operand);
            temp = temp.reverse();
            int[] num = new int[len];
            for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
            int bit = 0x0;
            int carry = 0x1;
            char[] res = new char[len];
            for (int i = 0; i < len; i++) {
                bit = num[i] ^ carry;
                carry = num[i] & carry;
                res[i] = (char) ('0' + bit);  //显示转化为char
            }
            String result = new StringBuffer(new String(res)).reverse().toString();
            return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
        }

        /**
         * the 2 bits' full adder
         *
         * @param x first char
         * @param y second char
         * @param c carry from the former bit
         * @return result after adding, the first position means the carry to the next and second means the result in this position
         */
        public String fullAdder(char x, char y, char c) {
            int bit = (x - '0') ^ (y - '0') ^ (c - '0');  //三位异或
            int carry = ((x - '0') & (y - '0')) | ((y - '0') & (c - '0')) | ((x - '0') & (c - '0'));  //有两位为1则产生进位
            return "" + carry + bit;  //第一个空串让后面的加法都变为字符串加法
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


}
