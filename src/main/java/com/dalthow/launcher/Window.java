
package com.dalthow.launcher;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.FontUIResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dalthow.launcher.framework.Download;
import com.dalthow.launcher.framework.Encrypter;
import com.dalthow.launcher.framework.Game;
import com.dalthow.launcher.framework.JTextAreaOutputStream;
import com.dalthow.launcher.framework.Unzip;
import com.dalthow.launcher.framework.XML;

@Component
public class Window extends JFrame
{
	public static LinkedList<Game> games = new LinkedList<Game>();
	private final Map<String, ImageIcon> imageMap;

	String username;
	String password;

	String[] nameList;

	private void addGamesToList()
	{
		for(int i = 0; i < games.size(); i++)
		{
			nameList[i] = games.get(i).getName();
		}
	}

	private boolean isGameInstalled()
	{
		File file = new File(System.getenv("AppData") + "/Dalthow/" + games.get(gameList.getSelectedIndex()).getName() + "/");
		if(file.exists())
		{
			return true;
		}
		return false;
	}

	private boolean isUpdateAvalaible() throws IOException
	{
		for(int i = 0; i < games.size(); i++)
		{
			games.get(i).setUpdateAvailable(false);
			File file = new File(System.getenv("AppData") + "/Dalthow/" + games.get(i).getName() + "/application.properties");
			if(file.exists())
			{
				String line;
				String version = null;
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while((line = reader.readLine()) != null)
				{
					if(line.startsWith("game.version="))
					{
						version = line.substring("game.version=".length());
						games.get(i).setVersion(version);
					}
				}

				for(int j = 0; j < XML.getUpdates().size(); j++)
				{
					if(XML.getUpdates().get(j).getGameName().equalsIgnoreCase(games.get(i).getName()) && XML.getUpdates().get(i).isLatest())
					{
						if(!XML.getUpdates().get(j).getVersion().trim().equalsIgnoreCase(version.trim()))
						{
							System.out.println(XML.getUpdates().get(j).getVersion());
							System.out.println(version);
							games.get(i).setUpdateAvailable(true);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void initComponents()
	{
		progress = new JProgressBar();
		tabbedPane = new JTabbedPane();
		gamesPanel = new JPanel();
		gameScrollPane = new JScrollPane();
		gameList = new JList<Object>(nameList);
		loginPanel = new JPanel();
		register = new JButton();
		login = new JButton();
		saveCrendentials = new JRadioButton();
		usernameText = new JTextField();
		passwordTexet = new JPasswordField();
		usernameLabel = new JLabel();
		passwordLabel = new JLabel();
		consolePanel = new JPanel();
		ConsoleScrollPane = new JScrollPane();
		consoleTextArea = new JTextArea();
		gameControlWrapper = new JPanel();
		gameControl = new JPanel();
		playButton = new JButton();
		versionLabel = new JLabel();

		gameList.setSelectedIndex(0);

		MouseAdapter mouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent mouseEvent)
			{
				JList theList = (JList) mouseEvent.getSource();
				if(isGameInstalled())
				{					
					if(games.get(gameList.getSelectedIndex()).isUpdateAvailable())
					{
						playButton.setText("Update");
						versionLabel.setText("Ready to update to version: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
					}
					
					else
					{
						playButton.setText("Play");
						versionLabel.setText("Version: " + games.get(gameList.getSelectedIndex()).getVersion());
					}
				}
				else
				{
					playButton.setText("Download");
					
					versionLabel.setText("Ready to download: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
				}
			}
		};

		gameList.addMouseListener(mouseListener);

		System.setOut(new PrintStream(new JTextAreaOutputStream(consoleTextArea)));

		{
			this.setMinimumSize(new Dimension(690, 485));
			this.setResizable(false);
			Container LauncherContentPane = this.getContentPane();
			LauncherContentPane.setLayout(new BorderLayout());
			LauncherContentPane.add(progress, BorderLayout.SOUTH);

			{

				{

					gamesPanel.setLayout(new BorderLayout());
					{
						GameListRenderer renderer = new GameListRenderer();
						renderer.setPreferredSize(new Dimension(100, 50));

						gameList.setPreferredSize(new Dimension(200, 0));
						gameList.setCellRenderer(renderer);
						gameScrollPane.setViewportView(gameList);
						gameScrollPane.setWheelScrollingEnabled(true);

					}
					gamesPanel.add(gameScrollPane, BorderLayout.WEST);
				}

				// ======== gameControlWrapper ========
				{
					gameControlWrapper.setLayout(new BorderLayout());

					// ======== gameControl ========
					{
						gameControl.setPreferredSize(new Dimension(0, 75));
						gameControl.setLayout(null);

						// ---- playButton ----
						if(isGameInstalled())
						{
							if(games.get(gameList.getSelectedIndex()).isUpdateAvailable())
							{
								playButton.setText("Update");

							}
							else
							{
								playButton.setText("Play");
							}
						}
						else
						{
							playButton.setText("Download");
						}
						gameControl.add(playButton);
						playButton.setBounds(5, 5, 120, 35);

						// ---- versionLabel ----
						if(games.get(gameList.getSelectedIndex()).getVersion() != null)
						{
							versionLabel.setText("Version: " + games.get(gameList.getSelectedIndex()).getVersion());
						}
						
						else if(isGameInstalled())
						{
							versionLabel.setText("Version: " + "Could not retieve version number");
						}
						
						else
						{
							versionLabel.setText("Ready to download to version: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
						}
						
						gameControl.add(versionLabel);
						versionLabel.setBounds(130, 30, 100, 10);

						{
							gameControl.setMinimumSize(new Dimension(0, 45));
							gameControl.setPreferredSize(new Dimension(0, 45));
						}
					}
					gameControlWrapper.add(gameControl, BorderLayout.SOUTH);
				}
				gamesPanel.add(gameControlWrapper, BorderLayout.CENTER);

				tabbedPane.addTab("Games", gamesPanel);
				{
					loginPanel.setLayout(null);

					register.setText("Register");
					loginPanel.add(register);
					register.setBounds(270, 240, 90, register.getPreferredSize().height);

					login.setText("Enter");
					loginPanel.add(login);
					login.setBounds(new Rectangle(new Point(370, 240), login.getPreferredSize()));

					saveCrendentials.setText("Save Credentials");
					loginPanel.add(saveCrendentials);
					saveCrendentials.setBounds(275, 210, 161, saveCrendentials.getPreferredSize().height);
					loginPanel.add(usernameText);
					usernameText.setBounds(275, 155, 160, usernameText.getPreferredSize().height);
					loginPanel.add(passwordTexet);
					passwordTexet.setBounds(275, 185, 160, passwordTexet.getPreferredSize().height);

					usernameLabel.setText("Username:");
					loginPanel.add(usernameLabel);
					usernameLabel.setBounds(new Rectangle(new Point(205, 155), usernameLabel.getPreferredSize()));

					passwordLabel.setText("Password:");
					loginPanel.add(passwordLabel);
					passwordLabel.setBounds(new Rectangle(new Point(205, 185), passwordLabel.getPreferredSize()));
					{
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < loginPanel.getComponentCount(); i++)
						{
							Rectangle bounds = loginPanel.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = loginPanel.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						loginPanel.setMinimumSize(preferredSize);
						loginPanel.setPreferredSize(preferredSize);
					}
				}
				tabbedPane.addTab("Login", loginPanel);
				{
					consolePanel.setLayout(new BorderLayout());
					{

						consoleTextArea.setFocusable(false);
						ConsoleScrollPane.setViewportView(consoleTextArea);
					}
					consolePanel.add(ConsoleScrollPane, BorderLayout.CENTER);
				}
				tabbedPane.addTab("Console", consolePanel);
			}
			LauncherContentPane.add(tabbedPane, BorderLayout.CENTER);

			this.playButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent paramActionEvent)
				{
					try
					{

						for(int i = 0; i < games.size(); i++)
						{
							if(gameList.getSelectedValue().equals(games.get(i).getName()))
							{
								String output = games.get(i).getName().substring(0, 1).toUpperCase() + games.get(i).getName().substring(1);

								if(isGameInstalled() && !games.get(i).isUpdateAvailable())
								{
									launchGame(output, games.get(i).getMainClass(), "MattsMc", "test");
								}

								else
								{
									String downloadLink = null;

									for(int j = 0; j < XML.getUpdates().size(); j++)
									{
										if(games.get(i).getName().equalsIgnoreCase(XML.getUpdates().get(j).getGameName()))
										{
											if(XML.getUpdates().get(j).isLatest())
											{
												System.out.println(XML.getUpdates().get(j).getGameName());
												downloadLink = XML.getUpdates().get(j).getUpdateLink();
											}
										}
									}

									Download.downloadGame(downloadLink, games.get(i).getName());
									Unzip.unzip.join();
									games.get(i).setUpdateAvailable(false);
									isUpdateAvalaible();
									playButton.setText("Play");
								}
							}
						}

					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			});

			this.pack();
			this.setLocationRelativeTo(this.getOwner());
		}
	}

	private JProgressBar progress;
	private JTabbedPane tabbedPane;
	private JPanel gamesPanel;
	private JScrollPane gameScrollPane;
	private JList<?> gameList;
	private JPanel loginPanel;
	private JButton register;
	private JButton login;
	private JRadioButton saveCrendentials;
	private JTextField usernameText;
	private JPasswordField passwordTexet;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JPanel consolePanel;
	private JScrollPane ConsoleScrollPane;
	private JTextArea consoleTextArea;
	private JPanel gameControlWrapper;
	private JPanel gameControl;
	private JButton playButton;
	private JLabel versionLabel;

	public class GameListRenderer extends DefaultListCellRenderer
	{
		Font font = new Font("helvitica", Font.BOLD, 24);

		@Override
		public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(imageMap.get((String) value));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setFont(font);
			return label;
		}
	}

	private Map<String, ImageIcon> createImageMap(String[] list)
	{
		Map<String, ImageIcon> map = new HashMap<>();
		try
		{
			for(int i = 0; i < games.size(); i++)
			{
				map.put(games.get(i).getName(), games.get(i).getImage());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}

	@Autowired
	public Window(@Value("${launcher.width}") int width, @Value("${launcher.height}") int height, @Value("${launcher.title}") String title, @Value("${launcher.version}") String version) throws IOException
	{

		//getLogin();

		setPreferredSize(new Dimension(width, height));
		setTitle(title);

		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
		setIconImage(icon);

		try
		{
			XML.setUpdatesBETA();
			XML.setUpdates.join();

		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		XML.setLauncherGames();
		this.isUpdateAvalaible();

		nameList = new String[games.size()];

		imageMap = createImageMap(nameList);

		this.initComponents();
		this.addGamesToList();

		pack();
		setVisible(true);
	}

	Thread game;

	private void launchGame(final String path, final String mainClass, final String username, final String password) throws IOException
	{
		game = new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				Process proc;
				try
				{
					proc = Runtime.getRuntime().exec("java -cp " + mainClass + " -Djava.library.path=" + System.getenv("AppData") + "/Dalthow/" + path + "/target/natives -jar " + System.getenv("AppData") + "/Dalthow/" + path + "/game.jar -username=\"" + username + "\" -password=\"" + Encrypter.encryptString(password) + "\"");

					InputStream in = proc.getInputStream();
					InputStream err = proc.getErrorStream();

					java.util.Scanner error = new java.util.Scanner(err).useDelimiter("\\A");
					System.out.println(error.hasNext() ? error.next() : "");
					java.util.Scanner input = new java.util.Scanner(in).useDelimiter("\\A");
					System.out.println(input.hasNext() ? input.next() : "");
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}

			}
		};
		game.run();
	}

	private void getLogin() throws IOException
	{
		File file = new File(System.getenv("AppData") + "/Dalthow/Launcher/userproperties.txt");
		if(file.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(!line.startsWith("/"))
				{
					username = line.split(":")[0];
					password = line.split(":")[1];
				}
			}
			reader.close();
		}
	}
}
