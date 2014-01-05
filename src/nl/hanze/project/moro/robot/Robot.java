package nl.hanze.project.moro.robot;
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
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import nl.hanze.project.moro.SensorMeasures;
import nl.hanze.project.moro.commands.Command;
import nl.hanze.project.moro.geom.Measure;
import nl.hanze.project.moro.model.OccupancyMap;
import nl.hanze.project.moro.robot.algorithm.FollowTheWall;
import nl.hanze.project.moro.robot.algorithm.Pathfinding;
import nl.hanze.project.moro.robot.device.AbstractDevice;
import nl.hanze.project.moro.robot.device.Device;
import nl.hanze.project.moro.robot.device.Environment;
import nl.hanze.project.moro.robot.device.Platform;
import nl.hanze.project.moro.robot.device.Position;
import nl.hanze.project.moro.robot.device.PositionType;
import nl.hanze.project.moro.robot.device.sensor.AbstractSensor;
import nl.hanze.project.moro.robot.device.sensor.Laser;
import nl.hanze.project.moro.robot.device.sensor.Sonar;
import nl.hanze.project.moro.robot.event.DeviceEvent;
import nl.hanze.project.moro.robot.event.DeviceListener;
import nl.hanze.project.moro.robot.event.OccupancyMapEvent;
import nl.hanze.project.moro.robot.event.OccupancyMapListener;
import nl.hanze.project.moro.robot.event.SensorEvent;
import nl.hanze.project.moro.robot.event.SensorListener;
import nl.hanze.project.moro.util.CountDown;

/**
 * Title: The MObile RObot Simulation Environment Description: Copyright:
 * Copyright (c) 2001 Company: Universit di Bergamo
 * 
 * @author Davide Brugali
 * @version 1.0
 */
public class Robot implements Device, DeviceListener, SensorListener
{
	private String name = "robot";
	private Position position;
	private Platform platform;

	/**
	 * Listeners that will be informed when the device is ready for a new task.
	 */
	protected List<DeviceListener> listeners = new ArrayList<DeviceListener>();	
	
	/**
	 * Allows the robot to keep track of how many sensors are done scanning. 
	 */
	private CountDown countDown = new CountDown(0);
	
	/**
	 * A list containing all the sensors used by the robot to scan the environment.
	 */
	private List<AbstractSensor> sensors = new ArrayList<AbstractSensor>();	
	
	/**
	 * The pathfinding algorithm which the robot uses to move through the environment.
	 */
	private Pathfinding algorithm;
	
	/**
	 * Commands that can't be executed are placed inside a queue of tasks.
	 */
	private List<Command> tasks = new ArrayList<Command>();		
	
	/**
	 * The map which is used by the robot to find obstacles within the environment.
	 */
	private OccupancyMap map = new OccupancyMap();
	
	/**
	 * A list containing all listeners that need to be informed of changes to the map.
	 */
	private List<OccupancyMapListener> mapListeners = new ArrayList<OccupancyMapListener>();
	
	/**
	 * This enum type consists of predefined direction which the algorithm
	 * knows about.
	 * 
	 * @author Chris Harris
	 * @version 0.0.1
	 * @since 0.0.1
	 */
	public static enum Direction
	{
		NORTH(270), EAST(0), SOUTH(90), WEST(180);
		
		/**
		 * The angle in degrees associated with the given direction.
		 */
		private final int angle;
		
		/**
		 * Creates a new <code>Direction</code> with the given angle in degrees.
		 * 
		 * @param angle the angle in degrees.
		 */
		private Direction(int angle) 
		{
			this.angle = angle;
		}
		
		/**
		 * Returns the angle in degrees for this direction.
		 * 
		 * @return the angle in degrees.
		 */
		public int getAngle()
		{
			return angle;
		}
		
		/**
		 * Returns the <code>Direction</code> for the given angle, 
		 * or <i>null</i> if no direction exists for the given angle.
		 * 
		 * @param angle the angle to find a <code>Direction</code> for.
		 * @return the <code>Direction</code> that was found.
		 */
		public static Direction getDirection(int angle)
		{
			// flag indicating if a direction was found.
			boolean contains = false;
			int index = 0;
			
			// all directions and the direction matching the angle.
			Direction direction = null;
			Direction[] directions = values();
			
			// find direction with the given angle.
			while (index < directions.length && !contains) {
				// direction found.
				if (directions[index].getAngle() == angle) {
					// update direction.
					direction = directions[index];
					// update flag.
					contains = true;
				}
				// increment index.
				index++;
			}
			
			return direction;
		}
	}	
	
