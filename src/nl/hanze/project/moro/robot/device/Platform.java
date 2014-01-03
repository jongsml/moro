package nl.hanze.project.moro.robot.device;

import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.event.DeviceEvent;



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


/**
 * Title:        The MObile RObot Simulation Environment
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Universit di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

public class Platform extends AbstractDevice
{
	private int orientation = 1; // 1: clockwise -1: otherwise
	private double rotStep = 1.0; // one degree
	private double moveStep = 1.0;
	private double numRotSteps = 0;
	private double numMoveSteps = 0;

	public Platform(String name, Robot robot, Environment environment)
	{	
		super(name, robot, new Position(0.0, 0.0, 0.0), environment);
        this.addPoint(20, 20);
        this.addPoint(30, 10);
        this.addPoint(30, -10);
        this.addPoint(20, -20);
        this.addPoint(-20, -20);
        this.addPoint(-20, 20);
	}
	
	/**
	 * Will rotate the platform to the given angle in degrees to it's right.
	 * 
	 * @param angle the rotation in degrees.
	 */
	public void rotateRight(double angle)
	{
		if (angle < 0 || angle > 360) {
			throw new IllegalArgumentException(String.format("the angle should be between 0 and 360 inclusive, received %d", angle));
		}

		numRotSteps = angle / rotStep;
		orientation = 1;
		isRunning(true);
	}
	
	/**
	 * Will rotate the platform to the given angle in degrees to it's left.
	 * 
	 * @param angle the rotation in degrees.
	 */
	public void rotateLeft(double angle)
	{
		if (angle < 0 || angle > 360) {
			throw new IllegalArgumentException(String.format("the angle should be between 0 and 360 inclusive, received %d", angle));
		}
		
		numRotSteps = angle / rotStep;
		orientation = -1;
		isRunning(true);
	}
	
	/**
	 * Will move the platform forwards by the given distance.
	 * 
	 * @param distance the amount the platform should move forward.
	 */
	public void moveForward(double distance)
	{
		if (distance < 0) {
			throw new IllegalArgumentException("The given distance should be a positive value.");
		}
		
		numMoveSteps = distance / moveStep;
		orientation = 1;
		isRunning(true);	
	}
	
	/**
	 * Will move the platform backwards by the given distance.
	 * 
	 * @param distance the amount the platform should move backwards.
	 */
	public void moveBackwards(double distance)
	{
		if (distance < 0) {
			throw new IllegalArgumentException("The given distance should be a positive value.");
		}		
		
		numMoveSteps = distance / moveStep;
		orientation = -1;
		isRunning(true);
	}

	@Override
	public void nextStep()
	{
		if (numRotSteps > 0.0) {
			if (numRotSteps < 1.0) {
				robotPos.rototras(0.0, 0.0, numRotSteps*orientation*rotStep);
			} else {
				robotPos.rototras(0.0, 0.0, orientation*rotStep);
			}

			// updates the robot's position
			getRobot().writePosition(robotPos);
			// redraws the environment
			getEnvironment().repaint();
			numRotSteps-=1.0;
		} else if (numMoveSteps > 0.0) {
			if (numMoveSteps < 1.0) {
				robotPos.rototras(numMoveSteps * moveStep * orientation, 0.0, 0.0);
			} else {
				robotPos.rototras(moveStep * orientation, 0.0, 0.0);
			}
			// updates the robot's position
			getRobot().writePosition(robotPos);
			// redraws the environment
			getEnvironment().repaint();
			numMoveSteps-=1.0;
		} else {
			// make the device ready to receive a new task.
			isRunning(false);
			// notify listeners that this device is ready for a new task.
			fireDeviceReady(new DeviceEvent(this));
		}
	}

}
