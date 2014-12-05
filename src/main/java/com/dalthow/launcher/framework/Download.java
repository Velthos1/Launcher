package com.dalthow.launcher.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Download
{
	public static Thread download;
	
	public static void downloadGame(final String url, final String outputFilename) throws MalformedURLException {
	download = new Thread() {
		public void run() {
			try {
				if(!new File("downloads/").exists()){
					new File("downloads/").mkdir();
				}
				URL website = new URL(
						url);
				ReadableByteChannel rbc = Channels.newChannel(website
						.openStream());
				FileOutputStream fos = new FileOutputStream("downloads/"+outputFilename);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				System.out.println("Download Complete!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	download.run();
}
}
