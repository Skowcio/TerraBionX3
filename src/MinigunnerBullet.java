import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.FloatControl;

public class MinigunnerBullet {
    private int x, y;
    private int dx, dy;
    private final int speed = 30;
    private final long creationTime;
    private static final long LIFETIME = 1100;
    private static final Random random = new Random();
    private static final int SPREAD_ANGLE = 8; // rozrzut w stopniach

    public MinigunnerBullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.creationTime = System.currentTimeMillis();

        // Oblicz kierunek z rozrzutem
        double angleToTarget = Math.atan2(targetY - y, targetX - x);
        double spread = Math.toRadians(random.nextInt(SPREAD_ANGLE * 2 + 1) - SPREAD_ANGLE);
        double finalAngle = angleToTarget + spread;

        this.dx = (int) (speed * Math.cos(finalAngle));
        this.dy = (int) (speed * Math.sin(finalAngle));

    }
    public MinigunnerBullet(int x, int y, int targetX, int targetY,
                            int cameraX, int cameraY, int screenWidth, int screenHeight) {
        this(x, y, targetX, targetY);
        playShootSound(cameraX, cameraY, screenWidth, screenHeight); // ZAWSZE graj dźwięk, ale dynamicznie
    }

    private void playShootSound(int cameraX, int cameraY, int screenWidth, int screenHeight) {
        try {
            File soundFile = new File("F:\\projekty JAVA\\TerraBionX3\\src\\shoot\\shoot5.wav");
            if (!soundFile.exists()) {
                System.err.println("Nie znaleziono pliku dźwięku: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // 🔹 Oblicz dystans do środka widoku
            int viewCenterX = cameraX + screenWidth / 2;
            int viewCenterY = cameraY + screenHeight / 2;
            double dx = this.x - viewCenterX;
            double dy = this.y - viewCenterY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // 🔹 Przelicz dystans na skalę głośności
            float maxDistance = 1800f; // dystans, po którym już nic nie słychać - jak daleko slychac
            float volume = (float) Math.pow(Math.max(0f, 1.0f - distance / maxDistance), 0.7); // wolniejsze ściszanie


            // 🔹 Zmień głośność
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (20f * Math.log10(Math.max(0.01, volume))); // dB logarytmiczny
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
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 5, 5); // Rozmiar pocisku
    }

    public boolean checkCollision(Enemy enemy) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 30, 30);
        return bulletRect.intersects(enemyRect);
    }

    public boolean checkCollision(EnemyToo enemyToo) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        return bulletRect.intersects(enemyToo.getBounds());
    }

    public boolean checkCollision(Hive hive) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle hiveRect = new Rectangle(hive.getX(), hive.getY(), 40, 40);
        return bulletRect.intersects(hiveRect);
    }

    public boolean checkCollision(EnemyShooter shooter) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle shooterRect = new Rectangle(shooter.getX(), shooter.getY(), 40, 40);
        return bulletRect.intersects(shooterRect);
    }

    public boolean checkCollision(EnemyHunter hunter) {
        Rectangle bulletRect = new Rectangle(x, y, 5, 5);
        Rectangle hunterRect = new Rectangle(hunter.getX(), hunter.getY(), 40, 40);
        return bulletRect.intersects(hunterRect);
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > LIFETIME;
    }
}
