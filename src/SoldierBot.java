import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Image;        // Do reprezentacji obrazów
import java.io.IOException;   // Do obsługi wyjątków podczas ładowania obrazów
// soldierBot uzywa bullet do strzelania


public class SoldierBot {
    private int x, y;
    private final int range = 140; // Zasięg strzelania w pikselach
    private final int width = 25, height = 25;
    private int health = 3;
    private int speed = 3;
    private final int shootCooldown = 600; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0; // Czas ostatniego strzału
    private Random random = new Random();

    public SoldierBot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }
    public Point getPosition() {
        return new Point(x, y);
    }


    public boolean isDead() {
        return health <= 0; // lub inna logika zależna od Twojej gry
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
    public boolean isInRange(HiveToo hiveToo){
        int dx = hiveToo.getX() - x;
        int dy = hiveToo.getY() - y;
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

    public void shoot(
            Graphics g,
            ArrayList<Bullet> Bullets,
            ArrayList<Enemy> enemies,
            ArrayList<EnemyToo> enemyToos,
            ArrayList<Hive> hives,
            ArrayList<HiveToo> hiveToos,
            ArrayList<EnemyShooter> enemyShooters,
            ArrayList<EnemyHunter> enemyHunters,
            int cameraX, int cameraY,
            int screenWidth, int screenHeight
    ) {
        long currentTime = System.currentTimeMillis();

        boolean outOfRange = false;
        if (currentTarget instanceof Enemy e && !enemies.contains(e)) outOfRange = true;
        if (currentTarget instanceof EnemyToo et && !enemyToos.contains(et)) outOfRange = true;
        if (currentTarget instanceof Hive h && !hives.contains(h)) outOfRange = true;
        if (currentTarget instanceof HiveToo ht && !hiveToos.contains(ht)) outOfRange = true;
        if (currentTarget instanceof EnemyShooter es && !enemyShooters.contains(es)) outOfRange = true;
        if (currentTarget instanceof EnemyHunter eh && !enemyHunters.contains(eh)) outOfRange = true;

        boolean notInRange =
                !(currentTarget instanceof Enemy e && isInRange(e)) &&
                        !(currentTarget instanceof EnemyToo et && isInRange(et)) &&
                        !(currentTarget instanceof Hive h && isInRange(h)) &&
                        !(currentTarget instanceof HiveToo ht && isInRange(ht)) &&
                        !(currentTarget instanceof EnemyShooter es && isInRange(es)) &&
                        !(currentTarget instanceof EnemyHunter eh && isInRange(eh));

        if (currentTarget == null || outOfRange || notInRange) {
            chooseTarget(enemies, enemyToos, hives, hiveToos, enemyShooters, enemyHunters);
        }

        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            int startX = x + 15;
            int startY = y + 15;

            if (currentTarget instanceof Enemy e && isInRange(e)) {
                Bullets.add(new Bullet(startX, startY, e.getX() + 15, e.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyToo et && isInRange(et)) {
                Bullets.add(new Bullet(startX, startY, et.getX() + 15, et.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof Hive h && isInRange(h)) {
                Bullets.add(new Bullet(startX, startY, h.getX() + 15, h.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof HiveToo h && isInRange(h)) {
                Bullets.add(new Bullet(startX, startY, h.getX() + 15, h.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyShooter es && isInRange(es)) {
                Bullets.add(new Bullet(startX, startY, es.getX() + 15, es.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyHunter eh && isInRange(eh)) {
                Bullets.add(new Bullet(startX, startY, eh.getX() + 15, eh.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }

            lastShotTime = currentTime;
        }
    }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<HiveToo> hiveToos, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
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
        for (HiveToo hiveToo : hiveToos) {
            if (isInRange(hiveToo)) {
                currentTarget = hiveToo;
                return; // Znaleziono cel
            }
        }
    }
    public void update(List<Enemy> enemies, List<Hive> hives, List<HiveToo> hiveToos) {

        moveTowardsEnemy(enemies);
        moveTowardsHive(hives);
        moveTowardsHiveToo(hiveToos);

//        moveTowardsPowerPlant(powerPlants);
//        attackClosestSoldier(soldiers);
//        attackClosestBattleVehicle(battleVehicles);
//        moveTowardsHarvester(harvesters);
//        attackClosestHarvester(harvesters);
//        moveTowardsBuilderVehicle(builderVehicles);
//        attackClosestBuilderVehicles(builderVehicles);
//        attackClosestArtylerys(artylerys);
//        attackClosestPowerPlant(powerPlants);
//        attackClosestFactory(factorys);
//        moveTowardsFactory(factorys);

    }

    private Enemy getClosestEnemy(java.util.List<Enemy> enemies) {
        Enemy closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = enemy .getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy ;
            }
        }
        return closest;
    }
    private Hive getClosestHive(java.util.List<Hive> hives) {
        Hive closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Hive hive : hives) {
            double distance = hive.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = hive;
            }
        }
        return closest;
    }
    private HiveToo getClosestHiveToo(java.util.List<HiveToo> hiveToos) {
        HiveToo closest = null;
        double minDistance = Double.MAX_VALUE;

        for (HiveToo hiveToo : hiveToos) {
            double distance = hiveToo.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = hiveToo;
            }
        }
        return closest;
    }


    public void  moveTowardsHive(List<Hive> hives){
        Hive closestHive = getClosestHive(hives);
        if (closestHive != null) {
            int dx = closestHive.getX() - x;
            int dy = closestHive.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }
    }public void  moveTowardsHiveToo(List<HiveToo> hiveToos){
        HiveToo closestHiveToo = getClosestHiveToo(hiveToos);
        if (closestHiveToo != null) {
            int dx = closestHiveToo.getX() - x;
            int dy = closestHiveToo.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }
    }

    public void moveTowardsEnemy(List<Enemy> enemies) {
        Enemy closestEnemy = getClosestEnemy(enemies);

        if (closestEnemy != null) {
            int dx = closestEnemy.getX() - x;
            int dy = closestEnemy.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }


    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 250)); // Bordowy
        g.fillRect(x, y, width, height);

        int maxHealth = 3; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 25; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);
    }

//    public Projectile shootAtNearestSoldier(ArrayList<Soldier> soldiers) {
//        Soldier nearest = null;
//        double minDistance = Double.MAX_VALUE;
//
//        for (Soldier soldier : soldiers) {
//            double distance = Math.sqrt(Math.pow(soldier.getX() - x, 2) + Math.pow(soldier.getY() - y, 2));
//            if (distance <= range && distance < minDistance) {
//                minDistance = distance;
//                nearest = soldier;
//            }
//        }
//        if (nearest != null) {
//            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
//        }
//        return null;
//    }

}
