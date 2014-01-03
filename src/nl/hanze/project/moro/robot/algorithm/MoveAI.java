package nl.hanze.project.moro.robot.algorithm;


import java.awt.Point;
import java.util.LinkedList;

import nl.hanze.project.moro.controller.Controller;
import nl.hanze.project.moro.model.OccupancyMap;
import nl.hanze.project.moro.robot.device.Position;
import nl.hanze.project.moro.robot.device.PositionType;

public class MoveAI implements Pathfinding, Runnable
{
	private boolean running = false;
	private Thread thisThread;
	private boolean algorithmDone = false;
	private Controller controller;
	private static final int rogueNumSteps = 50;
	private static final int rogueNumRotateSteps = 90;
	private static final int WALL_NOT_FOUND = -1;

	public void runAlgorithm(Controller controller)
	{
		this.controller = controller;
		// Start the thread to execute the algorithm.
		thisThread = new Thread(this);
		thisThread.start();
	}

	
	public boolean isRunning()
	{
		return running;
	}
	
	private boolean executeControllerCommand(String command)
	{
		return controller.executeCommand(command);
	}

	/**
	 * Execute a scan.
	 * @return
	 */
	
	private boolean scan()
	{
		return executeControllerCommand("SCAN");
	}
	
	private boolean moveForward(int steps)
	{
		return executeControllerCommand("MOVEFW " + steps);
	}
	
	private boolean moveBackward(int steps)
	{
		return executeControllerCommand("MOVEBW " + steps);
	}
	
	private boolean rotateLeft(int d)
	{
		return executeControllerCommand("ROTATELEFT " + d);
	}
	
	private boolean rotateRight(int d)
	{
		return executeControllerCommand("ROTATERIGHT " + d);
	}
	
	/**
	 * Get the current position of the platform.
	 * @return
	 */
	private Position getPosition()
	{
		return controller.getCurrentPosition();
	}
	
	public void run()
	{
		do
		{
			try
			{
				// Start with finding a wall.
				Position startPoint = findWall();
				// Then follow it.
				LinkedList<Position> route = followTheWall(startPoint);
				// Scan all spots with adjacent unknown points.
				followNearestUnknown(route);
			}
			catch (RuntimeException e)
			{
				algorithmDone = true;
				throw e;
			}
		}
		while (!isAlgorithmDone());
		System.out.println("Algorithm is done");
	}
	
	/**
	 * Determine whether or not the algorithm is finished.
	 * @return
	 */
	private boolean isAlgorithmDone()
	{
		if(controller.getMap().isMapComplete()) { 
			algorithmDone = true;
		}
		return algorithmDone;
	}

	/**
	 * Find the wall. 
	 * @return
	 */
	private Position findWall()
	{
		Position startPoint = null;
		
		boolean wallFound = false;
		while (!wallFound)
		{
			// Move forward until we can't move any further.
			do
			{
				// Obviously we need to scan in order to determine
				// what's surrounding the robot.
				scan();
			}
			while (moveForward(rogueNumSteps));
			wallFound = true;

			// Move forward until we reach the wall. 
			while (moveForward(1));

			// Rotate left, otherwise rotate right.
			if (rotateLeft(rogueNumRotateSteps) || rotateRight(rogueNumRotateSteps))
				// This is the starting point.
				startPoint = getPosition();
			else
				System.out.println("Unable to turn left or right. Can't handle this!");
		}
		
		return startPoint;
	}
	
