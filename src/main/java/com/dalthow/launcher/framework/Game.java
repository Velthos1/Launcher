package com.dalthow.launcher.framework;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class Game extends JPanel {
	private String name;
	private String version;
	private Image image;

	private String downloadLink;

	public Game(String name, String version, String downloadLink, Image image) {
		this.name = name;
		this.version = version;
		this.downloadLink = downloadLink;
		this.image = image;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(image!=null)
		{
			g.drawImage(image, this.getX(), this.getY(), this);
			g.drawString(this.name, 4, image.getHeight(this) + 5);
		}
	}
}
