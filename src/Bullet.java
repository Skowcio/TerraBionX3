import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Bullet { // Bullet jest używany przez Soldier
    private int x, y;
    private int dx, dy; // Przemieszczenie w poziomie i pionie
    private final int speed = 30;
    private final long creationTime; // Czas utworzenia pocisku
    private static final long LIFETIME = 900; // Czas życia pocisku w milisekundach
    private static int playingSounds = 0;
    private static final int MAX_SIMULTANEOUS_SOUNDS = 8;


    public Bullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.creationTime = System.currentTimeMillis();

        // Obliczamy wektory prędkości w kierunku celu
        int dx = targetX - x;
        int dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        this.dx = (int) (speed * dx / distance); // Normalizujemy prędkość do jednostkowej długości
        this.dy = (int) (speed * dy / distance);

    }
    public Bullet(int x, int y, int targetX, int targetY,
                            int cameraX, int cameraY, int screenWidth, int screenHeight) {
        this(x, y, targetX, targetY);
        playShootSound(cameraX, cameraY, screenWidth, screenHeight); // ZAWSZE graj dźwięk, ale dynamicznie
    }
    private void playShootSound(int cameraX, int cameraY, int screenWidth, int screenHeight) {
        try {
            // --- LIMIT RÓWNOLEGŁYCH DŹWIĘKÓW ---
            if (playingSounds >= MAX_SIMULTANEOUS_SOUNDS) {
                return;
            }

            File soundFile = new File("F:\\projekty JAVA\\TerraBionX3\\src\\shoot\\shoot3.wav");
            if (!soundFile.exists()) {
                System.err.println("Nie znaleziono pliku dźwięku: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            playingSounds++;   // 🟦 zwiększamy licznik grających dźwięków

            // Kiedy dźwięk się kończy:
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    playingSounds--;   // 🟥 zmniejszamy licznik
                }
            });

            // --- OBLICZANIE GŁOŚNOŚCI WG DYSTANSU ---
            int viewCenterX = cameraX + screenWidth / 2;
            int viewCenterY = cameraY + screenHeight / 2;
            double dx = this.x - viewCenterX;
            double dy = this.y - viewCenterY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            float maxDistance = 1800f;
            float volume = (float) Math.pow(Math.max(0f, 1.0 - distance / maxDistance), 0.7);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (20f * Math.log10(Math.max(0.01f, volume)));
            gainControl.setValue(dB);

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Efekt świetlny — poświata
        for (int i = 0; i < 3; i++) {
            int radius = 10 + i * 8;
            float alpha = 0.25f - i * 0.07f; // im dalej od środka, tym słabsza poświata
            g2d.setColor(new Color(1f, 1f, 0.6f, alpha)); // jasnożółta poświata
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }

        // Główny pocisk (jasny środek)
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 2, y - 2, 5, 5);

        g2d.dispose();
    }
    public boolean checkCollision(Enemy enemy) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 30, 30);
        return bulletRect.intersects(enemyRect);
    }

    public boolean checkCollision(Hive hive) {
        Rectangle hiveRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(hive.getX(), hive.getY(), 80, 80);
        return hiveRect.intersects(bulletRect);
    }
    public boolean checkCollision(QubeFactory qubeFactory) {
        Rectangle hiveRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(qubeFactory.getX(), qubeFactory.getY(), 200, 200);
        return hiveRect.intersects(bulletRect);
    }
    public boolean checkCollision(HiveToo hiveToo) {
        Rectangle hiveRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(hiveToo.getX(), hiveToo.getY(), 80, 80);
        return hiveRect.intersects(bulletRect);
    }
        public boolean checkCollision(EnemyShooter enemyShooter) {
        Rectangle enemyShooterRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(enemyShooter.getX(), enemyShooter.getY(), 40, 40);
        return enemyShooterRect.intersects(bulletRect);
    }
    public boolean checkCollision(EnemyHunter enemyHunter) {
        Rectangle enemyHunterRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(enemyHunter.getX(), enemyHunter.getY(), 40, 40);
        return enemyHunterRect.intersects(bulletRect);
    }

    public boolean checkCollision(EnemyToo enemyToo) {
        Rectangle bulletRect = new Rectangle(x, y, 10, 10);
        return bulletRect.intersects(enemyToo.getBounds());
    }
    public boolean checkCollision(EnemyBehemoth enemyBehemoth) {
        Rectangle bulletRect = new Rectangle(x, y, 10, 10);
        return bulletRect.intersects(enemyBehemoth.getBounds());
    }
    public boolean checkCollision(Qube qube) {
        Rectangle bulletRect = new Rectangle(x, y, 10, 10);
        return bulletRect.intersects(qube.getBounds());
    }
    public boolean checkCollision(QubeTower qubeTower) {
        Rectangle bulletRect = new Rectangle(x, y, 10, 10);
        return bulletRect.intersects(qubeTower.getBounds());
    }



    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > LIFETIME;
    }
}
