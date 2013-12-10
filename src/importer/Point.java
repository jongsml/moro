package importer;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * this class is representate the node in the xml file that POINT
 * see xml </POINT>  this xml node has attributes x and y
 * @author Michel Tartarotti
 * @version 0.0.1
 * @since 0.0.1
 */
@XmlRootElement(name = "POINT")
public class Point
{
    private int y;
    private int x;
    public Point(){}
    public Point(int x, int y){
        setX(x);
        setY(y);
    }
    /**
     * getter for retrieving the x coordinate
     * @return x int coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * sets the x coordinate form the xml file
     * @param x coordinate
     */
    @XmlAttribute(name = "X")
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * getter for retrieving the y coordinate
     * @return y int coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * set the y coordinate
     * @param y int coordinate
     */
    @XmlAttribute(name = "Y")
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * overwrite to string method representation for <code>Point</code> object
     * @return string object presentation
     */
    @Override
    public String toString()
    {
        return String.format("%s x: %s, y: %s",  Obstacle.class.getName(), getX(), getY());
    }
}
