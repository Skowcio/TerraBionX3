import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;

public class SoldierBot {
    private int x, y;
    private final int range = 150; // Zasięg strzelania w pikselach
    private final int width = 25, height = 25;
    private int health = 3;
    private int speed = 4;
    private final int shootCooldown = 600; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0;
    private Random random = new Random();

    private int wanderDirX = 0;
    private int wanderDirY = 0;
    private long lastWanderDirectionChange = 0;
    private final int wanderSpeed = 2;
    private final int wanderChangeInterval = 2000; // co 2 sekundy zmiana kierunku

    private Rectangle patrolArea; // obszar działania bota

    private boolean dead = false;

    private String currentDirection = "down"; // Domyślny kierunek
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;

    public SoldierBot(int x, int y, Rectangle patrolArea) {
        this.x = x;
        this.y = y;
        this.patrolArea = patrolArea;

        try {
            imgUp = ImageIO.read(getClass().getResource("/APC/APC3.png"));
            imgDown = ImageIO.read(getClass().getResource("/APC/APC2.png"));
            imgLeft = ImageIO.read(getClass().getResource("/APC/APC4.png"));
            imgRight = ImageIO.read(getClass().getResource("/APC/APC1.png"));
            imgUpLeft = ImageIO.read(getClass().getResource("/APC/APCupleft.png"));
            imgUpRight = ImageIO.read(getClass().getResource("/APC/APCupright.png"));
            imgDownLeft = ImageIO.read(getClass().getResource("/APC/APCdownleft.png"));
            imgDownRight = ImageIO.read(getClass().getResource("/APC/APCdownright.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SoldierBot(int x, int y) {
        this(x, y, new Rectangle(x - 200, y - 200, 400, 400));
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
        if (health <= 0) {
            markAsDead();
            return true;
        }
        return false;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public boolean isDead() {
        return dead;
    }

    public void markAsDead() {
        this.dead = true;
    }

    private void updateDirection(int dx, int dy) {
        if (dx > 0 && dy > 0) currentDirection = "downRight";
        else if (dx > 0 && dy < 0) currentDirection = "upRight";
        else if (dx < 0 && dy > 0) currentDirection = "downLeft";
        else if (dx < 0 && dy < 0) currentDirection = "upLeft";
        else if (dx > 0) currentDirection = "right";
        else if (dx < 0) currentDirection = "left";
        else if (dy > 0) currentDirection = "down";
        else if (dy < 0) currentDirection = "up";
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
    private boolean isCollidingWithOtherBots(int targetX, int targetY, List<SoldierBot> allBots) {
        Rectangle futureBounds = new Rectangle(targetX, targetY, width, height);
        for (SoldierBot other : allBots) {
            if (this == other) continue; // Nie sprawdzaj samego siebie
            if (futureBounds.intersects(other.getBounds())) {
                return true;
            }
        }
        return false;
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
    public void update(List<Enemy> enemies, List<EnemyShooter> enemyShooters, List<EnemyToo> enemyToos, List<Hive> hives, List<HiveToo> hiveToos, List<SoldierBot> allBots) {
        Object target = getClosestTarget(enemies, enemyShooters, enemyToos, hives, hiveToos);

        if (target != null && patrolArea.contains(getTargetPosition(target))) {
            moveTowardsTarget(target, allBots);
        } else {
            wander(allBots);
        }
    }

    private Point getTargetPosition(Object target) {
        if (target instanceof Enemy e) return e.getPosition();
        if (target instanceof EnemyShooter es) return es.getPosition();
        if (target instanceof EnemyToo et) return et.getPosition();
        if (target instanceof Hive h) return h.getPosition();
        if (target instanceof HiveToo ht) return ht.getPosition();
        return new Point(0, 0);
    }

    public Object getClosestTarget(List<Enemy> enemies, List<EnemyShooter> enemyShooters, List<EnemyToo> enemyToos, List<Hive> hives, List<HiveToo> hiveToos) {
        Object closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double dist = enemy.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = enemy;
            }
        }
        for (EnemyShooter enemyShooter : enemyShooters){
            double dist = enemyShooter.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = enemyShooter;
            }
        }

        for (EnemyToo enemyToo : enemyToos) {
            double dist = enemyToo.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = enemyToo;
            }
        }

        for (Hive hive : hives) {
            double dist = hive.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = hive;
            }
        }

        for (HiveToo hiveToo : hiveToos) {
            double dist = hiveToo.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = hiveToo;
            }
        }

        return closest;
    }

    public void moveTowardsTarget(Object target, List<SoldierBot> allBots) {
        if (target == null) return;

        int tx = 0, ty = 0;

        if (target instanceof Enemy e) {
            tx = e.getX(); ty = e.getY();
        } else if (target instanceof EnemyShooter es) {
            tx = es.getX(); ty = es.getY();
        } else if (target instanceof Hive h) {
            tx = h.getX(); ty = h.getY();
        } else if (target instanceof HiveToo ht) {
            tx = ht.getX(); ty = ht.getY();
        } else if (target instanceof EnemyToo et) {
            tx = et.getX(); ty = et.getY();
        }

        if (!patrolArea.contains(tx, ty)) {
            return; // 👈 ignoruj cel spoza strefy
        }

        int dx = tx - x;
        int dy = ty - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // 🔽 AKTUALIZUJ KIERUNEK RUCHU
        if (distance != 0) updateDirection(dx, dy);

        // tu jest dystans na jaki się zbliży do przeciwnika
        if (distance > 100) {
            int nextX = x + (int) (speed * dx / distance);
            int nextY = y + (int) (speed * dy / distance);

            if (!isCollidingWithOtherBots(nextX, nextY, allBots)) {
                x = nextX;
                y = nextY;
            }
        }
    }

    private long lastWanderTime = 0;
    private final long wanderDelay = 500; // co 500 ms

    private void wander(List<SoldierBot> allBots) {
        long now = System.currentTimeMillis();

        // co 2 sekundy losuj nowy kierunek
        if (now - lastWanderDirectionChange > wanderChangeInterval || (wanderDirX == 0 && wanderDirY == 0)) {
            wanderDirX = random.nextInt(3) - 1; // -1, 0 lub 1
            wanderDirY = random.nextInt(3) - 1;
            lastWanderDirectionChange = now;
        }

        // 🔽 AKTUALIZUJ KIERUNEK RUCHU
        updateDirection(wanderDirX, wanderDirY);

        int nextX = x + wanderDirX * wanderSpeed;
        int nextY = y + wanderDirY * wanderSpeed;

        Rectangle nextBounds = new Rectangle(nextX, nextY, width, height);

        if (patrolArea.contains(nextBounds) && !isCollidingWithOtherBots(nextX, nextY, allBots)) {
            x = nextX;
            y = nextY;
        } else {
            // jeśli kolizja lub poza strefą, zmień kierunek przy najbliższej okazji
            lastWanderDirectionChange = 0;
        }
    }

    public void draw(Graphics g) {
        Image imgToDraw = switch (currentDirection) {
            case "up" -> imgUp;
            case "down" -> imgDown;
            case "left" -> imgLeft;
            case "right" -> imgRight;
            case "upLeft" -> imgUpLeft;
            case "upRight" -> imgUpRight;
            case "downLeft" -> imgDownLeft;
            case "downRight" -> imgDownRight;
            default -> imgDown;
        };

        if (imgToDraw != null) {
            g.drawImage(imgToDraw, x, y, width, height, null);
        }

        int maxHealth = 3;
        int healthBarWidth = 25;
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3);
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
