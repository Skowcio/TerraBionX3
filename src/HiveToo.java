import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class HiveToo {
    private int x, y;
    private int health = 50; // Liczba uderzeń, które Hive może wytrzymać
    private final int size = 100; // Rozmiar Hive
    private long lastSpawnTime = System.currentTimeMillis();
    private final int SPAWN_INTERVAL = 30000; // 20 sekund w milisekundach
    private Random random = new Random();
    private BufferedImage hiveImage;

    public HiveToo(int x, int y) {
        this.x = x;
        this.y = y;
        // Ładowanie grafiki PNG
        try {
            hiveImage = ImageIO.read(getClass().getResource("/hive/hive2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void spawnEnemiesToo(Graphics g, ArrayList<EnemyToo> enemiesToo, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
//        int numEnemies = random.nextInt(13) + 8; // Losowa liczba między 8 a 20
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
            for (int i = 0; i < 0; i++) {
                int spawnX = random.nextInt(size * 2) + x - size; // Wokół Hive
                int spawnY = random.nextInt(size * 2) + y - size;
                enemiesToo.add(new EnemyToo(spawnX, spawnY));
                lastSpawnTime = currentTime;
            }
            for (int i = 0; i < 3; i++) {
                int spawnX = random.nextInt(size * 5) + x - size; // Wokół Hive
                int spawnY = random.nextInt(size * 5) + y - size;
                enemyShooters.add(new EnemyShooter(spawnX, spawnY));
                lastSpawnTime = currentTime;
            }
            for (int i = 0; i < 0; i++) {
                int spawnX = random.nextInt(size * 5) + x - size; // Wokół Hive
                int spawnY = random.nextInt(size * 5) + y - size;
                enemyHunters.add(new EnemyHunter(spawnX, spawnY));
                lastSpawnTime = currentTime;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}