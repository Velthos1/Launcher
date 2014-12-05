package com.dalthow.launcher.framework;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XML {

HashMap<String, Update> updates = new HashMap<String, Update>();
	
	public HashMap<String, Update> getUpdates(){
		return updates;
	}
	
	public static void setUpdates() throws ParserConfigurationException, MalformedURLException, SAXException, IOException{
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder b = f.newDocumentBuilder();
		Document doc = b.parse(new URL("https://dl.dropboxusercontent.com/u/14369750/update.xml").openStream());
		
		NodeList updatelist = doc.getElementsByTagName("update");
		
		for(int i=0;i<updatelist.getLength();i++){
			Node n = updatelist.item(i);
			if(n.getNodeType()==Node.ELEMENT_NODE){
				Element update = (Element) n;
				
				String version=update.getAttribute("version");
				String branch=update.getAttribute("branch");
				boolean latest=Boolean.parseBoolean(update.getAttribute("latest"));
				boolean requireReset=Boolean.parseBoolean(update.getAttribute("requireReset"));
				
				NodeList updateChild = update.getChildNodes();
				for(int j=0;i<updatelist.getLength();j++){
					Node nc = updateChild.item(j);
					if(nc.getNodeType()==Node.ELEMENT_NODE){
						Element name = (Element) nc;
						System.out.println(name.getTagName());
					}
				}
			}
		}
	}
	
}
