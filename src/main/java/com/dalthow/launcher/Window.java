package com.dalthow.launcher;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.dalthow.launcher.framework.Profile;
import com.dalthow.launcher.utils.Download;
import com.dalthow.launcher.utils.Encrypter;
import com.dalthow.launcher.utils.GameUtils;
import com.dalthow.launcher.utils.XML;

@Component
public class Window extends JFrame
{
	private static final long serialVersionUID = 8768561047038086348L;

	public static LinkedList<Game> games = new LinkedList<Game>();
	public static Map<String, ImageIcon> imageMap;

	private LinkedList<Profile> profiles = new LinkedList<Profile>();

	public static String baseDIR = System.getenv("AppData") + "/Dalthow/";
	public static String launcherDIR = baseDIR + "launcher/";

	private Profile selectedProfile;

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
	private DefaultListModel profileModel = new DefaultListModel();

	private JComboBox versions;
	public static JTextArea consoleTextArea;
	private JPanel gameControlWrapper;
	private JPanel gameControl;
	public static JButton playButton;
	private JLabel versionLabel;
	private JMenuItem uninstall;
	private JPopupMenu gameRightClick;
	private JEditorPane newsFeed;
	public static JTextArea launcherConsoleTextArea;
	private JSplitPane consoleSplit;
	private JSplitPane gameSplit;
	private JSplitPane profileSplit;
	private JScrollPane scrollPane1;
	private JList<?> profilesList;
	private JPanel addProfilePanel;
	private JLabel usernameLabel;
	private JTextField textField1;
	private JLabel passwordLabel;
	private JPasswordField passwordField1;
	private JButton registerButton;
	private JButton addProfileButton;
	private JMenuItem removeProfile;
	private JPopupMenu rightClickProfile;

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
			// if
			// (!versions.getItemAt(versions.getSelectedIndex()).toString().split(" ")[1].equalsIgnoreCase(games.get(gameList.getSelectedIndex()).getVersion()))
			// {
			// playButton.setText("Update");
			// versionLabel.setText("Ready to update to version: " +
			// versions.getItemAt(versions.getSelectedIndex()).toString());
			// uninstall.setEnabled(true);
			// }
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

