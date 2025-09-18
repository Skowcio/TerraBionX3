import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Image;        // Do reprezentacji obrazów
import java.io.IOException;   // Do obsługi wyjątków podczas ładowania obrazów
// soldier uzywa bullet do strzelania


public class Soldier {
    private int x, y;
    private boolean selected;
    private int width = 50, height = 50;
    private int health = 5;
    private boolean dead = false;
    private Point target;
    private final int range = 180;
    private final int shootCooldown = 850;
    private Object currentTarget;
    private long lastShotTime = 0;

    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.003;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;

    // --- kierunki ---
    private Image[] directionSprites = new Image[16]; // 16 sprite’ów co 22,5°
    private int currentDirectionIndex = 0; // 0–15




    public Soldier(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;

        // Ładowanie grafik – nazwy plików: APC0.png ... APC15.png
        try {
            for (int i = 0; i < 16; i++) {
                directionSprites[i] = ImageIO.read(getClass().getResource("/jet/jet" + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
    }

    public int getCurrentDirection() {
        return currentDirectionIndex;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean takeDamage() {
        health--;
        if (health <= 0) {
            markAsDead();
            return true;
        }
        return false;
    }
    public boolean isDead() {
        return dead;
    }

    public void markAsDead() {
        this.dead = true;
    }

    // Getter pozycji jako obiekt Point
    public Point getPosition() {
        return new Point(x, y);
    }

    //    public void setPosition(int x, int y, ArrayList<PowerPlant> powerPlants) {
//        for (PowerPlant powerPlant : powerPlants) {
//            // Jeśli docelowa pozycja koliduje z elektrownią, nie wykonuj ruchu
//            if (isCollidingWithPowerPlant(powerPlant, x, y)) {
//                return; // Nie wykonuj ruchu
//            }
//        }
//
//        if (x > this.x) {
//            direction = Direction.RIGHT;
//        }
//        else if (x < this.x) {
//            direction = Direction.LEFT;
//        } else if (y > this.y) {
//            direction = Direction.DOWN;
//        } else if (y < this.y) {
//            direction = Direction.UP;
//        }
//
//        this.x = x;
//        this.y = y;
//    }
    public void setCurrentDirection(int directionIndex) {
        // normalizacja do 0..15
        directionIndex = ((directionIndex % 16) + 16) % 16;
        this.currentDirectionIndex = directionIndex;
    }

    // to jest kierunek Soldiera po lodagame - z tego co czaje?

    public void setPosition(int x, int y,
                            ArrayList<PowerPlant> powerPlants,
                            ArrayList<Baracks> baracks,
                            ArrayList<Soldier> soldiers,
                            ArrayList<Hive> hives,
                            ArrayList<BattleVehicle> battleVehicles) {
        // Kolizje (zostawiłem tak jak było)
//        for (PowerPlant p : powerPlants) if (isCollidingWithPowerPlant(p, x, y)) return;
        for (Soldier s : soldiers) if (isCollidingWithSoldier(s, x, y)) return;
//        for (Baracks b : baracks) if (isCollidingWithBarack(b, x, y)) return;
        for (Hive h : hives) if (isCollidingWithHive(h, x, y)) return;
//        for (BattleVehicle bv : battleVehicles) if (isCollidingWithBattleV(bv, x, y)) return;

        this.x = x;
        this.y = y;
    }

    public void updateDirection(Point delta) {
        double angle = Math.atan2(delta.y, delta.x); // -π..π
        angle = Math.toDegrees(angle); // na stopnie
        if (angle < 0) angle += 360;   // 0–360
        currentDirectionIndex = (int)Math.round(angle / 22.5) % 16;
    }

    public void updateFly(long deltaTime) {
        // 🔁 Aktualizacja efektu "unoszenia się"
        hoverTime += deltaTime;
        hoverOffset = Math.sin(hoverTime * hoverSpeed) * hoverAmplitude;

    }



    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean isSelected() { return selected; }
    private boolean isCollidingWithPowerPlant(PowerPlant powerPlant, int targetX, int targetY) {
        // Tworzymy prostokąt reprezentujący nową pozycję żołnierza
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);

        // Pobieramy granice elektrowni
        Rectangle powerPlantBounds = powerPlant.getBounds();

        // Sprawdzamy, czy nowa pozycja żołnierza nachodzi na elektrownię
        return targetBounds.intersects(powerPlantBounds);
    }
    private boolean isCollidingWithSoldier(Soldier soldier, int targetX, int targetY) {
        if (this == soldier) return false; // ignoruj kolizję z samym sobą

        int allowedOverlap = 10; // maksymalna liczba pikseli, na którą mogą nachodzić

        // Tworzymy prostokąty kolizji, zmniejszając je o allowedOverlap
        Rectangle targetBounds = new Rectangle(
                targetX + allowedOverlap,
                targetY + allowedOverlap,
                width - 2 * allowedOverlap,
                height - 2 * allowedOverlap
        );

        Rectangle soldierBounds = new Rectangle(
                soldier.getX() + allowedOverlap,
                soldier.getY() + allowedOverlap,
                soldier.getWidth() - 2 * allowedOverlap,
                soldier.getHeight() - 2 * allowedOverlap
        );

        return targetBounds.intersects(soldierBounds);
    }
    private  boolean isCollidingWithHive(Hive hive, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle hiveBounds = hive.getBounds();
        return targetBounds.intersects(hiveBounds);
    }
//    private boolean isCollidingWithBarack(Baracks barack, int targetX, int targetY) {
//
//        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
//        Rectangle barackBounds = barack.getBounds();
//        return targetBounds.intersects(barackBounds);
//    }
//    private boolean isCollidingWithBattleV(BattleVehicle battleVehicle, int targetX, int targetY){
//        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
//        Rectangle battleBounds = battleVehicle.getBounds();
//        return targetBounds.intersects(battleBounds);
//    }



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

    public void shoot(
            Graphics g,
            ArrayList<Bullet> Bullets,
            ArrayList<Enemy> enemies,
            ArrayList<EnemyToo> enemyToos,
            ArrayList<Hive> hives,
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
        if (currentTarget instanceof EnemyShooter es && !enemyShooters.contains(es)) outOfRange = true;
        if (currentTarget instanceof EnemyHunter eh && !enemyHunters.contains(eh)) outOfRange = true;

        boolean notInRange =
                !(currentTarget instanceof Enemy e && isInRange(e)) &&
                        !(currentTarget instanceof EnemyToo et && isInRange(et)) &&
                        !(currentTarget instanceof Hive h && isInRange(h)) &&
                        !(currentTarget instanceof EnemyShooter es && isInRange(es)) &&
                        !(currentTarget instanceof EnemyHunter eh && isInRange(eh));

        if (currentTarget == null || outOfRange || notInRange) {
            chooseTarget(enemies, enemyToos, hives, enemyShooters, enemyHunters);
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
            } else if (currentTarget instanceof EnemyShooter es && isInRange(es)) {
                Bullets.add(new Bullet(startX, startY, es.getX() + 15, es.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyHunter eh && isInRange(eh)) {
                Bullets.add(new Bullet(startX, startY, eh.getX() + 15, eh.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }

            lastShotTime = currentTime;
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

        // ✨ przesunięcie w osi Y o hoverOffset
        int drawX = x;
        int drawY = (int)(y + hoverOffset);

        // Rysowanie sprite'a
        Image imgToDraw = directionSprites[currentDirectionIndex];
        if (imgToDraw != null) {
            g2d.drawImage(imgToDraw, drawX, drawY, width, height, null);
        } else {
            // fallback — obrazek niezaładowany
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(drawX, drawY, width, height);
        }

        // Obrys przy zaznaczeniu
        if (selected) {
            g2d.setColor(Color.WHITE);
            g2d.drawRect(drawX - 2, drawY - 2, width + 4, height + 4);
        }

        // Pasek życia (nad sprite'em, unoszony razem z nim)
        g2d.setColor(Color.GREEN);
        int maxHealth = 5;
        int healthBarWidth = 50;
        int currentHealthWidth = (int)((health / (double)maxHealth) * healthBarWidth);
        g2d.fillRect(drawX, drawY - 5, currentHealthWidth, 3);
    }
}



