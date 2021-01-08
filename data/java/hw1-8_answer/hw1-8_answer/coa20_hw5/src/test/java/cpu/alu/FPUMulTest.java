package cpu.alu;

import org.junit.Test;
import transformer.Transformer;
import util.IEEE754Float;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FPUMulTest {

	private FPU fpu = new FPU();
	static Transformer transformer = new Transformer();
	private static String P_ONE = transformer.floatToBinary( "1.0" );    //     positive one
	private static String N_ONE = transformer.floatToBinary( "-1.0" );   //     negative one

	//  +0 * +0 = +0
	@Test
	public void fpuMulTest1(){
		String result = fpu.mul(IEEE754Float.P_ZERO, IEEE754Float.P_ZERO );
		assertEquals( IEEE754Float.P_ZERO, result );
	}

	//  +0 * -0 = -0
	@Test
	public void fpuMulTest2(){
		String result = fpu.mul( IEEE754Float.P_ZERO, IEEE754Float.N_ZERO );
		assertEquals( IEEE754Float.N_ZERO, result );
	}

	//  +0 * +无穷 = NaN
	@Test
	public void fpuMulTest3(){
		String result = fpu.mul( IEEE754Float.P_ZERO, IEEE754Float.P_INF );
		assertTrue(result.matches(IEEE754Float.NaN) || result.equals(IEEE754Float.NaN));
	}

	//   +0 * NaN = NaN
	@Test
	public void fpuMulTest4(){
		String result = fpu.mul( IEEE754Float.P_ZERO, "11111111101010101010101010101010" );
		assertTrue(result.matches(IEEE754Float.NaN) || result.equals(IEEE754Float.NaN));
	}

	// +0 * -1 = -0
	@Test
	public void fpuMulTest5(){
		String result = fpu.mul( IEEE754Float.P_ZERO, N_ONE );
		assertEquals( IEEE754Float.N_ZERO, result );
	}

	@Test
	public void fpuMulTest6(){
		String result = fpu.mul( IEEE754Float.N_ZERO, IEEE754Float.P_ZERO );
		assertEquals( IEEE754Float.N_ZERO, result );
	}

	// -0 * +1 = -0
	@Test
	public void fpuMulTest7(){
		String result = fpu.mul( IEEE754Float.N_ZERO, P_ONE );
		assertEquals( IEEE754Float.N_ZERO, result );
	}

	@Test
	public void fpuMulTest8(){
		String a = transformer.floatToBinary( "0.25" );
		String b = transformer.floatToBinary( "4" );
		String result = fpu.mul(a , b);
		assertEquals( P_ONE, result );
	}

	@Test
	public void fpuMulTest9(){
		String a = transformer.floatToBinary( "1.25" );
		String b = transformer.floatToBinary( "1.375" );
		String c = transformer.floatToBinary( "1.71875" );
		String result = fpu.mul(a , b);
		assertEquals( c, result );
	}

	@Test
	public void fpuMulTest10(){
		String a = transformer.floatToBinary( "2.875" );
		String b = transformer.floatToBinary( "4.5" );
		String c = transformer.floatToBinary( "12.9375" );
		String result = fpu.mul(a , b);
		System.out.println(transformer.binaryToFloat(result));
		assertEquals( c, result );
	}

}
