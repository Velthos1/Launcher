package com.dalthow.launcher;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.dalthow.launcher.framework.Download;
import com.dalthow.launcher.framework.Encrypter;
import com.dalthow.launcher.framework.Game;
import com.dalthow.launcher.framework.Unzip;
import com.dalthow.launcher.framework.XML;

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
        
        try {
			//Download.downloadGame("http://dalthow.com/share/downloads/software/etaron/etaron-alpha.zip");
			//Unzip.unzip.join();
			
			launchGame(null, null);
		} catch (Exception e){
			e.printStackTrace();
		}
        
//        try {
//			XML.setUpdates();
//		} catch (Exception e){
//			e.printStackTrace();
//		}
        
        games.add(new Game(title, version, version, icon));
        
        for(int i = 0; i < games.size(); i++)
        {
        	this.add(games.get(i));
        }
        
		this.pack();
		this.setVisible(true);		
	}
	
	private static void launchGame(String username, String password)
			throws IOException {
		System.setProperty("java.library.path", "target/natives"); //TODO as soon as we build the game it should work like a charm...

		Field field;

		try
		{
			field = ClassLoader.class.getDeclaredField("sys_paths");
			field.setAccessible(true);
			field.set(null, null);
		}

		catch(Exception error)
		{
			error.printStackTrace();
		}
		
		Process proc = Runtime
				.getRuntime()
				.exec("java -cp com.dalthow.etaron.Launcher -Djava.library.path="+System.getenv("AppData")+"/Dalthow/Etaron/target/natives -jar "+System.getenv("AppData")+"/Dalthow/Etaron/etaron.jar -username=\"MattsMc\" -password=\""+Encrypter.encryptString("test")+"\"");
		
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();
		java.util.Scanner error = new java.util.Scanner(err)
				.useDelimiter("\\A");
		System.out.println(error.hasNext() ? error.next() : "");
		java.util.Scanner input = new java.util.Scanner(in).useDelimiter("\\A");
		System.out.println(input.hasNext() ? input.next() : "");
	}
}
