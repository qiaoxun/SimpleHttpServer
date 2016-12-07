package com.test.SimpleHttpServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {
	private Map<String,String> servlets;
	private Map<String,List<String>> mappings;
	private String servletName="";
	private String mappingName="";
	private String tagName="";
	private String typeTagName="";
	private boolean isBegin = false;
	@Override
	public void startDocument() throws SAXException {
		servlets = new HashMap<String, String>();
		mappings = new HashMap<String, List<String>>();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		isBegin = true;
		if(!isBlank(qName) && (qName.equals("servlet")||qName.equals("mapping"))){
			typeTagName = qName;
		}
		if(!isBlank(typeTagName) && (typeTagName.equals("servlet")||typeTagName.equals("mapping"))){
			tagName = qName;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		isBegin = false;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String str = new String(ch,start,length);
		if(isBegin){
			if(!isBlank(typeTagName) && typeTagName.equals("servlet")){
				if(!isBlank(tagName) && tagName.equals("name")){
					servletName = str;
				}else if(!isBlank(tagName) && tagName.equals("class")){
					servlets.put(servletName, str);
				}
			}else if(!isBlank(typeTagName) && typeTagName.equals("mapping")){
				if(!isBlank(tagName) && tagName.equals("name")){
					mappingName =str; 
				}else if(!isBlank(tagName) && tagName.equals("url-pattern")){
					if(mappings.containsKey(mappingName)){
						List<String> urlList = mappings.get(mappingName);
						urlList.add(str);
					}else{
						List<String> urlList = new ArrayList<String>();
						urlList.add(str);
						mappings.put(mappingName, urlList);
					}
				}
			}
			
//			System.err.println("typeTagName:"+typeTagName+"  tagName:"+tagName+"  mappingName:"+mappingName+"  servletName:"+servletName);
//			System.out.println("characters : "+str);
		}
	}
	
	public boolean isBlank(String str){
		if(null == str || str.trim().equals(""))
			return true;
		
		return false;
	}
	
	public Map<String, String> getServlets() {
		return servlets;
	}

	public void setServlets(Map<String, String> servlets) {
		this.servlets = servlets;
	}

	public Map<String, List<String>> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, List<String>> mappings) {
		this.mappings = mappings;
	}

	
}
