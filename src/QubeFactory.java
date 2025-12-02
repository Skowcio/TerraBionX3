import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class QubeFactory {
    private int x, y;
    private int health = 200; // Liczba uderzeń, które Hive może wytrzymać
    private final int size = 200; // Rozmiar Hive
    //    private long lastSpawnTime = System.currentTimeMillis(); // to potrzebne do czasowego respa
    private final int SPAWN_INTERVAL = 70000; // 20 sekund w milisekundach
    private final int width = 360, height = 240;
    private long lastSpawnTime;
    private Random random = new Random();
    private BufferedImage hiveImage;
    private boolean activated = false;


    private boolean dead = false;

    private int shield = 100;                    // ile ma dodatkowego życia
    private final int maxShield = 20;           // maksymalna pojemność
    private long lastShieldRegenTime = 0;       // czas ostatniej regeneracji
    private final long shieldRegenInterval = 3000; // co 3 sekundy

    public QubeFactory (int x, int y) {
        this.x = x;
        this.y = y;
        // Ładowanie grafiki PNG
        try {
            hiveImage = ImageIO.read(getClass().getResource("/Qube/QubeFactory.png"));
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
    public void checkActivation(ArrayList<Soldier> soldiers, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyToo> enemyToos, ArrayList<SoldierBot> soldierBots, ArrayList<BuilderVehicle> builders) {
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
            for (EnemyShooter enemyShooter : enemyShooters){
                double dx = enemyShooter.getX() - x;
                double dy = enemyShooter.getY() - y;
                if (Math.sqrt(dx * dx + dy * dy) <= 500) {
                    activated = true;
                    lastSpawnTime = System.currentTimeMillis();
                    return;
                }
            }

            for (EnemyToo enemyToo : enemyToos){
                double dx = enemyToo.getX() - x;
                double dy = enemyToo.getY() - y;
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
    public void spawnEnemiesToo(Graphics g,  ArrayList<Qube> qubes

    ) {


        qubes.removeIf(Qube::isDead);

        if (!activated) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {


            final int MAX_ENEMY_QUBE = 2;

            int currentEnemyQubeCount = qubes.size();
            int qubesToSpawn = MAX_ENEMY_QUBE - currentEnemyQubeCount;

            if (qubesToSpawn > 0) {
                int spawnCount = 1 + random.nextInt(2); // 1–3 losowo
                spawnCount = Math.min(spawnCount, qubesToSpawn);
                for (int i = 0; i < spawnCount; i++) {
                    int spawnX = random.nextInt(size * 2) + x - size;
                    int spawnY = random.nextInt(size * 2) + y - size;
                    qubes.add(new Qube(spawnX, spawnY));
                }
            }


            lastSpawnTime = currentTime;
        }
    }


    public void updateActivationAndSpawning(Graphics g,
                                            ArrayList<Soldier> soldiers,
                                            ArrayList<EnemyToo> enemiesToo,
                                            ArrayList<EnemyShooter> enemyShooters,
                                            ArrayList<SoldierBot> soldierBots,
                                            ArrayList<BuilderVehicle> builders,

                                            ArrayList<Qube> qubes)
    {
        checkActivation(soldiers, enemyShooters, enemiesToo, soldierBots, builders);
        spawnEnemiesToo(g, qubes);
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
        Graphics2D g2d = (Graphics2D) g;
        if (hiveImage != null) {
            g.drawImage(hiveImage,x, y, width, height, null);
        } else {
            // Fallback na rysowanie prostokąta, jeśli obraz nie został załadowany
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, size, size);
        }
        // ===== RYSOWANIE POLA SIŁOWEGO =====
        if (shield > 0) {
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            int r = width + 20;

            // otoczka
            g2d.setColor(new Color(0, 170, 255, 40));
            g2d.fillOval(centerX - r / 2, centerY - r / 2, r, r);

            // pole siłowe
            g2d.setColor(new Color(100, 200, 255, 80));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawOval(centerX - r / 2, centerY - r / 2, r, r);

            // reset pędzla
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1f));
        }
// Rysowanie paska zdrowia
        int maxHealth = 200; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 360; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        // ===== PASEK TARCZY =====
        int maxShield = 100;
        int barW = 360;
        int shiledW = (int) ((shield / (double) maxShield) * barW);

        // pasek nad
        int barYy = y - 10; // Shield

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.RED);
        g.fillRect(x, barYy, shiledW, 3);

        g.setColor(Color.BLACK);
        g.drawRect(x, barYy, barW, 3);


    }
    public void update(

    ) {

        // 🔋 Regeneracja pola siłowego
        long now = System.currentTimeMillis();
        if (now - lastShieldRegenTime >= shieldRegenInterval) {
            if (shield < maxShield) {
                shield++; // regeneracja 1 punktu
            }
            lastShieldRegenTime = now;
        }

    }



    public boolean takeDamage() {

        // Najpierw schodzi tarcza
        if (shield > 0) {
            shield--;
            return false; // żyje
        }

        // Dopiero później HP
        health--;

        if (health <= 0) {
            markAsDead();
            return true;
        }

        return false;
    }

    public void markAsDead() { this.dead = true; }
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
