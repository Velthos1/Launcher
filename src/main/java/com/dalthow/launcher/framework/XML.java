package com.dalthow.launcher.framework;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

	static HashMap<Integer, Update> updates = new HashMap<Integer, Update>();

	public static HashMap<Integer, Update> getUpdates() {
		return updates;
	}

	public static void setUpdates() throws ParserConfigurationException,
			MalformedURLException, SAXException, IOException {
		updates.clear();
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder b = f.newDocumentBuilder();
		Document doc = b.parse(new URL(
				"https://dl.dropboxusercontent.com/u/14369750/update.xml")
				.openStream());

		NodeList updatelist = doc.getElementsByTagName("update");

		for (int i = 0; i < updatelist.getLength(); i++) {
			Node n = updatelist.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element update = (Element) n;

				String version = update.getAttribute("version");
				String branch = update.getAttribute("branch");
				boolean latest = Boolean.parseBoolean(update
						.getAttribute("latest"));
				System.out.println(latest);
				boolean requireReset = Boolean.parseBoolean(update
						.getAttribute("requireReset"));
				String link = null;
				String changelog = null;

				NodeList nl = update.getChildNodes();
		        for (int j = 0; j < nl.getLength(); j++){
		            Node nc = nl.item(j);
		            if(nc.getNodeType() == Node.ELEMENT_NODE &&nc.getNodeName().equals("link")) {
		                link=nc.getTextContent().trim();
		            }
		            if(nc.getNodeType() == Node.ELEMENT_NODE &&nc.getNodeName().equals("changelog")) {
		                changelog=nc.getTextContent().trim();
		            }
		            
		        }
				updates.put(updates.isEmpty()?0:updates.size()+1, new Update(version, branch, link,
						changelog, latest, requireReset));
			}
		}
	}
}
