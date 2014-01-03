package nl.hanze.project.moro.model;
/*
 * (C) Copyright 2005 Davide Brugali, Marco Torchiano
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA
 */

// package moro;

/**
 * Title:        The MObile RObot Simulation Environment
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Universit di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.hanze.project.moro.SensorMeasures;
import nl.hanze.project.moro.robot.device.AbstractDevice;
import nl.hanze.project.moro.robot.device.Position;
import nl.hanze.project.moro.robot.device.PositionType;
import nl.hanze.project.moro.robot.device.sensor.Laser;

public class OccupancyMap 
{
	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private Map<Point, PositionType> map = new HashMap<Point, PositionType>();

	int cellDim = 10;
	int width = 510;
	int height = 460;
	
	int cellDimNew = 1;
	
	private PositionType grid[][] = new PositionType[width / cellDim][height / cellDim];
	private PositionType gridNew[][] = new PositionType[width / cellDimNew][height / cellDimNew];
	
	/*
	private final static char UNKNOWN = 'n';
	private final static char EMPTY = 'e';
	private final static char OBSTACLE = 'o';
*/
	public OccupancyMap() 
	{
		initGrids();
	}

	/**
	 * Method to check if there or still unknowns in the Grid.
	 * 
	 */
	public boolean isMapComplete() 
	{
		return getUnknownAdjacent() == null;
	}
	
	/**
	 * Get an x/y coordinate of a position with position type unknown, adjacent
	 * to an empty point, relatively nearest to the given point
	 * This is useful for knowing where to go next for the MRE
	 * @param currentPoint the given Point
	 * @return The unknown point adjacent to the empty point.
	 */
	public Point getNearestUnknownAdjacent(Position currentPoint)
	{
		Point nearestPoint = getUnknownAdjacent();
		// No point 
		if (nearestPoint == null)
			return null;
		
		// Checking the outside boundary is useless. Therefore, start off with x,y=1,1.
		for (int x = 1; x < grid.length-1; x++)
			for (int y = 1; y < grid[x].length - 1; y++)
			{
				// Controleren of een unknown ook een empty omzich heen heeft anders is die niet relevant.
				// Omdat die dan binnen of buiten een type muur ligt.(Hier bevinden zich namelijk geen empty) 
				if (isUnknownAdjacentToEmpty(x, y))
				// Comparing the distance of this unknown point to 
				if ((Math.sqrt((Math.pow((currentPoint.getX()-(x * cellDim)), 2)+(Math.pow((currentPoint.getY()-y * cellDim), 2)))))<(Math.sqrt((Math.pow((currentPoint.getX()-nearestPoint.getX()), 2)+(Math.pow((currentPoint.getY()-nearestPoint.getY()), 2))))))
					nearestPoint = new Point(x, y);
				
			}
		

			return nearestPoint;
	}
	
	/**
	 * Get the position type. The x and y coordinates may be negative when
	 * using relative coordinates.  
	 * @param x
	 * @param y
	 * @return MapPosition.UNKNOWN if nothing known. Otherwise a MapPosition enum value.
	 */
	public PositionType getPositionType(int x, int y)
	{
		PositionType position = map.get(new Point(x, y));
		if (position == null)
			return PositionType.UNKNOWN;
		return position;
	}
	
	private PositionType getPositionType(int x, int y, int cellDim, PositionType[][] grid)
	{
		// Calculate the grid position from the x+y.
		int gridX = (int) Math.ceil(x / cellDim);
		int gridY = (int) Math.ceil(y / cellDim);
		
		// Prevent an array out of bounds exception.
		if (gridX < 0 || gridX > grid.length || gridY < 0 || gridY > grid[gridX].length)
		{
			System.err.println("Attempted to get position outside field");
			return PositionType.UNKNOWN;
		}
		
		return grid[gridX][gridY];
	}
	
	public PositionType getPositionType(int x, int y, boolean fromGrid)
	{
		if (!fromGrid)
			return getPositionType(x, y);
		return getPositionType(x, y, cellDimNew, gridNew);
	}
	
	/**
	 * Get an x/y coordinate of a position with position type unknown, adjacent
	 * to an empty point. This is useful for testing whether or not there is
	 * anything left in the field to scan.
	 * @return The unknown point adjacent to the empty point.
	 */
	public Point getUnknownAdjacent()
	{
		// Checking the outside boundary is useless. Therefore, start off with x,y=1,1.
		for (int x = 1; x < grid.length-1; x++)
			for (int y = 1; y < grid[x].length - 1; y++)
			{
				// Controleren of een unknown ook een empty omzich heen heeft anders is die niet relevant.
				// Omdat die dan binnen of buiten een type muur ligt.(Hier bevinden zich namelijk geen empty) 
					if (isUnknownAdjacentToEmpty(x, y))
					
						// Multiply with the cell dimension to get the real x/y
						 return new Point(x * cellDim, y * cellDim);
			}
		
		// No point 
		return null;
	}
	
	/**
	 * Checking if an unknown is next to an empty PositionType
	 * @param x the X of the point in the grid	
	 * @param y the Y of the point in the grid
	 * @return
	 */
	private boolean isUnknownAdjacentToEmpty(int x, int y)
	{
		return grid[x][y] == PositionType.UNKNOWN && 
			(	
				// Check if the unknown field has an empty field adjacent to it.
				grid[x-1][y] == PositionType.EMPTY ||  
				grid[x][y-1] == PositionType.EMPTY ||
				grid[x-1][y-1] == PositionType.EMPTY ||
				grid[x+1][y] == PositionType.EMPTY ||
				grid[x][y+1] == PositionType.EMPTY ||
				grid[x+1][y+1] == PositionType.EMPTY ||
				grid[x-1][y+1] == PositionType.EMPTY ||
				grid[x+1][y-1] == PositionType.EMPTY
			);
	}
	
	/**
	 * Get the new grid
	 * @return the new grid
	 */
	 public PositionType[][] getNewGrid() 
	 { 
		 return gridNew; 
	 }
	
	/**
	 * Clear the occupancy map.
	 */
	public void clear()
	{
		initGrids();
		obstacles.clear();
		map.clear();
	}
	
	private void initGrids()
	{
		// Set all grid items' initial status to unknown.
		initGrid(grid, height, width, cellDim);
		// New grid
		initGrid(gridNew, height, width, cellDimNew);
	}
	
	private void initGrid(PositionType[][] grid, int height, int width, int cellDim)
	{
		for (int i = 0; i < width / cellDim; i++)
			for (int j = 0; j < height / cellDim; j++)
				grid[i][j] = PositionType.UNKNOWN;
	}
	
	
	/**
	 * Get the array of obstacles in the map.
	 * @return
	 */
	public Obstacle[] getObstacles()
	{
		Obstacle[] o = new Obstacle[obstacles.size()];
		return obstacles.toArray(o);
	}

	/**
	 * Get the current grid.
	 * @return
	 */
	public PositionType[][] getGrid()
	{
		return grid;
	}
	
	/**
	 * Update the contents of the occupancy map. This method updates the occupancy map
	 * with MapPosition entries.
	 * @param sm The data measured by the sensor.
	 */
	public void update(SensorMeasures sm)
	{
		Position position = sm.getPosition();
		double[] measures = sm.getMeasures();
		// The offset 
		double rx = Math.round(position.getX() + 20.0 * Math.cos(position.getT()));
		double ry = Math.round(position.getY() + 20.0 * Math.sin(position.getT()));
		
		int dir = (int) Math.round(Math.toDegrees(position.getT()));

		// Shamelessly stolen from the original OccupancyMapView code.
		for (int i = 0; i < 360; i++)
		{
			int d = i - dir;
			// This is necessary in order to convert the measured positions to
			// real x/y coordinates.
			while (d < 0)
				d += 360;
			while (d >= 360)
				d -= 360;
			// The real coordinates in the map.
			// XXX: This may lead to rounding errors.
			double fx = Math.round(rx + measures[d] * Math.cos(Math.toRadians(i)));
			double fy = Math.round(ry + measures[d] * Math.sin(Math.toRadians(i)));
			
			PositionType posType;
			// TODO: Implement opaque as well. Currently impossible, because the source device is unknown.
			// Maybe this should not be done here anyway.
			// If the value of the measurement is less than 100, an obstacle of some type is found.
			AbstractDevice sensor = sm.getSource();
			if (measures[d] < 100.0)
			{
				if (sensor instanceof Laser)
					posType = PositionType.OBSTACLE;
				else
					posType = PositionType.OPAQUE;
			}
			else
				// Otherwise, the position is empty.
				posType = PositionType.EMPTY;
			// Update the grid.
			updateGrid(rx, ry, fx, fy, posType);
			// XXX: To be obsoleted.
			setPositionType((int) fx, (int) fy, posType);
		}
	}
	
	/**
	 * Set the status of a position. The x and y coordinates may be negative when
	 * using relative coordinates. 
	 * @param x
	 * @param y
	 * @param type The new type
	 */
	public void setPositionType(int x, int y, PositionType type)
	{
		// When the given status is unknown, remove it from the map to save memory.
		Point p = new Point(x, y);
		if (type == PositionType.UNKNOWN)
			map.remove(p);
		else
		{
			// Otherwise, just set it.
			map.put(p, type);
//			System.out.println("Set " + x + "," + y + " to " + type);
		}
	}
	
	
	/**
	 * 
	 * @param rx
	 * @param ry
	 * @param x
	 * @param y
	 * @param type
	 */
	private void updateGrid(double rx, double ry, double x, double y, PositionType type)
	{
		// 
		updateGrid(rx, ry, x, y, type, cellDim, grid);
		updateGrid(rx, ry, x, y, type, cellDimNew, gridNew);
	}
	
	private void updateGrid(double rx, double ry, double x, double y, PositionType type, int cellDim, PositionType grid[][])
	{
		// ???
		int rxi = (int) Math.ceil(rx / cellDim);
		int ryj = (int) Math.ceil(ry / cellDim);
		int xi = (int) Math.ceil(x / cellDim);
		int yj = (int) Math.ceil(y / cellDim);

		// Prevent an array out of bounds exception.
		if (xi < 0 || yj < 0 || xi >= width / cellDim || yj >= height / cellDim)
			return;

		// Store the position type.
		// This is quite a stupid workaround for some measurement bug, but for now it works.
		// Without this check, measured/scanned positions may get overwritten.
		if (grid[xi][yj] != PositionType.UNKNOWN && grid[xi][yj] != PositionType.EMPTY && type != PositionType.OBSTACLE)
		{
			System.err.println("Not overwriting grid[" + xi + "][" + yj + "] (" + grid[xi][yj] + ") with " + type);
			return;
		}
		grid[xi][yj] = type;
//		if (type != PositionType.UNKNOWN)
//			System.out.println("Set grid " + xi + "/" + yj + " to " + type);

		// ???
		int xmin = Math.min(rxi, xi);
		int xmax = Math.max(rxi, xi);
		int ymin = Math.min(ryj, yj);
		int ymax = Math.max(ryj, yj);

		if (rx == x)
			// XXX: Find out how this works.
			for (int j = ymin; j <= ymax; j++)
			{
				// TODO: This needs to be fixed when OPAQUE is implemented.
				if ((grid[xmin][j] != PositionType.OBSTACLE) && (grid[xmin][j] != PositionType.OPAQUE))
					grid[xmin][j] = PositionType.EMPTY;			
			}
		else
		{
			// XXX: Find out how this works.
			double m = (y - ry) / (x - rx);
			double q = y - m * x;
			for (int i = xmin; i <= xmax; i++)
			{
				int h = (int) Math.ceil((m * (i * cellDim) + q) / cellDim);
				if (h >= ymin && h <= ymax)
				{
					if ((grid[i][h] != PositionType.OBSTACLE) && (grid[i][h] != PositionType.OPAQUE))
						grid[i][h] = PositionType.EMPTY;				
				}
			}
		}
	}
	
	
	
	
	/**
	 * Add an obstacle to the map.
	 * @param obstacle The obstacle to add to the map.
	 */
	public void addObstacle(Obstacle obstacle)
	{
		obstacles.add(obstacle);
	}
}
