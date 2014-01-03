package nl.hanze.project.moro.commands;

import nl.hanze.project.moro.robot.Robot;

/**
 * The <code>MoveBackwardsCommand</code> allows the receiver
 * to rotate to the left either by a default angle or the given angle
 * in degrees.
 * 
 * @author Chris Harris
 */
public class RotateLeftCommand implements Command {
	/**
	 * The angle that will be added to the robot's currents rotation.
	 */
	private double angle = 90;
	
	/**
	 * The robot that will be rotated to the left.
	 */
	private Robot robot;
	
	/**
	 * Create a new <code>Command</code> that is responsible for rotating 
	 * the given <code>Robot</code> to the left.
	 * 
	 * @param robot the robot that will perform the command.
	 */
	public RotateLeftCommand(Robot robot)
	{
		if (robot == null) {
			throw new IllegalArgumentException(String.format("robot should be an instance of %s, and not null", Robot.class.getName()));
		}
		
		this.robot = robot;
	}
	
	/**
	 * Create a new <code>Command</code> that is responsible for rotating 
	 * the given <code>Robot</code> to the left.
	 * 
	 * @param robot the robot that will perform the command.
	 */
	public RotateLeftCommand(Robot robot, double angle)
	{
		this(robot);
		setAngle(angle);
	}
	
	/**
	 * Set the angle that is added to the robot's current rotation.
	 * 
	 * @param angle the angle to be added to the robot's rotation.
	 */
	public void setAngle(double angle) 
	{
		if (angle < 0 || angle > 360) {
			throw new IllegalArgumentException(String.format("the angle should be between 0 and 360 inclusive, received %d", angle));
		}
		
		this.angle = angle;
	}
	
	/**
	 * Returns the angle that is added to the robot's current rotation.
	 * 
	 * @return the angle to be added to the robot's rotation.
	 */
	public double getAngle()
	{
		return angle;
	}

	@Override
	public void execute() {
		robot.rotateLeft(angle);
	}
}