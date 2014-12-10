
package com.dalthow.launcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.dalthow.launcher.Window;

public class Download
{
	public static void downloadGame(final String url, final String gameName, final String version) throws IOException, NoSuchAlgorithmException
	{
		System.out.println("Downloading " + gameName + " " + version);
		Window.playButton.setEnabled(false);
		HTTPDownloadUtil util = new HTTPDownloadUtil();
		util.downloadFile(url);

		String saveFilePath = System.getProperty("java.io.tmpdir") + "/Dalthow/" + File.separator + util.getFileName();

		InputStream inputStream = util.getInputStream();
		// opens an output stream to save into file
		FileOutputStream outputStream = new FileOutputStream(saveFilePath);

		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		long totalBytesRead = 0;
		int percentCompleted = 0;
		long fileSize = util.getContentLength();

		while((bytesRead = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, bytesRead);
			totalBytesRead += bytesRead;
			percentCompleted = (int) (totalBytesRead * 100 / fileSize);

			Window.progress.setValue(percentCompleted);
			Window.progress.setToolTipText("Downloading...");
		}

		
		try
		{
			if(getMD5Checksum(saveFilePath).matches(XML.getUpdates().get(Window.gameList.getSelectedIndex()).getMD5()))
			{
				Unzip.unpackGame(saveFilePath, gameName);
			}
		}
		
		catch(Exception error)
		{
			error.printStackTrace();
		}

		outputStream.close();

		util.disconnect();
	}

	public static byte[] createChecksum(String filename) throws Exception
	{
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];

		MessageDigest complete = MessageDigest.getInstance("MD5");

		int numRead;

		do
		{
			numRead = fis.read(buffer);

			if(numRead > 0)
			{
				complete.update(buffer, 0, numRead);
			}
		}

		while(numRead != -1);

		fis.close();

		return complete.digest();
	}

	public static String getMD5Checksum(String filename) throws Exception
	{
		byte[] b = createChecksum(filename);

		String result = "";

		for(int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}

		return result;
	}
}
