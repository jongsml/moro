package nl.hanze.project.moro.commands;

import nl.hanze.project.moro.robot.Robot;

/**
 * The <code>ScanCommand</code> allows the receiver
 * to scan the environment and to detect on or more 
 * obstacles.
 * 
 * @author Chris Harris
 */
public class ScanCommand implements Command 
{
	/**
	 * The robot which has sensors that will scan the environment.
	 */
	private Robot robot;
	
	/**
	 * Create a new <code>Command</code> that will scan the environment 
	 * using the <code>Robot</code>s sensors.
	 * 
	 * @param robot the robot containing sensors.
	 */
	public ScanCommand(Robot robot)
	{
		if (robot == null) {
			throw new IllegalArgumentException(String.format("robot should be an instance of %s, and not null", Robot.class.getName()));
		}
		
		this.robot = robot;
	}	
	
	@Override
	public void execute() {
		robot.scan();
	}
}
