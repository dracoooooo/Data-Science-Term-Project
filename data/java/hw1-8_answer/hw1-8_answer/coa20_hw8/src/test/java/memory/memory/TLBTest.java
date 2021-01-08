package memory.memory;

import cpu.MMU;
import cpu.TLB;
import memory.Disk;
import memory.DiskInterface;
import memory.Memory;
import memory.MemoryInterface;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;

/**
 * @Author: pkun
 * @CreateTime: 2020-11-28 21:03
 */
public class TLBTest {

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

    /**
     * Situation: 段替换
     */
    @Test
    public void TLBtest() {
        if(TLB.isAvailable){
            int len1 = 20 * 1024 * 1024;
            int len2 = 32 * 1024 * 1024;
            int len3 = 16 * 1024 * 1024;
            int t = 1024 * 1025 ;
            char[] data1 = helper.fillData((char) 0b00001111, len1/2);    // 磁盘存储位置[0M-20M)
            char[] data2 = helper.fillData((char)0b01010101, t);	// 磁盘存储位置[32M-64M)
            char[] data3 = helper.fillData((char) 0b00110011, 1025 * 1024);    // 磁盘存储位置[64M-80M)
            // 初始化内存(10 16 -6)
            String eip1 = "00000000000000000000000000000000";
            String eip2 = "00000000101000000000000000000000";
            String eip3 = "00000001101000000000000000000000";
            memory.alloc_seg_force(0, eip1, len1 / 2, true, "");
            memory.alloc_seg_force(1, eip2, len2 / 2, true, "");
            memory.alloc_seg_force(2, eip3, len3 / 2, false, "");
//		memory.invalid(2, -1);
            // 读取第三个段(替换第一个段)(内存状态8 16 -8)
            //assertArrayEquals(data3, mmu.read("000000000001000000010000000000000000000000000000", 1025 * 1024));
            mmu.readTest("000000000001000000001000000000000000000000000000", t);
            // 段三直接移除内存(内存状态-8 16 -8)
            memory.invalid(2, -1);
            // 读取第一个段(内存状态16 10 -6)
            //assertArrayEquals(data1, mmu.read("000000000000000000000000000000000000000000000000", 77));
            mmu.readTest("000000000000000000000000000000000000000000000000", len1/2);
            String expect = new String(data2) + new String(data1);
            String actual = outputStream.toString();
            assertEquals(expect, actual);
        }else{
            Assert.fail();
        }
    }
}
