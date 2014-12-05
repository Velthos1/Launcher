package com.dalthow.launcher;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Window extends JFrame
{
	@Autowired
	public Window(@Value("${launcher.width}") int width, @Value("${launcher.height}") int height, @Value("${launcher.title}") String title, @Value("${launcher.version}") String version)
	{
		this.setPreferredSize(new Dimension(width, height));
		this.setTitle(title);
		
		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
        this.setIconImage(icon);
		this.pack();
		this.setVisible(true);
	}
}
