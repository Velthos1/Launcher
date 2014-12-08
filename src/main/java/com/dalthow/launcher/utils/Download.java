
package com.dalthow.launcher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dalthow.launcher.Window;

public class Download
{
	public static Thread download;

	public static void downloadGame(final String url, final String gameName) throws IOException
	{
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
		}
		
		Unzip.unpackGame(saveFilePath, gameName);

		outputStream.close();

		util.disconnect();
	}
}
