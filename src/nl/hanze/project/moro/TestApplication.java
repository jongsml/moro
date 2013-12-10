package nl.hanze.project.moro;
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
 * Title:        The MObile RObot Simulation Environment
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Universit di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nl.hanze.project.moro.controller.Controller;
import nl.hanze.project.moro.devices.Environment;
import nl.hanze.project.moro.view.ControlWindow;
import nl.hanze.project.moro.view.SimulWindow;

public class TestApplication 
{
	Environment environment = null;
	Controller controller = null;
	SimulWindow simulWindow = null;
	ControlWindow controlWindow = null;

	/*
	 * TestApplication() starts the Mobile Robot Environment and Minimap Frames
	 * It uses the environment to place objects en fill the minimap
	 */
	public TestApplication() {
		
		
		environment = new Environment(); 
		simulWindow = new SimulWindow(environment);// This window shows the entire environment and the robot moving in it.
		simulWindow.validate();
		simulWindow.setVisible(true);

		controller = new Controller(environment.getRobot());
		controlWindow = new ControlWindow(controller);	// This window shows what the robot discovers
		controlWindow.validate();
		controlWindow.setVisible(true);
	}

	public static void main(String[] args) {
		/*
		 * Set the Look and Feel 
		 */
		//try {
	          // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	           //  } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalArgumentException | IllegalAccessException ex){
	       //     ex.printStackTrace();
	     //   }
	        /* Turn off metal's use bold fonts */
	   //     UIManager.put("swing.boldMetal", Boolean.FALSE);

		@SuppressWarnings("unused")
		TestApplication testApplication = new TestApplication();
	}
}
