package nl.hanze.project.moro.robot.device;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.hanze.project.moro.model.Obstacle;
import nl.hanze.project.moro.model.OccupancyMap;
import nl.hanze.project.moro.robot.Robot;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class Environment extends JPanel 
{
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private Robot robot = null;
	
	OccupancyMap occupancyMap = new OccupancyMap();


	/**Construct the application*/
	public Environment() 
	{
		// defines the starting position of the robot (name, x, y, direction, environment)
		robot = new Robot(90, 200, 270, this);
	}
	
	
	
	public ArrayList<Obstacle> getObstacles() {
		return obstacles;
	}



	public void setObstacles(ArrayList<Obstacle> obstacles) 
	{
		this.obstacles = obstacles;
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
	 * Get an obstacle object from an XML node.
	 * @param obstacleNode
	 * @return The obstacle with x and y points.
	 */
	private Obstacle getObstacleFromNode(Node obstacleNode)
	{
		// Get the children of the obstacle node. The obstacle node contains point nodes.
		NodeList children = obstacleNode.getChildNodes();
		// Get the name and opaqueness
		NamedNodeMap obstacleAttributes = obstacleNode.getAttributes();
		String name = obstacleAttributes.getNamedItem("NAME").getNodeValue();
		String opaque = obstacleAttributes.getNamedItem("OPAQUE").getNodeValue();
		
		// Lists to store x and y points in.
		List<Integer> xPoints = new ArrayList<Integer>();
		List<Integer> yPoints = new ArrayList<Integer>();
		
		// Walk through the list of points in this obstacle.
		for (int i = 0; i < children.getLength(); i++)
		{
			Node nodeChild = children.item(i);
			if (nodeChild.getNodeName().equals("POINT"))
			{
				// The X and Y are stored in the attributes of the POINT node.
				NamedNodeMap pointChildren = nodeChild.getAttributes();
				int x = Integer.parseInt(pointChildren.getNamedItem("X").getNodeValue());
				int y = Integer.parseInt(pointChildren.getNamedItem("Y").getNodeValue());
				xPoints.add(x);
				yPoints.add(y);
			}
		}
		
		// Convert the retrieved points to a 2-dimensional array.
		int[][] xy = new int[xPoints.size()][2];
		for (int i = 0; i < xPoints.size(); i++)
			xy[i] = new int[] { xPoints.get(i), yPoints.get(i) };

		// Return a new obstacle.
		System.out.println("name: " + name + "xy: " + xy + " equals: "  + opaque.equalsIgnoreCase("true"));
		return new Obstacle(name, xy, opaque.equalsIgnoreCase("true"));
	}
	
	/**
	 * Load a map from an input stream.
	 * @param input
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private boolean loadMap(InputStream input) throws ParserConfigurationException, SAXException, IOException
	{
		// removes all the obstacles already loaded
		obstacles.clear();
		occupancyMap.clear();
		
		Document doc = null;
		// Create a new document builder.
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = docBuilder.parse(input);
		
		// Get all nodes with the OBSTACLE tag name.
		NodeList obstacleNodes = doc.getElementsByTagName("OBSTACLE");
		for (int i = 0; i < obstacleNodes.getLength(); i++)
		{
			Node obstacleNode = obstacleNodes.item(i);
			Obstacle obstacle = getObstacleFromNode(obstacleNode);
			// Add it to the (soon to be obsoleted) list of obstacles.
			obstacles.add(obstacle);
			// And the occupancy map.
			occupancyMap.addObstacle(obstacle);
		}
		
		return true;
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
