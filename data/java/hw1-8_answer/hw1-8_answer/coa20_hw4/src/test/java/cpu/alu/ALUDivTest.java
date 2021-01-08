package cpu.alu;

import org.junit.Test;
import transformer.Transformer;
import util.BinaryIntegers;

import static org.junit.Assert.assertEquals;

public class ALUDivTest {
    private ALU alu = new ALU();

    /**
     * 10 / 10 = 1 (0)
     */
    @Test
    public void test1() {
        String quotient = "00000000000000000000000000000001";
        String remainder = "00000000000000000000000000000000";
        assertEquals("0"+quotient+remainder, alu.div("00000000000000000000000000001010", "00000000000000000000000000001010"));
    }


    /**
     * -267 / 3711 = 0 (-267)
     */
    @Test
    public void test2() {
        String quotient = "00000000000000000000000000000000";
        String remainder = "11111111111111111111111011110101";
        assertEquals("0"+quotient+remainder, alu.div("11111111111111111111111011110101", "00000000000000000000111001111111"));
    }


    /**
     * -7510 / -347 = 21 (-223)
     */
    @Test
    public void test3() {
        String quotient = "00000000000000000000000000010101";
        String reminder = "11111111111111111111111100100001";
        assertEquals("0"+quotient+reminder, alu.div("11111111111111111110001010101010", "11111111111111111111111010100101"));
    }


    /**
     * 9859 / 8794 = 1 (1065)
     */
    @Test
    public void test4() {
        String quotient = "00000000000000000000000000000001";
        String reminder = "00000000000000000000010000101001";
        assertEquals("0"+quotient+reminder, alu.div("00000000000000000010011010000011", "00000000000000000010001001011010"));
    }


    /**
     * 5327 / -7229 = 0 (5327)
     */
    @Test
    public void test5() {
        String quotient = "00000000000000000000000000000000";
        String reminder = "00000000000000000001010011001111";
        assertEquals("0"+quotient+reminder, alu.div("00000000000000000001010011001111", "11111111111111111110001111000011"));
    }


    /**
     * 0 / -1 = 0 (0)
     */
    @Test
    public void test6() {
        String quotient = "00000000000000000000000000000000";
        String reminder = "00000000000000000000000000000000";
        assertEquals("0"+quotient+reminder, alu.div("00000000000000000000000000000000", "11111111111111111111111111111111"));
    }


    /**
     * 0 / 0 = NaN
     */
    @Test
    public void test7() {
        assertEquals( BinaryIntegers.NaN, alu.div("00000000000000000000000000000000", "00000000000000000000000000000000"));
    }


    /**
     * 1 / 0  除0异常
     */
    @Test(expected = ArithmeticException.class)
    public void test8() {
        alu.div("00000000000000000000000000000001", "00000000000000000000000000000000");
    }


    /**
     * -2^31 / -1 = -2^31(0) 溢出
     */
    @Test
    public void test9() {
        String res =  alu.div("10000000000000000000000000000000", "11111111111111111111111111111111");
        assertEquals( "11000000000000000000000000000000000000000000000000000000000000000", res);
    }

    /**
     * 1 / 1 = 1
     */
    @Test
    public void test10() {
        String quotient = "00000000000000000000000000000001";
        String remainder = "00000000000000000000000000000000";
        String res =  alu.div("00000000000000000000000000000001", "00000000000000000000000000000001");
        assertEquals( "0"+quotient+remainder, res);
    }


    /**
     * -10 / 6 = -1 (-4)
     */
    @Test
    public void test11() {
        String quotient = "11111111111111111111111111111111";
        String reminder = "11111111111111111111111111111100";
        String res =  alu.div("11111111111111111111111111110110", "00000000000000000000000000000110");
        assertEquals( "0"+quotient + reminder, res);
    }

    /**
     * -8 / 2 = -4 (0)
     */
    @Test
    public void test12() {
        Transformer t = new Transformer();
        String quotient = t.intToBinary("-4");
        String reminder = t.intToBinary("0");
        String res =  alu.div(t.intToBinary("-8"), t.intToBinary("2"));
        assertEquals( "0"+quotient + reminder, res);
    }

}
