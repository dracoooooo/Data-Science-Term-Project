package cpu.alu;

import transformer.Transformer;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    Transformer t = new Transformer();

    ALU alu = new ALU();

    myALU myALU = new myALU();

    String zero = "00000000000000000000000000000000";

    String inf = "01111111100000000000000000000000";

    String max_float = "01111111011111111111111111111111";
    /**
     * compute the float add of (a + b)
     **/
    String add(String a,String b){
        // TODO
        char signalA = a.charAt(0);
        char signalB = b.charAt(0);
        char signal = signalA;

        int expA = Integer.parseInt(t.binaryToInt(a.substring(1,9))) - 127;
        int expB = Integer.parseInt(t.binaryToInt(b.substring(1,9))) - 127;

        if(expA == -127) return b;
        if(expB == -127) return a;

        String valueA = "01" + a.substring(9) + "0000";
        String valueB = "01" + b.substring(9) + "0000";

        if(signalA != signalB) valueB = t.oneAdder(t.negation(valueB)).substring(1);
        if(signalA == signalB && (a.equals(max_float) || b.equals(max_float))) return inf;

        if(expA < expB){
            String gap = t.intToBinary(String.valueOf(expB - expA));
//            valueA = alu.sar(gap, valueA);
            for(int i = 0; i < Integer.parseInt(t.binaryToInt(gap)); ++i){
                valueA = alu.sar("1", valueA);
            }
            expA += expB - expA;
            if(Integer.parseInt(t.binaryToInt(valueA))==0) return b;
        }
        if(expB < expA){
            String gap = t.intToBinary(String.valueOf(expA - expB));
//            valueB = alu.sar(gap, valueB);
            for(int i = 0; i < Integer.parseInt(t.binaryToInt(gap)); ++i){
                valueB = alu.sar("1", valueB);
            }
            expB += expA - expB;
            if(Integer.parseInt(t.binaryToInt(valueB))==0 || Integer.parseInt(t.binaryToInt(gap)) >= 23) return a;
        }


        String value = myALU.add(valueA ,valueB);
        int exp = expA;
        if(Integer.parseInt(t.binaryToInt(value))==0) return zero;

        // 00.0000001 and 01.010101的情况
        int zeros = countZeros(value) - 1;
        if(zeros < 0) zeros = 0;
        exp -= zeros;
        value = alu.shl(t.intToBinary(String.valueOf(zeros)), value);
        if(exp < -127) return zero;

        // 10.xxx and 11.xxx
        if(value.charAt(0) == '1' && signalA == signalB){ // ????
            value = alu.shr("1", value);
            exp += 1;
            if(exp >= 255) return inf;
        }


        if(value.charAt(0) == '1' && signalA != signalB){
            value = t.oneAdder(t.negation(value)).substring(1);
            signal = signalB;
        }

        String expString = t.intToBinary(String.valueOf(exp + 127)).substring(24);
        String valueString = null;
        if(value.charAt(0) == '0') valueString = value.substring(2,25); // todo ??
        else{
            valueString = value.substring(1, 24);
        }

        String result = signal + expString + valueString;

        return result;
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a,String b){
        // TODO
        char signalB = b.charAt(0);
        if(signalB == '1') b = '0' + b.substring(1);
        else b = '1' + b.substring(1);
		return add(a, b);
    }

    public int countZeros(String bin){
        int i = 0;
        for(i = 0; i < bin.length() &&  bin.charAt(i) !='1' ; ++i);
        return i;
    }


    public static void main(String[] args){
        FPU f = new FPU();
        Transformer t = new Transformer();
        String a = t.floatToBinary(String.valueOf(Math.pow(2,127)));
        String b = t.floatToBinary("1");
        String res = f.add("01000000111011011100000000000000", "01111111011111111111111111111111");
        System.out.println(res);
    }
}
