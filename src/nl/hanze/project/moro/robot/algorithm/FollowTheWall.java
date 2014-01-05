package nl.hanze.project.moro.robot.algorithm;

import java.awt.Dimension;

import nl.hanze.project.moro.commands.MoveForwardCommand;
import nl.hanze.project.moro.commands.RotateLeftCommand;
import nl.hanze.project.moro.commands.RotateRightCommand;
import nl.hanze.project.moro.commands.ScanCommand;
import nl.hanze.project.moro.model.OccupancyMap;
import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.Robot.Direction;
import nl.hanze.project.moro.robot.device.PositionType;
import nl.hanze.project.moro.robot.event.DeviceEvent;
import nl.hanze.project.moro.robot.event.DeviceListener;

public class FollowTheWall implements Pathfinding, DeviceListener, Runnable 
{
	/**
	 * A flag indicating if the algorithm is currently running.
	 */
	private boolean running = false;
	
	/**
	 * A monitor lock which allows the algorithm to go into a waiting state. 
	 */
	protected Object lock = new Object();
	
	/**
	 * Separate thread in which the algorithm will run.
	 */
	private Thread thread;
	
	/**
	 * The Robot which moves through the environment.
	 */
	private Robot robot;
	
	/**
	 * Creates a FollowTheWall algorithm that can be used to find a path
	 * within the environment.
	 * 
	 * @param robot
	 */
	public FollowTheWall(Robot robot)
	{
		// create thread in which the algorithm will run.
		thread = new Thread(this);
		// store a reference to the robot.
		setRobot(robot);
	}
	
	/**
	 * Stores the robot which uses this algorithm to find it's path
	 * through the environment.
	 * 
	 * @param robot the robot that moves through the environment.
	 */
	public void setRobot(Robot robot) 
	{
		if (robot == null) {
			throw new IllegalArgumentException(String.format("robot should be instance of %s, and not null", Robot.class.getName()));
		}
		
		// remove listener from the current robot.
		if(this.robot != null) {
			// prevent unnecessary updates.
			if (this.robot == robot) {
				return;
			}
			
			// remove itself (as listener) from the to-be-replaced robot. 
			robot.removeDeviceListener(this);
		}
		
		// add itself as listener to the (new) robot.
		robot.addDeviceListener(this);
		// replace robot.
		this.robot = robot;
	}
	
	@Override
	public void runAlgorithm() 
	{
		// update flag.
		running = true;
		// only allow one algorithm to run at a given time.
		if (!thread.isAlive()) {
			thread.start();
		}
	}

	@Override
	public boolean isRunning() 
	{
		return running;
	}
	
	/**
	 * Starts the actual algorithm that will explore the environment in which the robot is
	 * located.
	 */
	@Override
	public void run() 
	{
		try {
			// a flag that determines if the algorithm is still running.
			boolean isRunning = !robot.getMap().isMapComplete();
			
			// let the algorithm run until the environment has been scanned.
			while (isRunning) {				
				/*
				 * scan the environment when the robot reaches one or more unknown
				 * fields, otherwise we move the robot towards these unknown fields.
				 */
				if (shouldScan()) {
					robot.executeCommand(new ScanCommand(robot));
				} else {		
					System.out.println(Math.toDegrees(robot.getPosition().getT()));
					System.out.println(robot.getDirection());					
					
					// try to move the robot forward.
					if (!robot.willCollide(Robot.Action.MOVE_FORWARD, 1)) {
						robot.executeCommand(new MoveForwardCommand(robot, 1));
					} else if (!robot.willCollide(Robot.Action.ROTATE_RIGHT, 90)) {
						robot.executeCommand(new RotateRightCommand(robot));
					} else if (!robot.willCollide(Robot.Action.ROTATE_LEFT, 90)) {
						robot.executeCommand(new RotateLeftCommand(robot));
					}
				}
				// wait until robot is ready again.
				synchronized(lock) {
					lock.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the distance to the first wall in the given direction, if no
	 * wall is found the given distance will be returned instead.
	 * 
	 * @param direction the direction which we should look for.
	 * @param distance the distance to look ahead.
	 * @return the actual distance to the first wall.
	 */
	private int wallIsNear(Direction direction, int distance)
	{
		
		return 0;
	}
	
	/**
	 * Returns <i>true</i> if the field on which the robot resides or one
	 * of the adjacent field are unknown to the robot, <i>false</i> otherwise.
	 * 
	 * @return <i>true</i> if the robot is near unknown terrain.
	 */
	private boolean shouldScan()
	{
		// a flag indicating if the environment should be scanned.
		boolean shouldScan = false;
		// the map that can be used to determine where we are.
		OccupancyMap map = robot.getMap();
		// the actual size of a map cell.
		Dimension cellDim = map.getCellSize();
		
		// get adjacent fields on the horizontal axis.
		mainloop:
		for (int x = -1; x <= 1; x++) {
			// get adjacent fields on the vertical axis.
			for (int y = -1; y <= 1; y++) {
				// column and row index of an adjacent field.
				int colIndex = ((int) robot.getPosition().getX()) / cellDim.width + x;
				int rowIndex = ((int) robot.getPosition().getY()) / cellDim.width + y;
				
				// make sure this field exists and determine it's position type.
				if (map.fieldExists(colIndex, rowIndex) && 
						map.getFieldType(colIndex, rowIndex) == PositionType.UNKNOWN) {
					shouldScan = true;
					// break loop.
					break mainloop;
				}
			}
		}

		return shouldScan;
	}

	@Override
	public void onDeviceReady(DeviceEvent event) 
	{
		// wake up algorithm and send new command.
		synchronized(lock) {
			lock.notify();
		}
	}
}