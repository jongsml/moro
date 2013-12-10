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



import java.awt.Color;
import java.awt.Polygon;

/**
 * Title: The MObile RObot Project Description: The Obstacle represents an
 * obstacle on the map. Copyright: Copyright (c) 2001 Company: Universidi Bergamo
 
 * @author Davide Brugali
 * @version 1.0
 */
public class Obstacle
{
	// Default names.
	private String name = "Obstacle";
	// This polygon contains the cornerpoints of the obstacle.
	private Polygon polygon = null;

	private Color bgColor = Color.orange;
	private Color fgColor = Color.blue;
	private boolean opaque = true;
	private final int[][] points;

	/**
	 * Class constructor.
	 * @param name The name of the obstacle.
	 * @param points The x+y points of the obstacle.
	 * @param opaque Whether or not the obstacle is opaque.
	 */
	public Obstacle(String name, int[][] points, boolean opaque)
	{
		this.name = name;
		this.opaque = opaque;
		this.points = points;
	}
	
	/**
	 * Get a polygon representation for this obstacle.
	 * @return
	 */
	public Polygon getPolygon()
	{
		// Create the polygon if it hasn't been done yet.
		if (polygon == null)
		{
			polygon = new Polygon();
			for (int i = 0; i < points.length; i++)
			{
				int[] xy = points[i];
				polygon.addPoint(xy[0], xy[1]);
			}
		}
		
		return polygon;
	}
	
	/**
	 * Get the name of this obstacle.
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get the background color of this obstacle.
	 * @return
	 */
	public Color getBgColor()
	{
		return bgColor;
	}

	/**
	 * Get the foreground color of this obstacle.
	 * @return
	 */
	public Color getFgColor()
	{
		return fgColor;
	}

	/**
	 * Check whether or not this obstacle is opaque.
	 * @return
	 */
	public boolean isOpaque()
	{
		return opaque;
	}
	
	/**
	 * Return the array of points of this obstacle.
	 * @return
	 */
	public int[][] getPoints()
	{
		return points;
	}

	@Override
	public String toString()
	{
		String xml = "  <OBSTACLE NAME=" + '"' + name + '"' + " OPAQUE=" + '"' + opaque + '"'
				+ ">\n";
		for (int i = 0; i < points.length; i++)
			xml += "    <POINT X=" + '"' + points[i][0] + '"' + " Y=" + '"'
					+ points[i][1] + '"' + "/>\n";
		xml += "  </OBSTACLE>";
		return xml;
	}
}
