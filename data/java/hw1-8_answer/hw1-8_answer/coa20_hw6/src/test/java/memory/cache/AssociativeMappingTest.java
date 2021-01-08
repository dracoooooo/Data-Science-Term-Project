package memory.cache;


import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.SetAssociativeMapping;
import memory.cacheReplacementStrategy.FIFOReplacement;
import memory.cacheWriteStrategy.WriteBackStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class AssociativeMappingTest {

    SetAssociativeMapping setAssociativeMapping;
    @Before
    public void init(){
        setAssociativeMapping = new SetAssociativeMapping();
        setAssociativeMapping.setSETS(1);
        setAssociativeMapping.setSetSize(1024);
        Cache.getCache().setStrategy(setAssociativeMapping, new FIFOReplacement(), new WriteBackStrategy());
    }

    @Test
    public void test01() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();;
        char[] input = new char[1024 * 1024];
        char[] second = new char[1024];
        char[] third = new char[1024];
        Arrays.fill(input, (char) 0b11111111);
        Arrays.fill(second, (char) 0b10001111);
        Arrays.fill(third, (char) 0b10001100);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000000000000000010000000000";
        String eip3 = "00000001000000000000000000000000";
		//write First
        memory.write(eip1, input.length, input);
        char[] dataRead = cache.read(eip1, input.length);
        assertTrue(Arrays.equals(input, dataRead));;
		assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        //write Sec  HIT!
        memory.write(eip2, second.length, second);
        dataRead = cache.read(eip2, second.length);
        assertTrue(Arrays.equals(second, dataRead));
		//write Third
		memory.write(eip3, third.length, third);
		dataRead = cache.read(eip3, third.length);
		assertTrue(Arrays.equals(third, dataRead));
		assertTrue(cache.checkStatus(new int[]{1, 0}, new boolean[]{true, true}, new char[][]{"0000000000000000000001".toCharArray(), "0000000100000000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test02() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        Arrays.fill(input1, (char) 0b10001011);
        Arrays.fill(input2, (char) 0b10101101);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000000000000001110000000000";
        memory.write(eip1, input1.length, input1);
        char[] dataRead = cache.read(eip1, input1.length);
        assertTrue(Arrays.equals(input1, dataRead));
        //eip2
        memory.write(eip2, input2.length, input2);
        dataRead = cache.read(eip2, input2.length);
        assertTrue(Arrays.equals(input2, dataRead));
		assertTrue(cache.checkStatus(new int[]{7}, new boolean[]{true}, new char[][]{"0000000000000000000111".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test03() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();

        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        Arrays.fill(input1, (char)0b11111111);
        Arrays.fill(input2, (char)0b01010101);
        Arrays.fill(input3, (char)0b01110111);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000101000000000010000000000";
        String eip3 = "00000000011100000000010000000001";

        memory.write(eip1, input1.length, input1);
        // cache里现在应该全是 0b11111111
        char[] dataRead = cache.read(eip1, 1024 * 1024);
        assertTrue(Arrays.equals(input1, dataRead));

        memory.write(eip2, input2.length, input2);
        // cache中第一个块应该被替换，相应的tag需要改动
        dataRead = cache.read(eip2, 1024);
        assertTrue(Arrays.equals(input2, dataRead));
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000010100000000001".toCharArray()});

        // cache中的第二个块和第三个块应该被替换，相应的tag需要改动
        memory.write(eip3, input3.length, input3);
        dataRead = cache.read(eip3, 1024);
        assertTrue(Arrays.equals(input3, dataRead));
        assertTrue(cache.checkStatus(new int[]{1, 2}, new boolean[]{true, true}, new char[][]{"0000000001110000000001".toCharArray(), "0000000001110000000010".toCharArray()}));
		cache.clear();
    }
}
