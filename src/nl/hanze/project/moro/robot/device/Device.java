package nl.hanze.project.moro.robot.device;

import nl.hanze.project.moro.robot.event.DeviceEvent;
import nl.hanze.project.moro.robot.event.DeviceListener;

public interface Device 
{	
	/**
	 * Returns a name that identifies the device.
	 * 
	 * @return the name of the device.
	 */
	public String getName();

	/**
	 * Set the name that identifies the device.
	 * 
	 * @param name the name of the device.
	 */
	public void setName(String name);
	
	/**
	 * Returns <i>true</i> if the <code>Device</code> is currently busy performing
	 * an operation.
	 * 
	 * @return <i>true</i> if the device is busy, <i>false</i> otherwise.
	 */
	public boolean isRunning();
	
	/**
	 * Set listener that will receive events dispatched by the device.
	 * 
	 * @param listener the listener that will receive events from the device
	 */
    public void addDeviceListener(DeviceListener listener);
    
    /**
     * Remove the given listener from the device, in effect preventing the listener 
     * from receiving any further updates.
     * 
	 * @param listener the listener that will no longer receive updates
     */
    public void removeDeviceListener(DeviceListener listener);
    
    /**
     * Returns true if the given listener is currently registered with the device,
     * false otherwise.
     * 
     * @param listener the listener to lookup.
     * @return returns <i>true</i> if the listener was found, <i>false> otherwise
     */
    public boolean hasDeviceListener(DeviceListener listener);
	
	/**
	 * Notifies all registered <code>DeviceListener</code>s that this device
	 * is ready processing all it's tasks.
	 * 
	 * @param event the event that has taken place.
	 */
	public void fireDeviceReady(DeviceEvent event);
}
