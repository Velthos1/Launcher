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
	
	public static void downloadGame(final String url) throws MalformedURLException {
	download = new Thread() {
		public void run() {
			File downloadFolder = new File(System.getProperty("java.io.tmpdir")+"/etaron/");
			try {
				if(!downloadFolder.exists()){
					downloadFolder.mkdir();
				}
				URL website = new URL(
						url);
				ReadableByteChannel rbc = Channels.newChannel(website
						.openStream());
				FileOutputStream fos = new FileOutputStream(downloadFolder+"etarondownload.zip");
			
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				System.out.println("Download Complete!");
				Unzip.unpackGame(downloadFolder+"etarondownload.zip");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	download.run();
}
	
	public static URL getLatestURL(){
		return null;
	}
}
