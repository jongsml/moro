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
 * Company:      Universit� di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

import java.awt.Graphics;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Robot {
	String name;
	Position position;
	Platform platform;
	ArrayList<Device> sensors = new ArrayList<Device>();
	protected PrintWriter output = null;

	public Robot(String name, double x, double y, double t, Environment environment) {
		this.name = name;
		position = new Position(x, y, Math.toRadians(t));
		platform = new Platform("P1", this, environment);
		sensors.add(new Laser("L1", this, new Position(20.0, 0.0, 0.0), environment));
        sensors.add(new Sonar("S1", this, new Position(20.0, 0.0, 0.0), environment));
	}

	/*
	 * Reads the current position of the robot
	 * and copies this to the x,y and t values
	 */
	public void readPosition(Position position) {
		synchronized(this.position) {
			this.position.copyTo(position);
		}
	}

	/*
	 * Writes the x,y and t values to x,y and t
	 */
	public void writePosition(Position position) {
		synchronized(this.position) {
			position.copyTo(this.position);
		}
	}

    public void paint(Graphics g)
    {
        platform.paint(g);
        for (Device sensor : sensors)
            sensor.paint(g);
    }

    public void start()
    {
        platform.start();
        for (Device sensor : sensors)
            sensor.start();
    }

	/*
	 * ?????
	 */
	protected synchronized void writeOut(String data) {
		if(output != null)
			output.println(data);
		else
			System.out.println(this.name + " output not initialized");
	}

	/*
	 * Sends the commands to the platform or sensor
	 */
	public boolean sendCommand(String p_command) {
		int indexInit = p_command.indexOf(".");
		if(indexInit < 0)
			return false;
		String deviceName = p_command.substring(0, indexInit);
		String command = p_command.substring(indexInit+1);

		if(deviceName.equals(this.name) && command.equalsIgnoreCase("GETPOS")) {
			writeOut("GETPOS X=" + position.getX() +
					" Y=" + position.getY() +
					" DIR=" + Math.toDegrees(position.getT()));
		}
		else if(deviceName.equals(platform.name))
			return platform.sendCommand(command);
		else
			for(Device sensor: sensors ) {
				if(deviceName.equals(sensor.name))
					return sensor.sendCommand(command);
			}
		return false;
	}

	/*
	 * Sends the status output to the streamReader
	 */
	public void setOutput(PrintWriter output) {
		this.output = output;
		platform.setOutput(output);
		for(Device sensor: sensors ) {
			sensor.setOutput(output);
		}
	}

/*
 * Test methode
 */
	public void test() {
		platform.start();
//		platform.println("P1.MOVEFW 50");
//		platform.println("P1.ROTATERIGHT 45");
//		platform.println("P1.MOVEFW 100");
		Laser laser = (Laser) sensors.get(0);
		laser.start();
		laser.sendCommand("L1.SCAN");
	}



    public Object getDeviceByName(String name)
    {
        for (Device dev : sensors)
            if (dev.name.equals(name))
                return dev;
        if (platform.name.equals(name))
            return platform;
        else if (this.name.equals(name))
            return this;
        return null;
    }
}
