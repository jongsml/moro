package nl.hanze.project.moro.robot.event;

import nl.hanze.project.moro.robot.event.DeviceEvent;

/**
 * The <code>DeviceListener</code> defines the interface for an object that 
 * listens to changes in a <code>Device</code>.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public interface DeviceListener {
	/**
	 * Tells all listeners that a device is ready to perform a new command.
	 * 
	 * @param event the event describing the device and it's current state.
	 */
	public void onDeviceReady(DeviceEvent event);
}
