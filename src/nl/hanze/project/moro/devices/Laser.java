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


/**
 * Title:        The MObile RObot Simulation Environment
 * Description:  The laser accepts commands from the controller and executes them in steps.
 *               The most important tasks are rotating the laser and getting measurements
 *               from the environment.
 * Copyright:    Copyright (c) 2001
 * Company:      Universit di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import nl.hanze.project.moro.geom.Measure;
import nl.hanze.project.moro.model.Obstacle;

public class Laser extends Device{
		
	int orientation = 1;      // 1: clockwise  -1: otherwise
	double rotStep = 1.0;     // one degree
	double numSteps = 0;
	
	int range = 100;          // maximum range

	Measure detectMeasure = null;
	ArrayList<Measure> scanMeasures = new ArrayList<Measure>();

	public Laser(String name, Robot robot, Position localPos, Environment environment) {
		super(name, robot, localPos, environment);
		
		// Color of the laser
		bgColor = Color.cyan;
		
		// Defines the size of the laser (length 100 and width 2)
		this.addPoint(0, 2);
		this.addPoint(100, 2);
		this.addPoint(100, -2);
		this.addPoint(0, -2);
	}

	
	public double read(boolean first) {
		// Defines the center of the laserbeam
		Point2D centre = new Point2D.Double(localPos.getX(), localPos.getY());
		// Defines the end of the laserbeam
		Point2D front = new Point2D.Double(localPos.getX() + range * Math.cos(localPos.getT()),
				localPos.getY() + range * Math.sin(localPos.getT()));
		// reads the robot's position
		getRobot().readPosition(robotPos);
		// center's coordinates according to the robot position
		robotPos.rototras(centre);
		// front's coordinates according to the robot position
		robotPos.rototras(front);

		// Defines the minimum distance of a collision. If no collision is detected the
		// minDistance is -1.0
		double minDistance = -1.0;
		
		// Checks if the laser has a collision with any of the obstacles
		for(int i=0; i < getEnvironment().getObstacles().size(); i++) {
			// This is really dirty: the laser uses direct access to environment's obstacles
			Obstacle obstacle = (Obstacle) getEnvironment().getObstacles().get(i);
			
			// Checks if obstacle is a Wall
			if(obstacle.isOpaque())
				continue;
			
			// Uses pointToObstacle() to check if there is a collision with a obstacle
			// and returns the distance to that obstacle
			double dist = pointToObstacle(obstacle.getPolygon(), centre, front, first);
			
			if(minDistance == -1.0 || (dist > 0 && dist < minDistance)) {
				minDistance = dist;
				if(minDistance > -1 && first) {
					return minDistance;
				}
			}
		}
		if(minDistance > 0)
			return minDistance;
		return -1.0;
	}

	// receives the vertex coordinates of segment beam;
	// if segment beam intersects an edge of this PhysicalShape, it returns
	// the distance of the first vertex of beam from the closest edge
	// if beam does not intersect the PhysicalShape, the return value is -1.0
	public double pointToObstacle(Polygon polygon, Point2D centre, Point2D front, boolean first) {
		int j = 0;
		double minDistance = -1.0;
		double dist = -1.0;
		double px, py;
		double x1, y1, x2, y2;
		double m1, q1, m2, q2;
		
		// Draws the laserbeam
		Line2D.Double beam = new Line2D.Double(centre, front);

		// Checks if beam intersects with a obstacle
		for(int i=0; i < polygon.npoints; i++) {
			j = i+1;
			if(j == polygon.npoints)
				j = 0;
			x1 = polygon.xpoints[i];
			y1 = polygon.ypoints[i];
			x2 = polygon.xpoints[j];
			y2 = polygon.ypoints[j];
			if(beam.intersectsLine(x1, y1, x2, y2)) {
				// calculates the intersection point
				if(centre.getX() == front.getX()) {
					px = centre.getX();
					py = (y2 - y1) / (x2 - x1) * (px - x1) + y1;
				}
				else
					if(x1 == x2) {
						//beam is vertical
						px = x1;
						py = (front.getY()-centre.getY()) / (front.getX()-centre.getX()) * (px - centre.getX()) + centre.getY();
					}
					else {
						// laserbeam is in an angle
						m1 = (y2 - y1) / (x2 - x1);
						q1 = y1 - m1 * x1;
						m2 = (front.getY()-centre.getY()) / (front.getX()-centre.getX());
						q2 = centre.getY() - m2 * centre.getX();
						px = (q2 - q1) / (m1 - m2);
						py = m1 * px + q1;
					}
				// calculates the distance between (cx, cy) and the intersection point
				dist = Point2D.Double.distance(centre.getX(), centre.getY(), px, py);
				
				if(minDistance == -1.0 || minDistance > dist)
					minDistance = dist;
				// returns minDistance if a collision has occured
				if(first && minDistance > 0.0)
					return minDistance;
			}
		}
		return minDistance;
	}

	/*
	 * @see Device#executeCommand(java.lang.String)
	 */
	public void executeCommand(String command) {
		// handles the Rotation of the robot
		if(command.equalsIgnoreCase("SCAN")) {
			rotStep = 1.0;
			// Clears the measures of the last scan
			scanMeasures.clear();
			numSteps = 360.0 / rotStep;
			orientation = 1;
			// send the list of measures
			commands.add("GETMEASURES");
			running = true;
		}
		else if(command.equalsIgnoreCase("GETMEASURES")) {
			Measure measure = null;
			String measures = "SCR=L1";
			// Files the measures array with the individual measure
			for(int i=0; i < scanMeasures.size(); i++) {
				measure = scanMeasures.get(i);
				measures += " d=" + measure.distance + " t=" + measure.direction;
			}
			// Sends the array back to Controller
			writeOut(measures);
		}
	}


	public void nextStep() {
		if(running && numSteps > 0.0) {
			if(numSteps < 1.0) {
				localPos.rototras(0.0, 0.0, orientation*numSteps*rotStep);
			} else {
				localPos.rototras(0.0, 0.0, orientation*rotStep);
			}
			getEnvironment().repaint();
			numSteps-=1.0;
			running = true;
		} else if(running) {
			running = false;
		}
		
		double distance = this.read(false);
		if(distance > -1.0) {
			// adds the distance and angle in the scanMeasures array
			scanMeasures.add(new Measure(distance, localPos.getT()));   
		}
	}
}
