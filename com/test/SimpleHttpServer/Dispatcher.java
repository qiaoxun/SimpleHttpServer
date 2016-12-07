package com.test.SimpleHttpServer;

import java.net.Socket;

public class Dispatcher implements Runnable {
	private Socket client;
	private int code;
	private Request req;
	private Response rep;
	public Dispatcher(){}
	public Dispatcher(Socket client){
		this.client = client;
		this.code = 	200;
		req = new Request(this.client);
		rep = new Response(this.client, this.code);
	}
	
	public void run() {
		try {
			Servlet servlet = WebApp.getServlet(req.getUrl());
			servlet.service(req, rep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
