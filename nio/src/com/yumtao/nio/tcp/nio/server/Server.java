package com.yumtao.nio.tcp.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {
    private SocketChannel sc;
    private ServerSocketChannel ssc = null;
    
    private Map<String, SocketChannel> clientSocketMap = new HashMap<>();
    
    private Set<String> nameList = new HashSet<>();
    
    private final String SPLIT = "#@#";
    
    private final String NAME_EXIST = "sorry, name already exist";
    
    /**
     * NIO 服务端
     * 1.阻塞模式基本与BIO相同,accept(),read()方法都会造成阻塞
     * 2.非阻塞模式下,accept(),read()不会阻塞,无论是否有链接/数据可读,程序都会往下执行
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        new Server().init();
    }
    
    
    private void init() {
        try {
            ssc = ServerSocketChannel.open();   // NIO 创建实例方式,此对象等同于BIO的ServerSocket
            ssc.configureBlocking(false);       // 开启非阻塞模式
            ssc.socket().bind(new InetSocketAddress(8888));
            System.err.println("服务端启动完成,监听8888端口");
            
            while (true) {
                
                // 非阻塞模式下:有链接过来返回socketChannel, 没有链接时,返回null
                // 同一客户端,tcp连接成功后,第一次会返回socketChannel, 往后调用仍然返回null
                sc = ssc.accept();// 此对象等同于BIO的Socket
                
                if (null != sc) {
                    System.out.println("远端地址:" + sc.getRemoteAddress());
                    clientSocketMap.put(sc.getRemoteAddress().toString(), sc);
                    
                    // 开启线程，监听客户端发来的数据
                    doRead(sc);
                    
                    // 客户端第一次连接，提示输入名字
                    ByteBuffer response = ByteBuffer.allocate(1024);
                    String content = "Please input your name:";
                    response.put(content.getBytes());
                    response.flip();    // 写模式 -> 读模式 
                    while (response.hasRemaining()) {
                        sc.write(response);
                    }
                }
                
                /** ==================================================================  **/
                /**    NIO非阻塞模式下还可以做其他事情,比如:播放音乐、人物行走等与聊天内容无关的事                   **/
                /**    BIO模式下由于阻塞，其他与客户端交互的数据无关的任务也会阻塞，所以只能另外新建线程         **/
                /** ==================================================================  **/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != sc) {
                    sc.close();
                }
                if (null != ssc) {
                    ssc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 线程:读取客户端发来的数据，一个客户端连接对应一条线程
     */
    private void doRead(SocketChannel sc) {
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
                            if (!"".equals(content)) {
                                String[] args = content.split(SPLIT);
                                if (null != args && args.length == 1) {
                                    // 记录客户端的名字
                                    String name = args[0]; // name
                                    if (!nameList.contains(name)) {
                                        nameList.add(name);
                                    } else {
                                        // 用户名已存在
                                        buff.clear();
                                        buff.put(NAME_EXIST.getBytes());
                                        buff.flip();
                                        while (buff.hasRemaining()) {
                                            sc.write(buff);
                                        }
                                    }
                                }else if (null != args && args.length > 1) {
                                    // 消息处理
                                    String name = args[0];      // name
                                    String message = args[1];   // message
                                    content = name + ":" + message;
                                    System.out.println(sc.getRemoteAddress() + "发来数据：" + message);
                                    broadCast(sc.getRemoteAddress().toString(), content);
                                }
                                
                                
                                
                                
                            }
                            
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
    
    /**
     * 广播消息
     * @param except
     * @param msg
     * @throws IOException
     */
    private void broadCast(String except, String msg) throws IOException {
        Set<String> keySet = clientSocketMap.keySet();
        for (String string : keySet) {
            if (except.equals(string)) {
                continue;
            }
            SocketChannel sc = clientSocketMap.get(string);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(msg.getBytes());
            buffer.flip();
            sc.write(buffer);
        }

    }
}
