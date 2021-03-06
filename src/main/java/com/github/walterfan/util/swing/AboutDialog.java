package com.github.walterfan.util.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AboutDialog extends JDialog {
	public AboutDialog(JFrame owner, String title, String content
			, int width, int height) {
		this(owner, title, content);

		setSize(width, height);
		setLocationRelativeTo(owner);
	}
	
	public AboutDialog(JFrame owner, String title, String content) {
		super(owner, title, true);

		// add HTML label to center

		add(new JLabel(content,SwingConstants.CENTER),BorderLayout.CENTER);

		// Ok button closes the dialog

		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
			}
		});

		// add Ok button to southern border

		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);

		//setSize(width, height);
		//setLocationRelativeTo(owner);
	}
}