

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

public class Platform extends Device
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
	


	@Override
	public void executeCommand(String command)
	{
		if (command.indexOf("ROTATERIGHT") > -1)
		{
			double angle = Math.abs(Double.parseDouble(command.trim().substring(12).trim()));
			numRotSteps = angle / rotStep;
			orientation = 1;
			running = true;
		}
		else if (command.indexOf("ROTATELEFT") > -1)
		{
			double angle = Math.abs(Double.parseDouble(command.trim().substring(11).trim()));
			numRotSteps = angle / rotStep;
			orientation = -1;
			running = true;
		}
		else if (command.indexOf("MOVEFW") > -1)
		{
			double distance = Math.abs(Double.parseDouble(command.trim().substring(7).trim()));
			numMoveSteps = distance / moveStep;
			orientation = 1;
			running = true;
		}
		else if (command.indexOf("MOVEBW") > -1)
		{
			double distance = Math.abs(Double.parseDouble(command.trim().substring(7).trim()));
			numMoveSteps = distance / moveStep;
			orientation = -1;
			running = true;
		}
		else
			writeOut("DECLINED");
	}

	@Override
	public void nextStep()
	{
		if (!running)
			return;
		if (numRotSteps > 0.0)
		{
			if (numRotSteps < 1.0)
				robotPos.rototras(0.0, 0.0, numRotSteps * orientation * rotStep);
			else
				robotPos.rototras(0.0, 0.0, orientation * rotStep);
			// updates the robot's position
			robot.writePosition(robotPos);
			// redraws the environment
			environment.repaint();
			numRotSteps -= 1.0;
		}
		else if (numMoveSteps > 0.0)
		{
			if (numMoveSteps < 1.0)
				robotPos.rototras(numMoveSteps * moveStep * orientation, 0.0, 0.0);
			else
				robotPos.rototras(moveStep * orientation, 0.0, 0.0);
			// updates the robot's position
			robot.writePosition(robotPos);
			// redraws the environment
			environment.repaint();
			numMoveSteps -= 1.0;
		}
		else
		{
			running = false;
			writeOut("PLATFORM ARRIVED");
		}
	}

}
