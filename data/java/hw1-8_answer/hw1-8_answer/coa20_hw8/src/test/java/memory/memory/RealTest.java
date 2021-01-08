package memory.memory;

import cpu.MMU;
import memory.Disk;
import memory.DiskInterface;
import memory.Memory;
import memory.MemoryInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * 实模式读取数据，逻辑地址(48-bits)等于物理地址(32-bits)
 */
public class RealTest {

	static MMU mmu;

	static Memory memory;

	static MemTestHelper helper;


	InputStream in = null;
	PrintStream out = null;

	InputStream inputStream = null;
	OutputStream outputStream = null;


	@Before
	public void setUp() {
		in = System.in;
		out = System.out;
		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}


	@After
	public void tearDown() {
		System.setIn(in);
		System.setOut(out);
	}


	@BeforeClass
	public static void init() {
		mmu = MMU.getMMU();
		Memory.PAGE = false;
		Memory.SEGMENT = false;
		memory = Memory.getMemory();
		helper = new MemTestHelper();
		MemoryInterface proxyMemory = (MemoryInterface) Proxy.newProxyInstance(
				MemoryInterface.class.getClassLoader(),
				new Class[]{MemoryInterface.class},
				new ProxyMemory(memory));
		DiskInterface proxyDisk = (DiskInterface) Proxy.newProxyInstance(
				DiskInterface.class.getClassLoader(),
				new Class[]{DiskInterface.class},
				new ProxyDisk(Disk.getDisk()));
		mmu.setMemory(proxyMemory);
		proxyMemory.setDisk(proxyDisk);
	}

	@Test
	public void test1() {
		int len = 128;
		char[] data = helper.fillData((char)0b00001111, 128);
//		assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
		mmu.readTest("000000000000000000000000000000000000000000000000", len);
		String actual = outputStream.toString();
		String expect = new String(data);
		assertEquals(expect, actual);
	}

	@Test
	public void test2() {
		String eip = "00000000000000000000000000000000";
		int len = 128;
		char[] data = helper.fillData((char)0b00001111, 128);
//		assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
		String expect = new String(data);
		mmu.readTest("000000000000000000000000000000000000000000000000", len);
		data = helper.fillData((char)0b00000011, 128);
		Disk.getDisk().write(eip, len, data);
//		assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
		mmu.readTest("000000000000000000000000000000000000000000000000", len);
		String actual = outputStream.toString();
		expect = expect + new String(data);
		assertEquals(expect, actual);

	}

	@After
	public void after() {
		// test2会写磁盘
		Disk.getDisk().write("00000000000000000000000000000000", 128, helper.fillData((char)0b00001111, 128));
		helper.clearAll();
	}

}