	/**
	 * This enum type consists of predefined actions which the robot can perform.
	 * 
	 * @author Chris Harris
	 * @version 0.0.1
	 * @since 0.0.1
	 */
	public static enum Action {
		MOVE_FORWARD(1), ROTATE_RIGHT(1), MOVE_BACKWARDS(-1), ROTATE_LEFT(-1);
		
		/**
		 * The value associated with the given action.
		 */
		private final int orientation;
		
		/**
		 * Creates a new <code>Action</code> which the robot can perform.
		 * 
		 * @param orientation the value associated with the given action.
		 */
		private Action(int orientation) 
		{
			this.orientation = orientation;
		}
		
		/**
		 * Returns the value associated with the action.
		 * 
		 * @return the value associated with the given action.
		 */
		public int getOrientation() 
		{
			return orientation;
		}
	}
	
	/**
	 * Creates a new robot.
	 * 
	 * @param x
	 * @param y
	 * @param t
	 * @param environment
	 */
	public Robot(double x, double y, double t, Environment environment)
	{
		// set location of the robot within the environment.
		position = new Position(x, y, Math.toRadians(t));
		// set platform.
		setPlatform(new Platform("P1", this, environment));
		// add laser.
		addSensor(new Laser("L1", this, new Position(20.0, 0.0, 0.0), environment));
		// add sonar.
		addSensor(new Sonar("S1", this, new Position(20.0, 0.0, 0.0), environment));
		// set path finding algorithm.
		algorithm = new FollowTheWall(this);
	}

	/**
	 * Returns the occupancy map which the robot uses to find obstacles in the environment.
	 * 
	 * @return the map that the robot uses to explore the environment.
	 */
	public OccupancyMap getMap()
	{
		return map;
	}	
	
	/**
	 * Returns the direction the robot is facing.
	 * 
	 * @return the direction the robot is facing.
	 */
	public Direction getDirection() 
	{
		// the angle in degrees the robot is facing.
		int angle = (int) Math.round(Math.toDegrees(getPosition().getT()));
		// prevent null literal from being returned.
		if (angle == 360) {
			angle = 0;
		}
		
		// the direction the robot is facing.
		return Direction.getDirection(angle);
	}

	public void readPosition(Position position)
	{
		synchronized (this.position)
		{
			this.position.copyTo(position);
		}
	}

	public void writePosition(Position position)
	{
		synchronized (this.position)
		{
			position.copyTo(this.position);
		}
	}	
	
	public void paint(Graphics g)
	{
		getPlatform().paint(g);
		for (AbstractDevice sensor : sensors)
			sensor.paint(g);
	}

	public void start()
	{
		getPlatform().start();
		for (AbstractDevice sensor : sensors) {
			sensor.start();
		}
		
		algorithm.runAlgorithm();
	}
	
	/**
	 * Add <code>Command</code> to a queue of tasks.
	 * 
	 * @param command the command that will be added to the queue.
	 */
	protected void addCommand(Command command)
	{
		if (command == null) {
			throw new IllegalArgumentException(String.format("first argument should be instance of %s, null given.", Command.class.getName()));
		}
		
		tasks.add(command);
	}
	
	/**
	 * Will execute the given command, and store the command to 
	 * a list of executed commands
	 * 
	 * @param command the <code>Command</code> to execute.
	 */
	public synchronized void executeCommand(Command command)
	{
		// execute command and return prematurely.
		if (!isRunning()) {
			command.execute();
			return;
		}
		
		// handle command at a later time.
		addCommand(command);
	}	
	
	/**
	 * Returns <i>true</i> if the robot will collide moving or rotating into
	 * the given direction, <i>false</i> otherwise.
	 * 
	 * @param action the action which the robot will perform like moving or rotating.
	 * @return <i>true</i> if the robot will collide, <i>false</i> otherwise.
	 */
	public boolean willCollide(Action action, double amount) 
	{
		// a flag that determines if a collision will occur.
		boolean willCollide = true;
		
		/*
		 * different directions require different requirements so determine
		 * which method is suitable to check for a possible collision.
		 */
		switch (action) {
			case MOVE_FORWARD:
			case MOVE_BACKWARDS:
				willCollide = willMoveCollide(action, amount);
				break;
			case ROTATE_RIGHT:
			case ROTATE_LEFT:
				willCollide = willRotateCollide(action, amount);
				break;
		}
		
		return willCollide;
	}
	
