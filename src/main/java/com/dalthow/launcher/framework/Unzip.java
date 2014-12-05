package com.dalthow.launcher.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip 
{
	public static Thread unzip;
	
	public static void unZipIt(final String zipFile, final String outputFolder) {
		unzip = new Thread() {
			@Override
			public void run() {
					  try {
       String destinationname = outputFolder;
					        byte[] buf = new byte[1024];
					        ZipInputStream zipinputstream = null;
					        ZipEntry zipentry;
					        zipinputstream = new ZipInputStream(
					                new FileInputStream(zipFile));

					        zipentry = zipinputstream.getNextEntry();
					        while (zipentry != null) {
					            String entryName = destinationname + zipentry.getName();
					            entryName = entryName.replace('/', File.separatorChar);
					            entryName = entryName.replace('\\', File.separatorChar);
					            int n;
					            FileOutputStream fileoutputstream;
					            File newFile = new File(entryName);
					            if (zipentry.isDirectory()) {
					                if (!newFile.mkdirs()) {
					                    break;
					                }
					                zipentry = zipinputstream.getNextEntry();
					                continue;
					            }

					            fileoutputstream = new FileOutputStream(entryName);

					            while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
					                fileoutputstream.write(buf, 0, n);
					            }

					            fileoutputstream.close();
					            zipinputstream.closeEntry();
					            zipentry = zipinputstream.getNextEntry();

					        }

					        zipinputstream.close();
					    } catch (Exception e) {
					        e.printStackTrace();
					    }
			}
		};
		unzip.run();

	}
}
