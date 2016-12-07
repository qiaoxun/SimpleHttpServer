package com.test.SimpleHttpServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

import com.test.Util.CloseUtil;

public class Response {
	
	private String CRLF="\r\n";
	private String BLANK=" ";
	private StringBuffer responseHeader;
	private StringBuffer responseText;
	private BufferedWriter bw;
	private int code;
	private int len;
	
	public Response(){}
	
	public Response(BufferedWriter bw,StringBuffer responseText,int code){
		this(code);
		this.bw=bw;
	}
	
	public Response(Socket client,int code){
		this(code);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Response(int code){
		this.code = code;
		responseText = new StringBuffer();
		responseHeader = new StringBuffer();
		String stateStatus="";
		switch(code){
			case 200:stateStatus = "OK";	
			break;
			case 404:stateStatus = "NOT FOUND";len=0;	
			break;
			case 500:stateStatus = "SERVER ERROR";	
			break;
		}
		responseHeader.append("HTTP/1.1").append(BLANK).append(this.code).append(BLANK).append(stateStatus).append(CRLF);
		responseHeader.append("Server:Apache/2.2.25 (Win32) mod_jk/1.2.39").append(CRLF);
		responseHeader.append("Date:").append(new Date()).append(CRLF);
		responseHeader.append("Content-Type:text/html;charset=UTF-8").append(CRLF);
		
	};
	
	public void print(String str){
		System.out.println(str);
		responseText.append(str);
	}
	
	public void responseToClient() throws IOException{
		len = this.responseText.toString().getBytes().length;
		responseHeader.append("Content-Length:"+len).append(CRLF);
		responseHeader.append(CRLF);
		
		bw.append(this.responseHeader);
		bw.append(this.responseText);
		bw.flush();
		CloseUtil.closeAll(bw);
	}
	
	
}
