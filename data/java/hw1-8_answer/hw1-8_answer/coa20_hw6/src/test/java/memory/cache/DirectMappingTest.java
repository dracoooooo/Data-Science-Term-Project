package memory.cache;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.SetAssociativeMapping;
import memory.cacheReplacementStrategy.FIFOReplacement;
import memory.cacheWriteStrategy.WriteBackStrategy;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import transformer.Transformer;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// 直接映射无替换策略
public class DirectMappingTest {

    SetAssociativeMapping setAssociativeMapping;

    @Before
    public void init(){
        setAssociativeMapping = new SetAssociativeMapping();
        setAssociativeMapping.setSetSize(1);
        setAssociativeMapping.setSETS(1024);
        Cache.getCache().setStrategy(setAssociativeMapping, new FIFOReplacement(), new WriteBackStrategy());
    }

    @Test
    public void test01() {
		Memory memory = Memory.getMemory();
		Cache cache = Cache.getCache();
        char[] data = {0b11110000, 0b10101011, 0b000101011, 0b10100001, 0b00111100, 0b10011101};
        String eip = "00000000000000000000000000000000";
        memory.write(eip, data.length, data);
        char[] dataRead = cache.read(eip, data.length);
        // 判断是否能够正确读出数据
        assertArrayEquals(data, dataRead);
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test02() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        char[] data = {0b11110000, 0b10101011, 0b000101011, 0b10100001, 0b00111100, 0b10011101};
        String eip = "00000000010100000000000000000000";
        memory.write(eip, data.length, data);
        char[] dataRead = cache.read(eip, data.length);
        // 判断是否能够正确读出数据
        assertTrue(Arrays.equals(data, cache.read(eip, data.length)));
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000001010000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test03() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Transformer t = new Transformer();
        char[] data = {0b11110000, 0b10101011, 0b000101011, 0b10100001, 0b00111100, 0b10011101};
        String eip = "00000001111101110100100001010101";
        memory.write(eip, data.length, data);
		// 判断是否能够正确读出数据
        assertTrue(Arrays.equals(data, cache.read(eip, data.length)));
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{Integer.valueOf(t.binaryToInt("0111010010"))}, new boolean[]{true}, new char[][]{"0000000111110000000000".toCharArray()}));
        cache.clear();
    }

}
