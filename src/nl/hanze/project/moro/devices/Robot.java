package nl.hanze.project.moro.devices;
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

import java.awt.Graphics;
import java.io.PrintWriter;
import java.util.ArrayList;

import nl.hanze.project.moro.devices.Sonar;



/**
 * Title: The MObile RObot Simulation Environment Description: Copyright:
 * Copyright (c) 2001 Company: Universit di Bergamo
 * 
 * @author Davide Brugali
 * @version 1.0
 */
public class Robot
{
	String name;
	private Position position;
	private Platform platform;
	private ArrayList<Device> sensors = new ArrayList<Device>();
	protected PrintWriter output = null;

	public Robot(String name, double x, double y, double t, Environment environment)
	{
		this.name = name;
		position = new Position(x, y, Math.toRadians(t));
		setPlatform(new Platform("P1", this, environment));
		sensors.add(new Laser("L1", this, new Position(20.0, 0.0, 0.0), environment));
		sensors.add(new Sonar("S1", this, new Position(20.0, 0.0, 0.0), environment));
	}

	void readPosition(Position position)
	{
		synchronized (this.position)
		{
			this.position.copyTo(position);
		}
	}

	void writePosition(Position position)
	{
		synchronized (this.position)
		{
			position.copyTo(this.position);
		}
	}

	public void paint(Graphics g)
	{
		getPlatform().paint(g);
		for (Device sensor : sensors)
			sensor.paint(g);
	}

	public void start()
	{
		getPlatform().start();
		for (Device sensor : sensors)
			sensor.start();
	}

	protected synchronized void writeOut(String data)
	{
		if (output != null)
			output.println("SRC=" + this.name + " " + data);
		else
			System.out.println(this.name + " output not initialized");
	}

	/**
	 * Send the command. Returns the number of subdevices which commands got sent to.
	 * @param command
	 * @return
	 */
	public int sendCommand(String command)
	{
		int numDelegates = 0;
		if (command.equalsIgnoreCase("GETPOS"))
		{
			getPositionByCommand();
			numDelegates = 1;
		}
		else if (isPlatformCommand(command))
		{
			getPlatform().sendCommand(command);
			numDelegates = 1;
		}
		else if (isSensorCommand(command))
		{
			// Send the command to all sensors. 
			for (Device sensor : sensors)
			{
				sensor.sendCommand(command);
				numDelegates++;
			}
		}
		
		return numDelegates;
	}
	
	/**
	 * Determine if the given command is a command which needs to be sent
	 * to the Platform.
	 * @param command
	 * @return True if so, false otherwise.
	 */
	private boolean isPlatformCommand(String command)
	{
		return command.startsWith("ROTATE") || command.startsWith("MOVE") || 
			command.startsWith("GETPOS"); 
	}
	
	/**
	 * Determine if a 
	 * @param command
	 * @return
	 */
	private boolean isSensorCommand(String command)
	{
		return 
			command.startsWith("SCAN") || 
			command.startsWith("GETMEASURES") ||
			command.startsWith("DETECT") ||
			command.startsWith("READ")
			;
	}
	
	public synchronized Position getPosition()
	{
		return new Position(position.getX(), position.getY(), position.getT());
	}
	
	
	private void getPositionByCommand()
	{
		writeOut("GETPOS X=" + position.getX() + " Y=" + position.getY() + " DIR="
				+ Math.toDegrees(position.getT()));
	}

	/**
	 * Set command output destination.
	 * @param output The output destination PrintWriter.
	 */
	public void setOutput(PrintWriter output)
	{
		this.output = output;
		getPlatform().setOutput(output);
		for (Device sensor : sensors)
			sensor.setOutput(output);
	}
	
	/**
	 * Get a device instance by name.
	 * @param name The name of the device.
	 * @return Null if the device with the given name is not found.
	 */
	public Object getDeviceByName(String name)
	{
		for (Device dev : sensors)
			if (dev.getName().equals(name))
				return dev;
		if (getPlatform().getName().equals(name))
			return getPlatform();
		else if (this.name.equals(name))
			return this;
		return null;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
}