package com.dalthow.launcher;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dalthow.launcher.framework.Game;

@Component
public class Window extends JFrame
{
	private LinkedList<Game> games = new LinkedList<Game>();
	
	@Autowired
	public Window(@Value("${launcher.width}") int width, @Value("${launcher.height}") int height, @Value("${launcher.title}") String title, @Value("${launcher.version}") String version)
	{
		this.setPreferredSize(new Dimension(width, height));
		this.setTitle(title);
		
		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
        this.setIconImage(icon);
        
        games.add(new Game(title, version, version, icon));
        
        for(int i = 0; i < games.size(); i++)
        {
        	this.add(games.get(i));
        }
        
		this.pack();
		this.setVisible(true);
	}
}
