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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * Title:        The MObile RObot Simulation Environment
 * Description:  This window shows what the robot discovers.
 * Copyright:    Copyright (c) 2002
 * Company:      Università di Bergamo
 * @author       Davide Brugali
 * @version 1.0
 */

@SuppressWarnings("serial")
public class ControlWindow extends JFrame {
	JPanel contentPane;
	JMenuBar menuBar = new JMenuBar();
	JMenu menuSimulation = new JMenu();
	JMenuItem menuSimulationStart = new JMenuItem();
	JMenuItem menuSimulationQuit = new JMenuItem();

	Controller controller = null;

	public ControlWindow(Controller p_controller) {
		controller = p_controller;
		// See remark in SimulWindow about the statement below
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		// ------------------------- contentPane -----------------------------------
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.setTitle("Occupancy Grid");
		// --------------------------- Menu ----------------------------------------
		// Menu Simulation
		menuSimulation.setText("Simulation");
		// Menu Simulation Start
		menuSimulationStart.setText("Start");
		menuSimulationStart.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				menuSimulationStart_actionPerformed(e);
			}
		});
		// Menu Simulation Quit
		menuSimulationQuit.setText("Quit");
		menuSimulationQuit.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				menuSimulationQuit_actionPerformed(e);
			}
		});
		menuSimulation.add(menuSimulationStart);
		menuSimulation.add(menuSimulationQuit);
		menuBar.add(menuSimulation);

		// --------------------------- Panels --------------------------------------
		setBounds(510, 0, 510, 460);
		// draw the OccupancyMap
		this.getContentPane().add(controller.map);
		this.setJMenuBar(menuBar);
	}

	// --------------------------- Menu actions ----------------------------------
	/** Menu Simulation Start*/
	void menuSimulationStart_actionPerformed(ActionEvent e) {
		controller.start();
	}

	/** Menu Simulation Quit*/
	void menuSimulationQuit_actionPerformed(ActionEvent e) {
		controller.quit();
	}
	/**Overridden so we can exit when window is closed*/
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			menuSimulationQuit_actionPerformed(null);
		}
	}
}
