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

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-12 23:53
 */
public class WriteCacheTest {

    private Memory memory;
    private Cache cache;


    @Before
    public void init(){
        cache = Cache.getCache();
        memory = Memory.getMemory();
        char[] init = new char[1024 * 1024 * 32];
        Arrays.fill(init, (char) 0b00000000);
        memory.write("00000000000000000000000000000000", init.length, init);
        SetAssociativeMapping setAssociativeMapping = new SetAssociativeMapping();
        setAssociativeMapping.setSETS(256);
        setAssociativeMapping.setSetSize(4);
        this.cache.setStrategy(setAssociativeMapping, new FIFOReplacement(), new WriteBackStrategy());
    }

    /**
     * 写入数据，但是不写回内存
     */
    @Test
    public void test01() {
        String eip = "00000000000000000000000000000000";
        char[] input = {0b11110100, 0b11010100};
        memory.write(eip, input.length, input);
        assertTrue(!cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.read(eip, input.length);
        cache.write(eip, 2, new char[]{0b00000000, 0b00000011});
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        assertTrue(Arrays.equals(new char[]{0b00000000, 0b00000011}, cache.read(eip, 2)));
        assertTrue(Arrays.equals(memory.read(eip, 2), new char[]{0b11110100, 0b11010100}));
        cache.clear();
    }

    /**
     * 从cache读入数据，然后修改cache，然后再读内存，触发写回
     */
    @Test
    public void test02(){
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        Arrays.fill(input1, (char)0b11111111);
        Arrays.fill(input2, (char)0b01010101);
        Arrays.fill(input3, (char)0b10101010);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000101000000000010000000000";
        String eip3 = "00000000000000000000010000000000";
        memory.write(eip1, input1.length, input1);
        cache.read(eip1,input1.length);
        cache.write("00000000000000000000010000000000", input2.length, input2);
        //assertTrue(Arrays.equals(memory.read(eip3, input2.length)));
        memory.write(eip2, input3.length, input3);
        cache.read(eip2, input3.length);
        assertTrue(Arrays.equals(memory.read(eip3, input2.length), input2));
    }

    /**
     * 写入数据，修改内存，发生替换，数据写回，cache数据更新
     * TODO 先写1KB的内容到Memory，然后用Cache读出来，然后再修改这1MB的内容，然后写1MB进内存，然后用Cache读出来，此时会发生替换，之前的1KB就会写回内存
     */
    @Test
    public void test03(){
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024 * 1024];
        char[] input4 = new char[1024];
        Arrays.fill(input1, (char)0b11111111);
        Arrays.fill(input2, (char)0b01010101);
        Arrays.fill(input3, (char)0b01110111);
        Arrays.fill(input4, (char)0b00000000);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000101000000000010000000000";
        // 数据位于cache第二组的第一行
        cache.write(eip2, input2.length, input2);
        assertTrue(Arrays.equals(input2, cache.read(eip2, input2.length)));
        assertTrue(Arrays.equals(memory.read(eip2, input2.length), input4));
        memory.write(eip1, input1.length, input1);
        assertTrue(Arrays.equals(input1, cache.read(eip1, input1.length)));
        // memory 被修改，所以cache中的所有被Match的行的valid都变成了False，再次替换的时候，有一行会被插入到第二组第一行的位置，此时发生替换
        assertTrue(cache.checkStatus(new int[]{4}, new boolean[]{true}, new char[][]{"0000000000001100000000".toCharArray()}));
        assertTrue(Arrays.equals(memory.read(eip2, input2.length), input2));
    }

    /**
     * 在同一个地址写入两次数据，不会发生变化
     */
    @Test
    public void test04(){
        String eip = "00000000000000000000000000000010";
        char[] input = "这样你满意了吗？".toCharArray();
        char[] input2 = "这样我满意了".toCharArray();
        cache.write(eip, input.length, input);
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.write(eip, input2.length, input2);
        assertTrue(Arrays.equals(cache.read(eip, input2.length), input2));
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
    }

    /**
     * same to test02
     * 先写1KB的内容到Memory，然后用Cache读出来，然后再修改这1MB的内容，然后写1MB进内存，然后用Cache读出来，此时会发生替换，之前的1KB就会写回内存
     */
    @Test
    public void test05(){
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1025];
        char[] input3 = new char[1024 * 1024];
        char[] input4 = new char[1025];
        Arrays.fill(input1, (char)0b11111111);
        Arrays.fill(input2, (char)0b01010101);
        Arrays.fill(input3, (char)0b01110111);
        Arrays.fill(input4, (char)0b00000000);
        String eip1 = "00000000000000000000000000000000";
        String eip2 = "00000000101000000000010000000000";
        // 数据位于cache第二组的第一行
        cache.write(eip2, input2.length, input2);
        assertTrue(Arrays.equals(input2, cache.read(eip2, input2.length)));
        assertTrue(Arrays.equals(memory.read(eip2, input2.length), input4));
        memory.write(eip1, input1.length, input1);
        assertTrue(Arrays.equals(input1, cache.read(eip1, input1.length)));
        // memory 被修改，所以cache中的所有被Match的行的valid都变成了False，再次替换的时候，有一行会被插入到第二组第一行的位置，此时发生替换
        assertTrue(cache.checkStatus(new int[]{4, 8}, new boolean[]{true, true}, new char[][]{"0000000000001100000000".toCharArray(), "0000000000001100000000".toCharArray()}));
        assertTrue(Arrays.equals(memory.read(eip2, input2.length), input2));
    }

    @Test
    public void EasterEgg(){
        String answer = "0101010001001001010010010110010001101000011011100111001101110011011001010100001101001000011011110101010001001111011000010110110101000001010000010110111001100101";
        String real =   "0101010001101000011001010101010001000001010010010110111001000011010011110100000101001001011100110100100001100001011011100110010001110011011011110110110101100101";
        String eip = "00000000011000110110111101100001";
        String eip2 = "00000000000000000110110001100011";
        cache.write(eip, eip2.length(), eip2.toCharArray());
        memory.write(eip2, answer.length(), answer.toCharArray());
        Boolean t = true;
        char[] d = cache.read(eip2, answer.length());
        for (int i = 0; i<answer.length(); i++){
            if(d[i]!=answer.charAt(i)) {
                t = false;
                break;
            }
        }
        if(t){
            assertTrue(true);
            System.out.println("Challenge me!");
            return;
        }
        t = true;
        for(int i = 0;i<real.length();i++){
            if(d[i]!=real.charAt(i)){
                t = false;
                break;
            }
        }
        if(t){
            assertTrue(true);
            System.out.println("Congratulation! You find the true answer~");
            return;
        }
        assertTrue(false);

    }



}
