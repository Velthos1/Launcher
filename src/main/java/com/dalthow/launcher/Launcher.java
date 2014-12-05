package com.dalthow.launcher;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
	
	public static void main(String[] args) 
	{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring-config.xml");
	}
}
