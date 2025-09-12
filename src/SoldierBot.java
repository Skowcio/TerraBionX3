import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;

public class SoldierBot {
    private int x, y;
    private final int range = 180; // Zasięg strzelania w pikselach
    private final int width = 50, height = 50;
    private int health = 3;
    private int speed = 5;
    private final int shootCooldown = 900; // Czas odnowienia strzału (ms)
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


    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;

    private Image dir0, dir1, dir2, dir3, dir4, dir5, dir6, dir7;
    private Image dir8, dir9, dir10, dir11, dir12, dir13, dir14, dir15;

    private int currentDirection = 0; // 0 - 15

    public SoldierBot(int x, int y, Rectangle patrolArea) {
        this.x = x;
        this.y = y;
        this.patrolArea = patrolArea;

        try {
            dir0 = ImageIO.read(getClass().getResource("/jet/jet0.png"));
            dir1 = ImageIO.read(getClass().getResource("/jet/jet15.png"));
            dir2 = ImageIO.read(getClass().getResource("/jet/jet14.png"));
            dir3 = ImageIO.read(getClass().getResource("/jet/jet13.png"));
            dir4 = ImageIO.read(getClass().getResource("/jet/jet12.png"));
            dir5 = ImageIO.read(getClass().getResource("/jet/jet11.png"));
            dir6 = ImageIO.read(getClass().getResource("/jet/jet10.png"));
            dir7 = ImageIO.read(getClass().getResource("/jet/jet9.png"));
            dir8 = ImageIO.read(getClass().getResource("/jet/jet8.png"));
            dir9 = ImageIO.read(getClass().getResource("/jet/jet7.png"));
            dir10 = ImageIO.read(getClass().getResource("/jet/jet6.png"));
            dir11 = ImageIO.read(getClass().getResource("/jet/jet5.png"));
            dir12 = ImageIO.read(getClass().getResource("/jet/jet4.png"));
            dir13 = ImageIO.read(getClass().getResource("/jet/jet3.png"));
            dir14 = ImageIO.read(getClass().getResource("/jet/jet2.png"));
            dir15 = ImageIO.read(getClass().getResource("/jet/jet1.png"));
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
        if (dx == 0 && dy == 0) return;

        double angle = Math.atan2(-dy, dx); // -dy bo Y idzie w dół
        angle = Math.toDegrees(angle);
        if (angle < 0) angle += 360;

        currentDirection = (int) Math.round(angle / 22.5) % 16;
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
        // Kolizja tylko na 2/3 szerokości i wysokości
        int collisionW = (int)(width * 0.67);   // np. 33px przy 50px
        int collisionH = (int)(height * 0.67);
        int offsetX = (width - collisionW) / 2;
        int offsetY = (height - collisionH) / 2;

        Rectangle futureBounds = new Rectangle(targetX + offsetX, targetY + offsetY, collisionW, collisionH);

        for (SoldierBot other : allBots) {
            if (this == other) continue;

            Rectangle otherBounds = new Rectangle(
                    other.x + offsetX,
                    other.y + offsetY,
                    collisionW,
                    collisionH
            );

            if (futureBounds.intersects(otherBounds)) {
                return true; // kolizja dopiero gdy przekroczą te 2/3
            }
        }
        return false;
    }

//    private boolean isCollidingWithOtherBots(int targetX, int targetY, List<SoldierBot> allBots) {
//        Rectangle futureBounds = new Rectangle(targetX, targetY, width, height);
//        for (SoldierBot other : allBots) {
//            if (this == other) continue; // Nie sprawdzaj samego siebie
//            if (futureBounds.intersects(other.getBounds())) {
//                return true;
//            }
//        }
//        return false;
//    }

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
        resolveHardOverlap(allBots);
    }

    /// /// to jest do tego by soldierBot zostal przesuniety gdy naszedl na siebie


