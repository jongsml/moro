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
 * Company:      Università di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */


import java.awt.Color;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class OccupancyMap extends JPanel {
	/*
	 * The class OccupanceMap is responsible for containing the data that has been discovered by
	 * the robot. It is also reponsible for drawing this data. Not a great example of cohesion.
	 */
	int cellDim = 10;
	int width = 510;
	int height = 460;
	char grid[][] = new char[width/cellDim][height/cellDim];
	private final static char UNKNOWN = 'n';
	private final static char EMPTY = 'e';
	private final static char OBSTACLE = 'o';

	public OccupancyMap() {
		this.setBackground(SystemColor.window);
		for(int i=0; i < width/cellDim; i++)
			for(int j=0; j < height/cellDim; j++)
				grid[i][j] = UNKNOWN;

		this.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// --------------------------- draw map --------------------------------------
		for(int i=0; i < width/cellDim; i++)
			for(int j=0; j < height/cellDim; j++) {
				if(grid[i][j] == UNKNOWN) {
					g.setColor(Color.yellow);
					g.fillRect(i*cellDim, j*cellDim, cellDim, cellDim);
				}
				else if(grid[i][j] == OBSTACLE) {
					g.setColor(Color.blue);
					g.fillRect(i*cellDim, j*cellDim, cellDim, cellDim);
				}
			}
		// --------------------------- draw grid --------------------------------------
		g.setColor(Color.lightGray);
		for(int i=0; i <= width/cellDim; i++)
			g.drawLine(i*cellDim, 0, i*cellDim, height);
		for(int j=0; j <= height/cellDim; j++)
			g.drawLine(0, j*cellDim, width, j*cellDim);
	}

	/*
	 * 
	 */
	public void drawLaserScan(double position[], double measures[]) {
		// Calculates x and y position using distance and angle
		double rx = Math.round( position[0] + 20.0 * Math.cos(Math.toRadians(position[2])) );
		double ry = Math.round( position[1] + 20.0 * Math.sin(Math.toRadians(position[2])) );
		int   dir = (int) Math.round(position[2]);

		
		for(int i=0; i<360; i++) {
			int d = i - dir;
			while(d < 0)
				d += 360;
			while(d >= 360)
				d -= 360;
			
			// calculates the x and y values for all angles and collisions
			// When a collision is detected the measure is < 100
			// When no collision is detected the measure is 100
			double fx = Math.round( rx + measures[d] * Math.cos(Math.toRadians(i)) );
			double fy = Math.round( ry + measures[d] * Math.sin(Math.toRadians(i)) );

			if(measures[d] < 100)
				// When collision is detected an obstacle is detected and value is true
				drawLaserBeam(rx, ry, fx, fy, true);
			else
				drawLaserBeam(rx, ry, fx, fy, false);
		}
		this.repaint();
	}

	private void drawLaserBeam(double rx, double ry, double x, double y, boolean obstacle) {
		int rxi = (int) Math.ceil(rx / cellDim);
		int ryj = (int) Math.ceil(ry / cellDim);
		int  xi = (int) Math.ceil(x / cellDim);
		int  yj = (int) Math.ceil(y / cellDim);

		if(xi < 0 || yj < 0 || xi >= width/cellDim || yj >= height/cellDim )
			return;

		// This fills the grid coordinate with the type of obstacle
		// If no obstacle then the grid coordinate is empty
		if(obstacle)
			grid[xi][yj] = OBSTACLE;
		else if(grid[xi][yj] != OBSTACLE)
			grid[xi][yj] = EMPTY;

		int xmin = Math.min(rxi, xi);
		int xmax = Math.max(rxi, xi);
		int ymin = Math.min(ryj, yj);
		int ymax = Math.max(ryj, yj);

		// ???????
		if(rx == x)
			for(int j=ymin; j<=ymax; j++) {
				if(grid[xmin][j] != OBSTACLE)
					grid[xmin][j] = EMPTY;
			}
		else {
			double m = (y - ry) / (x - rx);
			double q = y - m * x;
			for(int i=xmin; i<=xmax; i++) {
				int h = (int) Math.ceil((m * (i*cellDim) + q) / cellDim);
				if(h >= ymin && h <= ymax) {
					if(grid[i][h] != OBSTACLE)
						grid[i][h] = EMPTY;
//					if(grid[i+1][h] != OBSTACLE)
//					grid[i+1][h] = EMPTY;
				}
			}
		}
	}
}
