package com.dalthow.launcher.framework;

public class Modifications
{
	private String target;
	private String name;
	private String button;
	private String help;
	
	public Modifications(String name, String target, String button, String help)
	{
		this.target = target;
		this.name = name;
		this.button = button;
		this.help = help;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getButton()
	{
		return button;
	}

	public void setButton(String button)
	{
		this.button = button;
	}

	public String getHelp()
	{
		return help;
	}

	public void setHelp(String help)
	{
		this.help = help;
	}
}
