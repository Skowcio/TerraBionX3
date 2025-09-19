import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Consumer;

public class Factory {
    private int x, y;
    private int width = 140, height = 140;
    private final int size = 140;
    private boolean selected;
    private boolean producing = false;
    private int productionSecondsLeft = 0;
    private boolean spawnTimerActive = false;
    private SoldierBot producedBot = null;
    private boolean upgrading = false;
    private int health = 20;
    private int upgradeSecondsLeft = 0;
    /// // do licznika wyswietlanego w HUD
    // i LIMIT FABRYK
    private static int totalFactories = 0;
    private static int MAX_FACTORIES = 4;

    // soldier production
    private boolean producingSoldier = false;
    private long soldierStartTime;
    private final int soldierProductionTime = 20000; // 10s



    public static int getTotalFactories() {
        return totalFactories;
    }

//////////////////////////////////////////////////////////////
// to bedzie do przyciskow gettery i setter by np zwiekrzac zasieg patrolu soldierbot itp
    public int getPatrolSize() {
        return patrolSize;
    }

    public void setPatrolSize(int patrolSize) {
        this.patrolSize = patrolSize;
    }
/////////////////////////////////////////////////////
    private long productionStartTime = 0;

    private BufferedImage baracImage;
    private long lastSpawnTime = System.currentTimeMillis();
    private final int SPAWN_INTERVAL = 5000; // 30 sekund

    private ArrayList<SoldierBot> producedBots = new ArrayList<>();
    private int maxBots = 0; // Domyślnie 1, ale można zwiększyć przyciskiem
    private int patrolSize = 3000; // zasieg obszaru patrolowania


    private Random random = new Random();

