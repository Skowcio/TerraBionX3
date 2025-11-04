import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class Hive {
    private int x, y;
    private int health = 50; // Liczba uderzeń, które Hive może wytrzymać
    private final int size = 100; // Rozmiar Hive
//    private long lastSpawnTime = System.currentTimeMillis(); // to potrzebne do czasowego respa
    private final int SPAWN_INTERVAL = 20000; // 20 sekund w milisekundach
    private final int width = 80, height = 80;
    private long lastSpawnTime;
    private Random random = new Random();
    private BufferedImage hiveImage;
    private boolean activated = false;

    public Hive(int x, int y) {
        this.x = x;
        this.y = y;
        // Ładowanie grafiki PNG
        try {
            hiveImage = ImageIO.read(getClass().getResource("/hive/hive2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public boolean isDead() {
        return health <= 0;
    }
    // tu masz zaleznosc ze  gdy zblizy sie przeciwnik to sie zacznie produkcja
    public void checkActivation(ArrayList<Soldier> soldiers, ArrayList<SoldierBot> soldierBots, ArrayList<BuilderVehicle> builders) {
        if (!activated) {
            for (Soldier soldier : soldiers) {
                double dx = soldier.getX() - x;
                double dy = soldier.getY() - y;
                if (Math.sqrt(dx * dx + dy * dy) <= 500) {
                    activated = true;
                    lastSpawnTime = System.currentTimeMillis();
                    return;
                }
            }

            for (BuilderVehicle builder : builders) {
                double dx = builder.getX() - x;
                double dy = builder.getY() - y;
                if (Math.sqrt(dx * dx + dy * dy) <= 700) {
                    activated = true;
                    lastSpawnTime = System.currentTimeMillis();
                    return;
                }
            }
            for (SoldierBot soldierBot : soldierBots){

                double dx = soldierBot.getX() - x;
                double dy = soldierBot.getY() - y;
                if (Math.sqrt(dx * dx + dy * dy) <= 400) {
                    activated = true;
                    lastSpawnTime = System.currentTimeMillis();
                    return;
                }
            }
        }
    }
    public void spawnEnemiesToo(Graphics g, ArrayList<EnemyToo> enemiesToo,
                                ArrayList<EnemyShooter> enemyShooters
//                                ,ArrayList<EnemyHunter> enemyHunters
    ) {

        enemiesToo.removeIf(EnemyToo::isDead);
        enemyShooters.removeIf(EnemyShooter::isDead);

        if (!activated) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
            final int MAX_ENEMY_TOO = 10;
            final int MAX_ENEMY_SHOOTER = 15;
            final int MAX_ENEMY_HUNTER = 5;

            // ==== EnemyToo ====
            int currentEnemyTooCount = enemiesToo.size();
            int enemiesTooToSpawn = MAX_ENEMY_TOO - currentEnemyTooCount;

            if (enemiesTooToSpawn > 0) {
                int spawnCount = 1 + random.nextInt(3); // 1–3 losowo
                spawnCount = Math.min(spawnCount, enemiesTooToSpawn);
                for (int i = 0; i < spawnCount; i++) {
                    int spawnX = random.nextInt(size * 2) + x - size;
                    int spawnY = random.nextInt(size * 2) + y - size;
                    enemiesToo.add(new EnemyToo(spawnX, spawnY));
                }
            }

            // ==== EnemyShooter ====
            int currentShooterCount = enemyShooters.size();
            int shootersToSpawn = MAX_ENEMY_SHOOTER - currentShooterCount;

            if (shootersToSpawn > 0) {
                int spawnCount = 1 + random.nextInt(5); // 1–3 losowo
                spawnCount = Math.min(spawnCount, shootersToSpawn);
                for (int i = 0; i < spawnCount; i++) {
                    int spawnX = random.nextInt(size * 8) + x - size;
                    int spawnY = random.nextInt(size * 8) + y - size;
                    enemyShooters.add(new EnemyShooter(spawnX, spawnY));
                }
            }

            // ==== EnemyHunter ====
//            int currentHunterCount = enemyHunters.size();
//            int huntersToSpawn = MAX_ENEMY_HUNTER - currentHunterCount;
//
//            if (huntersToSpawn > 0) {
//                int spawnCount = 1 + random.nextInt(6); // 1–6 losowo
//                spawnCount = Math.min(spawnCount, huntersToSpawn);
//                for (int i = 0; i < spawnCount; i++) {
//                    int spawnX = random.nextInt(size * 5) + x - size;
//                    int spawnY = random.nextInt(size * 5) + y - size;
//                    enemyHunters.add(new EnemyHunter(spawnX, spawnY));
//                }
//            }

            lastSpawnTime = currentTime;
        }
    }


    public void updateActivationAndSpawning(Graphics g,
                                            ArrayList<Soldier> soldiers,
                                            ArrayList<SoldierBot> soldierBots,
                                            ArrayList<BuilderVehicle> builders,
                                            ArrayList<EnemyToo> enemiesToo,
                                            ArrayList<EnemyShooter> enemyShooters,
                                            ArrayList<EnemyHunter> enemyHunters)
    {
        checkActivation(soldiers, soldierBots, builders);
        spawnEnemiesToo(g, enemiesToo, enemyShooters
//                , enemyHunters
        );
    }
    public Point getPosition() {
        return new Point(x, y);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        if (hiveImage != null) {
            g.drawImage(hiveImage, x, y, size, size, null);
        } else {
            // Fallback na rysowanie prostokąta, jeśli obraz nie został załadowany
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, size, size);
        }
// Rysowanie paska zdrowia
        int maxHealth = 50; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 100; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

//        g.setColor(Color.GREEN);
//        int healthBarWidth = (int) ((health / 5.0) * size); // Zdrowie jako procent rozmiaru
//        g.fillRect(x, y - 5, healthBarWidth, 3); // Pasek nad wrogiem
    }

//    public boolean checkCollision(Bullet bullet) {
//        Rectangle hiveRect = new Rectangle(x, y, size, size);
//        Rectangle bulletRect = new Rectangle(bullet.getX(), bullet.getY(), 5, 5);
//        return hiveRect.intersects(bulletRect);
//    }

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Hive zostało zniszczone
    }
// spawn co 30s dobry do factory bedzie
//    public void spawnEnemiesToo(Graphics g, ArrayList<EnemyToo> enemiesToo, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
////        int numEnemies = random.nextInt(13) + 8; // Losowa liczba między 8 a 20
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
//            for (int i = 0; i < 2; i++) {
//                int spawnX = random.nextInt(size * 2) + x - size; // Wokół Hive
//                int spawnY = random.nextInt(size * 2) + y - size;
//                enemiesToo.add(new EnemyToo(spawnX, spawnY));
//                lastSpawnTime = currentTime;
//            }
//            for (int i = 0; i < 1; i++) {
//                int spawnX = random.nextInt(size * 5) + x - size; // Wokół Hive
//                int spawnY = random.nextInt(size * 5) + y - size;
//                enemyShooters.add(new EnemyShooter(spawnX, spawnY));
//                lastSpawnTime = currentTime;
//            }
//            for (int i = 0; i < 2; i++) {
//                int spawnX = random.nextInt(size * 5) + x - size; // Wokół Hive
//                int spawnY = random.nextInt(size * 5) + y - size;
//                enemyHunters.add(new EnemyHunter(spawnX, spawnY));
//                lastSpawnTime = currentTime;
//            }
//        }
//    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
