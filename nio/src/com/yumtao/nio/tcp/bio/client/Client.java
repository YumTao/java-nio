package com.yumtao.nio.tcp.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		Socket socket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			socket = new Socket("127.0.0.1", 8888);
			inputStream = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			outputStream = socket.getOutputStream();
			PrintStream pw = new PrintStream(outputStream);
			
			String response = null;
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			System.out.println("输入。。。");
			String content = scanner.nextLine();// 进入阻塞状态，直到有数据输入
			pw.println(content);
			
			response = br.readLine();	// 进入阻塞状态，直到有数据可读
			System.out.println(response);
			// 向server 写入东西后读取
			
			System.out.println("再次输入。。。");
			content = scanner.nextLine();	// 进入阻塞状态，直到有数据输入
			pw.println(content);
		} catch (Exception e) {
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
