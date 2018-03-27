package com.yumtao.nio;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 通道间数据交互  channel --> channel
 * @author YQT
 *
 */
public class TransferData {
    public static void main(String[] args) throws Exception{
        RandomAccessFile srcFile = new RandomAccessFile("src/test.txt", "rw");
        FileChannel srcChannel = srcFile.getChannel();
        
        RandomAccessFile tarFile = new RandomAccessFile("src/toTest.txt", "rw");
        FileChannel tarChannel = tarFile.getChannel();
        
        long position = 0;
        long count = srcChannel.size();
        System.out.println(count);
//        tarChannel.transferFrom(srcChannel, position, count);
        
        srcChannel.transferTo(position, count, tarChannel);

    }
}