    public Factory(int x, int y) {
        if (totalFactories >= MAX_FACTORIES) {
            throw new IllegalStateException("Osiągnięto limit " + MAX_FACTORIES + " fabryk!");
        }

        this.x = x;
        this.y = y;
        this.selected = false;

        try {
            baracImage = ImageIO.read(getClass().getResource("/factory/factory1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        totalFactories++; // zwiększamy globalny licznik
    }
    public static int getMaxFactories() {
        return MAX_FACTORIES;
    }
    public static int getFactoryCount() {
        return totalFactories;
    }
    public static void increaseMaxFactories(int amount) {
        MAX_FACTORIES += amount;
    }

    // 🚮 Metoda do usuwania fabryki (np. jak zniszczysz budynek)
    public static void decreaseFactoryCount() {
        if (totalFactories > 0) {
            totalFactories--;
        }
    }

    // 🚀 Reset liczby fabryk np. przy nowej misji
    public static void resetFactoryCount() {
        totalFactories = 0;
        MAX_FACTORIES = 4; // reset do domyślnego limitu
    }
    public boolean takeDamage() {
        health--;
        return health <= 0;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public void destroy() {
        health = 0;
    }

    /// ///// produkcja soldierow
    ///
    public double getSoldierProgress() {
        if (producingSoldier) {
            long now = System.currentTimeMillis();
            long elapsed = now - soldierStartTime;
            return Math.min(1.0, (double) elapsed / soldierProductionTime);
        }
        return 0.0;
    }
    public void startProducingSoldier() {
        if (!producingSoldier) {
            producingSoldier = true;
            soldierStartTime = System.currentTimeMillis();
            System.out.println("Produkcja Buildera rozpoczęta!");
        }
    }

    public void updateSoldierProduction(ArrayList<Soldier> soldiers) {
        if (producingSoldier) {
            long now = System.currentTimeMillis();
            if (now - soldierStartTime >= soldierProductionTime) {
                producingSoldier = false;

                // ➕ dodajemy soldier po ukończeniu produkcji
                int soldierX = x + width + 20; // np. obok Baracks
                int soldierY = y;
                soldiers.add(new Soldier(soldierX, soldierY));

                System.out.println("Builder wyprodukowany!");
            }
        }
    }

    public int getRemainingSoldierTime() {
        if (producingSoldier) {
            long now = System.currentTimeMillis();
            long remaining = soldierProductionTime - (now - soldierStartTime);
            return (int) Math.max(0, remaining / 1000);
        }
        return 0;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
/// //// to jest do produkcjiphenix drone
    public void startProduction(int duration) {
        producing = true;
        productionSecondsLeft = duration;
    }


    public void updateProduction() {
        if (producing && productionSecondsLeft > 0) {
            productionSecondsLeft--;
            if (productionSecondsLeft == 0) {
                producing = false;
            }
        }
    }

    public void spawnBots(Graphics g, ArrayList<SoldierBot> soldierBots, Supplier<Integer> getPower, Runnable consumePower) {
        long currentTime = System.currentTimeMillis();

        producedBots.removeIf(SoldierBot::isDead);

        if (producedBots.size() >= maxBots) {
            spawnTimerActive = false;
            return;
        }

        // Jeśli spawn timer nie jest aktywny — próbujemy go aktywować
        if (!spawnTimerActive) {
            if (getPower.get() >= 50) {
                consumePower.run(); // Zmniejszamy power o 50
                lastSpawnTime = currentTime;
                spawnTimerActive = true;
            } else {
                return; // Za mało power — nie uruchamiaj spawn
            }
        }

        if (spawnTimerActive && currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
            int spawnX = random.nextInt(size * 2) + x - size;
            int spawnY = random.nextInt(size * 2) + y - size;
            Rectangle patrol = new Rectangle(x + width / 2 - patrolSize / 2, y + height / 2 - patrolSize / 2, patrolSize, patrolSize);
            SoldierBot newBot = new SoldierBot(spawnX, spawnY, patrol);
            soldierBots.add(newBot);
            producedBots.add(newBot);

            if (producedBots.size() < maxBots) {
                lastSpawnTime = currentTime;
            } else {
                spawnTimerActive = false;
            }
        }
    }

    public boolean isProducing() {
        return producing;
    }

    public int getProductionSecondsLeft() {
        return productionSecondsLeft;
    }

//    public void upgradeBotLimit() {
//        maxBots++;
//    }

    public void upgradeBotLimit() {
        if (!upgrading && maxBots < 6) {
            upgrading = true;
            upgradeSecondsLeft = 3; // np. 30 sekund upgradu
        }
    }

    public void updateUpgrade() {
        if (upgrading && upgradeSecondsLeft > 0) {
            upgradeSecondsLeft--;
            if (upgradeSecondsLeft == 0) {
                upgrading = false;
                maxBots++;
            }
        }
    }

    public int getMaxBots() {
        return maxBots;
    }

    public int getAliveBots() {
        return producedBots.size();
    }

    // bez zaznaczenia
//    public void draw(Graphics g) {
//        if (baracImage != null) {
//            g.drawImage(baracImage, x, y, width, height, null);
//        }
//        if (producing) {
//            g.setColor(Color.WHITE);
//            g.drawString(productionSecondsLeft + "s", x + 10, y - 5); // Nad fabryką
//        }
//
//        if (selected) {
//            g.setColor(Color.GRAY);
//            g.drawRect(x - 2, y - 2, width + 4, height + 4);
//        }
//
//        // Liczba botów i limit
//        g.setColor(Color.WHITE);
//        g.drawString("Max Bots: " + maxBots, x + 5, y - 20);
//        g.drawString("Live: " + producedBots.size(), x + 5, y - 8);
//
//        // Czas do następnego bota
//        if (producedBots.size() < maxBots) {
//            long timeUntilNextSpawn = Math.max(0, (SPAWN_INTERVAL - (System.currentTimeMillis() - lastSpawnTime)) / 1000);
//            g.setColor(Color.YELLOW);
//            g.drawString("Next bot: " + timeUntilNextSpawn + "s", x + 5, y - 32);
//        }
//    }
    public void draw(Graphics g) {
        if (baracImage != null) {
            g.drawImage(baracImage, x, y, width, height, null);
        }

        if (producing) {
            g.setColor(Color.WHITE);
            g.drawString(productionSecondsLeft + "s", x + 10, y + 8); // Nad fabryką
        }

        if (selected) {
            // Szare obramowanie
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, width + 4, height + 4);

            // 🔵 Półprzezroczysty niebieski obszar patrolu

            int patrolX = x + width / 2 - patrolSize / 2;
            int patrolY = y + height / 2 - patrolSize / 2;

            g.setColor(new Color(0, 0, 255, 10)); // Przezroczysty niebieski
            g.fillRect(patrolX, patrolY, patrolSize, patrolSize);

            g.setColor(Color.BLUE);
            g.drawRect(patrolX, patrolY, patrolSize, patrolSize); // Obramowanie
        }
        if (producingSoldier) {
            g.setColor(Color.CYAN);
            g.drawString("AX-20M PRODUCTION: " + getRemainingSoldierTime() + "s", x, y + height + 45);
        }
        /// //pasek postepu budowy
        if (producingSoldier) {
            int barX = x;
            int barY = y + height + 60; // pod budynkiem, niżej niż napis
            int barWidth = 100;
            int barHeight = 10;

            // tło
            g.setColor(Color.GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // wypełnienie
            int fillWidth = (int) (barWidth * getSoldierProgress());
            g.setColor(Color.CYAN);
            g.fillRect(barX, barY, fillWidth, barHeight);

            // ramka
            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);

//            // licznik czasu
//            g.setColor(Color.CYAN);
//            g.drawString(" : " + getRemainingSoldierTime() + "s", x, y + height + 55);
        }

        // Liczba botów i limit
        g.setColor(Color.WHITE);
        g.drawString("Max Bots: " + maxBots, x + 5, y - 20);
        g.drawString("Live: " + producedBots.size(), x + 5, y - 8);

        // Czas do następnego bota
        if (producedBots.size() < maxBots) {
            long timeUntilNextSpawn = Math.max(0, (SPAWN_INTERVAL - (System.currentTimeMillis() - lastSpawnTime)) / 1000);
            g.setColor(Color.YELLOW);
            g.drawString("Next bot: " + timeUntilNextSpawn + "s", x + 5, y - 32);
        }
        if (upgrading) {
            g.setColor(Color.GREEN);
            g.drawString("Upgrade time: " + upgradeSecondsLeft + "s", x + 5, y - 44);
        }
        int maxHealth = 20; // Maksymalne zdrowie
    int healthBarWidth = 140; // Stała długość paska zdrowia
    int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek zdrowia

    // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.BLACK);
}

    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
