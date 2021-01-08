package memory;


import transformer.Transformer;
import util.CRC;

import java.util.Arrays;

/**
 * 磁盘抽象类，磁盘大小为64MB
 */
public class Disk {

    public static int DISK_SIZE_B = 64 * 1024 * 1024;      // 磁盘大小 64 MB

    private static Disk diskInstance = new Disk();

    /**
     * 请勿修改下列属性，至少不要修改一个扇区的大小，如果要修改请保证磁盘的大小为64MB
     */
    public static final int CYLINDER_NUM = 8;
    public static final int TRACK_PER_PLATTER = 16;
    public static final int SECTOR_PER_TRACK = 128;
    public static final int BYTE_PER_SECTOR = 512;
    public static final int PLATTER_PER_CYLINDER = 8;

    public static final String POLYNOMIAL = "11000000000100001";
    public disk_head DISK_HEAD = new disk_head();

    RealDisk disk = new RealDisk();

    /**
     * 初始化
     */
    private Disk() { }

    public static Disk getDisk() {
        return diskInstance;
    }

    /**
     * 读磁盘
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len) {
        int start = Integer.parseInt(new Transformer().binaryToInt("0" + eip));
        int real_start = start;
        if (start % BYTE_PER_SECTOR != 0) {
            real_start = (start / BYTE_PER_SECTOR) * BYTE_PER_SECTOR;
        }

        int real_len = len;
        if (len % BYTE_PER_SECTOR != 0) {
            real_len = (len / BYTE_PER_SECTOR + 1) * BYTE_PER_SECTOR;
        }

        char[] data = new char[real_len];
        DISK_HEAD.Seek(real_start);
        for (int i = 0; i < real_len; i++) {
            data[i] = disk.getData(DISK_HEAD);
            if (i % BYTE_PER_SECTOR == BYTE_PER_SECTOR - 1) {
                assert Arrays.equals(CRC.Check(ToBitStream(Arrays.copyOfRange(data, i - BYTE_PER_SECTOR + 1, i + 1)), POLYNOMIAL, ToBitStream(disk.getCRC(DISK_HEAD))), "0000000000000000".toCharArray());
            }
        }
        return Arrays.copyOfRange(data, start - real_start, start - real_start + len);
    }

    /**
     * 写磁盘
     * @param eip
     * @param len
     * @param data
     */
    public void write(String eip, int len, char[] data) {
        int start = Integer.parseInt(new Transformer().binaryToInt("0" + eip));
        DISK_HEAD.Seek(start);
        for (int i = 0; i < len; i++) {
            disk.setData(DISK_HEAD, data[i]);
        }
        disk.setCRC(DISK_HEAD);
        DISK_HEAD.adjust();
    }

    /**
     * 写磁盘（地址为Integer型）
     * @param eip
     * @param len
     * @param data
     */
    public void write(int eip, int len, char[] data) {
        DISK_HEAD.Seek(eip);
//        int sectorNum = len / BYTE_PRE_SECTOR;
//        for (int i = 0; i < sectorNum; i++) {
//            disk.setSector(DISK_HEAD, Arrays.copyOfRange(data, i*BYTE_PRE_SECTOR, (i+1)*BYTE_PRE_SECTOR));
//        }
        for (int i = 0; i < len; i++) {
            disk.setData(DISK_HEAD, data[i]);
        }
        disk.setCRC(DISK_HEAD);
        DISK_HEAD.adjust();
    }

    /**
     * 该方法仅用于测试
     */
    public char[] getCRC(int eip) {
        DISK_HEAD.Seek(eip);
        return disk.getCRC(DISK_HEAD);
    }

    /**
     * 磁头
     */
    private class disk_head {
        int cylinder;
        int platter;
        int track;
        int sector;
        int point;

        /**
         * 调整磁头的位置
         */
        public void adjust() {
            if (point == BYTE_PER_SECTOR) {
                point = 0;
                sector++;
            }
            if (sector == SECTOR_PER_TRACK) {
                sector = 0;
                track++;
            }
            if (track == TRACK_PER_PLATTER) {
                track = 0;
                platter++;
            }
            if (platter == PLATTER_PER_CYLINDER) {
                platter = 0;
                cylinder++;
            }
            if (cylinder == CYLINDER_NUM) {
                cylinder = 0;
            }
        }

        /**
         * 磁头回到起点
         */
        public void Init() {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cylinder = 0;
            track = 0;
            sector = 0;
            point = 0;
            platter = 0;
        }

