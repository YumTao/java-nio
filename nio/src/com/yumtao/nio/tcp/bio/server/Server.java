package com.yumtao.nio.tcp.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	/**
	 * 	传统BIO 的TCP 通讯步骤：
	 *  1.服务器开启服务，绑定端口
	 *  2.客户端根据服务器IP 与端口，建立连接
	 *  3.服务器与客户端通信，通过IO流进行数据交互
	 *  4.输入流的读取会阻塞，直至有数据可读时才释放（同理键盘输入也是阻塞的，直至触发相应事件）
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			server = new ServerSocket(8888);
			System.out.println("server run success bind port 8888");
			while (true) {
				socket = server.accept();
				System.out.println(socket);
				if (null != socket) {
					
					inputStream = socket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					outputStream = socket.getOutputStream();
					PrintStream printStream = new PrintStream(outputStream);
					printStream.println("hello client, it is first");
					
					String request = null;
					request = br.readLine();		// 进入阻塞状态，直到有数据可读
					System.out.println("第一次读取");
					System.out.println(request);
					request = br.readLine();
					System.out.println("第二次读取");
					System.out.println(request);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != outputStream) {
					outputStream.close();
				}
				if (null != socket) {
					socket.close();
				}
				if (null != server) {
					server.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
