import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BuilderVehicle {
    private int x, y;
    private int width = 25, height = 25;
    private int speed = 2;
    private boolean selected;
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
        this.target = target;
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
        g.setColor(Color.MAGENTA);
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, 35, 35);
        }



        // Pasek zdrowia
        int maxHealth = 5; // Maksymalne zdrowie
        int healthBarWidth = 25; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem
    }
}
