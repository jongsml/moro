package nl.hanze.project.moro.view;
/*
 * (C) Copyright 2005 Davide Brugali, Marco Torchiano
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA
 */

// package moro;

/**
 * Title:
 * Description:  This window shows the entire environment and the robot moving in it.
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.xml.parsers.ParserConfigurationException;

import nl.hanze.project.moro.devices.Environment;

import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class SimulWindow extends JFrame {
	Environment environment = null;
	JPanel contentPane;
	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile = new JMenu();
	JMenuItem menuFileOpenMap = new JMenuItem();
	JMenuItem menuFileExit = new JMenuItem();
	JMenuItem aboutUs = new JMenuItem();

	public void redraw() {
		environment.repaint();
	}

	/**Construct the frame*/
	public SimulWindow(Environment p_environment) {
		environment = p_environment;
		// The call to enableEvents(..) in combination with the processWindowEvent(..)
		// is a really old fashioned way to close the window. Just ignore it.
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		// ------------------------- contentPane -----------------------------------
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.setTitle("Mobile Robot Environment");

		// --------------------------- Menu ----------------------------------------
		// Menu File
		menuFile.setText("File");
		// Menu File Open Map
		menuFileOpenMap.setText("Open Map");
		menuFileOpenMap.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				try {
					menuFileOpenMap_actionPerformed(e);
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		// Menu File Exit
		menuFileExit.setText("Exit");
		menuFileExit.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				menuFileExit_actionPerformed(e);
			}
		});
		
		
		// Adds the menu components
		menuFile.add(menuFileOpenMap);
		menuFile.add(menuFileExit);
		menuBar.add(menuFile);
		menuBar.add(aboutUs);
		
		
		// --------------------------- Panels --------------------------------------
		setBounds(0, 0, 510, 460);
		environment.setBackground(SystemColor.window);
		this.getContentPane().add(environment);
		this.setJMenuBar(menuBar);
	}

	// --------------------------- Menu actions ----------------------------------
	/** Menu File Open Map
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws HeadlessException */
	void menuFileOpenMap_actionPerformed(ActionEvent e) throws HeadlessException, ParserConfigurationException, SAXException, IOException {
		JFileChooser chooser = new JFileChooser(new File("c:"));
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			if(environment.loadMap(chooser.getSelectedFile()))
				environment.repaint();
			else
				JOptionPane.showMessageDialog(null, "This is not a valid Map file!");
		}
	}

	/**File | Exit action performed*/
	public void menuFileExit_actionPerformed(ActionEvent e) {
		System.exit(0);
	}

	/**Overridden so we can exit when window is closed*/
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			menuFileExit_actionPerformed(null);
		}
	}
}
