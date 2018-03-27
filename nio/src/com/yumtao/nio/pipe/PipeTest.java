package com.yumtao.nio.pipe;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PipeTest {
    public static void main(String[] args) {
        Pipe pipe = null;
        ExecutorService exec = Executors.newFixedThreadPool(2);

        try {
            pipe = Pipe.open();

            final Pipe pipeTmp = pipe;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    sink(pipeTmp);
                }
            });

            exec.execute(new Runnable() {
                @Override
                public void run() {
                    source(pipeTmp);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * write data to the sink
     * @param pipeTmp
     */
    private static void sink(final Pipe pipeTmp) {
        SinkChannel sinkChannel = pipeTmp.sink(); // 获取sink通道
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("put data to the sink");
                String newData = "Pipe Test At Time " + System.currentTimeMillis();
                ByteBuffer buf = ByteBuffer.allocate(1024);
                buf.clear();
                buf.put(newData.getBytes());
                buf.flip();

                while (buf.hasRemaining()) {
                    System.out.println("\t\t\t" + buf);
                    sinkChannel.write(buf);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * read data from the source
     * @param pipeTmp
     */
    private static void source(final Pipe pipeTmp) {
        SourceChannel sourceChannel = pipeTmp.source(); // 获取source通道
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("get data from the source");
                ByteBuffer buf = ByteBuffer.allocate(1024);
                buf.clear();
                int bytesRead = 0;
                while ((bytesRead = sourceChannel.read(buf)) > 0) {
                    System.out.println("\t\t\t" + "bytesRead = " + bytesRead);
                    buf.flip();
                    System.out.print("\t\t\t");
                    while (buf.hasRemaining()) {
                        System.out.print((char) buf.get());
                    }
                    System.out.println();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