	private void updateNewsFeed()
	{
		try
		{
			newsFeed.setPage(XML.getUpdates().get(gameList.getSelectedIndex()).getChangelogLink());
		} catch (IOException e1)
		{
			e1.printStackTrace();
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

		initComponents();

		getLogin();
		addGamesToList();

		if (profiles.isEmpty() == false)
		{
			this.profilesList.setSelectedIndex(0);
			this.selectedProfile = profiles.get(profilesList.getSelectedIndex());
		}

		pack();
		setVisible(true);
	}

	private void populateProfileList()
	{

		rightClickProfile.add(removeProfile);
		profilesList.setComponentPopupMenu(rightClickProfile);

		for (int i = 0; i < profiles.size(); i++)
		{
			this.profileModel.addElement(profiles.get(i).getUsername());
		}
	}

	private void addRecord() throws IOException
	{
		File file = new File(launcherDIR + "profiles.txt");

		if (!file.exists())
		{
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(this.textField1.getText() + ":" + Encrypter.encryptString(this.passwordField1.getText()));
		writer.newLine();
		writer.close();
	}

	private void getLogin() throws IOException
	{
		File file = new File(launcherDIR + "/profiles.txt");

		if (file.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (!line.startsWith("/") && !line.isEmpty())
				{
					profiles.add(new Profile(line.split(":")[0], line.split(":")[1]));
				}
			}
			reader.close();

			this.populateProfileList();
		}
	}

	public void updateVersion()
	{
		for (int j = 0; j < versions.getItemCount(); j++)
		{
			versions.removeItemAt(j);
		}
		int count = 0;
		String[] versionsList = new String[XML.getUpdates().size()];
		for (int i = 0; i < XML.getUpdates().size(); i++)
		{
			if (XML.getUpdates().get(i).getGameName().equalsIgnoreCase(games.get(gameList.getSelectedIndex()).getName()) && !XML.getUpdates().get(i).getBranch().equalsIgnoreCase("stable"))
			{
				versions.addItem(XML.getUpdates().get(i).getBranch() + " " + XML.getUpdates().get(i).getVersion());
				count++;
			}
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
		uninstall = new JMenuItem("Uninstall");
		launcherConsoleTextArea.setEditable(false);
		launcherConsoleScroll.setViewportView(launcherConsoleTextArea);
		profileSplit = new JSplitPane();
		scrollPane1 = new JScrollPane();
		profilesList = new JList<Object>();
		addProfilePanel = new JPanel();
		usernameLabel = new JLabel();
		textField1 = new JTextField();
		passwordLabel = new JLabel();
		passwordField1 = new JPasswordField();
		registerButton = new JButton();
		addProfileButton = new JButton();
		removeProfile = new JMenuItem("Delete");
		rightClickProfile = new JPopupMenu();

		profilesList.setModel(profileModel);
		progress.setStringPainted(true);
		profilesList.setPreferredSize(new Dimension(200, 0));
		consoleSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameConsoleScroll, launcherConsoleScroll);
		gameSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameList, gameInfo);

		gameList.setSelectedIndex(0);
		versions = new JComboBox();

		updateVersion();

		// TODO: Enable for console
		System.setOut(new PrintStream(new JTextAreaOutputStream(this.launcherConsoleTextArea)));
		{
			setMinimumSize(new Dimension(690, 485));
			setResizable(true);// TODO decide XD
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
					gamesPanel.add(versions, BorderLayout.NORTH);
				}

				{
					// ======== scrollPane1 ========
					{
						scrollPane1.setViewportView(profilesList);
					}
					profileSplit.setLeftComponent(scrollPane1);

					// ======== panel1 ========
					{
						addProfilePanel.setLayout(new GridBagLayout());

						// ---- label1 ----
						usernameLabel.setText("Username:");
						addProfilePanel.add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
						addProfilePanel.add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

						// ---- label2 ----
						passwordLabel.setText("Password:");
						addProfilePanel.add(passwordLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
						addProfilePanel.add(passwordField1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

						// ---- button1 ----
						registerButton.setText("Register");
						addProfilePanel.add(registerButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

						// ---- button2 ----
						addProfileButton.setText("Add Profile");
						addProfilePanel.add(addProfileButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					}
					profileSplit.setRightComponent(addProfilePanel);
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

				consolePanel.setLayout(new BorderLayout());
				{
					consoleTextArea.setFocusable(false);
					gameConsoleScroll.setViewportView(consoleTextArea);
					consolePanel.add(consoleSplit, BorderLayout.CENTER);
				}

				tabbedPane.addTab("Profiles", profileSplit);

				tabbedPane.addTab("Console", consolePanel);
			}
			LauncherContentPane.add(tabbedPane, BorderLayout.CENTER);

			removeProfile.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						removeProfile();

					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}

			});

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

								for (int i = 0; i < games.size(); i++)
								{
									if (gameList.getSelectedValue().equals(games.get(i).getName()))
									{
										String output = games.get(i).getName().substring(0, 1).toUpperCase() + games.get(i).getName().substring(1);

										if (GameUtils.isGameInstalled(baseDIR + games.get(gameList.getSelectedIndex()).getName() + "/") && !games.get(i).isUpdateAvailable())
										{
											if (!profiles.isEmpty())
											{
												GameUtils.launchGame(output, games.get(i).getMainClass(), selectedProfile.getUsername(), selectedProfile.getEncryptedPassword());
											}
											else
											{
												JOptionPane.showMessageDialog(null, "You don't have a profile created.", "Warning", JOptionPane.WARNING_MESSAGE);
												tabbedPane.setSelectedIndex(1);
											}
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
											GameUtils.isUpdateAvalaible();
											games.get(i).setUpdateAvailable(false);
											updatePlayButton();
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

			uninstall.addActionListener(new ActionListener()
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

			addProfileButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					boolean existant = false;

					if (!profiles.isEmpty())
					{
						for (int j = 0; j < profiles.size(); j++)
						{
							if (profiles.get(j).getUsername().equalsIgnoreCase(textField1.getText()))
							{
								existant = true;
								break;
							}
						}
					}
					if (!existant)
					{
						if (!textField1.getText().equalsIgnoreCase("") && !passwordField1.getText().equalsIgnoreCase(""))
						{
							profiles.add(new Profile(textField1.getText(), Encrypter.encryptString(passwordField1.getText())));
							profileModel.addElement(textField1.getText());

							try
							{
								addRecord();
							}

							catch (IOException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							profilesList.setModel(profileModel);

							profilesList.setSelectedIndex(profileModel.size() - 1);
							selectedProfile = profiles.get(profileModel.size() - 1);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "There is already a username called " + textField1.getText() + ", remove that one before creating a new one.", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
			});

			registerButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
					{
						try
						{
							System.out.println("Opening Registration Website");
							desktop.browse(new URL("http://dalthow.com/registration.php").toURI());
						} catch (Exception e1)
						{
							e1.printStackTrace();
						}
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
					updateVersion();
				}
			});

			profilesList.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent mouseEvent)
				{
					if (!profiles.isEmpty())
					{
						selectedProfile = profiles.get(profilesList.getSelectedIndex());
					}
				}
			});

			pack();
			setLocationRelativeTo(getOwner());
		}
	}

	public void removeProfile() throws IOException
	{
		if (!profilesList.isSelectionEmpty())
		{
			profiles.remove(profilesList.getSelectedIndex());
			profileModel.remove(profilesList.getSelectedIndex());
			profilesList.setModel(profileModel);
			removeLineFromFile(launcherDIR + "/profiles.txt", selectedProfile.getUsername() + ":" + selectedProfile.getEncryptedPassword());
			profilesList.setSelectedIndex(0);
		}
	}

	public void removeLineFromFile(String file, String lineToRemove)
	{

		try
		{

			File inFile = new File(file);

			if (!inFile.isFile())
			{
				System.out.println("Parameter is not an existing file");
				return;
			}

			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			while ((line = br.readLine()) != null)
			{

				if (!line.trim().equals(lineToRemove))
				{

					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			if (!inFile.delete())
			{
				System.out.println("Could not delete file");
				return;
			}

			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		} catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
