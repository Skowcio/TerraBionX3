import java.util.ArrayList;

public class GridMap {
    private final int tileSize = 40;
    private final int cols = 4000 / tileSize;
    private final int rows = 4000 / tileSize;
    private final boolean[][] occupied;

    private ArrayList<PowerPlant> powerPlants = new ArrayList<>();
    private ArrayList<Baracks> baracks = new ArrayList<>();
    private ArrayList<Soldier> soldiers = new ArrayList<>();
    private ArrayList<Hive> hives = new ArrayList<>();
    private ArrayList<BattleVehicle> battleVehicles = new ArrayList<>();

    public GridMap() {
        occupied = new boolean[cols][rows];
    }

    // Ustaw zajętość pola
    public void setOccupied(int x, int y, boolean isOccupied) {
        int col = x / tileSize;
        int row = y / tileSize;
        if (isValid(col, row)) {
            occupied[col][row] = isOccupied;
        }
    }


    // Sprawdź, czy pole jest zajęte
    public boolean isOccupied(int x, int y) {
        int col = x / tileSize;
        int row = y / tileSize;
        if (isValid(col, row)) {
            return occupied[col][row];
        }
        return true; // poza mapą = zajęte
    }

    // Sprawdź, czy współrzędne są w granicach
    private boolean isValid(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    // Wyczyść dynamiczne przeszkody (np. żołnierze)
    public void clearDynamicObstacles() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                occupied[col][row] = false;
            }
        }

        // Dodaj stałe przeszkody ponownie (np. PowerPlant)
        for (PowerPlant p : powerPlants) {
            int col = p.getX() / tileSize;
            int row = p.getY() / tileSize;
            if (isValid(col, row)) {
                occupied[col][row] = true;
            }
        }

        // Możesz tu też dodać inne statyczne przeszkody jak Baracks, Hive, itp.
    }

    // Aktualizuj mapę zajętości dynamicznie — podajesz jednostkę, która aktualnie się porusza
    public void updateOccupied(Soldier movingSoldier) {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                occupied[col][row] = false;
            }
        }

        for (PowerPlant plant : powerPlants) {
            int gx = plant.getX() / tileSize;
            int gy = plant.getY() / tileSize;
            if (isValid(gx, gy)) {
                occupied[gx][gy] = true;
            }
        }

        for (Soldier soldier : soldiers) {
            if (soldier != movingSoldier) {
                int gx = soldier.getX() / tileSize;
                int gy = soldier.getY() / tileSize;
                if (isValid(gx, gy)) {
                    occupied[gx][gy] = true;
                }
            }
        }
    }

    // Gettery
    public ArrayList<PowerPlant> getPowerPlants() {
        return powerPlants;
    }

    public ArrayList<Baracks> getBaracks() {
        return baracks;
    }

    public ArrayList<Soldier> getSoldiers() {
        return soldiers;
    }

    public ArrayList<Hive> getHives() {
        return hives;
    }

    public ArrayList<BattleVehicle> getBattleVehicles() {
        return battleVehicles;
    }
}
