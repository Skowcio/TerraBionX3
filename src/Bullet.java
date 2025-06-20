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

        playShootSound(); // 🎯 Tu dźwięk!
    }
    private void playShootSound() {
        try {
            File soundFile = new File("F:\\projekty JAVA\\TerraBionX3\\src\\shoot\\shoot2.wav");
            if (!soundFile.exists()) {
                System.err.println("Nie znaleziono pliku dźwięku: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("Błąd przy odtwarzaniu dźwięku:");
            e.printStackTrace();
        }
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 5, 5); // Pocisk o rozmiarze 5x5
    }

    public boolean checkCollision(Enemy enemy) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 30, 30);
        return bulletRect.intersects(enemyRect);
    }

    public boolean checkCollision(Hive hive) {
        Rectangle hiveRect = new Rectangle(x, y, 5, 5);
        Rectangle bulletRect = new Rectangle(hive.getX(), hive.getY(), 40, 40);
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

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > LIFETIME;
    }
}
