package nl.hanze.project.moro.geom;

public class Measure {
	public double distance = 0;
	public double direction = 0.0;

	public Measure(double dist, double dir)
	{
		distance = dist;
		direction = dir;
		while (direction >= 2.0 * Math.PI)
			direction -= 2.0 * Math.PI;
		while (direction < 0.0)
			direction += 2.0 * Math.PI;
	}

	public void set(double dist, double dir)
	{
		distance = dist;
		direction = dir;
		while (direction >= 2.0 * Math.PI)
			direction -= 2.0 * Math.PI;
		while (direction < 0.0)
			direction += 2.0 * Math.PI;
	}
}
