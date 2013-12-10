package nl.hanze.project.moro.controller;
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
 * Description:  The Controller class controls the robot
 * Copyright:    Copyright (c) 2002
 * Company:      Universit di Bergamo
 * @author       Davide Brugali
 * @version 1.0
 */

import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import nl.hanze.project.moro.SensorMeasures;
import nl.hanze.project.moro.devices.Device;
import nl.hanze.project.moro.devices.Position;
import nl.hanze.project.moro.devices.PositionType;
import nl.hanze.project.moro.devices.Robot;
import nl.hanze.project.moro.model.OccupancyMap;



/**
 * Title:        The MObile RObot Simulation Environment
 * Description:  The Controller class controls the robot
 * Copyright:    Copyright (c) 2002
 * Company:      Universit di Bergamo
 * @author       Davide Brugali
 * @version 1.0
 */
public class Controller
{
	private Robot robot = null;
	private OccupancyMap map = new OccupancyMap();
	// The objects listening to actions of this model.
	private List<ActionListener> actionlisteners;
	// 
    private BufferedReader input = null;
    private PrintWriter output = null;
    private Position currentPosition = null;


	public Controller(Robot robot)
	{
		this.robot = robot;
		if (robot == null)
			System.exit(1);
	}

	public void start()
	{
		robot.start();
        PipedInputStream pipeIn = new PipedInputStream();
        input = new BufferedReader(new InputStreamReader(pipeIn));
		try
		{
			output = new PrintWriter(new PipedOutputStream(pipeIn), true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
        // Inform robot/device in other thread where to write.
        robot.setOutput(output);		
	}

	public void quit()
	{
	}

	/**
	 * Execute a command.
	 * @param command
	 * @return True if the command succeeded, false otherwise.
	 */
	public boolean executeCommand(String command) 
	{
		// Check if a collision will occur.
		if (willCollide(command, currentPosition, robot.getPlatform().getShape()))
			return false;
		
		// Send the command to the robot.
		int linesExpected = robot.sendCommand(command);
		List<String> results = new ArrayList<String>();
		try
		{
			// Read the result.
			for (int i = 0; i < linesExpected; i++)
				results.add(input.readLine());
		}
		catch (IOException e)
		{
			System.err.println("Error occured while attempting to read result");
			return false;
		}
		
		// Get the position of the robot.
		currentPosition = robot.getPosition();

		// Certain commands need concluding actions.
		if (command.equals("GETPOS"))
			// Parse the current position.
			currentPosition = parsePosition(results.get(0));
		else if (command.contains("SCAN"))
		{
			for (String result : results)
			{
				// Parse the measures.
				SensorMeasures sm = parseMeasures(currentPosition, result);
				// Update the map with the measured data.
				map.update(sm);
			}
			// Notify all ActionListeners.
			processEvent(new ActionEvent(map, ActionEvent.ACTION_PERFORMED, ""));
		}
		else
			System.out.println("Heading " + Math.round(Math.toDegrees(currentPosition.getT())));
		// The command has been executed.
		return true;
	}
		
	
	public Position getCurrentPosition()
	{
		return currentPosition;
	}
	
	/**
	 * This method parses the given robot command, calculates the new destination
	 * of "sourceShape" and checks if it collides with the information currently stored
	 * in the OccupancyMap (this.map). 
	 * @param command
	 * @param position
	 * @param sourceShape
	 * @return
	 */
	private boolean willCollide(String command, Position position, Polygon sourceShape)
	{
		String[] params = command.split(" ");
		if (params == null || params.length != 2)
			return false;
		String robotCommand = params[0];
		if (robotCommand.startsWith("ROTATE"))
		{
			// The rotation angle is given in the command.
			double angle = Double.parseDouble(params[1]);
			int orientation = robotCommand.equals("ROTATELEFT") ? -1 : 1;
			return willRotateCollide(sourceShape, orientation, angle);
		}
		else if (robotCommand.startsWith("MOVE"))
		{
			double distance = Double.parseDouble(params[1]);
			int orientation = robotCommand.equals("MOVEFW") ? 1 : -1;
			return willMoveCollide(sourceShape, orientation, distance);
		}
		
		// No collision detected. This is never supposed to be reached.
		return false;
	}
	
	/**
	 * Check if a MOVE* command will collide with the information stored in the
	 * OccupancyMap.
	 * @param sourceShape
	 * @param orientation
	 * @param distance
	 * @return
	 */
	private boolean willMoveCollide(Polygon sourceShape, int orientation, double distance)
	{
		// 1.0 is actually Platform.moveStep
		double moveStep = 1.0;
		double numMoveSteps = distance / moveStep;
		
		// Copy the robot's position.
		Position newPos = new Position();
		robot.getPosition().copyTo(newPos);
		
		// If this isn't done in this way, rounding errors will occur.
		while (numMoveSteps > 0.0)
		{
			if (numMoveSteps < 1.0)
				newPos.rototras(numMoveSteps * moveStep * orientation, 0.0, 0.0);
			else
				newPos.rototras(moveStep * orientation, 0.0, 0.0);
			numMoveSteps -= 1.0;
		}
		
		Polygon newPlatform = calculateNewShape(newPos, sourceShape);
		if (polygonCollides(newPlatform))
		{
			System.out.println("Would collide. Ignoring command");
			return true;
		}

		return false;
	}

	/**
	 * Check if a ROTATE* command will collide with the information stored in the
	 * OccupancyMap.
	 * @param sourceShape
	 * @param orientation
	 * @param angle
	 * @return
	 */
	private boolean willRotateCollide(Polygon sourceShape, int orientation, double angle)
	{
		// 1.0 is actually Platform.rotStep
		double rotStep = 1.0;
		double numRotSteps = angle / 1.0;
		
		// Copy the robot's position.
		Position newPos = new Position();
		robot.getPosition().copyTo(newPos);
		// If this isn't done in this way, rounding errors will occur.
		while (numRotSteps > 0.0)
		{
			if (numRotSteps < 1.0)
				newPos.rototras(0.0, 0.0, numRotSteps * orientation * rotStep);
			else
				newPos.rototras(0.0, 0.0, orientation * rotStep);
			numRotSteps -= 1.0;
		}
		
		Polygon newPlatform = calculateNewShape(newPos, sourceShape);
		if (polygonCollides(newPlatform))
		{
			//System.out.println("Would collide. Ignoring command");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determine if a given polygon will collide with the information stored in
	 * the current OccupancyMap.
	 * @param platform
	 * @return
	 */
	private boolean polygonCollides(Polygon platform)
	{
		PositionType[][] grid = map.getNewGrid();
		 for (int x = 0; x < grid.length; x++)
			 for (int y = 0; y < grid[x].length; y++)
			 {
				 PositionType type = grid[x][y];
				 if ((type == PositionType.OBSTACLE || type == PositionType.OPAQUE) && platform.contains(x, y))
					 return true;
			 } 

		return false;
	}
	
	/**
	 * Get a sensor by name. 
	 * @param name
	 * @return
	 */
	private Object getDeviceByName(String name)
	{
		return robot.getDeviceByName(name);
	}
	
	/**
	 * Parse the position from the GETPOS command of the Robot.
	 * @param value The string as received from the Robot.
	 * @return
	 */
	private Position parsePosition(String value)
	{
		int indexInit, indexEnd;
		String parameter;
		indexInit = value.indexOf("X=");
		parameter = value.substring(indexInit + 2);
		indexEnd = parameter.indexOf(' ');
		double x = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("Y=");
		parameter = value.substring(indexInit + 2);
		indexEnd = parameter.indexOf(' ');
		double y = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("DIR=");
		parameter = value.substring(indexInit + 4);
		double t = Double.parseDouble(parameter);
		
		return new Position(x, y, t);
	}

	
	
	/**
	 * Parse the measures which are sent from a sensor object.
	 * @param value The String which contains the measurement result
	 * @param measures Parsed measurements are stored in here
	 */
	private SensorMeasures parseMeasures(Position position, String value)
	{
		double measures[] = new double[360];
		for (int i = 0; i < 360; i++)
			measures[i] = 100.0;
		if (value.length() < 5)
			return null;
		String source = value.substring(0, value.indexOf(" "));
		value = value.substring(5 + source.length()).trim(); // removes the "SCAN " keyword
		StringTokenizer tokenizer = new StringTokenizer(value, " ");
		double dist;
		int dir;
		while (tokenizer.hasMoreTokens())
		{
			dist = Double.parseDouble(tokenizer.nextToken().substring(2));
			dir = (int) Math.round(Math.toDegrees(Double.parseDouble(tokenizer.nextToken()
					.substring(2))));
			if (dir == 360)
				dir = 0;
			measures[dir] = dist;
		}
		
		// Remove the SRC= from the source.
		String sensorName = source.substring(4).trim();
		return new SensorMeasures(position, (Device) getDeviceByName(sensorName), measures);
	}
	
	private Polygon calculateNewShape(Position newPos, Polygon original)
	{
		Polygon newShape = new Polygon();
		Point2D point = new Point2D.Double();
		for (int i = 0; i < original.npoints; i++)
		{
			point.setLocation(original.xpoints[i], original.ypoints[i]);
			// calculates the coordinates of the point according to the robot position
			newPos.rototras(point);
			// adds the point to the global shape
			newShape.addPoint((int) Math.round(point.getX()), (int) Math.round(point.getY()));
		}
		return newShape;
	}
	
	/**
	 * Add an action listener. Processed events will be passed to the
	 * listeners.
	 * @param l
	 */
	public void addActionListener(ActionListener l)
	{
		// Create the actionlisteners array if it doesn't exist yet.
		synchronized(this)
		{
			if (actionlisteners == null)
				actionlisteners = Collections.synchronizedList(new ArrayList<ActionListener>());
		}
		actionlisteners.add(l);
	}

	private void processEvent(ActionEvent e)
	{
		// No use processing events if there are no listeners.
		if (actionlisteners == null || actionlisteners.size() == 0)
			return;
		for (ActionListener listener : actionlisteners)
			listener.actionPerformed(e);
	}
	
	/**
	 * Get the current occupancy map.
	 * @return
	 */
	public OccupancyMap getMap()
	{
		return map;
	}
}

