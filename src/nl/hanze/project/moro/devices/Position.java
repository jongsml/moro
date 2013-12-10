package nl.hanze.project.moro.devices;
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

import java.awt.geom.Point2D;

public class Position {
	private double x = 0.0;
	private double y = 0.0;
	private double t = 0.0;

	/*
	 * Set default position
	 */
	public Position() 
	{
		set(0.0, 0.0, 0.0);
	}

	/*
	 * Position x, y and angle (t)
	 */
	public Position(double x, double y, double t) {
		set(x, y, t);
	}

	public void rototras(double da, double db, double dt) 
	{
		this.x += da * Math.cos(this.t) - db * Math.sin(this.t);
		this.y += da * Math.sin(this.t) + db * Math.cos(this.t);
		this.t += Math.toRadians(dt);
		while(this.t > 2.0*Math.PI)
			this.t -= 2.0*Math.PI;
		while(this.t < 0.0)
			this.t += 2.0*Math.PI;
	}

	/*
	 * ??????
	 */
	public void rototras(Point2D point) {
		double px = point.getX() * Math.cos(t) - point.getY() * Math.sin(t) + x;
		double py = point.getX() * Math.sin(t) + point.getY() * Math.cos(t) + y;
		point.setLocation(px, py);
	}
	/*
	 * Get x position
	 */
	public double getX() {
		return x;
	}
	/*
	 * Get x position
	 */
	public double getY() {
		return y;
	}
	/*
	 * Get angle
	 */
	public double getT() {
		return t;
	}
	
	/*
	 * Sets the x,y and angle (t) values
	 */
	public void set(double x, double y, double t) {
		this.x = x;
		this.y = y;
		this.t = t;
		while(this.t > 2.0*Math.PI)
			this.t -= 2.0*Math.PI;
		while(this.t < 0.0)
			this.t += 2.0*Math.PI;
	}
	/*
	 * Copy the position x,y and angle value to the local x,y and t
	 */
	public void copyTo(Position position) {
		position.x = this.x;
		position.y = this.y;
		position.t = this.t;
	}
	
	/**
	 * Determine if this position object is close to "position".
	 * A max offset/distance should be given to determine proximity.
	 * @param position
	 * @param maxXdistance If x is within this distance, it matches.
	 * @param maxYdistance If y is within this distance, it matches.
	 * @return
	 */
	public boolean closeTo(Position position, int maxXdistance, int maxYdistance)
	{
		return ((position.x > this.x-maxXdistance) && (position.x < this.x+maxXdistance) && 
			    (position.y > this.y-maxYdistance) && (position.y < this.y+maxYdistance));
	}

	/*
	 * Copies x,y and angle(t) to a String
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "("+x+","+y+","+t+")";
	}
}
