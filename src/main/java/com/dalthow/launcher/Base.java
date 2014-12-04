package com.dalthow.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Base {
	static Thread download, unzip;

	public static void main(String[] args) {

		try {
			 downloadGame("http://dalthow.com/share/downloads/software/etaron/etaron-alpha.zip", "downloadEtaron.zip");
			 download.join();
			
			unZipIt("downloads/etarondownload.zip", "game/");
			unzip.join();

			launchGame("MattsMc", "test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean updateAvalable() {
		return false;
	}

	static URL url;

	private static void downloadGame(final String url, final String outputFilename) throws MalformedURLException {
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

	private boolean isDeveloper() {
		return false;
	}

	private boolean databaseRunning() {
		return true;
	}

	// returns true if login data is correct
	private boolean isValidated() {
		return false;
	}

	private static void launchGame(String username, String password)
			throws IOException {
		Process proc = Runtime
				.getRuntime()
				.exec("java -cp com.dalthow.etaron.Launcher -jar game/etaron.jar -username=\"MattsMc\" -password=\"test\"");
		InputStream in = proc.getInputStream();
		InputStream err = proc.getErrorStream();
		java.util.Scanner error = new java.util.Scanner(err)
				.useDelimiter("\\A");
		System.out.println(error.hasNext() ? error.next() : "");
		java.util.Scanner input = new java.util.Scanner(in).useDelimiter("\\A");
		System.out.println(input.hasNext() ? input.next() : "");
	}

}
