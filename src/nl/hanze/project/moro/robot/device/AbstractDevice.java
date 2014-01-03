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

/**
 * Title:        The MObile RObot Simulation Environment
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Universit di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.event.DeviceEvent;
import nl.hanze.project.moro.robot.event.DeviceListener;

public abstract class AbstractDevice implements Device, Runnable
{	
	/**
	 * Listeners that will be informed when the device is ready for a new task.
	 */
	protected List<DeviceListener> listeners = new ArrayList<DeviceListener>();	
	
	private String name;                  // the name of this device
	private Environment environment;          // a reference to the environment
	private Robot robot;
	private Polygon shape = new Polygon();        // the device's shape in local coords
    protected Boolean fillShape = true;          // filled or not
    
	// the robot current position
	protected Position robotPos = new Position();
	// origin of the device reference frame with regards to the robot frame
	protected Position localPos;

	protected Color bgColor = Color.red;
	protected Color fgColor = Color.blue;

	protected Thread thread = null;
	protected long delay = 3;
	protected boolean alive = false; // Is the device started?
	protected boolean running = false; // Is the device executing a command?
	protected Object lock = new Object();

	// the constructor
	public AbstractDevice(String name, Robot robot, Position position, Environment environment) 
	{
		this.name = name;
		this.setRobot(robot);
		robot.readPosition(this.robotPos);
		this.localPos = position;
		this.environment = environment;
		// This will cause the device run() to be started in a separate thread.
		thread = new Thread(this);
	}

	// this method is invoked when the geometric shape of the device is defined
	public void addPoint(int x, int y) {
		getShape().addPoint(x, y);
	}

    protected void resetShape()
    {
        getShape().reset();
    }

    public void paint(Graphics g)
    {
        // reads the robot's current position
        getRobot().readPosition(robotPos);
        // draws the shape
        Polygon globalShape = new Polygon();
        Point2D point = new Point2D.Double();
        for (int i = 0; i < getShape().npoints; i++)
        {
            point.setLocation(getShape().xpoints[i], getShape().ypoints[i]);
            // calculates the coordinates of the point according to the local position
            localPos.rototras(point);
            // calculates the coordinates of the point according to the robot position
            robotPos.rototras(point);
            // adds the point to the global shape
            globalShape.addPoint((int) Math.round(point.getX()), (int) Math.round(point.getY()));
        }
        ((Graphics2D) g).setColor(bgColor);
        if (fillShape) ((Graphics2D) g).fillPolygon(globalShape);
        ((Graphics2D) g).setColor(fgColor);
        ((Graphics2D) g).drawPolygon(globalShape);
    }
	
    @Override
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Determines if the device is busy processing a task, or if it's ready to receive
	 * a new task.
	 * 
	 * @param isRunning determines if the device is processing a task.
	 */
	public void isRunning(boolean isRunning)
	{
		running = isRunning;
		
		// if this device should run a thread will notified to process the task.
		if (running) {
			synchronized(lock) {
				lock.notify();
			}			
		}
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
				} else {
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
	
	public abstract void nextStep();

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) 
	{
		this.name = name;
	}	
	
	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public Polygon getShape() {
		return shape;
	}

	public void setShape(Polygon shape) {
		this.shape = shape;
	}

    @Override
    public void addDeviceListener(DeviceListener listener)
    {
    	listeners.add(listener);
    }
    
    @Override
    public void removeDeviceListener(DeviceListener listener)
    {
    	listeners.remove(listener);
    }
    
    @Override
    public boolean hasDeviceListener(DeviceListener listener)
    {
    	return listeners.contains(listener);
    }
	
    @Override
	public void fireDeviceReady(DeviceEvent event)
	{		
		for(DeviceListener listener : listeners) {
			listener.onDeviceReady(event);
		}
	}
}
