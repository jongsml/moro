package nl.hanze.project.moro.robot.device.sensor;

import java.util.List;

import nl.hanze.project.moro.geom.Measure;
import nl.hanze.project.moro.robot.event.SensorEvent;
import nl.hanze.project.moro.robot.event.SensorListener;

public interface Sensor 
{		
	/**
	 * Set listener that will receive events dispatched by the device.
	 * 
	 * @param l the listener that will receive events from the device
	 */
    public void addSensorListener(SensorListener l);
    
    /**
     * Remove the given listener from the device, in effect preventing the listener 
     * from receiving any further updates.
     * 
	 * @param l the listener that will no longer receive updates
     */
    public void removeSensorListener(SensorListener l);
    
    /**
     * Returns true if the given listener is currently registered with the device,
     * false otherwise.
     * 
     * @param l the listener to lookup.
     * @return returns <i>true</i> if the listener was found, <i>false> otherwise
     */
    public boolean hasSensorListener(SensorListener l);
    
	/**
	 * Notifies all registered <code>SensorListener</code>s that this sensor
	 * is done scanning the environment.
	 * 
	 * @param event the event that has taken place.
	 */
	public void fireScanCompleted(SensorEvent event);
	
	/**
	 * Set the range for the sensor that determines the size of the scannable area. 
	 * 
	 * @param size the size of the scannable area.
	 */
	public void setRange(int size);
	
	/**
	 * Returns the range for the sensor that determines the size of the scannable area.
	 * 
	 * @return the size of the scannable area.
	 */
	public int getRange();
	
	/**
	 * Scan the environment to find one or more obstacles.
	 */
	public void scan();
	
	/**
	 * Returns a list containing the results found while scanning the environment.
	 * 
	 * @return list containing results of the scan.
	 */
	public List<Measure> getResults();
}
