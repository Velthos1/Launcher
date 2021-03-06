
package com.dalthow.launcher.framework;

import javax.swing.ImageIcon;

public class Game
{
	private String name;
	private String version;
	private ImageIcon image;
	private String mainClass;
	private String downloadLink;
	private String changeLog;
	private boolean updateAvailable;

	public Game(String name, String mainClass, String version, String downloadLink, ImageIcon image, String changeLog, boolean updateAvailable)
	{
		this.name = name;
		this.version = version;
		this.downloadLink = downloadLink;
		this.image = image;
		this.mainClass = mainClass;
		this.setChangeLog(changeLog);
		this.setUpdateAvailable(updateAvailable);
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDownloadLink()
	{
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink)
	{
		this.downloadLink = downloadLink;
	}

	public ImageIcon getImage()
	{
		return image;
	}

	public void setImage(ImageIcon image)
	{
		this.image = image;
	}

	public String getMainClass()
	{
		return mainClass;
	}

	public void setMainClass(String mainClass)
	{
		this.mainClass = mainClass;
	}

	public boolean isUpdateAvailable()
	{
		return updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable)
	{
		this.updateAvailable = updateAvailable;
	}

	public String getChangeLog()
	{
		return changeLog;
	}

	public void setChangeLog(String changeLog)
	{
		this.changeLog = changeLog;
	}
}