	/**
	 * Returns <i>true</i> if the robot will collide with another object in the given direction,
	 * <i>false</i> otherwise.
	 * 
	 * @param action the action the robot should perform.
	 * @param distance the distance the robot should move.
	 * @return <i>true</i> if the robot will collide, <i>false</i> otherwise.
	 */
	private boolean willMoveCollide(Action action, double distance)
	{
		// 1.0 is actually Platform.moveStep
		double moveStep = 1.0;
		double numMoveSteps = distance / moveStep;
		
		// Copy the robot's position.
		Position newPos = new Position();
		getPosition().copyTo(newPos);

		// If this isn't done in this way, rounding errors will occur.
		while (numMoveSteps > 0.0)
		{
			if (numMoveSteps < 1.0) {
				newPos.rototras(numMoveSteps * moveStep * action.getOrientation(), 0.0, 0.0);
			} else {
				newPos.rototras(moveStep * action.getOrientation(), 0.0, 0.0);
			}
			numMoveSteps -= 1.0;
		}
		
		Polygon newPlatform = calculateNewShape(newPos, getPlatform().getShape());
		return polygonCollides(newPlatform);
	}
	
	/**
	 * Returns <i>true</i> if the robot will collide with another object if rotated into the given direction,
	 * <i>false</i> otherwise.
	 * 
	 * @param action the action the robot should perform.
	 * @param angle the angle the robot should rotate to.
	 * @return <i>true</i> if the robot will collide, <i>false</i> otherwise.
	 */
	private boolean willRotateCollide(Action action, double angle)
	{
		// 1.0 is actually Platform.rotStep
		double rotStep = 1.0;
		double numRotSteps = angle / 1.0;
		
		// Copy the robot's position.
		Position newPos = new Position();
		getPosition().copyTo(newPos);
		
		// If this isn't done in this way, rounding errors will occur.
		while (numRotSteps > 0.0)
		{
			if (numRotSteps < 1.0)
				newPos.rototras(0.0, 0.0, numRotSteps * action.getOrientation() * rotStep);
			else
				newPos.rototras(0.0, 0.0, action.getOrientation() * rotStep);
			numRotSteps -= 1.0;
		}
		
		Polygon newPlatform = calculateNewShape(newPos, getPlatform().getShape());
		return polygonCollides(newPlatform);
	}	
	
	/**
	 * Returns <i>true</i> if the shape of the robot will collide with another object.
	 * 
	 * @param shape the shap which will be tested will all other objects known to the robot.
	 * @return <i>true</i> if the given shape will collide, <i>false</i> otherwise.
	 */
	private boolean polygonCollides(Polygon shape)
	{
		PositionType[][] grid = map.getNewGrid();
		 for (int x = 0; x < grid.length; x++)
			 for (int y = 0; y < grid[x].length; y++)
			 {
				 PositionType type = grid[x][y];
				 if ((type == PositionType.OBSTACLE || type == PositionType.OPAQUE) && shape.contains(x, y))
					 return true;
			 } 

		return false;
	}
	
	/**
	 * Returns a new <i>Polygon</i> which is calculated using the original shape and the
	 * location where the shape should be placed.
	 * 
	 * @param newPos a new location where the shape should be painted.
	 * @param original the shape that needs to be relocated.
	 * @return a <code>Polygon</code> which represents the shape at the new location.
	 */
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
	 * Will move the robot forwards by the given distance.
	 * 
	 * The actual of task of moving the robot is delegated to an associated helper object, 
	 * namely the underlying platform.
	 * 
	 * @param distance the amount the platform should move forward.
	 * @return returns <i>true</i> if the robot will move, <i>false</i> otherwise.
	 * @see Platform#moveForward(double)
	 */
	public boolean moveForward(double distance)
	{
		boolean isReady = true;
		if (isReady = !platform.isRunning()) {
			platform.moveForward(distance);
		}
		return isReady;
	}
	
