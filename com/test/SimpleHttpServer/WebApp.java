package com.test.SimpleHttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class WebApp {
	private static ServletContext context;
	
	static{
		context = new ServletContext();
		Map<String,String> mapping = context.getMapping();
		
		Map<String,String> servlet = context.getServlet();
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser saxParser = spf.newSAXParser();
			
			MyHandler handler = new MyHandler();
			
			saxParser.parse("src/main/java/com/qiao/HttpServer/demo4/web.xml", handler);
			Map<String,String> servlets =  handler.getServlets();
			Set<String> servletNameSets = servlets.keySet();
			for(String name : servletNameSets){
				servlet.put(name, servlets.get(name));
			}
			
			Map<String,List<String>> mappings = handler.getMappings();
			Set<String> nameSet = mappings.keySet();
			for(String name : nameSet){
				List<String> list = mappings.get(name);
				if(null!=list && list.size()>0){
					for(String url : list){
						mapping.put(url, name);
					}
				}
			}
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Servlet getServlet(String url) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String path = "";
		if(null==url || url.trim().equals("") || !context.getMapping().containsKey(url)){
			url="/error";
		}
		path = context.getServlet().get(context.getMapping().get(url));
		return (Servlet)Class.forName(path).newInstance();
	}
	
}
