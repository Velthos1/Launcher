
package com.dalthow.launcher.framework;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;

import com.dalthow.launcher.Window;

public class GameListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 3417936567129777819L;

	Font font = new Font("Arial", Font.BOLD, 24);

	@Override
	public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		Border paddingBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
		label.setBorder(paddingBorder);
		label.setIcon(Window.imageMap.get(value));
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setIconTextGap(8);
		
		label.setFont(font);
		label.setAlignmentX(40);
		
		return label;
	}  
}
