package nl.hanze.project.moro.robot.algorithm;

import nl.hanze.project.moro.commands.MoveForwardCommand;
import nl.hanze.project.moro.commands.RotateRightCommand;
import nl.hanze.project.moro.commands.ScanCommand;
import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.device.Position;
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
			boolean isRunning = robot.getMap().isMapComplete();
			
			// let the algorithm run until the environment has been scanned.
			while (!isRunning) {
				Position pos = new Position();
				robot.readPosition(pos);
				
				// wait until robot is ready again.
				synchronized(lock) {
					lock.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		robot.executeCommand(new ScanCommand(robot));
		robot.executeCommand(new MoveForwardCommand(robot, 90));
		robot.executeCommand(new ScanCommand(robot));
		robot.executeCommand(new RotateRightCommand(robot));
		robot.executeCommand(new ScanCommand(robot));
		robot.executeCommand(new MoveForwardCommand(robot));
		robot.executeCommand(new ScanCommand(robot));
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