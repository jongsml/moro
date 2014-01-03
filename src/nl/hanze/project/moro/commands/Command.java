package nl.hanze.project.moro.commands;

/**
 * Interface that is implemented by all concrete <code>Command</code> objects.
 * 
 * The {@link #execute()} method is responsible for executing the underlying
 * code of the command.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public interface Command 
{
	/**
	 * By invoking this method the underlying code held by this command
	 * will be executed.
	 */
	public void execute();
}
