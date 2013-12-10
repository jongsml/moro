package importer;/*
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


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.util.ArrayList;

/**
 * Title: The MObile RObot Project Description: The Obstacle represents an
 * obstacle on the map. Copyright: Copyright (c) 2001 Company: Universidi
 * Bergamo
 * 
 * @author Davide Brugali
 * @version 1.0
 */
@XmlRootElement(name = "OBSTACLE")
public class Obstacle
{
	// Default names.
	private String name = "Obstacle";
	// This polygon contains the cornerpoints of the obstacle.
	private Polygon polygon = null;

	private Color bgColor = Color.orange;
	private Color fgColor = Color.blue;
	private boolean opaque = true;



    /**
     * XmlElement sets the name of the entities point
     * storage field for <code>Point</code> objects
     */
    @XmlElement(name = "POINT")
    private ArrayList<Point> pointList;



	/**
     *
	 * Class constructor backwart compatible arguments
	 * @param name The name of the obstacle.
	 * @param points The x+y points of the obstacle.
	 * @param opaque Whether or not the obstacle is opaque.
	 */
	public Obstacle(String name, int[][] points, boolean opaque)
	{
		this.name   = name;
		this.opaque = opaque;
        this.pointList = new  ArrayList<Point>();
        for (int i = 0; i < points.length; i++)
        {
            int[] xy = points[i];
            pointList.add(new Point(xy[0], xy[1]));
        }

	}


    public Obstacle(){}

    public boolean getOpaque(){
        return opaque;
    }

    /**
     * set the name of the <code>Obstacle</code>  see the xml attribute
     * of </OBSTACLE> for example <OBSTACLE NAME="WandLinks">
     * @param name obstacle
     */
    @XmlAttribute(name = "NAME")
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * set the boolean booleanb see xml node </OPAQUE>
     * @param opaque boolean
     */
    @XmlAttribute(name = "OPAQUE")
    public void setOpaque(boolean opaque)
    {
        this.opaque = opaque;
    }

	/**
	 * Get a polygon representation for this obstacle.
	 * @return
	 */
	public Polygon getPolygon()
	{
        if (polygon == null){
            polygon = new Polygon();
            for(Point point : pointList){
                polygon.addPoint(point.getX(),point.getY());
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
     * give back the list of Points
     * @return ArrayList points
     */
    public ArrayList<Point> getPointList() {
        return pointList;
    }

    /**
     * Return the array of points of this obstacle.
     * @return
     */
    public int[][] getPoints()
    {
        int[][] xy = new int[pointList.size()][2];
        for (int i = 0; i < pointList.size(); i++)
            xy[i] = new int[] { pointList.get(i).getX(), pointList.get(i).getY() };
        return xy;
    }


    @Override
	public String toString()
	{

        String points = "";
        for(Point point: getPointList()){
            points += "\t"+point.toString()+"\n";
        }
        return String.format("%s name: %s, opaque: %s Pounts:[\n%s]",  Obstacle.class.getName(), getName(), getOpaque(),points);
    }
}
