package nl.hanze.project.moro.pathfinding;

import nl.hanze.project.moro.controller.Controller;


public interface MovingAlgorithm
{
	public void runAlgorithm(Controller controller);
	public boolean isRunning();
}
