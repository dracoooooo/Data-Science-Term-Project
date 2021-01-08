package memory.cache;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.SetAssociativeMapping;
import memory.cacheReplacementStrategy.FIFOReplacement;
import memory.cacheWriteStrategy.WriteBackStrategy;
import org.junit.Before;
import org.junit.Test;
import transformer.Transformer;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

/**
 * Test for the Set-Associative Mapping cache
 */
public class SetAssociativeMappingTest {
    private Memory memory;
    private Cache cache;

    private String chReplicate(char ch, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            sb.append(ch);
        }
        return sb.toString();
    }
    @Before
    public void init() {
        this.memory = Memory.getMemory();
        this.cache = Cache.getCache();
        SetAssociativeMapping setAssociativeMapping = new SetAssociativeMapping();
        setAssociativeMapping.setSETS(256);
        setAssociativeMapping.setSetSize(4);
        this.cache.setStrategy(setAssociativeMapping, new FIFOReplacement(), new WriteBackStrategy());

    }

    @Test
    public void test1() {
        String eip = "00000000000000000000000000000000";
        char[] input = {0b11110100};
        memory.write(eip, input.length, input);
        assertTrue(Arrays.equals(input, cache.read(eip, input.length)));
        assertTrue(Arrays.equals(input, memory.read(eip, input.length)));
        input = new char[]{0b11110100, 0b11010100, 0b01010101, 0b01010100,
                0b01111110, 0b00000000, 0b00000000, 0b11010100,
                0b11100110, 0b11111010, 0b11010100, 0b11111010,
                0b10011010, 0b11010100, 0b01000110, 0b11110101};
        for (int i = 0; i < 10; ++i) {
            memory.write(eip, input.length, input);
            assertTrue(Arrays.equals(input, cache.read(eip, input.length)));
            assertTrue(Arrays.equals(input, memory.read(eip, input.length)));
        }
        assertTrue(Arrays.equals(new char[]{0b11100110, 0b11111010, 0b11010100},
                cache.read(eip + 8, 3)));
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test2() {
        String eip = "00000000001000000000010000000000";
        char[] input = {0b00000000, 0b11010100, 0b01111110, 0b00000000, 0b00000000,
                0b11010100, 0b10011010, 0b11010100, 0b01000110, 0b11110101,
                0b11110100, 0b11010100, 0b01010101, 0b01111110, 0b00000000,
                0b00000000, 0b11010100, 0b01111110, 0b00000000, 0b00000000,
                0b11010100, 0b10011010, 0b11010100, 0b01000110, 0b11110101,
                0b11110100, 0b11010100, 0b01010101, 0b01111110, 0b00000000};
        memory.write(eip, input.length, input);
        assertTrue(Arrays.equals(input, cache.read(eip, input.length)));
        assertTrue(Arrays.equals(input, memory.read(eip, input.length)));
        assertTrue(cache.checkStatus(new int[]{4}, new boolean[]{true}, new char[][]{"0000000000100000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test3() {
        String eip = "00000000010010101101100000000000";
        char[] input = {0b11110111, 0b01111110, 0b10010110, 0b01111110, 0b00110101,
                0b10010100, 0b11010111, 0b10011101, 0b01111100, 0b11000000};
        memory.write(eip, input.length, input);
        assertTrue(Arrays.equals(input, cache.read(eip, input.length)));
        assertTrue(Arrays.equals(input, memory.read(eip, input.length)));
        assertTrue(cache.checkStatus(new int[]{Integer.parseInt(new Transformer().binaryToInt("0" + "10110110")) * 4}, new boolean[]{true}, new char[][]{"0000000001001000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test4() {
        String group1 = chReplicate('0', 14) + "00000001" + chReplicate('0', 10);
        String group2 = chReplicate('0', 14) + "00000010" + chReplicate('0', 10);

        char[] lineInput = new char[1024];
        Arrays.fill(lineInput, (char) 0b11000000);
        memory.write(group1, 1024, lineInput);
        assertTrue(Arrays.equals(lineInput, cache.read(group1, 1024)));

        Arrays.fill(lineInput, (char) 0b11000001);
        memory.write(group2, 1024, lineInput);
        assertTrue(Arrays.equals(lineInput, cache.read(group2, 1024)));

        assertTrue(cache.checkStatus(new int[]{1*4, 2*4}, new boolean[]{true, true}, new char[][]{"0000000000000000000000".toCharArray(), "0000000000000000000000".toCharArray()}));

        cache.clear();
    }
}
