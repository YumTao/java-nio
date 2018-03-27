package com.yumtao.nio.tcp.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private SocketChannel sc = null;

    private ByteBuffer buffer = ByteBuffer.allocate(1024); // 获取buffer分配缓冲区大小
    
    private String name = "";
    
    private final String SPLIT = "#@#";
    
    private final String NAME_EXIST = "sorry, name already exist";
    
    public static void main(String[] args) throws Exception {
        new Client().init();
    }

    private void init() {
        try {
            sc = SocketChannel.open(); // 获取实例,此对象同BIO的Socket
            sc.configureBlocking(false); // 开启非阻塞模式
            sc.connect(new InetSocketAddress("127.0.0.1", 8888)); // 与服务器建立链接

            Scanner scanner = new Scanner(System.in);

            while (sc.finishConnect()) {
                // 开启线程读取信息
                doRead();
                
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * NIO的所有数据传输都需经过buffer 1.创建buffer 2.读取socketChannel 数据到buffer 3.读取buffer
                 */

                while (scanner.hasNextLine()) {
                    String request = scanner.nextLine();
                    if ("".equals(request)) {
                        continue;
                    }
                    
                    // 发给服务器通讯的格式：   名字 + 分隔符 + 内容 （内容可能为空）
                    if ("".equals(name)) {
                        name = request;
                        request = name + SPLIT;
                    }else {
                        request = name + SPLIT + request;
                    }
                    
                    buffer.clear();
                    buffer.put(request.getBytes());
                    buffer.flip();
                    sc.write(buffer);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != sc) {
                try {
                    sc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 线程:读取服务器信息
     */
    private void doRead() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        ByteBuffer buff = ByteBuffer.allocate(1024);
                        int read = sc.read(buff);
                        if (read > 0) {
                            buff.flip();
                            String content = "";
                            while (buff.hasRemaining()) {
                                content += (char) buff.get();
                            }
                            
                            // 若系统发送通知名字已经存在，则需要换个昵称
                            if (NAME_EXIST.equals(content)) {
                                name = "";
                            }
                            System.out.println(content);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });

        thread.start();
    }
}
