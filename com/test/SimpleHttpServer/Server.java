package com.test.SimpleHttpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.test.Util.CloseUtil;

public class Server {
	private ServerSocket server;
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	public void start(){
		try {
			server = new ServerSocket(9999);
			receive();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			CloseUtil.closeAll(server);
		}
	}
	public void stop(){
		
	}
	public void receive(){
		try {
			while(true){
				Socket client = server.accept();
				new Thread(new Dispatcher(client)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
