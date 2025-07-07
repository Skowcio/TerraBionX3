import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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
        mission1.resourcesPositions.add(new Point(350, 400));
        mission1.enemyPositions.add(new Point(1000, 1200));
        mission1.randomHiveCount = 5;
        mission1.hiveSpawnArea = new Rectangle(1800, 1200, 1000, 1000);
        mission1.requiredHivesDestroyed = 5;
        mission1.objectiveType = Mission.ObjectiveType.DESTROY_ALL_HIVES;

        // 🔧 Dodaj Hive w unikalnych miejscach bez kolizji
        int hiveSize = 80; // ustaw na prawdziwy rozmiar Hive (np. szerokość/grafika)
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
