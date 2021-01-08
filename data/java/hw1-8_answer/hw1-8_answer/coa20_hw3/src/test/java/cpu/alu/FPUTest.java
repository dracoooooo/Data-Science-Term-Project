package cpu.alu;

import org.junit.Test;
import transformer.Transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FPUTest {
    private FPU fpu = new FPU();
    static Transformer transformer = new Transformer();
    private static String P_ONE = transformer.floatToBinary( "1.0" );    //     positive one
    private static String N_ONE = transformer.floatToBinary( "-1.0" );   //     negative one
    public static final String P_ZERO = "00000000000000000000000000000000";  // 0X0           positive zero
    public static final String N_ZERO = "10000000000000000000000000000000";  // 0X80000000    negative zero
    public static final String P_INF = "01111111100000000000000000000000";  // 0X7f800000    positive infinity
    public static final String N_INF = "11111111100000000000000000000000";  // 0Xff800000    negative infinity
    public static final String NaN = "(0|1){1}1{8}(0+1+|1+0+)(0|1)*";// Not_A_Number

    @Test
    public void fpuAddTest1() {
        String result = fpu.add("00111111101000000000000000000000", "01000000010001100110011001100110");
        assertEquals("01000000100010110011001100110011", result);
    }

    @Test
    public void fpuAddTest2() {
        String result = fpu.add("11000000010010001111010111000010", "01000001001101010000000000000000");
        assertEquals("01000001000000101100001010001111", result);
    }

    @Test
    public void fpuAddTest3() {
        String result = fpu.add(P_INF, "01000001001101010000000000000000");
        assertEquals(P_INF, result);
    }

    @Test
    public void fpuAddTest4() {
        String result = fpu.add("01111111110000000000000000000000", "11000000010010111111010111000010");
        assertTrue(result.matches(NaN) || result.equals(NaN));
    }

    @Test
    public void fpuAddTest5() {
        String result = fpu.add("01111111110000000000000000000000", N_ZERO);
        assertTrue(result.matches(NaN) || result.equals(NaN));
    }

    @Test
    public void fpuAddTest6() {
        String result = fpu.add("01111111011111111111111111111111", "01111111011111111111111111111111");
        assertEquals(P_INF, result);
    }

    @Test
    public void fpuAddTest7() {
        String result = fpu.add("11111111011111111111111001101111", "01110001011111111111111111111111");
        assertEquals("11111111011111111111111001101111", result);
    }


    @Test
    public void fpuSubTest1(){
        String result = fpu.sub("01000000111011011100000000000000","11000001110101100000000000000000");
        assertEquals("01000010000010001011100000000000", result);
    }

    @Test
    public void fpuSubTest2(){
        String result = fpu.sub("01000000111011011100000000000000","01000001110101100000000000000000");
        assertEquals("11000001100110101001000000000000", result);
    }

    @Test
    public void fpuSubTest3(){
        String result = fpu.sub("01001001110100010000000000000000","11001001110100010000000000000000");
        assertEquals("01001010010100010000000000000000", result);
    }

    @Test
    public void fpuSubTest4(){
        String result = fpu.sub("01001001110100010000000000000000","01001001110100010000000000000000");
        assertEquals("00000000000000000000000000000000", result);
    }

    @Test
    public void fpuSubTest5(){
        String result = fpu.sub("00110101110100010000000000000000","11001001110100010000000000000000");
        assertEquals("01001001110100010000000000000000", result);
    }

    @Test
    public void fpuSubTest6(){
        String result = fpu.sub("00110101110100010000000000000000","01001001110100010000000000000000");
        assertEquals("11001001110100010000000000000000", result);
    }

    @Test
    public void fpuSubTest7(){
        String result = fpu.sub("00111111000000000000000000000000","10111110100000000000000000000000");
        assertEquals("00111111010000000000000000000000", result);
    }

    @Test
    public void fpuSubTest8(){
        String result = fpu.sub("00111111000000000000000000000000","00111110100000000000000000000000");
        assertEquals("00111110100000000000000000000000", result);
    }

    @Test
    public void fpuSubTest9(){
        String result = fpu.sub("01000010111101110100000000000000","01000001110101100000000000000000");
        assertEquals("01000010110000011100000000000000", result);
    }

    @Test
    public void fpuSubTest10(){
        String result = fpu.sub("01000010111101110100000000000000","11000001110101100000000000000000");
        assertEquals("01000011000101100110000000000000", result);
    }

}
