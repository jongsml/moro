package nl.hanze.project.moro.commands;

import nl.hanze.project.moro.robot.Robot;

/**
 * The <code>MoveForwardCommand</code> allows the receiver
 * to move backwards either by a default distance or the given 
 * distance.
 * 
 * @author Chris Harris
 */
public class MoveBackwardsCommand implements Command {
	/**
	 * The distance that this command will send to the invoker.
	 */
	private double distance = 60;
	
	/**
	 * The robot that will be moved backwards by the given distance.
	 */
	private Robot robot;

	/**
	 * Create a new <code>Command</code> that is responsible for moving 
	 * the given <code>Robot</code> backwards.
	 * 
	 * @param robot the robot that will perform the command.
	 */
	public MoveBackwardsCommand(Robot robot)
	{
		if (robot == null) {
			throw new IllegalArgumentException(String.format("robot should be an instance of %s, and not null", Robot.class.getName()));
		}
		
		this.robot = robot;
	}
	
	/**
	 * Create a new <code>Command</code> that is responsible for moving 
	 * the given <code>Robot</code> backwards.
	 * 
	 * @param robot the robot that will perform the command.
	 * @param distance the distance the robot will move.
	 */
	public MoveBackwardsCommand(Robot robot, double distance)
	{
		this(robot);
		setDistance(distance);
	}
	
	/**
	 * Set the distance the robot will move backwards.
	 * 
	 * @param distance the distance the robot will move.
	 */
	public void setDistance(double distance)
	{
		if (distance <= 0) {
			throw new IllegalArgumentException("the given distance should be larger than 0.");
		}
		
		this.distance = distance;
	}
	
	/**
	 * Returns the distance the robot will move backwards.
	 * 
	 * @return the distance the robot will move.
	 */
	public double getDistance()
	{
		return distance;
	}

	@Override
	public void execute() {
		robot.moveBackwards(distance);
	}
}
