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
 * Title:        The MObile RObot Project
 * Description:  The Obstacle represents an obstacle on the map. 
 * Copyright:    Copyright (c) 2001
 * Company:      Università di Bergamo
 * @author Davide Brugali
 * @version 1.0
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.IOException;

public class Obstacle {
	String name = "Obstacle";
	// This polygon contains the cornerpoints of the obstacle.
	Polygon polygon = new Polygon(); 
	
	Color bgColor = Color.orange;
	Color fgColor = Color.blue;
	boolean opaque = true;
	
	public Obstacle() {}

	/**
	 * Reads the <POINT X=".." Y=".."/> lines from the file and adds those points to the polygon
	 * @param line The <OBSTACLE> line read in Environment.
	 * @param lineReader The BufferedReader from which to read the <POINT ...> lines.
	 * @return Whether or not the obstacle was built succesfully.
	 */
	public boolean parseXML(String line, BufferedReader lineReader) {
		// Extracts the parameters
		name = parseAttribute(line, "NAME");
		opaque = Boolean.valueOf(parseAttribute(line, "OPAQUE")).booleanValue();

		String docLine;
		try {
			while ((docLine = lineReader.readLine())!=null) {
				// reads the file line by line
				if( docLine.indexOf("</OBSTACLE") > -1)
					return true;
				else
					if( docLine.indexOf("<POINT") > -1)
					{
						// extracts the coordinates of a vertex
						int px = Integer.parseInt(parseAttribute(docLine, "X"));
						int py = Integer.parseInt(parseAttribute(docLine, "Y"));
						// adds the new point to the shape
						polygon.addPoint(px, py);
					}
					else
						continue;
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return false;
	}

	/*
	 * Strips the value out of een String
	 */
	private String parseAttribute(String line, String attribute) {
		int indexInit = line.indexOf(attribute+"=");
		String parameter = line.substring(indexInit+attribute.length()+2);
		int indexEnd = parameter.indexOf('"');
		String value = parameter.substring(0, indexEnd);
		return value;
	}

	/*
	 * Creates xml for something
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String xml = "  <OBSTACLE NAME=" + '"' + name + '"' +
		" OPAQUE="  + '"' + opaque + '"' +">\n";
		for(int j=0; j<polygon.npoints; j++)
			xml += "    <POINT X=" + '"' + polygon.xpoints[j]+'"'+" Y="+'"'+polygon.ypoints[j]+'"'+"/>\n";
		xml += "  </OBSTACLE>";
		return xml;
	}

	/*
	 * sets parameter when creating graphics
	 */
	public void paint(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;
		if(opaque) {
			graphics2D.setColor(Color.magenta);
			graphics2D.fillPolygon(polygon);
			graphics2D.setColor(Color.darkGray);
			graphics2D.drawPolygon(polygon);
		}
		else {
			graphics2D.setColor(bgColor);
			graphics2D.fillPolygon(polygon);
			graphics2D.setColor(fgColor);
			graphics2D.drawPolygon(polygon);
		}
	}
}
