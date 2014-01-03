package nl.hanze.project.moro.view;
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
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JPanel;

import nl.hanze.project.moro.robot.device.PositionType;
import nl.hanze.project.moro.robot.event.OccupancyMapEvent;
import nl.hanze.project.moro.robot.event.OccupancyMapListener;

/**
 
 * @author Davide Brugali
 * @version 1.0
 */
@SuppressWarnings("serial")
public class OccupancyMapView extends JPanel implements OccupancyMapListener
{
	private int cellDim = 10;
	private int width = 510;
	private int height = 460;
	private PositionType grid[][] = new PositionType[width / cellDim][height / cellDim];

	public OccupancyMapView()
	{
		this.setBackground(SystemColor.window);
		
		// Set all grid items' initial status to unknown.
		// TODO: This is the initial grid. Later on this needs to be replaced.
		for (int i = 0; i < width / cellDim; i++)
			for (int j = 0; j < height / cellDim; j++)
				grid[i][j] = PositionType.UNKNOWN;

		
		
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// --------------------------- draw map --------------------------------------
		for (int i = 0; i < width / cellDim; i++)
			for (int j = 0; j < height / cellDim; j++)
			{
				final Color cellColor;
				switch (grid[i][j])
				{
					default:
						// Default color for unknown cells.
						cellColor = Color.yellow;
						break;
					case OBSTACLE:
						cellColor = Color.blue;
						break;
					case OPAQUE:
						cellColor = Color.magenta;
						break;
					case EMPTY:
						cellColor = Color.white;
						break;
				}
				// Set the color and paint the cell.
				g.setColor(cellColor);
				g.fillRect(i * cellDim, j * cellDim, cellDim, cellDim);
			}
		// --------------------------- draw grid --------------------------------------
		g.setColor(Color.lightGray);
		for (int i = 0; i <= width / cellDim; i++)
			g.drawLine(i * cellDim, 0, i * cellDim, height);
		for (int j = 0; j <= height / cellDim; j++)
			g.drawLine(0, j * cellDim, width, j * cellDim);
	}

	@Override
	public void onChange(OccupancyMapEvent event) 
	{
		// Update the grid using the OccupancyMap.
		grid = event.getOccupancyMap().getGrid();
		// repaint this component.
		repaint();
	}
}
