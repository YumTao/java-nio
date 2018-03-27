package com.yumtao.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerChannelClass {
    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            selector = Selector.open(); // 获取selector对象
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false); // 非阻塞模式
            serverSocketChannel.socket().bind(new InetSocketAddress(8888)); // 绑定8888端口
            System.err.println("服务端启动成功,绑定端口8888");
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // channel 注册到selector上

            while (true) {
                if (selector.select(3000) == 0) {       // 返回为0 表示没有就绪的通道
                    System.out.println("----正在等待----");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        doAccept(key);
                    }
                    if (key.isReadable()) {
                        doRead(key);
                    }
                    if (key.isValid() && key.isWritable()) {
                        doWrite(key);
                    }
                    if (key.isValid() && key.isConnectable()) {
                        System.out.println("isConnectable is true");
                    }
                    iterator.remove();  // 注意:事件处理后,需要自己移除事件
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入数据
     * @param key
     * @throws IOException
     */
    private static void doWrite(SelectionKey key) throws IOException {
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel socketChannel = (SocketChannel)key.channel();
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
        buffer.compact();
    }

    /**
     * 读取请求信息
     * @param key
     * @throws IOException
     */
    private static void doRead(SelectionKey key) throws IOException{
        // 获取socketchannel bytebuffer -> 读取
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        int len = 0;
        while ((len = socketChannel.read(buffer)) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
               System.out.print((char)buffer.get());
            }
            System.out.println();
            buffer.clear();
            len = socketChannel.read(buffer);
        }
        if (len == -1) {
            socketChannel.close();
        }
    }

    /**
     * 接收请求
     * 
     * @param key
     * @throws IOException
     */
    private static void doAccept(SelectionKey key) throws IOException {
        // 获取serversocketchannel -> socketchannel -> 注册可读事件到selector
        SelectableChannel channel = key.channel();
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) channel;
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(1024));  // arg3 为附加对象,anyObj
    }

}