        /**
         * 将磁头移动到目标位置
         * @param start
         */
        public void Seek(int start) {
            try {
                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = cylinder; i < CYLINDER_NUM; i++) {
                for (int t = platter; t < PLATTER_PER_CYLINDER; t++) {
                    for (int j = track; j < TRACK_PER_PLATTER; j++) {
                        for (int z = sector; z < SECTOR_PER_TRACK; z++) {
                            for (int k = point; k < BYTE_PER_SECTOR; k++) {
                                if ((i * PLATTER_PER_CYLINDER * TRACK_PER_PLATTER * SECTOR_PER_TRACK * BYTE_PER_SECTOR + t * TRACK_PER_PLATTER * SECTOR_PER_TRACK * BYTE_PER_SECTOR + j * SECTOR_PER_TRACK * BYTE_PER_SECTOR + z * BYTE_PER_SECTOR + k) == start) {
                                    cylinder = i;
                                    track = j;
                                    sector = z;
                                    point = k;
                                    platter = t;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            Init();
            Seek(start);
        }

        @Override
        public String toString() {
            return "The Head Of Disk Is In\n" +
                    "platter:\t" + cylinder + "\n" +
                    "track:\t\t" + track + "\n" +
                    "sector:\t\t" + sector + "\n" +
                    "point:\t\t" + point;
        }
    }

    /**
     * 600 Bytes/Sector
     */
    private class Sector {
        char[] gap1 = new char[17];
        IDField idField = new IDField();
        char[] gap2 = new char[41];
        DataField dataField = new DataField();
        char[] gap3 = new char[20];
    }

    /**
     * 7 Bytes/IDField
     */
    private class IDField {
        char SynchByte;
        char[] Track = new char[2];
        char Head;
        char sector;
        char[] CRC = new char[2];
    }

    /**
     * 515 Bytes/DataField
     */
    private class DataField {
        char SynchByte;
        char[] Data = new char[512];
        char[] CRC = new char[2];
    }

    /**
     * 128 sectors pre track
     */
    private class Track {
        Sector[] sectors = new Sector[SECTOR_PER_TRACK];

        Track() {
            for (int i = 0; i < SECTOR_PER_TRACK; i++) sectors[i] = new Sector();
        }
    }


    /**
     * 32 tracks pre platter
     */
    private class Platter {
        Track[] tracks = new Track[TRACK_PER_PLATTER];

        Platter() {
            for (int i = 0; i < TRACK_PER_PLATTER; i++) tracks[i] = new Track();
        }
    }

    /**
     * 8 platter pre Cylinder
     */
    private class Cylinder {
        Platter[] platters = new Platter[PLATTER_PER_CYLINDER];

        Cylinder() {
            for (int i = 0; i < PLATTER_PER_CYLINDER; i++) platters[i] = new Platter();
        }
    }


    private class RealDisk {
        Cylinder[] cylinders = new Cylinder[CYLINDER_NUM];

        public RealDisk() {
            for (int i = 0; i < CYLINDER_NUM; i++) cylinders[i] = new Cylinder();
        }

        public void setData(disk_head d, char s) {
            if (DISK_HEAD.point >= BYTE_PER_SECTOR) {
                setCRC(d);
                DISK_HEAD.adjust();
            }
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data[d.point++] = s;
        }

        public char getData(disk_head d) {
            if (DISK_HEAD.point >= BYTE_PER_SECTOR) {
                DISK_HEAD.adjust();
            }
            try {
//                Thread.sleep(0);
//                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data[d.point++];
        }

        @Deprecated
        public void setSector(disk_head d, char[] data) {
            if (DISK_HEAD.point >= BYTE_PER_SECTOR) {
                setCRC(d);
                DISK_HEAD.adjust();
            }
            cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.Data = data;
            DISK_HEAD.point = BYTE_PER_SECTOR;
        }

        public char[] getCRC(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC;
        }

        public void setCRC(disk_head d) {
            Sector sector = cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector];
            char[] data = sector.dataField.Data;
            sector.dataField.CRC = ToByteStream(CRC.Calculate(ToBitStream(data), POLYNOMIAL));
        }
    }

    /**
     * 将Byte流转换成Bit流
     * @param data
     * @return
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        int index = 0;
        for (char datum : data) {
            for (int j = 0; j < 8; j++) {
                t[index++] = (char) (((datum >> (7 - j)) & (0b00000001)) + '0');
            }
        }
        return t;
    }

    /**
     * 将Bit流转换为Byte流
     * @param data
     * @return
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        int j = 0;
        int index = 0;
        for (char datum : data) {
            t[index] = (char) (( datum-'0' << (7-j) ) | t[index]);
            j++;
            if (j % 8 == 0) {
                index ++;
                j = 0;
            }
        }
        return t;
    }

    /**
     * 这个方法仅供测试，请勿修改
     * @param eip
     * @param len
     * @return
     */
    public char[] readTest(String eip, int len){
        char[] data = read(eip, len);
        System.out.print(data);
        return data;
    }

    public static void main(String[] args) {

    }
}
