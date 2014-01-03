package nl.hanze.project.moro.robot.device.sensor;
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

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import nl.hanze.project.moro.geom.Measure;
import nl.hanze.project.moro.model.Obstacle;
import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.device.Environment;
import nl.hanze.project.moro.robot.device.Position;
import nl.hanze.project.moro.robot.event.DeviceEvent;
import nl.hanze.project.moro.robot.event.SensorEvent;



/**
 * Title: The MObile RObot Simulation Environment Description: The Sonar accepts
 * commands from the controller and executes them in steps. The most important
 * tasks are rotating the Sonar and getting measurements from the environment.
 * Copyright: Copyright (c) 2001 Company: Universit� di Bergamo
 * 
 * @author Davide Brugali
 * @version 1.0
 */
public class Sonar extends AbstractSensor
{

	private int range = 100; // maximum range, same as the laser
	int orientation = 1; // 1: clockwise -1: otherwise
	double rotStep = 360.0; 
	double numSteps = 0;
	
	Measure detectMeasure = null;
	ArrayList<Measure> scanMeasures = new ArrayList<Measure>();

	public Sonar(String name, Robot robot, Position localPos, Environment environment)
	{
		super(name, robot, localPos, environment);
		fgColor = Color.green; // color of the sonar circle
		fillShape = false; // do not fil the sonar shape
		
		// Defines the SHAPE of the sensor. default 10.
		defineSonar(10);		
	}

	private double read(boolean first)
	{
		Point2D centre = new Point2D.Double(localPos.getX(), localPos.getY());
		Point2D front = new Point2D.Double(localPos.getX() + range * Math.cos(localPos.getT()),
				localPos.getY() + range * Math.sin(localPos.getT()));
		// reads the robot's position
		getRobot().readPosition(robotPos);
		// center's coordinates according to the robot position
		robotPos.rototras(centre);
		// front's coordinates according to the robot position
		robotPos.rototras(front);

		double minDistance = -1.0;
		for (int i = 0; i < getEnvironment().getObstacles().size(); i++)
		{
			// This is really dirty: the laser uses direct access to
			// environment's obstacles
			Obstacle obstacle = getEnvironment().getObstacles().get(i);
			double dist = pointToObstacle(obstacle.getPolygon(), centre, front, first);
			if (minDistance == -1.0 || (dist > 0 && dist < minDistance))
			{
				minDistance = dist;
				if (minDistance > -1 && first)
				{
					return minDistance;
				}
			}
		}
		if (minDistance > 0)
			return minDistance;
		return -1.0;
	}

	// receives the vertex coordinates of segment beam;
	// if segment beam intersects an edge of this PhysicalShape, it returns
	// the distance of the first vertex of beam from the closest edge
	// if beam does not intersect the PhysicalShape, the return value is -1.0
	private double pointToObstacle(Polygon polygon, Point2D centre, Point2D front, boolean first)
	{
		int j = 0;
		double minDistance = -1.0;
		double dist = -1.0;
		double px, py;
		double x1, y1, x2, y2;
		double m1, q1, m2, q2;
		Line2D.Double beam = new Line2D.Double(centre, front);

		for (int i = 0; i < polygon.npoints; i++)
		{
			j = i + 1;
			if (j == polygon.npoints)
				j = 0;
			x1 = polygon.xpoints[i];
			y1 = polygon.ypoints[i];
			x2 = polygon.xpoints[j];
			y2 = polygon.ypoints[j];
			if (beam.intersectsLine(x1, y1, x2, y2))
			{
				// calculates the intersection point
				if (centre.getX() == front.getX())
				{
					px = centre.getX();
					py = (y2 - y1) / (x2 - x1) * (px - x1) + y1;
				}
				else if (x1 == x2)
				{
					px = x1;
					py = (front.getY() - centre.getY()) / (front.getX() - centre.getX())
							* (px - centre.getX()) + centre.getY();
				}
				else
				{
					m1 = (y2 - y1) / (x2 - x1);
					q1 = y1 - m1 * x1;
					m2 = (front.getY() - centre.getY()) / (front.getX() - centre.getX());
					q2 = centre.getY() - m2 * centre.getX();
					px = (q2 - q1) / (m1 - m2);
					py = m1 * px + q1;
				}
				// calculates the distance between (cx, cy) and the intersection
				// point
				dist = Point2D.Double.distance(centre.getX(), centre.getY(), px, py);
				if (minDistance == -1.0 || minDistance > dist)
					minDistance = dist;
				if (first && minDistance > 0.0)
					return minDistance;
			}
		}
		return minDistance;
	}
	
	@Override
	/**
	 * Defines the next scan step.
	 * 
	 */
	public void nextStep()
	{
		if (running && numSteps > 0.0) {
			if (numSteps < 1.0)
				localPos.rototras(0.0, 0.0, orientation * numSteps * rotStep);// orientation of sensors.
			else
				localPos.rototras(0.0, 0.0, orientation * rotStep);// orientation of sensor.
			
			defineSonar((int) (range - (range * numSteps / 360))); // redefine sonar circle
			getEnvironment().repaint(); // repaint sonar beam
			numSteps -= 1.0; //number of steps ie scans decreased by one
			running = true;	
		} else if (running) {
			defineSonar(10);
			getEnvironment().repaint();
			
			// make the sensor ready to perform a new scan.
			isRunning(false);
			// notify listeners that the sensor has finished scanning.
			fireScanCompleted(new SensorEvent(this));
			// notify listeners that the sensor is ready for a new task.
			fireDeviceReady(new DeviceEvent(this));
		}

		double distance = this.read(false);
		if (distance > -1.0) {
			scanMeasures.add(new Measure(distance, localPos.getT())); // ??????????????
		}
	}
	
	/**
	 * Defines all the polygons that make up the sonar circle shape
	 * All points are added to this sonar object.
	 */
	private void defineSonar(int radius) {
		this.resetShape(); // reset all polygons for this object
		double x = localPos.getX() - 20;
		double y = localPos.getY();
		for (int i = 0; i < 360; i++) {
			double t = i / 360.0;
			this.addPoint((int) (x + radius * Math.cos( t * 2 * Math.PI)), (int) (y + radius * Math.sin( t * 2 * Math.PI)));
		}
	}

	@Override
	public void setRange(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("size should be larger than 0, received %d", size));
		}
		
		range = size;
	}

	@Override
	public int getRange() {
		return range;
	}

	@Override
	public void scan() {
		rotStep = 1.0;
		scanMeasures.clear();
		numSteps = 360 / rotStep; 
		orientation = 1;
		
		isRunning(true);
	}
	
	@Override
	public List<Measure> getResults()
	{
		return scanMeasures;
	}
}