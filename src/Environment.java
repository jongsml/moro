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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import importer.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class Environment extends JPanel 
{
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	Robot robot = null;
	
	OccupancyMap occupancyMap = new OccupancyMap();


	/**Construct the application*/
	public Environment() {
		// defines the starting position of the robot (name, x, y, direction, environment)
		robot = new Robot("R1", 90, 200, 270, this);
	}
	
	/**
	 * Load a map from a file.
	 * @param mapFile
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean loadMap(File mapFile) throws ParserConfigurationException, SAXException, IOException
	{
		FileInputStream input = null;
		boolean rv = false;
		try
		{
			// Attempt to open the file.
			input = new FileInputStream(mapFile);
			rv = loadMap(input);
		}
		finally
		{
			// If input was actually created, close it to prevent fd leaks.
			if (input != null)
				input.close();
		}
		return rv;
	}


	
	/**
	 * Load a map from an input stream.
	 * @param input
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private boolean loadMap(InputStream input) throws ParserConfigurationException, SAXException, IOException
	{

        try{
            MapLoader loader = new MapLoader(input);

            System.out.println(loader.getMap().toString());

            for(importer.Obstacle obstacle : loader.getObstacle()){
                System.out.println(obstacle.toString());
                occupancyMap.addObstacle(obstacle);
                obstacles.add(obstacle);
            }
        }catch(JAXBException | IOException e){
            e.printStackTrace();
        }
		return true;
	}

    private void loadMap(String path){

    }



	/*
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// Draw robot
		robot.paint(g);
		// Draw map
		for (Obstacle obstacle : occupancyMap.getObstacles())
			paintObstacle(g, obstacle);
	}
	
	private void paintObstacle(Graphics g, Obstacle obstacle)
	{
		Graphics2D graphics2D = (Graphics2D) g;
		Polygon polygon = obstacle.getPolygon();
		
		if (obstacle.isOpaque())
		{
			graphics2D.setColor(Color.magenta);
			graphics2D.fillPolygon(polygon);
			graphics2D.setColor(Color.darkGray);
			graphics2D.drawPolygon(polygon);
		}
		else
		{
			graphics2D.setColor(obstacle.getBgColor());
			graphics2D.fillPolygon(polygon);
			graphics2D.setColor(obstacle.getFgColor());
			graphics2D.drawPolygon(polygon);
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
