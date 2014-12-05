package com.dalthow.launcher.framework;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dalthow.launcher.Window;

public class XML {

	static HashMap<Integer, Update> updates = new HashMap<Integer, Update>();

	static DocumentBuilderFactory f;
	static DocumentBuilder b;
	static Document doc;

	static {
		try {
			f = DocumentBuilderFactory.newInstance();
			b = f.newDocumentBuilder();
			doc = b.parse(new URL("http://dalthow.com/share/launcher/update.xml").openStream());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The XML file couldn't be found, please make shure you are connected to the internet and dalthow.com isn't offline", "Critical Error", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	public static HashMap<Integer, Update> getUpdates() {
		return updates;
	}

	public static void setUpdates() throws ParserConfigurationException, MalformedURLException, SAXException, IOException {
		updates.clear();

		NodeList updatelist = doc.getElementsByTagName("update");

		for (int i = 0; i < updatelist.getLength(); i++) {
			Node n = updatelist.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element update = (Element) n;

				String version = update.getAttribute("version");
				String branch = update.getAttribute("branch");
				boolean latest = Boolean.parseBoolean(update.getAttribute("latest"));
				System.out.println(latest);
				boolean requireReset = Boolean.parseBoolean(update.getAttribute("requireReset"));
				String link = null;
				String changelog = null;
				String md5 = null;

				NodeList nl = update.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++) {
					Node nc = nl.item(j);
					if (nc.getNodeType() == Node.ELEMENT_NODE && nc.getNodeName().equals("link")) {
						link = nc.getTextContent().trim();
					}
					if (nc.getNodeType() == Node.ELEMENT_NODE && nc.getNodeName().equals("changelog")) {
						changelog = nc.getTextContent().trim();
					}
					if (nc.getNodeType() == Node.ELEMENT_NODE && nc.getNodeName().equals("md5")) {
						md5 = nc.getTextContent().trim();
					}

				}
				updates.put(updates.isEmpty() ? 0 : updates.size() + 1, new Update(version, branch, link, changelog, md5, latest, requireReset));
			}
		}
	}

	public static void setLauncherGames() {
		NodeList launcher = doc.getElementsByTagName("launcher");
		for (int i = 0; i < launcher.getLength(); i++) {
			Node n = launcher.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element items = (Element) n;

				String gameName = items.getAttribute("gameName");
				String mainClass = items.getAttribute("mainClass");

				String imageURL = null;

				NodeList nl = items.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++) {
					Node nc = nl.item(j);
					if (nc.getNodeType() == Node.ELEMENT_NODE && nc.getNodeName().equals("img")) {
						imageURL = nc.getTextContent().trim();
					}
				}
				Image image = null;
				try {
					URL url = new URL(imageURL);
					System.out.println();
					image = Toolkit.getDefaultToolkit().createImage(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				Window.games.add(new Game(gameName, "", "", image));
			}
		}
	}
}
