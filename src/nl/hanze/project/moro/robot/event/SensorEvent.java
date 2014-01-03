package nl.hanze.project.moro.robot.event;

import java.util.List;

import nl.hanze.project.moro.geom.Measure;
import nl.hanze.project.moro.robot.device.sensor.AbstractSensor;

/**
 * A <code>SensorEvent</code> gets delivered whenever a <code>Sensor</code> is finished scanning
 * the environment. The <code>SensorEvent</code> contains the source (sensor) that has fired 
 * the event and a list of <code>Measure</code> object which contain the coordinates of obstacles
 * found by the sensor.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public class SensorEvent {
	/**
	 * A list containing results while scanning the environment.
	 */
	protected List<Measure> measurements;
	
	/**
	 * Specifies the sensor on which the event initially occurred.
	 */
	protected AbstractSensor sensor;
	
	/**
	 * Creates a new <code>SensorEvent</code> using a copy constructor.
	 * 
	 * @param event the event that will copied.
	 */
	public SensorEvent(SensorEvent event)
	{
		this(event.getSensor(), event.getResults());
	} 	
	
	/**
	 * Creates a <code>SensorEvent</code> containing the sensor that initiated the event
	 * and it's results while scanning the environment.
	 * 
	 * @param sensor the sensor on which the event initially occurred.
	 */
	public SensorEvent(AbstractSensor sensor)
	{
		this(sensor, sensor.getResults());
	}
	
	/**
	 * Creates a <code>SensorEvent</code> containing the sensor that initiated the event
	 * and it's results while scanning the environment.
	 * 
	 * @param sensor the sensor on which the event initially occurred.
	 * @param measurements a list containing results of the scan.
	 */
	public SensorEvent(AbstractSensor sensor, List<Measure> measurements)
	{
		this.sensor = sensor;
		this.measurements = measurements;
	}
	
	/**
	 * Returns the sensor on which the event initially occurred.
	 * 
	 * @return the sensor on which the event initially occurred.
	 */
	public AbstractSensor getSensor()
	{
		return sensor;
	}
	
	/**
	 * Returns a list containing the results while scanning the environment.
	 * 
	 * @return a list containing the results of the scan.
	 */
	public List<Measure> getResults()
	{
		return measurements;
	}
}
