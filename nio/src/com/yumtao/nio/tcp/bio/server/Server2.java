package com.yumtao.nio.tcp.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
	/**
	 * 开启多线程读取客户端通信的数据，避开因为读取的代码阻塞所造成主程序的不执行
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket server = new ServerSocket(8888);
		System.out.println("server run success bind port 8888");
		Socket socket = null;
		while (true) {
			socket = server.accept();
			System.out.println(socket);
			if (null != socket) {

				InputStream inputStream = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				OutputStream outputStream = socket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.println("hello client, it is first");

				Thread t1 = new Thread(new Runnable() {

					@Override
					public void run() {
						System.out.println("读取线程开始：");
						int i = 0;
						String request = null;
						try {
							while (null != (request = br.readLine())) {
								System.out.println("第" + ++i + "次读取，内容如下");
								System.out.println(request);
							}
						} catch (IOException e) {
						} finally {
							try {
								if (null != br) {
									br.close();
								}
							} catch (IOException e) {
							}
						}
					}
				});

				t1.start();

			}
		}
	}
}
