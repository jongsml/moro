package nl.hanze.project.moro.robot.event;

/**
 * The <code>OccupancyMapListener</code> defines the interface for an object that 
 * listens to changes of a <code>OccupancyMap</code>.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public interface OccupancyMapListener {
	/**
	 * Tells all listeners that the underlying data of a <code>OccupancyMap</code> has changed.
	 * 
	 * @param event the event that describes what has changed to the map.
	 */
	public void onChange(OccupancyMapEvent event);
}
