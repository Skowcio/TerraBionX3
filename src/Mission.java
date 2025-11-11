import java.awt.*;
import java.util.ArrayList;

public class Mission {
    public String name;

    public ArrayList<Point> soldierPositions = new ArrayList<>();
    public ArrayList<Point> valkiriaPositions = new ArrayList<>();
    public ArrayList<Point> builderPositions = new ArrayList<>();
    public ArrayList<Point> enemyPositions = new ArrayList<>();
    public ArrayList<Point> enemyBehemothPositions = new ArrayList<>();
    public ArrayList<Point> qubePositions = new ArrayList<>();
    public ArrayList<Point> resourcesPositions = new ArrayList<>();
    public ArrayList<Point> powerPlantPositions = new ArrayList<>();
    public ArrayList<Point> crystalPositions = new ArrayList<>();
    public ArrayList<Point> hiveTooPositions = new ArrayList<>();
    public ArrayList<Point> floraPositions = new ArrayList<>();
    public ArrayList<Point> barackPositions = new ArrayList<>();
    public ArrayList<Point> factoryPositions = new ArrayList<>();

    public ArrayList<String> floraTypes = new ArrayList<>(); // do wyboru typu np. "marsh", "Tree"



    public int randomHiveCount = 0;
    public int randomHiveCount2 = 0;
    public Rectangle hiveSpawnArea;
    public Rectangle hiveSpawnArea2;

    public int requiredHivesDestroyed = 5;
/// // do zebrania w 3 misji ilsoc
    public int requiredSteelAmount = 0;

    public ObjectiveType objectiveType;

    public enum ObjectiveType {
        DESTROY_ALL_HIVES,
        DEFEND_FOR_TIME,
        COLLECT_RESOURCES
    }

    public Mission(String name) {
        this.name = name;
        this.objectiveType = ObjectiveType.DESTROY_ALL_HIVES; // domyślnie
    }
}