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

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Environment extends JPanel {
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	Robot robot = null;

	/**Construct the application*/
	public Environment() {
		// defines the starting position of the robot (name, x, y, direction, environment)
		robot = new Robot("R1", 90, 200, 270, this);
	}

	public boolean loadMap(File mapFile) {
		// removes all the obstacles already loaded
		obstacles.clear();

		// loads the new obstacles
		try {
			FileInputStream inStream = new FileInputStream(mapFile);
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(inStream));
			String docLine;

			// Check the first line if this is a map-file
			docLine = lineReader.readLine();
			if ( (docLine == null) || (docLine.indexOf("<MAP>") == -1 )){
				inStream.close();
				return false;
			}

			// reads the file line by line
			while ((docLine = lineReader.readLine())!=null) {
				if(docLine.indexOf("<OBSTACLE") > -1) {
					Obstacle obstacle = new Obstacle();
					if(obstacle.parseXML(docLine, lineReader))
						obstacles.add(obstacle);
					else {
						inStream.close();
						return false;
					}
				}
				else
					continue;
			}
			inStream.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return true;
	} // end loadMap()


	/*
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draws robot
		robot.paint(g);
		// Draw obstacles on the map
		for(Obstacle obstacle: obstacles) {
			obstacle.paint(g);
		}
	}

	public Robot getRobot() {
		return robot;
	}

	/*
	 * 
	 * @see java.awt.Component#toString()
	 */
	public String toString() {
		String xml = "<MAP>" + "\n";
		Iterator<Obstacle> iterator = obstacles.iterator();
		
		// spits the XML file in individual objects
		
		while(iterator.hasNext()) {
			// Creates the obstacles using the Obstacle class
			Obstacle obstacle = iterator.next();
			xml += obstacle.toString() + "\n";
		}
		xml += "</MAP>\n\n";
		return xml;
	}
}