	/**
	 * Follow the wall.
	 * (needs additional comment)
	 * @param startPoint The starting point of the followTheWall algorithm.
	 * @return The linked list of the route.
	 */
	private LinkedList<Position> followTheWall(Position startPoint)
	{
		LinkedList<Position> route = new LinkedList<Position>();
		Position currentPoint = startPoint;
		boolean nearStartPoint = false;
		int totalMoves = 0;
		while (!nearStartPoint && !controller.getMap().isMapComplete())
		{
			// GO RIGHT if possible
			if (wallIsNear(currentPoint, 90, 50) < 0)  
			{
				// There is no wall on the right side in less than 50 positions.
				// Rotate right.
				System.err.println("No wall on the right side of the platform (currently heading " + 
						Math.toDegrees(currentPoint.getT()) + ")");

				if (!rotateRight(90)) {
					throw new RuntimeException("Unable to turn right, while expected to do so");
				}
				if (!moveForward(wallIsNear(currentPoint, 0, 50)-35)) {
					throw new RuntimeException("Unable to move forward, while expected to do so");
				}
				currentPoint = getPosition();
				route.add(currentPoint);
			}
			
			// GO FORWARD max steps
			else if (wallIsNear(currentPoint, 0, 40) < 0)
			{
				// There is no wall in front of us in less than 50 positions.
				// Move forward and scan.
				System.err.println("No wall on the front side of the platform (currently heading " + 
						Math.toDegrees(currentPoint.getT()) + ")");
				int numSteps = 0;
				while (moveForward(1))
				{
					int tmp = wallIsNear(currentPoint, 90, 50);
					System.out.println("Wall distance = " + tmp);
					if (tmp == WALL_NOT_FOUND /*|| (tmp > 10 && tmp < 50)*/)
						break;
					if (++numSteps % 25 == 0)
						scan();
					currentPoint = getPosition();
					totalMoves++;
				}
				scan();
				
				// Check if we're nearing the start point.
				currentPoint = getPosition();
				nearStartPoint = startPoint.closeTo(currentPoint, 25, 25);
				route.add(currentPoint);
			}
			
			// GO FORWARD until closest to wall as possible
			else if ((wallIsNear(currentPoint, 0, 40)-35) > 0)
			{
				// There is a wall in front of us in less than 50 positions.
				// Move forward and scan.
				int numSteps = 0;
				while (moveForward(1))
				{
					int tmp = wallIsNear(currentPoint, 90, 50);
					System.out.println("Wall distance = " + tmp);
					if (tmp == WALL_NOT_FOUND /*|| (tmp > 10 && tmp < 50) */)
						break;
					if (++numSteps % 25 == 0)
						scan();
					currentPoint = getPosition();
					totalMoves++;
				}
				scan();
				
				// Check if we're nearing the start point.
				currentPoint = getPosition();
				nearStartPoint = startPoint.closeTo(currentPoint, 35, 35);
				route.add(currentPoint); // was getLast()
			}
			
			// GO LEFT if possible
			else if (wallIsNear(currentPoint, 270, 50) < 0)
			{
				// There is no wall on the left side of us in less than 50 positions.
				// Rotate left.
				System.err.println("No wall on the left side of the platform (currently heading " + 
						Math.toDegrees(currentPoint.getT()) + ")");
				if (!rotateLeft(90)) {
					throw new RuntimeException("Unable to turn right, while expected to do so");
				}
				if (!moveForward(wallIsNear(currentPoint, 0, 50)-35)) {
					throw new RuntimeException("Unable to move forward, while expected to do so");
				}
				currentPoint = getPosition();
				route.add(currentPoint);
			}
			
			else if (!rotateLeft(180))
			{
				if (!driveTo(currentPoint, route.getLast()))
					if (rotateLeft(180) || rotateRight(180))
					
					throw new RuntimeException("Unable to drive to last position");
			}
			
		//	System.out.println("" + currentPoint.getX() + " " + currentPoint.getY() + " " + getPosition().getX() + " " + getPosition().getY());
			// Always refresh 
			currentPoint = getPosition();
			// Check if we're close to the start point.
			nearStartPoint = startPoint.closeTo(currentPoint, 25, 25);
			// If so and the total number of moves is higher than 100, it is likely that we've been around the field.
			if (nearStartPoint && totalMoves > 100)
				algorithmDone = true;
		}
		return route;
	}
	
