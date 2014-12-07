
package com.dalthow.launcher;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dalthow.launcher.framework.Game;
import com.dalthow.launcher.framework.GameListRenderer;
import com.dalthow.launcher.framework.JTextAreaOutputStream;
import com.dalthow.launcher.utils.Download;
import com.dalthow.launcher.utils.Encrypter;
import com.dalthow.launcher.utils.GameUtils;
import com.dalthow.launcher.utils.Unzip;
import com.dalthow.launcher.utils.XML;

@Component
public class Window extends JFrame
{
	private static final long serialVersionUID = 8768561047038086348L;

	public static LinkedList<Game> games = new LinkedList<Game>();
	public static Map<String, ImageIcon> imageMap;

	private static String currentUser;
	private static String encPassword;
	boolean usingLoginFile = false;

	public static String baseDIR = System.getenv("AppData") + "/Dalthow/";
	public static String launcherDIR = baseDIR + "launcher/";

	public static JProgressBar progress;
	private JTabbedPane tabbedPane;
	private JTabbedPane gameInfo;
	private JPanel gamesPanel;
	private JScrollPane changeLogScrollPane;
	private JList<?> gameList;
	private JPanel loginPanel;
	private JPanel consolePanel;
	private JScrollPane gameConsoleScroll;
	private JScrollPane launcherConsoleScroll;

	private JTextArea consoleTextArea;
	private JPanel gameControlWrapper;
	private JPanel gameControl;
	private JButton playButton;
	private JLabel versionLabel;
	private JMenuItem uninstall;
	private JPopupMenu gameRightClick;
	private JEditorPane newsFeed;
	private JTextArea launcherConsoleTextArea;
	private JSplitPane consoleSplit;
	private JSplitPane gameSplit;
	private JSplitPane splitPane1;
	private JScrollPane scrollPane1;
	private JList<?> list1;
	private JPanel panel1;
	private JLabel label1;
	private JTextField textField1;
	private JLabel label2;
	private JPasswordField passwordField1;
	private JButton button1;
	private JButton button2;

	String[] nameList;

	private void addGamesToList()
	{
		for(int i = 0; i < games.size(); i++)
		{
			nameList[i] = games.get(i).getName();
		}
	}

