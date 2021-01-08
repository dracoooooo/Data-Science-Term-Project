package memory.memory;

import cpu.MMU;
import memory.Disk;
import memory.DiskInterface;
import memory.Memory;
import memory.MemoryInterface;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

/**
 * 段页式内存管理模式下读数据，需要逻辑地址转线性地址再转物理地址
 */
public class PSTest {

    static MMU mmu;

    static MemoryInterface memory;

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
        Memory.PAGE = true;
        Memory.SEGMENT = true;
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
        String eip = "00000000000000000000000000000000";
        int len = 2 * 1024;
        char[] data = helper.fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len / 2, true, "");
//		assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
        mmu.readTest("000000000000000000000000000000000000000000000000", len);
        String actual = outputStream.toString();
        String expect = new String(data);
        assertEquals(expect, actual);
    }


    /**
     * Situation: 段不在内存 + 页替换
     */
    @Test
    public void test2() {
        String eip = "00000000000000000000000000000000";
        int len = 2 * 1024;
        char[] data = helper.fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len / 2, false, "");
//		memory.invalid(0, -1);
//		assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
        mmu.readTest("000000000000000000000000000000000000000000000000", len);
        String actual = outputStream.toString();
        String expect = new String(data);
        assertEquals(expect, actual);
    }

    @Test
    public void test3() {
        int len = 2 * 1024;
        char[] data_fraction1 = helper.fillData((char) 0b00001111, len / 2);    // 磁盘存储位置[19.999M-20M)
        char[] data_fraction2 = helper.fillData((char) 0b00000011, len / 2);    // 磁盘存储位置[20M-20.001M)
        memory.alloc_seg_force(0, "00000000000000000000000000000000", len / 2, false, "");
//		assertArrayEquals(data_fraction1, mmu.read("000000000000000000000100111111111111000000000000", len / 2));
//		assertArrayEquals(data_fraction2, mmu.read("000000000000000000000101000000000000000000000000", len / 2));
//		assertArrayEquals(data_fraction1, mmu.read("000000000000000000000100111111111111000000000000", len / 2));
//		assertArrayEquals(data_fraction2, mmu.read("000000000000000000000101000000000000000000000000", len / 2));
        mmu.readTest("000000000000000000000100111111111111000000000000", len / 2);
        mmu.readTest("000000000000000000000101000000000000000000000000", len / 2);
        mmu.readTest("000000000000000000000100111111111111000000000000", len / 2);
        mmu.readTest("000000000000000000000101000000000000000000000000", len / 2);
        String expect = new String(data_fraction1) + new String(data_fraction2) + new String(data_fraction1) + new String(data_fraction2);
        String actual = outputStream.toString();
        assertEquals(expect, actual);
    }




    @After
    public void after() {
        helper.clearAll();
    }

}
