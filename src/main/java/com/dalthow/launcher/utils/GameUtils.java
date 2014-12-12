
package com.dalthow.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import com.dalthow.launcher.Window;
import com.dalthow.launcher.framework.JTextAreaOutputStream;

public class GameUtils
{
	public static boolean isGameInstalled(String dir)
	{
		File file = new File(dir);
		if(file.exists())
		{
			return true;
		}
		return false;
	}

	public static boolean isUpdateAvalaible() throws IOException
	{
		for (int i = 0; i < Window.games.size(); i++)
		{
			Window.games.get(i).setUpdateAvailable(false);
			File file = new File(Window.baseDIR + Window.games.get(i).getName() + "/application.properties");
			if (file.exists())
			{
				String line;
				String version = null;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null)
				{
					if (line.startsWith("game.version="))
					{
						version = line.substring("game.version=".length());
						Window.games.get(i).setVersion(version);
					}
				}
				reader.close();

				for (int j = 0; j < XML.getUpdates().size(); j++)
				{
					if (XML.getUpdates().get(j).getGameName().equalsIgnoreCase(Window.games.get(i).getName()) && !version.matches(XML.getUpdates().get(i).getVersion()))
					{
						if (XML.getUpdates().get(i).isLatest())
						{

							Window.games.get(i).setUpdateAvailable(true);

							return true;
						}
						else
						{
							continue;
						}
					}
				}
			}
		}

		return false;
	}

	public static Thread game;

	public static void launchGame(final String path, final String mainClass, final String username, final String password) throws IOException
	{
		game = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String command = "javaw -cp " + mainClass + " -Djava.library.path=" + Window.baseDIR + path + "/game_lib -jar " + System.getenv("AppData") + "/Dalthow/" + path + "/game.jar -username=\"" + username + "\" -password=\"" + password + "\"";
				Process p;
				try
				{
					Window.consoleTextArea.setText("");
					p = Runtime.getRuntime().exec(command);

					final InputStream stream = p.getInputStream();
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							BufferedReader reader = null;
							try
							{
								reader = new BufferedReader(new InputStreamReader(stream));
								String line = null;
								while ((line = reader.readLine()) != null)
								{
									System.setOut(new PrintStream(new JTextAreaOutputStream(Window.consoleTextArea)));
									System.out.println(line);
									System.setOut(new PrintStream(new JTextAreaOutputStream(Window.launcherConsoleTextArea)));
								}
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{
								if (reader != null)
								{
									try
									{
										reader.close();
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}
						}
					}).start();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		try
		{
			if(getMD5Checksum(System.getenv("AppData") + "/Dalthow/" + path + "/game.jar").matches(XML.getUpdates().get(Window.gameList.getSelectedIndex()).getJarMd5()))
			{
				game.start();
			}
			
			else
			{
				JOptionPane.showMessageDialog(null, "The game you are trying to run is corrupt, try reinstalling it.", "Warning", JOptionPane.WARNING_MESSAGE);
				
			}
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean deleteDir(File file)
	{
		String[] children = null;

		if (file.isDirectory())
		{
			children = file.list();

			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(file, children[i]));

				if (!success)
				{
					return false;
				}
			}
		}

		boolean deleted = file.delete();

		return deleted;
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
		System.out.println(result);
		return result;
	}
}
