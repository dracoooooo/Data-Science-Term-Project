package cpu.alu;

import org.junit.Test;
import transformer.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ALUMulTest {

	private ALU alu = new ALU();

	@Test
	/**
	 * 10 * 10 = 100
	 */
	public void test1() {
		assertEquals("00000000000000000000000001100100", alu.mul("00000000000000000000000000001010", "00000000000000000000000000001010"));
	}

	@Test
	/**
	 * -267 * 3711 = -990837
	 */
	public void test2() {
		assertEquals("11111111111100001110000110001011", alu.mul("11111111111111111111111011110101", "00000000000000000000111001111111"));
	}

	@Test
	/**
	 * -7510 * -347 = 2605970
	 */
	public void test3() {
		assertEquals("00000000001001111100001110010010", alu.mul("11111111111111111110001010101010", "11111111111111111111111010100101"));
	}

	@Test
	/**
	 * 9859 * 8794 = 86700046
	 */
	public void test4() {
		assertEquals("00000101001010101111000000001110", alu.mul("00000000000000000010011010000011", "00000000000000000010001001011010"));
	}

	@Test
	/**
	 * 5327 * -7229 = -38508883
	 */
	public void test5() {
		assertEquals("11111101101101000110011010101101", alu.mul("00000000000000000001010011001111", "11111111111111111110001111000011"));
	}

	@Test
	/**
	 * 0 * -1 = 0
	 */
	public void test6() {
		assertEquals("00000000000000000000000000000000", alu.mul("00000000000000000000000000000000", "11111111111111111111111111111111"));
	}

	@Test
	/**
	 * 0 * 0 = 0
	 */
	public void test7() {
		assertEquals("00000000000000000000000000000000", alu.mul("00000000000000000000000000000000", "00000000000000000000000000000000"));
	}

	@Test
	/**
	 * 2^16 = 65536
	 */
	public void test8() {
		String op = "00000000000000000000000000000010";
		String result = "00000000000000000000000000000010";
		for (int i=0; i<15; i++) {
			result = alu.mul(result, op);
		}
		assertEquals("00000000000000010000000000000000", result);
	}

	@Test
	/**
	 * 2347823 * -1234212 = -2897711320476 = 1391604324‬‬-(2^32*675) -> 1391604324
	 */
	public void test9() {
		assertEquals("01010010111100100011001001100100", alu.mul("00000000001000111101001100101111", "11111111111011010010101011011100"));
	}

	@Test
	public void test10() {
		Random r = new Random();
		int a = r.nextInt(1000);
		int b = r.nextInt(1000);
		int result = a * b;
		Transformer t = new Transformer();
		assertEquals(t.intToBinary(String.valueOf(result)), alu.mul(t.intToBinary(String.valueOf(a)), t.intToBinary(String.valueOf(b))));
	}


}