	/**
	 * Will move the robot backwards by the given distance.
	 * 
	 * The actual of task of moving the robot is delegated to an associated helper object, 
	 * namely the underlying platform.
	 * 
	 * @param distance the amount the platform should move forward.
	 * @return returns <i>true</i> if the robot will move, <i>false</i> otherwise.
	 * @see Platform#moveBackwards(double)
	 */
	public boolean moveBackwards(double distance)
	{
		boolean isReady = true;
		if (isReady = !platform.isRunning()) {
			platform.moveBackwards(distance);
		}
		return isReady;
	}
	
	/**
	 * Will rotate the robot to the given angle in degrees to it's left.
	 * 
	 * The actual of task of rotating the robot is delegated to an associated helper object, 
	 * namely the underlying platform.
	 * 
	 * @param angle the rotation in degrees.
	 * @return returns <i>true</i> if the robot can rotate, <i>false</i> otherwise.
	 * @see Platform#rotateLeft(double)
	 */
	public boolean rotateLeft(double angle)
	{
		boolean isReady = true;
		if (isReady = !platform.isRunning()) {
			platform.rotateLeft(angle);
		}
		return isReady;
	}
	
	/**
	 * Will rotate the robot to the given angle in degrees to it's right.
	 * 
	 * The actual of task of rotating the robot is delegated to an associated helper object, 
	 * namely the underlying platform.
	 * 
	 * @param angle the rotation in degrees.
	 * @return returns <i>true</i> if the robot can rotate, <i>false</i> otherwise.
	 * @see Platform#rotateRight(double)
	 */
	public boolean rotateRight(double angle)
	{
		boolean isReady = true;
		if (isReady = !platform.isRunning()) {
			platform.rotateRight(angle);
		}
		return isReady;
	}
	
	/**
	 * Returns <i>true</i> if one or more devices of the robot are currently running.
	 * 
	 * @return <i>true</i> if the robot is performing an action, <i>false</i> otherwise.
	 */
	public boolean isRunning()
	{
		// flag that determines if the platform is moving.
		boolean isRunning = platform.isRunning();

		// check all sensor used by the robot.
		int index = 0;
		while (index < sensors.size() && !isRunning) {
			// flag that determines if the given sensor is scanning.
			isRunning = sensors.get(index).isRunning();
			// increase counter.
			index++;
		}
		
		return isRunning;
	}
	
	/**
	 * Will scan the environment using one or more sensors to find obstacles.
	 *
	 * The actual of task of scanning the environment is delegated to associated helper objects, 
	 * namely one or more underlying sensors.
	 * 
	 * @return returns <i>true</i> if all sensors are ready to scan, <i>false</i> otherwise.
	 * @see AbstractSensor#scan()
	 */
	public boolean scan()
	{
		boolean isReady = true;
		int index = 0;
		while (index < sensors.size() && isReady) {
			// flag that determines if the device is ready.
			isReady = !sensors.get(index).isRunning();
			// increase counter.
			index++;
		}
	
		// scan only if all sensors are ready.
		if (isReady) {
			for (AbstractDevice sensor : sensors) {
				// explicit cast to sensor.
				if (sensor instanceof AbstractSensor) {
					((AbstractSensor) sensor).scan();
				}
			}
		}
		
		return isReady;
	}
	
	public synchronized Position getPosition()
	{
		return new Position(position.getX(), position.getY(), position.getT());
	}
	
	/**
	 * Returns a list containing all sensors used by the robot.
	 * 
	 * @return all sensors used by the robot.
	 */
	public List<AbstractSensor> getSensors()
	{
		return sensors;
	}

	/**
	 * Add sensor to the robot which can be used to scan the environment.
	 * 
	 * @param sensor the sensor which will be added.
	 */
	public void addSensor(AbstractSensor sensor)
	{
		if (sensor == null) {
			throw new IllegalArgumentException(String.format("sensor should be instance of %s, and not null", AbstractSensor.class.getName()));
		}
		
		// add robot as sensor listener.
		sensor.addSensorListener(this);
		// add sensor to the robot.
		sensors.add(sensor);
		
		// create a new count down.
		countDown = new CountDown(sensors.size());
	}
	
