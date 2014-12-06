package com.dalthow.launcher;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
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
	private JMenuItem uninstall;
	private JPopupMenu gameRightClick;

	String[] nameList;

	private void addGamesToList()
	{
		for (int i = 0; i < games.size(); i++)
		{
			nameList[i] = games.get(i).getName();
		}
	}

	public void updatePlayButton()
	{
		if (GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"))
		{
			if (games.get(gameList.getSelectedIndex()).isUpdateAvailable())
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

	private Map<String, ImageIcon> createImageMap(String[] list)
	{
		Map<String, ImageIcon> map = new HashMap<>();
		try
		{
			for (int i = 0; i < games.size(); i++)
			{
				map.put(games.get(i).getName(), games.get(i).getImage());
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}

	@Autowired
	public Window(@Value("${launcher.width}") int width, @Value("${launcher.height}") int height, @Value("${launcher.title}") String title, @Value("${launcher.version}") String version) throws IOException
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width, height));
		setTitle(title);

		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
		setIconImage(icon);

		File FbaseDIR = new File(baseDIR);
		if (!FbaseDIR.exists())
		{
			FbaseDIR.mkdirs();
		}

		File FlauncherDIR = new File(launcherDIR);
		if (!FlauncherDIR.exists())
		{
			FlauncherDIR.mkdir();
		}

		getLogin();

		try
		{
			XML.setUpdatesBETA();
			synchronized (XML.setUpdates)
			{
				XML.setUpdates.join();
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		XML.setLauncherGames();
		GameUtils.isUpdateAvalaible();

		nameList = new String[games.size()];

		imageMap = createImageMap(nameList);

		this.initComponents();
		this.addGamesToList();

		pack();
		setVisible(true);
	}

	private void saveLogin() throws IOException
	{
		File file = new File(launcherDIR + "userproperties.txt");
		if (file.exists())
		{
			file.delete();
		}
		else
		{
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write("//DO NOT EDIT THIS FILE MANUALLY!");
		writer.newLine();
		writer.write(currentUser + ":" + encPassword);
		writer.close();
	}

	private void getLogin() throws IOException
	{
		File file = new File(launcherDIR + "/userproperties.txt");
		if (file.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (!line.startsWith("/"))
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
		gameRightClick = new JPopupMenu();
		uninstall = new JMenuItem("uninstall");

		gameList.setSelectedIndex(0);
		// TODO: Enable for console
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

						gameRightClick.add(uninstall);

						gameList.setComponentPopupMenu(gameRightClick);

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
						updatePlayButton();
						gameControl.add(playButton);
						playButton.setBounds(5, 5, 120, 35);

						// ---- versionLabel ----

						if (GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"))
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

					if (currentUser != null)
					{
						usernameText.setText(currentUser);
						passwordTexet.setText(encPassword);
					}

					usernameLabel.setText("Username:");
					loginPanel.add(usernameLabel);
					usernameLabel.setBounds(new Rectangle(new Point(205, 155), usernameLabel.getPreferredSize()));

					passwordLabel.setText("Password:");
					loginPanel.add(passwordLabel);
					passwordLabel.setBounds(new Rectangle(new Point(205, 185), passwordLabel.getPreferredSize()));
					{
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < loginPanel.getComponentCount(); i++)
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

			this.login.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					String username = usernameText.getText();
					char passwordChar[] = passwordTexet.getPassword();
					String password = "";
					for (int i = 0; i < passwordChar.length; i++)
					{
						password += passwordChar[i];
					}
					if (!username.trim().equals("") || !password.equals(""))
					{
						if (!usingLoginFile)
						{
							Window.currentUser = username;
							Window.encPassword = Encrypter.encryptString(password);
							if (saveCrendentials.isSelected())
							{
								try
								{
									saveLogin();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							}
							// TODO: Login
						}
						else
						{
							if (Window.currentUser.equalsIgnoreCase(username) && Window.encPassword.equalsIgnoreCase(password))
							{
								// TODO: Login
							}
							else
							{
								usingLoginFile = false;
								this.actionPerformed(arg0);
							}
						}
					}
					else
					{

					}
				}
			});

			this.register.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
					{
						try
						{
							System.out.println("Opening Registration Website");
							desktop.browse(new URL("http://dalthow.com/registration.php").toURI());
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			});

			this.playButton.addActionListener(new ActionListener()
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

								for (int i = 0; i < games.size(); i++)
								{
									if (gameList.getSelectedValue().equals(games.get(i).getName()))
									{
										String output = games.get(i).getName().substring(0, 1).toUpperCase() + games.get(i).getName().substring(1);

										if (GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/") && !games.get(i).isUpdateAvailable())
										{
											GameUtils.launchGame(output, games.get(i).getMainClass(), "MattsMc", "test");
										}

										else
										{
											String downloadLink = null;

											for (int j = 0; j < XML.getUpdates().size(); j++)
											{
												if (games.get(i).getName().equalsIgnoreCase(XML.getUpdates().get(j).getGameName()))
												{
													if (XML.getUpdates().get(j).isLatest())
													{
														downloadLink = XML.getUpdates().get(j).getUpdateLink();
													}
												}
											}
											Download.downloadGame(downloadLink, games.get(i).getName());
											synchronized (Download.download)
											{
												try
												{
													Download.download.wait();
													Unzip.unzip.join();
													GameUtils.isUpdateAvalaible();
													games.get(i).setUpdateAvailable(false);
													updatePlayButton();
												} catch (InterruptedException e)
												{
													e.printStackTrace();
												}
											}

										}
									}
								}

							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			this.uninstall.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + games.get(gameList.getSelectedIndex()).getName() + "?", "Warning", JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION)
					{
						GameUtils.deleteDir(new File(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/"));
						updatePlayButton();
					}

				}
			});

			this.gameList.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent mouseEvent)
				{

					updatePlayButton();
				}
			});

			this.pack();
			this.setLocationRelativeTo(this.getOwner());
		}
	}
}
