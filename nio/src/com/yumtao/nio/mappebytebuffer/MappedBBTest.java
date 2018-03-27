package com.yumtao.nio.mappebytebuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBBTest {
    public static void main(String[] args) throws Exception {
        System.out.println("ByteBuffer:");
        bBuffer();
        System.out.println("===========");
        System.out.println("MappedByteBuffer:");
        mappedBB();
    }

    private static void mappedBB() throws FileNotFoundException, IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("src/toTest.txt", "rw");
        FileChannel fChannel = randomAccessFile.getChannel();
        long timeBegin = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate((int)randomAccessFile.length());
        MappedByteBuffer map = fChannel.map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
        long timeEnd = System.currentTimeMillis();
        System.out.println("Read time:" + (timeEnd - timeBegin) + "ms");
    }

    private static void bBuffer() throws FileNotFoundException, IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("src/toTest.txt", "rw");
        FileChannel fChannel = randomAccessFile.getChannel();
        long timeBegin = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate((int)randomAccessFile.length());
        
        fChannel.read(buffer);
        long timeEnd = System.currentTimeMillis();
        System.out.println("Read time:" + (timeEnd - timeBegin) + "ms");
    }
}
