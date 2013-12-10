package nl.hanze.project.moro.devices;


/**
 * Enum for the types of things which can hold a position in the occupancy map.
 * @author Leon Jongsma
 * @version 1.0
 */
public enum PositionType 
{
	UNKNOWN("UNKNOWN"),
	EMPTY("EMPTY"),
	OBSTACLE("OBSTACLE"),
	OPAQUE("OPAQUE");
	private String s;
	
	private PositionType(String s)
	{
		this.s = s;
	}
	
	@Override
	public String toString()
	{
		return s;
	}
}
