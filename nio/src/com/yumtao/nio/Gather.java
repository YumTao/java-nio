package com.yumtao.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 测试聚集:通道将数据从多个缓存中聚集至通道中,test gather
 * @author YQT
 *
 */
public class Gather {
    public static void main(String[] args) throws Exception {
        RandomAccessFile file = new RandomAccessFile("src/test.txt", "rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        ByteBuffer buffer2 = ByteBuffer.allocate(20);
        gather(channel, buffer, buffer2);
        file.close();

    }
    
    public static void gather(FileChannel channel, ByteBuffer buffer,  ByteBuffer buffer2) throws FileNotFoundException, IOException {
        ByteBuffer[] buffers = { buffer, buffer2 }; // 按照数组的顺序进行写入, 先写buffer2,再写buffer
        long len = 0;
        while ((len = channel.read(buffers)) != -1) {
            System.out.println("len = " + len);
            buffer.flip();
            buffer2.flip();
            while (buffer.hasRemaining()) {
                System.out.println((char) buffer.get());
            }
            buffer.clear();
            while (buffer2.hasRemaining()) {
                System.out.println((char) buffer2.get());
            }
            buffer2.clear();
        }
        
        // =============分隔符================= 
        // 上面是Scatter,下面是Gather
        RandomAccessFile file = new RandomAccessFile("src/tar.txt", "rw");
        FileChannel channel2 = file.getChannel();
        channel2.write(buffers);
        file.close();
    }
}
