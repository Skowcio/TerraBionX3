import java.awt.*;
import java.util.Random;

public class MinigunnerBullet {
    private int x, y;
    private int dx, dy; // Przemieszczenie w poziomie i pionie
    private final int speed = 30;
    private final long creationTime; // Czas utworzenia pocisku
    private static final long LIFETIME = 1100; // Czas życia pocisku w milisekundach
    private static final Random random = new Random();
    private static final int SPREAD_ANGLE = 8; // Rozrzut w stopniach

    public MinigunnerBullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.creationTime = System.currentTimeMillis();

        // Obliczamy wektory prędkości w kierunku celu z rozrzutem
        double angleToTarget = Math.atan2(targetY - y, targetX - x); // Kąt do celu
        double spread = Math.toRadians(random.nextInt(SPREAD_ANGLE * 4 + 1) - SPREAD_ANGLE); // Losowy kąt rozrzutu
        double finalAngle = angleToTarget + spread; // Ostateczny kąt z rozrzutem

        // Przeliczamy na wektory prędkości
        this.dx = (int) (speed * Math.cos(finalAngle));
        this.dy = (int) (speed * Math.sin(finalAngle));
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 5, 5); // Pocisk o rozmiarze 5x5
    }

    public boolean checkCollision(Enemy enemy) {
        Rectangle bulletRect = new Rectangle(x, y, 2, 2);
        Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), 30, 30);
        return bulletRect.intersects(enemyRect);
    }

    public boolean checkCollision(Hive hive) {
        Rectangle hiveRect = new Rectangle(x, y, 2, 2);
        Rectangle bulletRect = new Rectangle(hive.getX(), hive.getY(), 40, 40);
        return hiveRect.intersects(bulletRect);
    }

    public boolean checkCollision(EnemyShooter enemyShooter) {
        Rectangle enemyShooterRect = new Rectangle(x, y, 2, 2);
        Rectangle bulletRect = new Rectangle(enemyShooter.getX(), enemyShooter.getY(), 40, 40);
        return enemyShooterRect.intersects(bulletRect);
    }

    public boolean checkCollision(EnemyHunter enemyHunter) {
        Rectangle enemyHunterRect = new Rectangle(x, y, 2, 2);
        Rectangle bulletRect = new Rectangle(enemyHunter.getX(), enemyHunter.getY(), 40, 40);
        return enemyHunterRect.intersects(bulletRect);
    }

    public boolean checkCollision(EnemyToo enemyToo) {
        Rectangle bulletRect = new Rectangle(x, y, 2, 2);
        return bulletRect.intersects(enemyToo.getBounds());
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > LIFETIME;
    }
}
