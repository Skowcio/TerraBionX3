import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BuilderVehicle {
    private int x, y;
    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.005;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;     // Jak bardzo w górę/dół się unosi
    private int width = 45, height = 45;
    private int speed = 2;
    private boolean selected;
    private BufferedImage vehicleImage;
    private double vehicleAngle; // Kąt obrotu pojazdu
    private double targetVehicleAngle; // Docelowy kąt obrotu podwozia
    private final double rotationTime = 800; // Czas obrotu w milisekundach (0.8 sekundy)
    private double rotationStartTime = -1;  // Czas rozpoczęcia obrotu
    private int health = 5;
    private Point target;

    private final int range = 180;
    private final int shootCooldown = 500; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0; // Czas ostatniego strzału

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Hive zostało zniszczone
    }

    public BuilderVehicle(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
        try {
            vehicleImage = ImageIO.read(getClass().getResource("basedrone/basedrone.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Getter pozycji jako obiekt Point
    public Point getPosition() {
        return new Point(x, y);
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        if (target != null) {
            this.target = new Point(target.x - width / 2, target.y - height / 2);
        } else {
            this.target = null;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isInRange(Enemy enemy) {
        int dx = enemy.getX() - x;
        int dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyShooter enemyShooter) {
        int dx = enemyShooter.getX() - x;
        int dy = enemyShooter.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public void update(long deltaTime) {
        // 🔁 Aktualizacja efektu "unoszenia się"
        hoverTime += deltaTime;
        hoverOffset = Math.sin(hoverTime * hoverSpeed) * hoverAmplitude;

        // 🔁 Aktualizacja obrotu
        if (target != null) {
            rotateVehicleTowardsTarget(deltaTime);
        }
    }

    // Tu PRACUJ NAD PLYNNYM RUCHEM podwozia
    private void rotateVehicleTowardsTarget(long deltaTime) {
        int dx = target.x - (x + width / 2);
        int dy = target.y - (y + height / 2);
        targetVehicleAngle = Math.atan2(dy, dx);

        if (rotationStartTime == -1) {
            rotationStartTime = System.currentTimeMillis(); // Startujemy obrót
        }

        double elapsedTime = System.currentTimeMillis() - rotationStartTime;
        double progress = Math.min(elapsedTime / rotationTime, 1.0); // 0.0 -> 1.0 (w ciągu 0.8s)

        // Obliczamy płynny obrót pojazdu zamiast nagłego skoku
        vehicleAngle = interpolateAngle(vehicleAngle, targetVehicleAngle, progress);

        if (progress >= 1.0) {
            rotationStartTime = -1; // Resetujemy licznik obrotu po zakończeniu
        }
    }

    // Funkcja do płynnej interpolacji kątów (uwzględnia przejścia przez 0°/360°)
    private double interpolateAngle(double from, double to, double progress) {
        double difference = to - from;

        if (difference > Math.PI) {
            difference -= 2 * Math.PI;
        } else if (difference < -Math.PI) {
            difference += 2 * Math.PI;
        }

        return from + difference * progress;
    }
    public boolean isInRange(Hive hive){
        int dx = hive.getX() - x;
        int dy = hive.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(EnemyHunter enemyHunters) {
        int dx = enemyHunters.getX() - x;
        int dy = enemyHunters.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyToo enemyToos) {
        int dx = enemyToos.getX() - x;
        int dy = enemyToos.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public void shoot(Graphics g, ArrayList<Bullet> bullets, ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
        long currentTime = System.currentTimeMillis();

        // Jeśli aktualny cel został zniszczony lub jest poza zasięgiem, wybierz nowy
        if (currentTarget == null ||
                (currentTarget instanceof Enemy && !enemies.contains(currentTarget)) ||
                (currentTarget instanceof EnemyToo && !enemyToos.contains(currentTarget)) ||
                (currentTarget instanceof Hive && !hives.contains(currentTarget)) ||
                (currentTarget instanceof EnemyShooter && !enemyShooters.contains(currentTarget)) ||
                (currentTarget instanceof EnemyHunter && !enemyHunters.contains(currentTarget)) ||

                !(currentTarget instanceof Enemy enemy && isInRange(enemy)) &&
                        !(currentTarget instanceof EnemyToo enemyToo && isInRange(enemyToo)) &&
                        !(currentTarget instanceof EnemyShooter enemyShooter && isInRange(enemyShooter)) &&
                        !(currentTarget instanceof EnemyHunter enemyHunter && isInRange(enemyHunter)) &&
                        !(currentTarget instanceof Hive hive && isInRange(hive)))
        {
            chooseTarget(enemies, enemyToos, hives, enemyShooters, enemyHunters); // Wybierz nowy cel
        }

        // Jeśli mamy ważny cel, strzelaj
        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (currentTarget instanceof Enemy enemy) {
                if (isInRange(enemy)) {
                    bullets.add(new Bullet(x + 15, y + 15, enemy.getX() + 15, enemy.getY() + 15));
                    lastShotTime = currentTime;
                }
            } else if (currentTarget instanceof EnemyToo enemyToo) {
                if (isInRange(enemyToo)) {
                    bullets.add(new Bullet(x + 15, y + 15, enemyToo.getX() + 15, enemyToo.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof Hive hive) {
                if (isInRange(hive)) {
                    bullets.add(new Bullet(x + 15, y + 15, hive.getX() + 15, hive.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof EnemyHunter enemyHunter) {
                if (isInRange(enemyHunter)) {
                    bullets.add(new Bullet(x + 15, y + 15, enemyHunter.getX() + 15, enemyHunter.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof EnemyShooter enemyShooter) {
                if (isInRange(enemyShooter)) {
                    bullets.add(new Bullet(x + 15, y + 15, enemyShooter.getX() + 15, enemyShooter.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
        }
    }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
        currentTarget = null;

        // Szukaj najbliższego Enemy w zasięgu
        for (Enemy enemy : enemies) {
            if (isInRange(enemy)) {
                currentTarget = enemy;
                return; // Znaleziono cel
            }
        }
        for (EnemyShooter enemyShooter : enemyShooters) {
            if (isInRange(enemyShooter)) {
                currentTarget = enemyShooter;
                return; // Znaleziono cel
            }
        }
        for (EnemyHunter enemyHunter : enemyHunters) {
            if (isInRange(enemyHunter)) {
                currentTarget = enemyHunter;
                return; // Znaleziono cel
            }
        }

        // Jeśli nie ma Enemy, szukaj EnemyToo
        for (EnemyToo enemyToo : enemyToos) {
            if (isInRange(enemyToo)) {
                currentTarget = enemyToo;
                return; // Znaleziono cel
            }
        }
        for (Hive hive : hives) {
            if (isInRange(hive)) {
                currentTarget = hive;
                return; // Znaleziono cel
            }
        }
    }



    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (vehicleImage != null) {
            int imageWidth = vehicleImage.getWidth();
            int imageHeight = vehicleImage.getHeight();

            double scaleX = width / (double) imageWidth;
            double scaleY = height / (double) imageHeight;

            AffineTransform transform = new AffineTransform();
            transform.translate(x + width / 2.0, y + height / 2.0 + hoverOffset); // użyj width i height
            transform.rotate(vehicleAngle);
            transform.scale(scaleX, scaleY);
            transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

            g2d.drawImage(vehicleImage, transform, null);
        }

        // Obramowanie przy zaznaczeniu
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
        }

        // Pasek zdrowia
        int maxHealth = 5;
        int healthBarWidth = 25;
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3);
    }
}
