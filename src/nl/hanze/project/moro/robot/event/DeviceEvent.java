package nl.hanze.project.moro.robot.event;

import nl.hanze.project.moro.robot.device.AbstractDevice;

/**
 * A <code>DeviceEvent</code> gets delivered whenever a <code>Device</code> is done executing
 * a specific command. The <code>DeviceEvent</code> contains the source (device) that has fired 
 * the event and a boolean value that determines if it's ready to receive a new command.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public class DeviceEvent {
	/**
	 * Specifies if the device that triggered the event is ready
	 * for to receive a new task.
	 */
	protected boolean isReady;
	
	/**
	 * Specifies the device on which the event initially occurred.
	 */
	protected AbstractDevice device;
	
	/**
	 * Creates a new <code>DeviceEvent</code> using a copy constructor.
	 * 
	 * @param event the event that will copied.
	 */
	public DeviceEvent(DeviceEvent event)
	{
		this(event.getDevice(), event.isReady());
	}
	
	/**
	 * Creates a <code>DeviceEvent</code> indicating that a specific <code>Device</code> is
	 * ready processing it's task.
	 * 
	 * @param device the device on which the event initially occurred.
	 */
	public DeviceEvent(AbstractDevice device)
	{
		this(device, !device.isRunning());
	}
	
	/**
	 * Creates a <code>DeviceEvent</code> indicating that a specific <code>Device</code> is
	 * ready processing it's task.
	 * 
	 * @param device the device on which the event initially occurred.
	 * @param isReady determines if the device is ready to process a new task.
	 */
	public DeviceEvent(AbstractDevice device, boolean isReady)
	{
		this.device = device;
		this.isReady = isReady;
	}
	
	/**
	 * Returns the device on which the event initially occurred.
	 * 
	 * @return the device on which the event initially occurred.
	 */
	public AbstractDevice getDevice()
	{
		return device;
	}
	
	/**
	 * Returns <i>true</i> if the device is ready to proces a new task.
	 * 
	 * @return <i>true</i> if the device is ready, <i>false</i> otherwise.
	 */
	public boolean isReady()
	{
		return isReady;
	}
}