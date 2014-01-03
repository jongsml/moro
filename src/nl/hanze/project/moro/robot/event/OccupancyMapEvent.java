package nl.hanze.project.moro.robot.event;

import nl.hanze.project.moro.model.OccupancyMap;

/**
 * A <code>OccupancyMapEvent</code> gets delivered whenever the underlying data of the
 * <code>OccupancyMap</code> has changed. The <code>OccupancyMapEvent</code> contains the source
 * that has fired the event and a integer value indicating the type of event.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public class OccupancyMapEvent 
{	
    /** 
     * Identifies a change to existing data. 
     */
    public static final int UPDATE = 0;
	
    /**
     * Identifies clearing all existing data.
     */
    public static final int CLEAR = 1;	
	
	/**
	 * Specifies the map on which the event initially occurred.
	 */
    protected OccupancyMap map;
	
    /**
     * Specifies the type of event that has taken place.
     */
    protected int type = 0;	
	
	/**
	 * Creates a new <code>DeviceEvent</code> using a copy constructor.
	 * 
	 * @param event the event that will copied.
	 */
	public OccupancyMapEvent(OccupancyMapEvent event)
	{
		this(event.getOccupancyMap(), event.getType());
	}    
    
	/**
	 * Create a new OccupancyMapEvent indicating that underlying data of
	 * the <code>OccupancyMap</code> has changed.
	 * 
	 * @param map the object on which the event initially occurred.
	 */
	public OccupancyMapEvent(OccupancyMap map) 
	{
		this(map, UPDATE);
	}
	
	/**
	 * Create a new OccupancyMapEvent indicating that underlying data of
	 * the <code>OccupancyMap</code> has changed.
	 * 
	 * @param map the object on which the event initially occurred.
	 * @param type the type of event that has taken place.
	 */
	public OccupancyMapEvent(OccupancyMap map, int type) 
	{
		this.map = map;
		this.type = type;
	}
	
	/**
	 * Returns the map on which the event initially occurred.
	 * 
	 * @return the map on which the event initially occurred.
	 */
	public OccupancyMap getOccupancyMap()
	{
		return map;
	}
	
	/**
	 * Returns the type of event that has taken place.
	 * 
	 * @return an int value representing the event that has taken place.
	 */
	public int getType() 
	{
		return type;
	}
}
