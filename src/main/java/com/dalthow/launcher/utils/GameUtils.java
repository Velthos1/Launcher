package com.dalthow.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.dalthow.launcher.Window;
import com.dalthow.launcher.framework.JTextAreaOutputStream;

public class GameUtils
{
	public static boolean isGameInstalled(String dir)
	{
		File file = new File(dir);
		if (file.exists())
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
					if (XML.getUpdates().get(j).getGameName().equalsIgnoreCase(Window.games.get(i).getName()) && XML.getUpdates().get(i).isLatest())
					{
						if (!XML.getUpdates().get(j).getVersion().trim().equalsIgnoreCase(version.trim()))
						{
							Window.games.get(i).setUpdateAvailable(true);
							return true;
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
				String command = "javaw -cp " + mainClass + " -Djava.library.path=" + Window.baseDIR + path + "/target/natives -jar " + System.getenv("AppData") + "/Dalthow/" + path + "/game.jar -username=\"" + username + "\" -password=\"" + password + "\"";
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
			game.start();
	

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
}
