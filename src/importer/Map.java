package importer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.util.ArrayList;

@XmlRootElement(name = "MAP")
public class Map {


    // XmlElement sets the name of the entities
    @XmlElement(name = "OBSTACLE")
    private ArrayList<Obstacle> obstacleList;


    public ArrayList<Obstacle> getObstacleList() {
        return obstacleList;
    }

    public String toString(){
        return "Amound of loaded Obstacles is: "+getObstacleList().size();
    }
}