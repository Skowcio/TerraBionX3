import java.io.*;
import java.util.ArrayList;

public class SaveLoadGame {

    // Zapisuje stan gry, w tym pozycje i kierunki żołnierzy
    public static void saveGame(ArrayList<Soldier> soldiers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("savegame.txt"))) {
            for (Soldier soldier : soldiers) {
                // Zapisz pozycję (x, y) oraz kierunek żołnierza
                writer.write(soldier.getX() + "," + soldier.getY() + "," + soldier.getCurrentDirection());
                writer.newLine();
            }
            System.out.println("Gra została zapisana pomyślnie!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ładuje stan gry i tworzy listę żołnierzy
    public static ArrayList<Soldier> loadGame() {
        ArrayList<Soldier> soldiers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String direction = parts[2];

                // Tworzymy nowego żołnierza na podstawie zapisanych danych
                Soldier soldier = new Soldier(x, y);
                soldier.setCurrentDirection(direction);
                soldiers.add(soldier);
            }
            System.out.println("Gra została wczytana pomyślnie!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return soldiers;
    }
}
