import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MissionManager {
    private ArrayList<Mission> missions = new ArrayList<>();
    private int currentMissionIndex = 0;


    public int getCurrentMissionIndex() {
        return currentMissionIndex;
    }
    public MissionManager() {
        loadMissions();

    }

    private void loadMissions() {
        // 🎯 Misja 1: Zniszcz wszystkie Hive
        Mission mission1 = new Mission("First Encounter");
        mission1.soldierPositions.add(new Point(700, 300));
        mission1.soldierPositions.add(new Point(800, 350));

        mission1.valkiriaPositions.add(new Point(1050, 400));

        mission1.crystalPositions.add(new Point(680, 700));

        mission1.floraPositions.add(new Point(400, 20));
        mission1.floraTypes.add("marsh");
        mission1.floraPositions.add(new Point(900,150));
        mission1.floraTypes.add("marsh2");
        mission1.floraPositions.add(new Point(1000,1700));
        mission1.floraTypes.add("marsh3");
        mission1.floraPositions.add(new Point(1200,800));
        mission1.floraTypes.add("marsh4");
        mission1.floraPositions.add(new Point(70,550));
        mission1.floraTypes.add("marsh5");
        mission1.floraPositions.add(new Point(220,110));
        mission1.floraTypes.add("marsh6");
        mission1.floraPositions.add(new Point(1111,500));
        mission1.floraTypes.add("marsh7");
        mission1.floraPositions.add(new Point(1711,350));
        mission1.floraTypes.add("marsh8");
        mission1.floraPositions.add(new Point(711,1950));
        mission1.floraTypes.add("marsh9");
        mission1.floraPositions.add(new Point(1511,1100));
        mission1.floraTypes.add("marsh10");
        mission1.floraPositions.add(new Point(1811,1200));
        mission1.floraTypes.add("marsh11");


        mission1.powerPlantPositions.add(new Point(450, 300));
        mission1.powerPlantPositions.add(new Point(450, 410));
        mission1.powerPlantPositions.add(new Point(450, 520));
        mission1.powerPlantPositions.add(new Point(450, 630));
        mission1.builderPositions.add(new Point(720, 370));
        mission1.barackPositions.add(new Point(660,  200));
        mission1.factoryPositions.add(new Point(950,  420));

        mission1.resourcesPositions.add(new Point(700, 450));
        mission1.resourcesPositions.add(new Point(850, 600));


//        mission1.enemyBehemothPositions.add(new Point(1100, 1300));
//
//        mission1.enemyPositions.add(new Point(1050, 1200));
//        mission1.enemyPositions.add(new Point(1100, 1200));
//        mission1.enemyPositions.add(new Point(1000, 1300));
//        mission1.enemyPositions.add(new Point(1000, 1250));


        mission1.randomHiveCount = 5;

        mission1.soldierPositions.add(new Point(1050, 9500));
        mission1.soldierPositions.add(new Point(1000, 900));
        mission1.soldierPositions.add(new Point(1350, 1000));
        mission1.soldierPositions.add(new Point(1350, 1300));
        mission1.soldierPositions.add(new Point(1350, 1200));
        mission1.soldierPositions.add(new Point(1350, 1050));
        mission1.soldierPositions.add(new Point(1800, 1050));

//        mission1.valkiriaPositions.add(new Point(1250, 1000));
//        mission1.valkiriaPositions.add(new Point(1250, 1100));

        mission1.hiveSpawnArea = new Rectangle(1800, 1200, 1000, 1000);
        mission1.requiredHivesDestroyed = 5;
        mission1.objectiveType = Mission.ObjectiveType.DESTROY_ALL_HIVES;

        // 🔧 Dodaj Hive w unikalnych miejscach bez kolizji
        int hiveSize = 100; // ustaw na prawdziwy rozmiar Hive (np. szerokość/grafika)
        ArrayList<Point> hivePositions = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < mission1.randomHiveCount; i++) {
            int attempts = 0;
            boolean placed = false;

            while (!placed && attempts < 100) {
                int x = mission1.hiveSpawnArea.x + rand.nextInt(mission1.hiveSpawnArea.width);
                int y = mission1.hiveSpawnArea.y + rand.nextInt(mission1.hiveSpawnArea.height);
                Rectangle newRect = new Rectangle(x, y, hiveSize, hiveSize);

                boolean overlaps = false;
                for (Point p : hivePositions) {
                    Rectangle existing = new Rectangle(p.x, p.y, hiveSize, hiveSize);
                    if (newRect.intersects(existing)) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    hivePositions.add(new Point(x, y));
                    placed = true;
                } else {
                    attempts++;
                }
            }

            if (!placed) {
                System.out.println("⚠️ Nie udało się umieścić Hive bez kolizji po 100 próbach.");
            }
        }

        // Teraz zamień punkty na prawdziwe Hive w GamePanel.loadMission()
        // (albo przechowuj je w Mission i stwórz Hive w GamePanel z tych pozycji)

        // Możesz dodać te punkty do mission1 jako nową listę jeśli chcesz:
        // mission1.hivePositions = hivePositions;

        missions.add(mission1);

        // 🎯 Misja 2: przygotowana do wypełnienia
        Mission mission2 = new Mission("Hold the Line");
        mission2.soldierPositions.add(new Point(300, 400));
        mission2.builderPositions.add(new Point(320, 430));
        mission2.builderPositions.add(new Point(380, 450));
        mission2.barackPositions.add(new Point(660,  200));

        mission2.enemyPositions.add(new Point(1400, 1500));
        mission2.enemyPositions.add(new Point(1200, 1300));

        mission2.resourcesPositions.add(new Point(550, 400));
        mission2.resourcesPositions.add(new Point(590, 450));
        mission2.resourcesPositions.add(new Point(1550, 1400));

        mission2.hiveTooPositions.add(new Point(1950, 1500));
        mission2.randomHiveCount = 30;
        mission2.hiveSpawnArea = new Rectangle(1800, 1400, 800, 800);
        mission2.enemyPositions.add(new Point(1800, 1300));
        mission2.requiredHivesDestroyed = 0;

        mission2.objectiveType = Mission.ObjectiveType.DEFEND_FOR_TIME; // <- inny typ celu

        missions.add(mission2);

        /// ////////////// misja 3
        Mission mission3 = new Mission("Resource Rush");
        mission3.soldierPositions.add(new Point(500, 400));
        mission3.builderPositions.add(new Point(520, 420));
        mission3.barackPositions.add(new Point(600, 200));

// Dodaj trochę surowców na mapę
        mission3.resourcesPositions.add(new Point(800, 600));
        mission3.resourcesPositions.add(new Point(900, 650));
        mission3.resourcesPositions.add(new Point(1000, 700));
        mission3.resourcesPositions.add(new Point(1500, 700));
        mission3.resourcesPositions.add(new Point(1600, 750));

// Ile trzeba zebrać surowca (np. stali)
        mission3.requiredHivesDestroyed = 0; // tu niepotrzebne, ale zostaje
        mission3.objectiveType = Mission.ObjectiveType.COLLECT_RESOURCES;
        mission3.requiredSteelAmount = 60000; // ile trzeba zebrać do ukończenia

        missions.add(mission3);
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