    private void resolveHardOverlap(List<SoldierBot> allBots) {
        int collisionW = (int)(width * 0.67);   // strefa dozwolonego zachodzenia
        int collisionH = (int)(height * 0.67);
        int offsetX = (width - collisionW) / 2;
        int offsetY = (height - collisionH) / 2;

        Rectangle thisAllowed = new Rectangle(x + offsetX, y + offsetY, collisionW, collisionH);
        Rectangle thisStrict = new Rectangle(x, y, width, height); // pełny hitbox

        for (SoldierBot other : allBots) {
            if (this == other) continue;

            Rectangle otherAllowed = new Rectangle(other.x + offsetX, other.y + offsetY, collisionW, collisionH);
            Rectangle otherStrict = new Rectangle(other.x, other.y, width, height);

            // jeśli dotykają się tylko w "allowed" → ignorujemy
            if (thisAllowed.intersects(otherAllowed)) {
                // jeśli pełne hitboxy też się przecinają → to już za dużo, teleport
                if (thisStrict.intersects(otherStrict)) {
                    teleportAway(allBots);
                }
            }
        }
    }

    private void teleportAway(List<SoldierBot> allBots) {
        int offset = 50;
        int dir = random.nextInt(8);

        int nx = x;
        int ny = y;

        switch (dir) {
            case 0 -> ny -= offset;
            case 1 -> { ny -= offset; nx += offset; }
            case 2 -> nx += offset;
            case 3 -> { ny += offset; nx += offset; }
            case 4 -> ny += offset;
            case 5 -> { ny += offset; nx -= offset; }
            case 6 -> nx -= offset;
            case 7 -> { ny -= offset; nx -= offset; }
        }

        Rectangle newPos = new Rectangle(nx, ny, width, height);

        if (patrolArea.contains(newPos) && !isCollidingWithOtherBots(nx, ny, allBots)) {
            x = nx;
            y = ny;
        }
    }
//    private void resolveHardOverlap(List<SoldierBot> allBots) {
//        for (SoldierBot other : allBots) {
//            if (this == other) continue;
//            if (this.getBounds().intersects(other.getBounds())) {
//
//                int offset = 50;
//                int dir = random.nextInt(8); // 0-7 (8 kierunków)
//
//                int nx = x;
//                int ny = y;
//
//                switch (dir) {
//                    case 0 -> ny -= offset;       // góra
//                    case 1 -> { ny -= offset; nx += offset; } // góra-prawo
//                    case 2 -> nx += offset;       // prawo
//                    case 3 -> { ny += offset; nx += offset; } // dół-prawo
//                    case 4 -> ny += offset;       // dół
//                    case 5 -> { ny += offset; nx -= offset; } // dół-lewo
//                    case 6 -> nx -= offset;       // lewo
//                    case 7 -> { ny -= offset; nx -= offset; } // góra-lewo
//                }
//
//                // Jeżeli nowa pozycja jest w obszarze patrolu i nie koliduje z innymi
//                if (patrolArea.contains(new Rectangle(nx, ny, width, height)) &&
//                        !isCollidingWithOtherBots(nx, ny, allBots)) {
//                    x = nx;
//                    y = ny;
//                }
//            }
//        }
//    }



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
            case 0 -> dir0;
            case 1 -> dir1;
            case 2 -> dir2;
            case 3 -> dir3;
            case 4 -> dir4;
            case 5 -> dir5;
            case 6 -> dir6;
            case 7 -> dir7;
            case 8 -> dir8;
            case 9 -> dir9;
            case 10 -> dir10;
            case 11 -> dir11;
            case 12 -> dir12;
            case 13 -> dir13;
            case 14 -> dir14;
            case 15 -> dir15;
            default -> dir0;
        };

        if (imgToDraw != null) {
            g.drawImage(imgToDraw, x, y, width, height, null);
        }

        int maxHealth = 3;
        int healthBarWidth = 50;
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