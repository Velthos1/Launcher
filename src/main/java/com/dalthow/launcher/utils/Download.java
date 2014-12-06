package com.dalthow.launcher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.dalthow.launcher.Window;

public class Download
{
	public static Thread download;

	public static synchronized void downloadGame(final String url, final String gameName) throws MalformedURLException
	{
		download = new Thread(new Runnable()
		{
			@Override
			public synchronized void run()
			{
				Window.progress.setStringPainted(true);
				Window.progress.setValue(25);
				File downloadFolder = new File(System.getProperty("java.io.tmpdir") + "/Dalthow/");
				try
				{
					if (!downloadFolder.exists())
					{
						downloadFolder.mkdir();
					}
					URL website = new URL(url);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());

					FileOutputStream fos = new FileOutputStream(downloadFolder + gameName + "download.zip");

					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

					System.out.println("Download Complete!");
					Window.progress.setValue(49);
					Unzip.unpackGame(downloadFolder + gameName + "download.zip", gameName);
					fos.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		download.start();
	}
}
