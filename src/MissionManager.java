import java.awt.*;
import java.util.ArrayList;

public class MissionManager {
    private ArrayList<Mission> missions = new ArrayList<>();
    private int currentMissionIndex = 0;

    public MissionManager() {
        loadMissions();
    }

    private void loadMissions() {
        // 🎯 Misja 1: Zniszcz wszystkie Hive
        Mission mission1 = new Mission("First Encounter");
        mission1.soldierPositions.add(new Point(200, 300));
        mission1.builderPositions.add(new Point(220, 330));
        mission1.resourcesPositions.add(new Point(350,400));
        mission1.enemyPositions.add(new Point(1000, 1200));
        mission1.randomHiveCount = 5;
        mission1.hiveSpawnArea = new Rectangle(1800, 1200, 1000, 1000);
        mission1.requiredHivesDestroyed = 5;
        mission1.objectiveType = Mission.ObjectiveType.DESTROY_ALL_HIVES;
        missions.add(mission1);

        // 🎯 Misja 2: przygotowana do wypełnienia
        Mission mission2 = new Mission("Hold the Line");
        mission2.soldierPositions.add(new Point(300, 400));
        mission2.builderPositions.add(new Point(320, 430));
        mission2.enemyPositions.add(new Point(1200, 1300));
        mission2.randomHiveCount = 3;
        mission2.hiveSpawnArea = new Rectangle(1600, 1300, 800, 800);
        mission2.requiredHivesDestroyed = 0;
        mission2.objectiveType = Mission.ObjectiveType.DEFEND_FOR_TIME; // <- inny typ celu
        missions.add(mission2);
    }

    public Mission getCurrentMission() {
        return missions.get(currentMissionIndex);
    }
    public boolean hasMoreMissions() {
        return currentMissionIndex < missions.size();
    }
    public void nextMission() {
        if (currentMissionIndex + 1 < missions.size()) {
            currentMissionIndex++;
        }
    }

    public void reset() {
        currentMissionIndex = 0;
    }
}
