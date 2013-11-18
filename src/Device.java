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
 * Company:      Università di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class Device implements Runnable{
	String name;                  // the name of this device
	Environment environment;          // a reference to the environment
	Polygon shape = new Polygon();        // the device's shape in local coords

	// a reference to the robot
	Robot robot;
	// the robot current position
	Position robotPos = new Position();
	// origin of the device reference frame with regards to the robot frame
	Position localPos;

	Color bgColor = Color.red;
	Color fgColor = Color.blue;

	protected Thread thread = null;
	protected long delay = 20;
	protected boolean alive = false; // Is the device started?
	protected boolean running = false; // Is the device executing a command?
	protected Object lock = new Object();
	protected ArrayList<String> commands = new ArrayList<String>();

	protected PrintWriter output = null;

	// the constructor
	public Device(String name, Robot robot, Position local, Environment environment) {
		this.name = name;
		this.robot = robot;
		robot.readPosition(this.robotPos);
		this.localPos = local;
		this.environment = environment;
		// This will cause the device run() to be started in a separate thread.
		thread = new Thread(this);
	}

	// this method is invoked when the geometric shape of the device is defined
	public void addPoint(int x, int y) {
		shape.addPoint(x, y);
	}

	// draws the device's geometric shape on the graphical interface
	public void paint(Graphics g) {
		// reads the robot's current position
		robot.readPosition(robotPos);
		// draws the shape
		Polygon globalShape = new Polygon();
		Point2D point = new Point2D.Double();
		for(int i=0; i < shape.npoints; i++) {
			point.setLocation(shape.xpoints[i], shape.ypoints[i]);
			// calculates the coordinates of the point according to the local position
			localPos.rototras(point);
			// calculates the coordinates of the point according to the robot position
			robotPos.rototras(point);
			// adds the point to the global shape
			globalShape.addPoint((int)Math.round(point.getX()),(int)Math.round(point.getY()));
		}
		((Graphics2D) g).setColor(bgColor);
		((Graphics2D) g).fillPolygon(globalShape);
		((Graphics2D) g).setColor(fgColor);
		((Graphics2D) g).drawPolygon(globalShape);
	}

	/*
	 * Sends a command to the robot in a new thread
	 */
	public boolean sendCommand(String command) {
		commands.add(command);
		synchronized(lock) {
			// Wake up another thread that is waiting for commands.
			lock.notify();
		}
		return true;
	}

	/*
	 * WriteOut() sends the status back to Controller
	 * by using the stream.
	 */
	protected synchronized void writeOut(String data) {
		
		if(output != null)
			output.println(data);
		else
			System.out.println(this.name + " output not initialized");
	}
	
	/*
	 * Starts the simulation
	 */
	public synchronized void start() {
		if(! alive) {
			alive = true;
			thread.start();
		}
	}
	
	/*
	 * Stops the simulation by interrupting the thread
	 */
	public synchronized void quit() {
		if(alive) {
			alive = false;
			// thread might be waiting for commands so give it an interrupt.
			thread.interrupt();
		}
	}
	
	/*
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Device " + this.name + " started");

		do {
			try {
				if(running) {
					// busy now, pause before the next step
					synchronized(this) {
						Thread.sleep(delay);
					}
				}
				else if(commands.size() > 0) {
					// extracts the the next command and executes it
					String command = commands.remove(0);
					executeCommand(command);
				}
				else {
					// waits for a new command
					synchronized(lock) {
						// suspend this thread and wait to be notified about 
						// a new command (in sendCommand()).
						lock.wait();
					}
				}
				// processes a new step
				nextStep();
			}
			catch (InterruptedException ie) {
				continue;
			}
		}
		while (alive);
	}
	
	public abstract void executeCommand(String command);
	
	public abstract void nextStep();

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

}
