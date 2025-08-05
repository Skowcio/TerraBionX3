import java.awt.*;
import java.util.ArrayList;

public class Mission {
    public String name;

    public ArrayList<Point> soldierPositions = new ArrayList<>();
    public ArrayList<Point> builderPositions = new ArrayList<>();
    public ArrayList<Point> enemyPositions = new ArrayList<>();
    public ArrayList<Point> resourcesPositions = new ArrayList<>();
    public ArrayList<Point> powerPlantPositions = new ArrayList<>();
    public ArrayList<Point> crystalPositions = new ArrayList<>();
    public ArrayList<Point> hiveTooPositions = new ArrayList<>();

    public int randomHiveCount = 0;
    public Rectangle hiveSpawnArea;

    public int requiredHivesDestroyed = 5;

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
