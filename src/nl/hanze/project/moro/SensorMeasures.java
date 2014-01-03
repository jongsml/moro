package nl.hanze.project.moro;
import nl.hanze.project.moro.robot.device.AbstractDevice;
import nl.hanze.project.moro.robot.device.Position;


public class SensorMeasures
{
    private final Position position;
    private final double[] measures;
    private final AbstractDevice source;

    /**
     * SensorMeasures constructor.
     * @param position The position from which the measurement was taken.
     * @param source The source device of the measurement.
     * @param measures The measured data.
     */
    public SensorMeasures(Position position, AbstractDevice source, double[] measures)
    {
        this.position = position;
        this.measures = measures;
        this.source = source;
    }

    /**
     * @return The position from which the measurement was taken.
     */
    public Position getPosition()
    {
        return position;
    }


    /**
     * @return The data from the measurement.
     */
    public double[] getMeasures()
    {
        return measures;
    }

    /**
     * @return The device which took the measurement.
     */
    public AbstractDevice getSource()
    {
        return source;
    }
}