	/**
	 * Removes the sensor at the specified position.
	 * 
	 * @param index index of the sensor to return.
	 * @return the sensor at the specified position.
	 */
	public AbstractSensor removeSensor(int index)
	{
		if (index < 0 && index >= sensors.size()) {
			throw new IndexOutOfBoundsException(String.format("only %d sensor(s) available, index given %d", sensors.size(), index));
		}
		
		// remove sensor from robot.
		AbstractSensor sensor = sensors.remove(index);
		// remove robot as sensor listener.
		sensor.removeSensorListener(this);
		
		// create a new count down.
		countDown = new CountDown(sensors.size());
		
		return sensor;
	}
	
	/**
	 * Removes the first sensor that matches with the given name.
	 * 
	 * @param name the name of the sensor to remove.
	 * @return the sensor with the specified name.
	 */
	public AbstractSensor removeSensor(String name)
	{
		boolean contains = false;
		int index = -1;
		
		// find sensor with the given name
		while (index < sensors.size() && !contains) {
			// increase index.
			index++;
			// stop searching if the sensor matches the given name.
			contains = (sensors.get(index).getName() == name);
		}

		// remove sensor from the list.
		if (contains) {
			return removeSensor(index);
		}
		
		// no sensor found.
		return null;
	}
	
	/**
	 * Removes the sensor that matches with the given sensor.
	 * 
	 * @param sensor the sensor to remove.
	 * @return <i>true</i> if the sensor was removed, <i>false</i> otherwise.
	 */
	public AbstractSensor removeSensor(AbstractSensor sensor)
	{
		// remove sensor from the list.
		int index = sensors.indexOf(sensor);
		if (index >= 0) {
			return removeSensor(index);
		}
		
		// no sensor found.
		return null;
	}
	
	/**
	 * Returns the first sensor that matches the given name.
	 * 
	 * @param name the name of the sensor.
	 * @return the sensor with the specified name.
	 */
	public AbstractSensor getSensor(String name) 
	{
		boolean sensorFound = false;
		int index = -1;
		
		// find sensor with the given name
		while (index < sensors.size() && !sensorFound) {
			// increase index.
			index++;
			// stop searching if the sensor matches the given name.
			sensorFound = sensors.get(index).getName() == name;
		}
		
		return (sensorFound) ? sensors.get(index) : null;
	}
	
	/**
	 * Returns the sensor at the specified position.
	 * 
	 * @param index index of the sensor to return.
	 * @return the sensor at the specified position.
	 */
	public AbstractSensor getSensor(int index)
	{
		if (index < 0 && index >= sensors.size()) {
			throw new IndexOutOfBoundsException(String.format("only %d sensor(s) available, index given %d", sensors.size(), index));
		}
		
		return sensors.get(index);
	}

	/**
	 * Returns <i>true</i> if the robot has a sensor with the specified name.
	 * 
	 * @param name the name of the sensor.
	 * @return <i>true</i> if a sensor was found with the given name, <i>false</i> otherwise.
	 */
	public boolean hasSensor(String name)
	{
		boolean sensorFound = false;
		int index = -1;
		
		// find sensor with the given name
		while (index < sensors.size() && !sensorFound) {
			// increase index.
			index++;
			// stop searching if the sensor matches the given name.
			sensorFound = sensors.get(index).getName() == name;
		}
		
		return sensorFound;
	}
	
	/**
	 * Returns <i>true</i> if the robot has the specified sensor.
	 * 
	 * @param sensor the sensor to find.
	 * @return <i>true</i> if a sensor was found, <i>false</i> otherwise.
	 */
	public boolean hasSensor(AbstractSensor sensor) 
	{
		return sensors.contains(sensor);
	}
	
	/**
	 * Set the platform that represents the robot within the environment.
	 * 
	 * @param platform the platform that represents the robot.
	 */
	public void setPlatform(Platform platform) {
		if (platform == null) {
			throw new IllegalArgumentException(String.format("platform should be instance of %s, and not null", Platform.class.getName()));
		}
		
		// add robot as device listener.
		platform.addDeviceListener(this);
		if (this.platform != null) {
			// remove robot from previous platform.
			platform.removeDeviceListener(this);
		}
		
		// set new platform.
		this.platform = platform;
	}
	
