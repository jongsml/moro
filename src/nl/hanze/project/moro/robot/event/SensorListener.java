package nl.hanze.project.moro.robot.event;

/**
 * The <code>SonarListener</code> defines the interface for an object that 
 * listens to changes of a <code>Sensor</code>.
 * 
 * @author Chris Harris
 * @version 0.0.1
 * @since 0.0.1
 */
public interface SensorListener {
	/**
	 * Tells all listeners that the sensor has finished scanning the environment.
	 * 
	 * @param event the event that describes what the sensor has found during it's scan.
	 */
	public void onSensorReady(SensorEvent event);
}