	public void updatePlayButton()
	{
		if(GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"))
		{
			if(games.get(gameList.getSelectedIndex()).isUpdateAvailable())
			{
				playButton.setText("Update");
				versionLabel.setText("Ready to update to version: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
				uninstall.setEnabled(true);
			}

			else
			{
				playButton.setText("Play");
				versionLabel.setText("Version: " + games.get(gameList.getSelectedIndex()).getVersion());
				uninstall.setEnabled(true);
			}
		}
		else
		{
			playButton.setText("Download");
			uninstall.setEnabled(false);

			versionLabel.setText("Ready to download: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
		}

	}

	private void updateNewsFeed()
	{
		try
		{
			newsFeed.setPage(XML.getUpdates().get(gameList.getSelectedIndex()).getChangelogLink());
		}
		catch(IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

		this.getLogin();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width, height));
		setTitle(title);

		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
		setIconImage(icon);

		File FbaseDIR = new File(baseDIR);
		if(!FbaseDIR.exists())
		{
			FbaseDIR.mkdirs();
		}

		File FlauncherDIR = new File(launcherDIR);
		if(!FlauncherDIR.exists())
		{
			FlauncherDIR.mkdir();
		}

		getLogin();

		try
		{
			XML.setUpdatesBETA();
			synchronized(XML.setUpdates)
			{
				XML.setUpdates.join();
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		XML.setLauncherGames();
		GameUtils.isUpdateAvalaible();

		nameList = new String[games.size()];

		imageMap = createImageMap(nameList);

		initComponents();
		addGamesToList();

		pack();
		setVisible(true);
	}

	private void saveLogin() throws IOException
	{
		File file = new File(launcherDIR + "user.properties");

		if(file.exists())
		{
			file.delete();
		}
		else
		{
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(currentUser + ":" + Encrypter.encryptString(encPassword));
		writer.close();
	}

	private void getLogin() throws IOException
	{
		File file = new File(launcherDIR + "/user.properties");
		if(file.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(!line.startsWith("/"))
				{
					currentUser = line.split(":")[0];
					encPassword = line.split(":")[1];
					usingLoginFile = true;
				}
			}
			reader.close();
		}
	}

	private void initComponents()
	{
		newsFeed = new JEditorPane();
		progress = new JProgressBar();
		tabbedPane = new JTabbedPane();
		gameInfo = new JTabbedPane();
		gamesPanel = new JPanel();
		changeLogScrollPane = new JScrollPane();
		gameList = new JList<Object>(nameList);
		loginPanel = new JPanel();
		consolePanel = new JPanel();
		gameConsoleScroll = new JScrollPane();
		launcherConsoleScroll = new JScrollPane();
		consoleTextArea = new JTextArea();
		launcherConsoleTextArea = new JTextArea();
		gameControlWrapper = new JPanel();
		gameControl = new JPanel();
		playButton = new JButton();
		versionLabel = new JLabel();
		gameRightClick = new JPopupMenu();
		uninstall = new JMenuItem("uninstall");
		launcherConsoleTextArea.setEditable(false);
		launcherConsoleScroll.setViewportView(launcherConsoleTextArea);
		splitPane1 = new JSplitPane();
		scrollPane1 = new JScrollPane();
		list1 = new JList<Object>();
		panel1 = new JPanel();
		label1 = new JLabel();
		textField1 = new JTextField();
		label2 = new JLabel();
		passwordField1 = new JPasswordField();
		button1 = new JButton();
		button2 = new JButton();

		consoleSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameConsoleScroll, launcherConsoleScroll);
		gameSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameList, gameInfo);

		gameList.setSelectedIndex(0);
		// TODO: Enable for console
		System.setOut(new PrintStream(new JTextAreaOutputStream(this.launcherConsoleTextArea)));

		{
			setMinimumSize(new Dimension(690, 485));
			setResizable(false);//TODO decide XD
			Container LauncherContentPane = getContentPane();
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
						gameRightClick.add(uninstall);

						gameList.setComponentPopupMenu(gameRightClick);
					}
					gamesPanel.add(this.gameSplit, BorderLayout.CENTER);
				}

				{

					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(list1);
					}
					splitPane1.setLeftComponent(scrollPane1);

					//======== panel1 ========
					{
						panel1.setLayout(new GridBagLayout());

						//---- label1 ----
						label1.setText("Username:");
						panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
						panel1.add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

						//---- label2 ----
						label2.setText("Password:");
						panel1.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
						panel1.add(passwordField1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

						//---- button1 ----
						button1.setText("Register");
						panel1.add(button1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

						//---- button2 ----
						button2.setText("Add Profile");
						panel1.add(button2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					}
					splitPane1.setRightComponent(panel1);
				}

				// ======== gameControlWrapper ========
				{
					gameControlWrapper.setLayout(new BorderLayout());

					// ======== gameControl ========
					{
						gameControl.setPreferredSize(new Dimension(0, 75));
						gameControl.setLayout(null);

						// ---- playButton ----
						updatePlayButton();
						gameControl.add(playButton);
						playButton.setBounds(5, 5, 120, 35);

						// ---- versionLabel ----

						if(GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"))
						{
							versionLabel.setText("Version: " + games.get(gameList.getSelectedIndex()).getVersion());
						}

						else
						{
							versionLabel.setText("Ready to download to version: " + XML.getUpdates().get(gameList.getSelectedIndex()).getVersion());
						}

						gameControl.add(versionLabel);
						versionLabel.setBounds(130, 30, versionLabel.getPreferredSize().width, 10);

						{
							gameControl.setMinimumSize(new Dimension(0, 45));
							gameControl.setPreferredSize(new Dimension(0, 45));
						}
					}
					gameControlWrapper.add(gameControl, BorderLayout.SOUTH);
				}

				JPanel wrapper;

				wrapper = new JPanel();
				wrapper.setLayout(new BorderLayout());

				newsFeed.setEditable(false);

				changeLogScrollPane.setViewportView(newsFeed);

				wrapper.add(changeLogScrollPane, BorderLayout.CENTER);

				updateNewsFeed();

				gameInfo.addTab("Changelog", wrapper);

				wrapper.add(gameControlWrapper, BorderLayout.SOUTH);

				tabbedPane.addTab("Games", gamesPanel);
				{
					loginPanel.setLayout(null);
				}
				consolePanel.setLayout(new BorderLayout());
				{

					consoleTextArea.setFocusable(false);
					gameConsoleScroll.setViewportView(consoleTextArea);
					consolePanel.add(consoleSplit, BorderLayout.CENTER);
				}

				tabbedPane.addTab("Login", splitPane1);
				tabbedPane.addTab("Console", consolePanel);
			}
			LauncherContentPane.add(tabbedPane, BorderLayout.CENTER);

			//			login.addActionListener(new ActionListener()
			//			{
			//
			//				@Override
			//				public void actionPerformed(ActionEvent arg0)
			//				{
			//					String username = usernameText.getText();
			//					char passwordChar[] = passwordTexet.getPassword();
			//					String password = "";
			//					for(int i = 0; i < passwordChar.length; i++)
			//					{
			//						password += passwordChar[i];
			//					}
			//					if(!username.trim().equals("") || !password.equals(""))
			//					{
			//						if(!usingLoginFile)
			//						{
			//							Window.currentUser = username;
			//							Window.encPassword = Encrypter.encryptString(password);
			//
			//							if(saveCrendentials.isSelected())
			//							{
			//								try
			//								{
			//									saveLogin();
			//								}
			//								catch(IOException e)
			//								{
			//									e.printStackTrace();
			//								}
			//							}
			//							// TODO: Login
			//						}
			//						else
			//						{
			//							if(Window.currentUser.equalsIgnoreCase(username) && Window.encPassword.equalsIgnoreCase(password))
			//							{
			//								// TODO: Login
			//							}
			//							else
			//							{
			//								usingLoginFile = false;
			//								actionPerformed(arg0);
			//							}
			//						}
			//					}
			//				}
			//			});
			//
			//			register.addActionListener(new ActionListener()
			//			{
			//				@Override
			//				public void actionPerformed(ActionEvent arg0)
			//				{
			//					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			//					if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			//					{
			//						try
			//						{
			//							System.out.println("Opening Registration Website");
			//							desktop.browse(new URL("http://dalthow.com/registration.php").toURI());
			//						}
			//						catch(Exception e)
			//						{
			//							e.printStackTrace();
			//						}
			//					}
			//				}
			//			});

			playButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent paramActionEvent)
				{
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{

								for(int i = 0; i < games.size(); i++)
								{
									if(gameList.getSelectedValue().equals(games.get(i).getName()))
									{
										String output = games.get(i).getName().substring(0, 1).toUpperCase() + games.get(i).getName().substring(1);

										if(GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/") && !games.get(i).isUpdateAvailable())
										{
											GameUtils.launchGame(output, games.get(i).getMainClass(), currentUser, encPassword);
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
														downloadLink = XML.getUpdates().get(j).getUpdateLink();
													}
												}
											}
											Download.downloadGame(downloadLink, games.get(i).getName());
											synchronized(Download.download)
											{
												try
												{
													Download.download.wait();
													Unzip.unzip.join();
													GameUtils.isUpdateAvalaible();
													games.get(i).setUpdateAvailable(false);
													updatePlayButton();
												}
												catch(InterruptedException e)
												{
													e.printStackTrace();
												}
											}

										}
									}
								}

							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			uninstall.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + games.get(gameList.getSelectedIndex()).getName() + "?", "Warning", JOptionPane.YES_NO_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION)
					{
						GameUtils.deleteDir(new File(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"));
						updatePlayButton();
					}

				}
			});

			gameList.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent mouseEvent)
				{
					updatePlayButton();
					updateNewsFeed();
				}
			});

			pack();
			setLocationRelativeTo(getOwner());
		}
	}
}