	/**
	 * Returns the platform that represents the robot within the environment.
	 * 
	 * @return the platform
	 */
	public Platform getPlatform() {
		return platform;
	}
	
	/**
	 * Adds a listener to the list that's notified each time a change to the 
	 * underlying data of the <code>OccupancyMap</code> occurs.
	 * 
	 * @param listener the <code>OccupancyMapListener</code> to be added.
	 * @return <i>true</i> if the listener was added, <i>false</i> otherwise.
	 */
	public boolean addOccupancyMapListener(OccupancyMapListener listener)
	{
		return mapListeners.add(listener);
	}
	
	/**
	 * Removes a listener from the list that's notified each time a change to the 
	 * underlying data of the <code>OccupancyMap</code> occurs.
	 * 
	 * @param listener the <code>OccupancyMapListener</code> to be removed.
	 * @return <i>true</i> if the listener was removed, <i>false</i> otherwise.
	 */
	public boolean removeOccupancyMapListener(OccupancyMapListener listener)
	{
		return mapListeners.remove(listener);
	}
	
	/**
	 * Returns <i>true</i> if the robot holds the given listener, <i>false</i> otherwise.
	 * 
	 * @param listener the <code>OccupancyMapListener</code> to lookup.
	 * @return <i>true</i> if the robot holds the given listener, <i>false</i> otherwise.
	 */
	public boolean hasOccupancyMapListener(OccupancyMapListener listener)
	{
		return mapListeners.contains(listener);
	}
	
	/**
	 * Returns an array of all the data listeners registered
	 * on this <code>GridModel</code>.
	 *
	 * @return all of the <code>OccupancyMapListener</code>s registered on
	 * 		   the robot, or an empty list if no listeners are currently registered.
	 */         
	public List<OccupancyMapListener> getOccupancyMapListeners()
	{
		return mapListeners;
	}
	
	/**
	 * Notifies all registered <code>OccupancyMapListener</code>s that the underlying
	 * data of the <code>OccupancyMap</code> has changed.
	 * 
	 * @param event the event that has taken place.
	 */
	public void fireOccupancyMapChanged(OccupancyMapEvent event)
	{
		for(OccupancyMapListener listener : mapListeners) {
			listener.onChange(event);
		}
	}
	
	@Override
	public synchronized void onDeviceReady(DeviceEvent e) 
	{
		// nothing to do anymore, let the algorithm run.
		if (tasks.isEmpty() && algorithm.isRunning()) {
			fireDeviceReady(new DeviceEvent(this));
		}
		
		// check if we have any remaining commands and the robot is ready.
		if (!isRunning() && !tasks.isEmpty()) {
			// remove the first command from the list and execute it.
			executeCommand(tasks.remove(0));
		}
	}

	@Override
	public synchronized void onSensorReady(SensorEvent event) {
		// sensor that performed the scan.
		AbstractSensor sensor = event.getSensor();
		// current location of the robot.
		Position position = getPosition();
		
		// array containing measurements.
		double measures[] = new double[360];
		for (int i = 0; i < 360; i++) {
			measures[i] = 100.0;
		}

		// populate array with measurements from the scanner.
		for (Measure measure : event.getResults()) {
			int direction = (int) Math.round(Math.toDegrees(measure.direction));
			// prevents an IndexOutOfBoundsException exception.
			if (direction == 360) {
				direction = 0;
			}
			
			// update measurement
			measures[direction] = measure.distance;
		}

		// get map used by the robot.
		OccupancyMap map = getMap();
		if (map != null) {
			// create new sensor measures.
			SensorMeasures sm = new SensorMeasures(position, sensor, measures);
			// update the map with the measured data.
			map.update(sm);
			// notify listeners that the underlying data of the map has changed.
			fireOccupancyMapChanged(new OccupancyMapEvent(map));
		}		

		// decrease the number of remaining sensors.
		countDown.countDown();
		// process this when all sensors are finished.
		if (countDown.getCount() == 0) {
			// nothing to do anymore, let the algorithm run.
			if (tasks.isEmpty() && algorithm.isRunning()) {
				fireDeviceReady(new DeviceEvent(this));
			}
			
			// execute one of the remaining commands.
			if (!isRunning() && !tasks.isEmpty()) {
				executeCommand(tasks.remove(0));
			}
			
			// reset the count down.
			countDown.reset();
		}
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