package com.dalthow.launcher.framework;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
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

	public static Thread setUpdates;
	static int index = 0;

	public static void setUpdatesBETA() {
		setUpdates = new Thread() {
			@Override
			public void run() {
				super.run();
				System.out.println("setupdates running");
				updates.clear();

				String game;
				String version = null;
				String branch = null;
				boolean latest = false;
				boolean requireReset = false;
				String link = null;
				String changelog = null;
				String md5 = null;

				NodeList gameList = doc.getElementsByTagName("games");
				for (int v = 0; v < gameList.getLength(); v++) {
					Node t = gameList.item(v);
					if (t.getNodeType() == Node.ELEMENT_NODE) {
						NodeList p = t.getChildNodes();
						for (int j = 0; j < p.getLength(); j++) {
							Node q = p.item(j);
							if (q.getNodeType() == Node.ELEMENT_NODE) {
								game = q.getNodeName();
								NodeList u = q.getChildNodes();
								for (int z = 0; z < u.getLength(); z++) {
									Node x = u.item(z);
									if (x.getNodeType() == Node.ELEMENT_NODE) {
										if (x.getNodeName() == "update") {

											Element update = (Element) x;
											System.out.println(update.getNodeName());
											version = update.getAttribute("version");
											branch = update.getAttribute("branch");
											latest = Boolean.parseBoolean(update.getAttribute("latest"));
											System.out.println(update.getAttribute("latest"));
											requireReset = Boolean.parseBoolean(update.getAttribute("requireReset"));
											link = null;
											changelog = null;
											md5 = null;

											NodeList updatelist = x.getChildNodes();

											for (int i = 0; i < updatelist.getLength(); i++) {
												Node n = updatelist.item(i);
												if (n.getNodeType() == Node.ELEMENT_NODE) {
													NodeList nl = update.getChildNodes();

													for (int b = 0; b < nl.getLength(); b++) {
														Node nc = nl.item(b);
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

												}//
											}
											System.out.println(index);
											updates.put(index, new Update(game, version, branch, link, changelog, md5, latest, requireReset));
											index++;
										}

									}

								}
							}
						}
					}
				}
			}
		};
		setUpdates.run();
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

				try {
					String downloadLink = null;
					synchronized (getUpdates()) {
						for (int z = 0; z < getUpdates().size(); z++) {
							if (!(getUpdates().get(z) == null)) {
								if (getUpdates().get(z).isLatest()) {
									downloadLink = getUpdates().get(z).getUpdateLink();
								}
							}
						}
						Window.games.add(new Game(gameName, mainClass, null, downloadLink, new ImageIcon(new URL(imageURL)), false));
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();

				}
			}
		}
	}
}