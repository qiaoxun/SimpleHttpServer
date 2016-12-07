package com.test.SimpleHttpServer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Request {
	private String CRLF="\r\n";
	private String requestInfo;
	private String parameterString;
	private String method;
	private String url;
	private Map<String,List<String>> paramatersMapValues = new HashMap<String, List<String>>();
	
	public Request(Socket client){
		byte[] b = new byte[10240];
		int len = 0;
		try {
			len = client.getInputStream().read(b);
			System.err.println(this.requestInfo);
			this.requestInfo  = new String(b, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String lines[] = this.requestInfo.split(CRLF);
		String firstLine = lines[0];
		this.method = firstLine.substring(0,this.requestInfo.indexOf("/")).trim();
		
		if(this.method.equalsIgnoreCase("get")){
			if(firstLine.contains("?")){
				this.parameterString = firstLine.substring(firstLine.indexOf("?")+1, firstLine.indexOf("HTTP/")).trim();
				this.url = firstLine.substring(firstLine.indexOf("/"),firstLine.indexOf("?")).trim();
			}else{
				this.url = firstLine.substring(firstLine.indexOf("/"),firstLine.indexOf("HTTP/")).trim();
			}
		}else if(this.method.equalsIgnoreCase("post")){
			this.parameterString = this.requestInfo.substring(this.requestInfo.lastIndexOf(CRLF)).trim();
			this.url = firstLine.substring(firstLine.indexOf("/"),firstLine.indexOf("HTTP/")).trim();
		}
		System.out.println("url:"+url);
		System.out.println(this.requestInfo);
		getParameters(this.parameterString);
	}
	
	
	public void getParameters(String str){
		if(str==null || str.equals("")){
			return ;
		}
		StringTokenizer token = new StringTokenizer(str,"&");
		while(token.hasMoreTokens()){
			String keyValue = token.nextToken();
			String[] kvs = keyValue.split("=");
			
			if(kvs.length==1){
				kvs = Arrays.copyOf(kvs, 2);
			}
			
			String key = kvs[0];
			String value = null==kvs[1]?null:kvs[1].trim();
			
			if(!paramatersMapValues.containsKey(key)){
				paramatersMapValues.put(key, new ArrayList<String>());
			}
			
			List<String> values = paramatersMapValues.get(key);
			values.add(value);
		}
				
	}
	
	public String[] getParameterValues(String name){
		List<String> l = paramatersMapValues.get(name);
		if(null==l){
			return null;
		}
		String values[] = l.toArray(new String[0]);
		return values;
	}
	
	public String getParameterValue(String name){
		List<String> l = paramatersMapValues.get(name);
		if(null==l){
			return null;
		}
		String values[] = l.toArray(new String[0]);
		return values[0];
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public String getParameterString(){
		
		return this.parameterString;
	}
	
}
