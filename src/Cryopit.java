import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Cryopit {
    private int x, y;
    private int maxSpawns = 1; // Maksymalnie 3 nowe Cryopity
    private int spawnedCryopits = 0; // Licznik utworzonych Cryopit
    private static final int SIZE = 7;
    private static final int SPAWN_INTERVAL = 2000; // 2 sekundy
    private static List<Cryopit> allCryopits = new ArrayList<>();
    private Random random = new Random();
    private Timer spawnTimer; // Timer odpowiedzialny za tworzenie nowych Cryopitów
    private static List<Cryopit> cryopits = new ArrayList<>();

    public Cryopit(int x, int y) {
        this.x = x;
        this.y = y;
        allCryopits.add(this);
        startSpawning();
    }

    public void draw(Graphics g) {
        g.setColor(Color.orange);
        g.fillRect(x, y, SIZE, SIZE);
    }
    public void tryToSpawnNewCryopit() {
        if (spawnedCryopits >= 1) return; // Jeśli osiągnął limit, kończymy

        List<Point> possiblePositions = getFreeAdjacentSpaces();
        if (possiblePositions.isEmpty()) return; // Jeśli nie ma wolnych miejsc, kończymy

        Point newPosition = possiblePositions.get(new Random().nextInt(possiblePositions.size()));
        Cryopit newCryopit = new Cryopit(newPosition.x, newPosition.y);
        cryopits.add(newCryopit);
        spawnedCryopits++;
    }

    private void startSpawning() {
        spawnTimer = new Timer(SPAWN_INTERVAL, e -> {
            List<Point> possibleLocations = getFreeAdjacentSpaces();

            // Jeśli nie ma wolnych miejsc lub osiągnięto limit, zatrzymaj timer
            if (maxSpawns <= 0 || possibleLocations.isEmpty()) {
                spawnTimer.stop();
                return;
            }

            spawnNewCryopit(possibleLocations);
        });
        spawnTimer.start();
    }


    private void spawnNewCryopit(List<Point> possibleLocations) {
        if (!possibleLocations.isEmpty()) {
            Point newLocation = possibleLocations.get(random.nextInt(possibleLocations.size()));
            new Cryopit(newLocation.x, newLocation.y);
            maxSpawns--; // Zmniejszamy licznik dostępnych spawnowań

            // Jeśli już nie może więcej spawnować, zatrzymujemy timer
            if (maxSpawns == 0) {
                spawnTimer.stop();
            }
        }
    }

    private List<Point> getFreeAdjacentSpaces() {
        List<Point> freeSpaces = new ArrayList<>();
        int[][] directions = {
                {-4, 0}, {4, 0}, {0, -4}, {0, 4}, // Góra, dół, lewo, prawo
                {-4, -4}, {4, -4}, {-4, 4}, {4, 4} // Skosy
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isPositionFree(newX, newY)) {
                freeSpaces.add(new Point(newX, newY));
            }
        }
        return freeSpaces;
    }

    public static boolean isPositionFree(int x, int y) {
        for (Cryopit c : allCryopits) {
            if (c.x == x && c.y == y) {
                return false; // Pozycja jest już zajęta
            }
        }
        return true;
    }

    public static void drawAll(Graphics g) {
        for (Cryopit cryopit : allCryopits) {
            cryopit.draw(g);
        }
    }
}
