package com.dalthow.launcher.framework;

import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.dalthow.launcher.Window;

public class GameListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 3417936567129777819L;

	Font font = new Font("helvitica", Font.BOLD, 24);

	@Override
	public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		label.setIcon(Window.imageMap.get(value));
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setFont(font);
		return label;
	}
}
