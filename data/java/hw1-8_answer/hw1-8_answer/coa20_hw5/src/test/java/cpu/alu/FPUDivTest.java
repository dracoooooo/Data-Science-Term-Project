package cpu.alu;

import org.junit.Test;
import transformer.Transformer;
import util.IEEE754Float;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FPUDivTest {
    private FPU fpu = new FPU();
    static Transformer transformer = new Transformer();

    //1.25/0.5=2.5
    @Test
    public void fpuDivTest1(){
        String dividend = transformer.floatToBinary( "1" );
        String divisor = transformer.floatToBinary( "4" );
        String expected = transformer.floatToBinary( "0.25" );
        String result = fpu.div(dividend,divisor);
        assertEquals(expected, result);
    }

    //2.2/1.1=2.0
    @Test
    public void fpuDivTest2(){
        String dividend = transformer.floatToBinary( "0.4375" );
        String divisor = transformer.floatToBinary( "0.5" );
        String expected = transformer.floatToBinary( "0.875" );
        String result = fpu.div(dividend,divisor);
        assertEquals(expected, result);
    }

    //2.2/0.0 报错
    @Test(expected = ArithmeticException.class)
    public void fpuDivTest3(){
        String dividend = transformer.floatToBinary( "2.2" );
        String divisor = transformer.floatToBinary( "0.0" );
        fpu.div(dividend,divisor);
    }

    //-2.0/1.0 = -2.0
    @Test
    public void fpuDivTest4(){
        String dividend = transformer.floatToBinary( "-2.0" );
        String divisor = transformer.floatToBinary( "1.0" );
        String expected = transformer.floatToBinary( "-2.0" );
        String result = fpu.div(dividend,divisor);
        assertEquals(expected, result);
    }
    // 1.0/-2.0 = -0.5
    @Test
    public void fpuDivTest5(){
        String dividend = transformer.floatToBinary( "1" );
        String divisor = transformer.floatToBinary( "-2.0" );
        String expected = transformer.floatToBinary( "-0.5" );
        String result = fpu.div(dividend,divisor);
        assertEquals(expected, result);
    }
    //-0.0/1.0 = -0.0
    @Test
    public void fpuDivTest6(){
        String dividend = IEEE754Float.N_ZERO;
        String divisor = transformer.floatToBinary( "1.0" );
        String result = fpu.div(dividend,divisor);
        assertEquals(IEEE754Float.N_ZERO, result);
    }
    //+0.0/5.0 = +0.0
    @Test
    public void fpuDivTest7(){
        String dividend = IEEE754Float.P_ZERO;
        String divisor = transformer.floatToBinary( "5.0" );
        String result = fpu.div(dividend,divisor);
        assertEquals(IEEE754Float.P_ZERO, result);
    }

    // +0.0 / -0.0  NaN
    @Test
    public void fpuDivTest8(){
        String dividend = IEEE754Float.P_ZERO;
        String divisor = IEEE754Float.N_ZERO;
        String result = fpu.div(dividend, divisor);
        assertTrue(result.matches(IEEE754Float.NaN) || result.equals(IEEE754Float.NaN));
        fpu.div(dividend,divisor);
    }


    // +无穷 / +1.0 = +无穷
    @Test
    public void fpuDivTest9(){
        String dividend = IEEE754Float.P_INF;
        String divisor = transformer.floatToBinary( "1.0" );
        String result = IEEE754Float.P_INF;
        assertEquals(result, fpu.div(dividend,divisor));
    }

    // +无穷 / -1.0 = -无穷
    @Test
    public void fpuDivTest10(){
        String dividend = IEEE754Float.P_INF;
        String divisor = transformer.floatToBinary( "-1.0" );
        String result = IEEE754Float.N_INF;
        assertEquals(result, fpu.div(dividend,divisor));
    }

    @Test
    public void fpuDivTest11(){
        String dividend = transformer.floatToBinary( "-8.0" );
        String divisor = transformer.floatToBinary( "2.0" );
        String expected = transformer.floatToBinary( "-4.0" );
        String result = fpu.div(dividend,divisor);
        assertEquals(expected, result);
    }

    // Thanks to 18SE ChenYanZe
    @Test
    public void fpuDivTest12(){
        String dividend = transformer.floatToBinary("0.4375");
        String divisor = transformer.floatToBinary("0.625");
        String expected = transformer.floatToBinary("0.7");
        String result = fpu.div(dividend, divisor);
        assertEquals(expected, result);
    }
}
