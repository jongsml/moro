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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Controller implements Runnable{
	Robot robot = null;
	OccupancyMap map = new OccupancyMap();

	public Controller(Robot robot) {
		this.robot = robot;
		if(robot == null)
			System.exit(1);
	}

	/*
	 * start() starts the robot in a new thread
	 */
	public void start() {
		robot.start();
		// This will cause the controller run() to be started in a separate thread.
		new Thread(this).start();
	}

	/*
	 * This function does not work
	 */
	public void quit() {
	}

	/**
	 * In this method the controller sends commands to the robot and its devices.
	 *  
	 */
	public void run() {
		String result = "";
		double position[] = new double[3]; // stores the position of the robot
		double measures[]  = new double[360]; // ???

		try {
			// *create the pipe and install buffered reader/writer
			// *so we can use readLine() and println

			PipedInputStream pipeIn = new PipedInputStream();
			// Puts information into a stream which can be read by other methodes
			BufferedReader input = new BufferedReader(new InputStreamReader(pipeIn)); 
			// Receives information from a stream which are set by other methodes
			PrintWriter output = new PrintWriter(new PipedOutputStream(pipeIn), true);

			// *inform robot/device in other thread where to write
			robot.setOutput(output);

			System.out.println("Controller started");

			/*
			 * Sends commands to the robot
			 */
			robot.sendCommand("R1.GETPOS");
			// *this thread now waits for result
			result = input.readLine();
			// The result is parsed to the method parsePosition to calculate the x,y position
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			//parses the collision results to parseMeasures
			parseMeasures(result, measures);
			//draws the collision results on the minimap
			map.drawLaserScan(position, measures);
			
			//moves the robot back 60 positions in the direction dir
			robot.sendCommand("P1.MOVEBW 60");
			// Receives result of the move 
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.ROTATERIGHT 90");
			result = input.readLine();

			robot.sendCommand("P1.MOVEFW 100");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.ROTATELEFT 45");
			result = input.readLine();

			robot.sendCommand("P1.MOVEFW 70");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.MOVEFW 70");
			result = input.readLine();

			robot.sendCommand("P1.ROTATERIGHT 45");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.MOVEFW 90");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.ROTATERIGHT 45");
			result = input.readLine();

			robot.sendCommand("P1.MOVEFW 90");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.ROTATERIGHT 45");
			result = input.readLine();

			robot.sendCommand("P1.MOVEFW 100");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.ROTATERIGHT 90");
			result = input.readLine();

			robot.sendCommand("P1.MOVEFW 80");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			map.drawLaserScan(position, measures);

			robot.sendCommand("P1.MOVEFW 100");
			result = input.readLine();

			robot.sendCommand("R1.GETPOS");
			result = input.readLine();
			parsePosition(result, position);

			robot.sendCommand("L1.SCAN");
			result = input.readLine();
			parseMeasures(result, measures);
			
			map.drawLaserScan(position, measures);
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * parsePosition(value, position[]) parses the results from GETPOS to a x and y position and a direction
	 * position is a multilevel array (x,y,dir)
	 * result is like "GETPOS X=90.0 Y=200.0 DIR=270.0"
	 * 
	 */
	private void parsePosition(String value, double position[]) {
		int indexInit, indexEnd;
		String parameter;
		indexInit = value.indexOf("X=");
		parameter = value.substring(indexInit+2); // removes the "X=" keyword
		indexEnd = parameter.indexOf(' ');
		// position[0] is filled with the x value
		position[0] = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("Y=");
		parameter = value.substring(indexInit+2); // removes the "Y=" keyword
		indexEnd = parameter.indexOf(' ');
		// position[1] is filled with the y value
		position[1] = Double.parseDouble(parameter.substring(0, indexEnd));

		indexInit = value.indexOf("DIR=");
		parameter = value.substring(indexInit+4);
		// position[2] is filled with the direction value (degrees) of the robot (0,90,180,270)
		position[2] = Double.parseDouble(parameter);
	}

	
	private void parseMeasures(String value, double measures[]) {
		System.out.println(value);
		
		// Fills all angles with the maximum value of 100
		// When a angle is not filled with a measure there was no collision
		for(int i=0; i < 360; i++)
			measures[i] = 100.0;
		if(value.length() < 5)
			return;
		value = value.substring(5);  // removes the "SCAN " keyword
		// fills tokenizer with values with distance (d=) and angle (t=)
		StringTokenizer tokenizer = new StringTokenizer(value, " ");
		double dist;
		int dir;
		
		// Splits tokens into distance (first 3 characters) and angle (0 - 360)
		while(tokenizer.hasMoreTokens()) {
			dist = Double.parseDouble(tokenizer.nextToken().substring(2));
			dir  = (int) Math.round( Math.toDegrees(Double.parseDouble(tokenizer.nextToken().substring(2))) );
			if(dir == 360)
				dir = 0;
			// fills array with the rounded integer of distance of the collisions
			measures[dir] = dist;
			//System.out.println("dir = " + ((int) dir) + " dist = " + dist);
		}
	}

}
