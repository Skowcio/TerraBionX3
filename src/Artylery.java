import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
// soldier uzywa bullet do strzelania


public class Artylery implements Serializable {
    private int x, y;
    private boolean selected;
    private Point target;
    private final int range = 440;
    private int width = 40, height = 40;
    private final int shootCooldown = 4500; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private int health = 10;
    private long lastShotTime = 0; // Czas ostatniego strzału
    private static int MAX_ARTYLERYS = 8;
    private static int totalArtys = 0;

    public Artylery(int x, int y) {
        if (totalArtys >= MAX_ARTYLERYS) {
            throw new IllegalStateException("Osiągnięto limit " + MAX_ARTYLERYS + " artylerys!");
        }
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
        totalArtys++; // ← dodaj to
    }
    public static void decreaseArtysCount() {
        if (totalArtys > 0) {
            totalArtys--;
        }
    }

    public static int getTotalArtys() {
        return totalArtys;
    }
    public static int getMaxArtylerys() {
        return MAX_ARTYLERYS;
    }
    public static void resetArtysCount() {
        totalArtys = 0;
        MAX_ARTYLERYS = 8; // reset do domyślnego limitu
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
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

    public boolean takeDamage() {
        health--;
        return health <= 0;
    }

    public void destroy() {
        health = 0;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public boolean isInRange(Enemy enemy) {
        int dx = enemy.getX() - x;
        int dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(Hive hive){
        int dx = hive.getX() - x;
        int dy = hive.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyToo enemyToos) {
        int dx = enemyToos.getX() - x;
        int dy = enemyToos.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(EnemyShooter enemyShooter) {
        int dx = enemyShooter.getX() - x;
        int dy = enemyShooter.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public void shoot(Graphics g, ArrayList<ArtBullet> artBullets, ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters) {
        long currentTime = System.currentTimeMillis();

        // Jeśli aktualny cel został zniszczony lub jest poza zasięgiem, wybierz nowy
        if (currentTarget == null ||
                (currentTarget instanceof Enemy && !enemies.contains(currentTarget)) ||
                (currentTarget instanceof EnemyToo && !enemyToos.contains(currentTarget)) ||
                (currentTarget instanceof EnemyShooter && !enemyShooters.contains(currentTarget)) ||
                (currentTarget instanceof Hive && !hives.contains(currentTarget)) ||
                !(currentTarget instanceof Enemy enemy && isInRange(enemy)) &&
                        !(currentTarget instanceof EnemyToo enemyToo && isInRange(enemyToo)) &&
                        !(currentTarget instanceof EnemyShooter enemyShooter && isInRange(enemyShooter)) &&
                        !(currentTarget instanceof EnemyToo hive && isInRange(hive)))
        {
            chooseTarget(enemies, enemyToos, hives, enemyShooters); // Wybierz nowy cel
        }

        // Jeśli mamy ważny cel, strzelaj
        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (currentTarget instanceof Enemy enemy) {
                if (isInRange(enemy)) {
                    artBullets.add(new ArtBullet(x + 15, y + 15, enemy.getX() + 15, enemy.getY() + 15));
                    lastShotTime = currentTime;
                }
            } else if (currentTarget instanceof EnemyToo enemyToo) {
                if (isInRange(enemyToo)) {
                    artBullets.add(new ArtBullet(x + 15, y + 15, enemyToo.getX() + 15, enemyToo.getY() + 15));
                    lastShotTime = currentTime;
                }
            }

        else if (currentTarget instanceof EnemyShooter enemyShooter) {
            if (isInRange(enemyShooter)) {
                artBullets.add(new ArtBullet(x + 15, y + 15, enemyShooter.getX() + 15, enemyShooter.getY() + 15));
                lastShotTime = currentTime;
            }
        }
            else if (currentTarget instanceof Hive hive) {
                if (isInRange(hive)) {
                    artBullets.add(new ArtBullet(x + 15, y + 15, hive.getX() + 15, hive.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
        }
        }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters) {
        currentTarget = null;

        // Szukaj najbliższego Enemy w zasięgu
        for (Enemy enemy : enemies) {
            if (isInRange(enemy)) {
                currentTarget = enemy;
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
        for (EnemyShooter enemyShooter : enemyShooters) {
            if (isInRange(enemyShooter)) {
                currentTarget = enemyShooter;
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
        g.setColor(new Color(65, 25, 139));
        g.fillRect(x, y, 42, 42);
        g.drawString("Ar", x + width / 2 - 5, y + height / 2 + 5);
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, 45, 45);
        }
        int maxHealth = 10; // Maksymalne zdrowie
        int healthBarWidth = 40; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.BLACK);
    }
}
