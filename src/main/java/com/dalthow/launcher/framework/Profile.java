package com.dalthow.launcher.framework;

public class Profile
{
	private String username;
	private String encryptedPassword;
	
	public Profile(String username, String encryptedPassword)
	{
		this.setUsername(username);
		this.setEncryptedPassword(encryptedPassword);
	}

	public String getEncryptedPassword()
	{
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword)
	{
		this.encryptedPassword = encryptedPassword;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}
}
