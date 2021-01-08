package cpu.alu;

import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 取模、逻辑/算术/循环左右移
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    //signed integer mod
    String imod(String src, String dest) {
        // TODO dest mod src
        Transformer t = new Transformer();
        myALU a = new myALU();
        char Q0 = '0';
        char srcSign = src.charAt(0);
        char destSign = dest.charAt(0);
        src = abs(src);
        dest = abs(dest);

        String zeros = "";
        for(int i = 0; i < dest.length(); ++i) zeros += "0";
        String AQ = zeros + dest;
        String M = src;

        String A = null;
        String Q = null;

        for(int i = 0; i < dest.length(); ++i){
//            System.out.println(AQ);
            AQ = shl("1", AQ).substring(0,AQ.length() - 1) + Q0;
            A = AQ.substring(0, dest.length());
            Q = AQ.substring(dest.length());
            A = a.sub(M, A);
            if(A.charAt(0) == '0') Q0 = '1';
            else{
                Q0 = '0';
                A = a.add(M, A);
            }
            AQ = A + Q;
        }

        if(destSign == '1') A = t.oneAdder(t.negation(A)).substring(1);
		return A;
    }

    String abs(String value){
        char sign = value.charAt(0);
        if(sign == '1'){
            Transformer t = new Transformer();
            value = t.negation(value);
            value = t.oneAdder(value).substring(1);
        }
        return value;
    }



    String shl(String src, String dest){
        return sl(src, dest, "l");
    }

    String shr(String src, String dest) {
        // TODO
		return sr(src, dest, "l");
    }

    String sal(String src, String dest) {
        // TODO
		return sl(src, dest, "a");
    }

    String sar(String src, String dest) {
        // TODO
		return sr(src, dest, "a");
    }

    String rol(String src, String dest) {
        // TODO
		return sl(src, dest, "r");
    }

    String ror(String src, String dest) {
        // TODO
		return sr(src, dest, "r");
    }

    String sr(String src, String dest, String mode){
        char tmp = '0';
        StringBuilder result = new StringBuilder();
        Transformer t = new Transformer();
        int offset = Integer.parseInt(t.binaryToInt(src));
        if(mode.equals("a")) tmp = dest.charAt(0);
        else if(mode.equals("l")) tmp = '0';
        if(offset >= dest.length()) offset = offset % dest.length();
        if(offset == 0) return dest;
        for(int i = 0; i < dest.length(); ++i){
            if(i < offset){
                if(mode.equals("r")) result.append(dest.charAt(i + dest.length() - offset ));
                else result.append(tmp);
            }
            else result.append(dest.charAt(i - offset));
        }
        return String.valueOf(result);
    }

    String sl(String src, String dest, String mode) {
        // TODO
        Transformer t = new Transformer();
        int offset = Integer.parseInt(t.binaryToInt(src)); // unsigned ?
        if(offset >= dest.length()) offset = offset % dest.length();
        if(offset == 0) return dest;
        StringBuilder result = new StringBuilder();
        for(int i = offset; i < dest.length() + offset; ++i){
            if(i < dest.length()) result.append(dest.charAt(i));
            else{
                if(mode.equals("r")) result.append(dest.charAt(i - dest.length()));
                else result.append('0');
            }
        }
        return String.valueOf(result);
    }

    public static void main(String[] args){
        ALU alu = new ALU();
        Transformer t = new Transformer();
        String a = t.intToBinary("12");
        String b = t.intToBinary("5");
        System.out.println(alu.imod("0011", "1111"));
    }

}
