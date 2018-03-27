package com.yumtao.nio.filechannel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * NIO FileChannle 读取文件
 * 
 * @author YQT
 *
 */
public class FileChannelTest {
    public static void main(String[] args) throws Exception {
        System.out.println("--------nio--------");
        nio();
        System.out.println("--------nio--------");
        System.out.println();
        System.out.println("--------bio--------");
        bio();
        System.out.println("--------bio--------");
    }

    private static void bio() throws FileNotFoundException, IOException {
        BufferedReader bReader = new BufferedReader(new FileReader("src/test.txt"));
        String content = null;
        while (null != (content = bReader.readLine())) {
            System.out.println(content);
        }
        bReader.close();

    }

    private static void nio() throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile("src/test.txt", "rw"); // 读写流关联文件
        FileChannel channel = file.getChannel(); // 获取通道channel
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 创建缓存
        int len = 0; // 定义读取长度
        while ((len = channel.read(buffer)) > 0) { // 开始读取通道数据到缓存
            System.out.println("valid length = " + len);
            buffer.flip(); // 缓存从写模式转换到读模式(设置limit=position,position=0,mark=-1)
            while (buffer.hasRemaining()) { // 开始读取缓存
                System.out.print((char) buffer.get()); // 获取缓存中的数据
            }
            buffer.clear(); // 清空缓存
            System.out.println();
        }
        file.close(); // 关流
    }
}