	/**
	 * Part 3 of the algorithm.
	 * @param route The followed route in followTheWall()
	 */
	/**
	 * Part 3 of the algorithm.
	 * @param route The followed route in followTheWall()
	 */
	private void followNearestUnknown(LinkedList<Position> route)
	{
		algorithmDone = true;
		while (!isAlgorithmDone())
		{
			int index = getNearestRoutePoint(route);
			int currentIndex= route.indexOf(getPosition());
			if ((route.size() / 2 > Math.abs(index - currentIndex) && index > currentIndex) || (route.size() / 2 < Math.abs(index - currentIndex) && index < currentIndex))	
			{
				//move forward
			}
			else
			{
				//move backward	
			}
			algorithmDone = true;
	
		}
	}
	private int getNearestRoutePoint(LinkedList<Position> route)
	{
		Position nearestRoutePoint = getPosition();
		int index= route.indexOf(nearestRoutePoint);
		for ( int a = 0; a<route.size(); a++)
		{
			double x = route.get(a).getX();
			double y = route.get(a).getY();
				
			Point nearestUnknown = controller.getMap().getNearestUnknownAdjacent(getPosition());
			if ((Math.sqrt((Math.pow((x -(nearestUnknown.getX())), 2)+(Math.pow((y-(nearestUnknown.getY())), 2)))))<(Math.sqrt((Math.pow((nearestRoutePoint.getX()-nearestUnknown.getX()), 2)+(Math.pow((nearestRoutePoint.getY()-nearestUnknown.getY()), 2)))))){
				nearestRoutePoint = route.get(a);
				index = a; 
			}

		}
		return index;		
	}
	
	
	
	/**
	 * Drive the platform from the given source to the given destination
	 * @param source
	 * @param destination
	 * @return True on success and false if something failed while driving to the destination.
	 */
	private boolean driveTo(Position source, Position destination)
	{
		double sourceX = source.getX();
		double sourceY = source.getY();
		double destX = destination.getX();
		double destY = destination.getY();
			
		if ( Math.abs(sourceY-destY)<2 ) return moveBackward((int) Math.abs(sourceX - destX) );
		if ( Math.abs(sourceX-destX)<2 ) return moveBackward((int) Math.abs(sourceY - destY) );
		return false; // last point not on same x or y axis !
	}
	
	/**
	 * Determine if a wall is near at position "point", with angle "degrees" and within
	 * "distance"
	 * @param point
	 * @param degrees
	 * @param distance
	 * @return True if so, false otherwise.
	 * @throws InterruptedException 
	 */
	private int wallIsNear(Position point, double degrees, int distance)
	{
		int gridSize1=0;
		int gridSize2=0;		
		if (degrees == 0.0) {
			gridSize1 = 22;//voorkant 1
			gridSize2 = 22;//voorkant 2
		}
		if (degrees == 270.0) {
			gridSize1 = 27;//zijkant lang
			gridSize2 = 26;//zijkant kort
		}
		if (degrees == 90.0) {
			gridSize1 = 27;//zijkant kort
			gridSize2 = 26;//zijkant lang
		}
		OccupancyMap map = controller.getMap();
		double newt = point.getT() + Math.toRadians(degrees);
		for (int z = 0; z <= distance+1; z++)
		{ 
			int x = (int)(point.getX() + z * Math.cos(newt));
			int y = (int)(point.getY() + z * Math.sin(newt));
			System.out.println("" + newt + " " + x + " " + y);
			PositionType type = map.getPositionType(x,y, true);
			
			if (Math.abs(x - (int)point.getX())<2) {
				int xi = x-gridSize2;
				while (type != PositionType.OBSTACLE && type != PositionType.OPAQUE && xi < x + gridSize1) {
					type = map.getPositionType(xi,y, true);
					xi++;
				}
				if (type == PositionType.OBSTACLE || type == PositionType.OPAQUE) return z;
			}
			
			if (Math.abs(y - (int)point.getY())<2) {
				int yi = y-gridSize2;

				while (type != PositionType.OBSTACLE && type != PositionType.OPAQUE && yi < y + gridSize1) {
					type = map.getPositionType(x,yi, true);
					yi++;
				}
				if (type == PositionType.OBSTACLE || type == PositionType.OPAQUE) return z;
			}
		}
		return WALL_NOT_FOUND;
	}


	@Override
	public void runAlgorithm() {
		// TODO Auto-generated method stub
		
	}

}
