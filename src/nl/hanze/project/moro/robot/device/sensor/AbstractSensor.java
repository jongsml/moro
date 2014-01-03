package nl.hanze.project.moro.robot.device.sensor;

import java.util.ArrayList;
import java.util.List;

import nl.hanze.project.moro.robot.Robot;
import nl.hanze.project.moro.robot.device.AbstractDevice;
import nl.hanze.project.moro.robot.device.Environment;
import nl.hanze.project.moro.robot.device.Position;
import nl.hanze.project.moro.robot.event.SensorEvent;
import nl.hanze.project.moro.robot.event.SensorListener;

/**
 * The <code>Sensor</code> interface can be implemented by concrete classes 
 * which can be used by the <code>Robot</code> class to can scan the environment
 * for possible obstacles.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class AbstractSensor extends AbstractDevice implements Sensor 
{
	/**
	 * Listeners that will be informed when the sensor is finished scanning.
	 */
	protected List<SensorListener> listeners = new ArrayList<SensorListener>();
	
	/**
	 * Creates a new sensor that can scan the environment.
	 * 
	 * @param name the name of the sensor.
	 * @param robot the robot to which the sensor is associated.
	 * @param position the position of the sensor.
	 * @param environment the environment the sensor scans.
	 */
	public AbstractSensor(String name, Robot robot, Position position, Environment environment) 
	{
		super(name, robot, position, environment);
	}
	
	@Override
    public void addSensorListener(SensorListener l)
    {
    	listeners.add(l);
    }
    
	@Override
    public void removeSensorListener(SensorListener l)
    {
    	listeners.remove(l);
    }
    
	@Override
    public boolean hasSensorListener(SensorListener l)
    {
    	return listeners.contains(l);
    }
    
	@Override
	public void fireScanCompleted(SensorEvent event)
	{		
		for(SensorListener listener : listeners) {
			listener.onSensorReady(event);
		}
	}
}
