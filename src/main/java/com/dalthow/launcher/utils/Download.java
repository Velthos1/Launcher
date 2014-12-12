
package com.dalthow.launcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

import com.dalthow.launcher.Window;

public class Download
{
	static File savedFile;
	static File tempDirectory;

	public static void downloadGame(final String url, final String gameName, final String version) throws IOException, NoSuchAlgorithmException
	{
		System.out.println("Downloading " + gameName + " " + version);
		Window.playButton.setEnabled(false);
		HTTPDownloadUtil util = new HTTPDownloadUtil();
		util.downloadFile(url);
		tempDirectory = new File(System.getProperty("java.io.tmpdir") + "/Dalthow/");
		if(!tempDirectory.exists())
		{
			tempDirectory.mkdir();
		}
		InputStream inputStream = util.getInputStream();

		savedFile = new File(tempDirectory + util.getFileName());
		if(!savedFile.exists())
		{
			savedFile.createNewFile();
		}
		else
		{
			savedFile.delete();
			savedFile.createNewFile();
		}
		FileOutputStream outputStream = new FileOutputStream(tempDirectory + "/" + util.getFileName());

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
			System.out.println(XML.getUpdates().get(Window.gameList.getSelectedIndex()).getMD5());
			if(GameUtils.getMD5Checksum(tempDirectory + util.getFileName()).matches(XML.getUpdates().get(Window.gameList.getSelectedIndex()).getMD5()))
			{
				Unzip.unpackGame(tempDirectory + "/" + util.getFileName(), gameName);
			}

			else
			{
				JOptionPane.showMessageDialog(null, "The file you downloaded is corrupt, try downloading it again.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}

		catch(Exception error)
		{
			error.printStackTrace();
		}

		if(tempDirectory.exists())
		{
			GameUtils.deleteDir(tempDirectory);
		}

		inputStream.close();
		outputStream.close();

		util.disconnect();
	}
}
