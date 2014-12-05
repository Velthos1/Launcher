
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dalthow.launcher.framework.Game;
import com.dalthow.launcher.framework.JTextAreaOutputStream;
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

		//	System.setOut(new JTextArea());

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
					}
					gamesPanel.add(gameScrollPane, BorderLayout.WEST);
				}
				
				//======== gameControlWrapper ========
				{
					gameControlWrapper.setLayout(new BorderLayout());

					//======== gameControl ========
					{
						gameControl.setPreferredSize(new Dimension(0, 75));
						gameControl.setLayout(null);

						//---- playButton ----
						playButton.setText("Play");
						gameControl.add(playButton);
						playButton.setBounds(10, 10, 120, 45);

						//---- versionLabel ----
						versionLabel.setText("Version");
						gameControl.add(versionLabel);
						versionLabel.setBounds(140, 35, 365, versionLabel.getPreferredSize().height);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < gameControl.getComponentCount(); i++) {
								Rectangle bounds = gameControl.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = gameControl.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							gameControl.setMinimumSize(preferredSize);
							gameControl.setPreferredSize(preferredSize);
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
			System.setOut(new JTextAreaOutputStream(consoleTextArea));
			this.playButton.addActionListener(new ActionListener()
			{

			@Override
			public void actionPerformed(ActionEvent paramActionEvent)
			{
				Window.launchGame("Etaron", "com.dalthow.etaron.Launcher", "MattsMc", "test");
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

	// JFormDesigner - End of variables declaration  //GEN-END:variables

	@Autowired
	public Window(@Value("${launcher.width}") int width, @Value("${launcher.height}") int height, @Value("${launcher.title}") String title, @Value("${launcher.version}") String version) throws IOException
	{
		
		getLogin();

		setPreferredSize(new Dimension(width, height));
		setTitle(title);

		Image icon = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("global/icon.png"));
		setIconImage(icon);

		try
		{
			XML.setUpdates();
			XML.setLauncherGames();
			
			nameList = new String[games.size()];
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//		
		//				for(int i = 0; i < XML.getUpdates().size(); i++)
		//				{
		//					try
		//					{
		//						if(XML.getUpdates().get(i).isLatest())
		//						{
		//							Download.downloadGame(XML.getUpdates().get(i).getUpdateLink());
		//							Unzip.unzip.join();
		//		
		//							launchGame("Etaron", "com.dalthow.etaron.Launcher", "MattsMc", "test");
		//		
		//						}
		//					}
		//					catch(Exception e)
		//					{
		//						e.printStackTrace();
		//					}
		//				}

		//		for(int i = 0; i < 25; i++)
		//		{
		//			//games.add(new Game(version, version, version, icon));
		//		}
		//
		//		for(int i = 0; i < games.size(); i++)
		//		{
		//			add(games.get(i));
		//		}
		imageMap = createImageMap(nameList);

		this.initComponents();
		this.addGamesToList();
		
		pack();
		setVisible(true);
	}

	private void getLogin() throws IOException
	{
		File file = new File(System.getenv("AppData") + "/Dalthow/Etaron/userproperties.txt");
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
