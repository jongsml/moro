package importer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class MapLoader {
    private Map map;
    private JAXBContext context;
    private Unmarshaller unmarshaller;


    public MapLoader() throws JAXBException, IOException
    {
        context      = JAXBContext.newInstance(Map.class);
        unmarshaller = context.createUnmarshaller();
    }


    public MapLoader(String path) throws JAXBException, IOException
    {
        this();
        map = (Map) unmarshaller.unmarshal(new FileReader(path));
    }


    public MapLoader(InputStream stream) throws JAXBException, IOException
    {
        this();
        map = (Map) unmarshaller.unmarshal(stream);
    }

    /**
     * return als list whit <code>Obstacle</code>
     * @return ArrayList<Obstacle>
     */
    public ArrayList<Obstacle> getObstacle(){
        return map.getObstacleList();
    }


    public Map getMap()
    {
        return map;
    }

    /**
     * Demonstrator method run and see!...
     * @param arg arguments
     */
    public static void main(String arg[])
    {


        try{
            MapLoader loader = new MapLoader(".\\xml\\MapMetSonarTest.xml");

            System.out.println(loader.getMap().toString());

            for(Obstacle obstacle : loader.getObstacle()){
                System.out.println(obstacle.toString());
            }
        }catch(JAXBException | IOException e){
            e.printStackTrace();
        }
    }
}
