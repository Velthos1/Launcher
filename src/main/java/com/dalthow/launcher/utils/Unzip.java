
package com.dalthow.launcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.dalthow.launcher.Window;

public class Unzip
{
	// Declaration

	private static final Logger logger = LogManager.getLogger(Unzip.class);

	public static void unpackGame(String zipFile, String gameName)
	{
		try
		{
			System.out.println("Unzipping " + gameName);
			String destinationname = Window.baseDIR + gameName + "/";
			File file = new File(destinationname);
			if(file.exists())
			{
				GameUtils.deleteDir(file);
				file.mkdir();
			}
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new FileInputStream(zipFile));

			zipentry = zipinputstream.getNextEntry();
			while(zipentry != null)
			{
				String entryName = destinationname + zipentry.getName();
				entryName = entryName.replace('/', File.separatorChar);
				entryName = entryName.replace('\\', File.separatorChar);

				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);
				if(zipentry.isDirectory())
				{
					if(!newFile.mkdirs())
					{
						break;
					}
					zipentry = zipinputstream.getNextEntry();
					continue;
				}

				fileoutputstream = new FileOutputStream(entryName);

				int bytesRead = -1;
				long totalBytesRead = 0;
				int percentCompleted = 0;
				long fileSize = zipentry.getSize();

				System.out.println("Unzipping: " + zipentry.getName());
				while((bytesRead = zipinputstream.read(buf, 0, 1024)) > -1)
				{
					fileoutputstream.write(buf, 0, bytesRead);

					totalBytesRead += bytesRead;
					percentCompleted = (int) (totalBytesRead * 100 / fileSize);

					Window.progress.setValue(percentCompleted);
					Window.progress.setToolTipText("Unzipping...");
				}

				fileoutputstream.close();
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}
			System.out.println("Unzip Complete!");

			zipinputstream.close();
			Window.playButton.setEnabled(true);
			Window.progress.setToolTipText("Done!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
