package nl.hanze.project.moro.commands;

/**
 * The <code>NullCommand</code> implements the Null Object pattern.
 * 
 * The <i>null</i> literal can be used as a value type for any reference type. <i>null</i> may be 
 * assigned to any variable, except variables of primitive types. There's little you can do with a 
 * <i>null</i> value beyond testing for it's presence. So instead of using <i>null</i> values 
 * which require testing we can use an object that implements the expected interface, but we leave 
 * it's method body empty, also known as the Null Object pattern.
 * 
 * @author Chris Harris
 * @verison 0.0.1
 * @since 0.0.1
 */
public class NoCommand implements Command {
	@Override
	public void execute() 
	{}
}
