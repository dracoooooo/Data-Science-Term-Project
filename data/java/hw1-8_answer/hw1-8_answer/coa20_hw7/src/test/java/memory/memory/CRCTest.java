package memory.memory;

import memory.Disk;
import org.junit.*;
import org.junit.runners.MethodSorters;
import transformer.Transformer;
import util.CRC;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @Author: Alpha Go
 * @CreateTime: 2020-11-23 23:38
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CRCTest {


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

    @Test
    public void CRCTrueTest1(){
        char[] data = "11100110".toCharArray();
        String p = "1011";
        char[] originCRC = CRC.Calculate(data, p);
        CRC.CalculateTest(data, p);
        CRC.CheckTest(data, p, originCRC);
        String expected = "100000";
        String actual = outputStream.toString();
        boolean result = expected.equals(actual);
        assertTrue(result);
    }

    @Test
    public void CRCTrueTest2(){
        char[] data = "100011".toCharArray();
        String p = "1001";
        char[] originCRC = CRC.Calculate(data, p);
        CRC.CalculateTest(data, p);
        CRC.CheckTest(data, p, originCRC);
        String expected = "111000";
        String actual = outputStream.toString();
        boolean result = expected.equals(actual);
        assertTrue(result);
    }

    @Test
    public void CRCTrueTest3(){
        char[] data = "100011".toCharArray();
        String p = "1011";
        char[] originCRC = CRC.Calculate(data, p);
        CRC.CalculateTest(data, p);
        CRC.CheckTest(data, p, originCRC);
        String expected = "111000";
        String actual = outputStream.toString();
        boolean result = expected.equals(actual);
        assertTrue(result);
    }

    @Test
    public void CRCFalseTest() {
        char[] data = "11100110".toCharArray();
        String p = "1001";
        char[] originCRC = CRC.Calculate(data, p); // 001
        originCRC[1]='1';
        CRC.CalculateTest(data, p);
        CRC.CheckTest(data, p, originCRC);
        String expected = "001010";
        String actual = outputStream.toString();
        boolean result = expected.equals(actual);
        assertTrue(result);
    }

}
