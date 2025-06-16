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
    private int width = 42, height = 34;

    private Point target;
    private final int range = 180;
    private final int shootCooldown = 500; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0; // Czas ostatniego strzału
    private Direction direction; // Kierunek ruchu
    private String currentDirection = "down"; // Domyślny kierunek

    // Obrazy dla każdego kierunku
    private Image imgUp, imgDown, imgLeft, imgRight, imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;


    public Soldier(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
        this.direction = Direction.RIGHT; // Domyślny kierunek

        // Ładowanie grafik
        try {
            imgUp = ImageIO.read(getClass().getResource("APC/APC3.png"));
            imgDown = ImageIO.read(getClass().getResource("APC/APC2.png"));
            imgLeft = ImageIO.read(getClass().getResource("APC/APC4.png"));
            imgRight = ImageIO.read(getClass().getResource("APC/APC1.png"));
// tu sa te ukosne
            imgUpLeft = ImageIO.read(getClass().getResource("/APC/APCupleft.png"));
            imgUpRight = ImageIO.read(getClass().getResource("/APC/APCupright.png"));
            imgDownLeft = ImageIO.read(getClass().getResource("/APC/APCdownleft.png"));
            imgDownRight = ImageIO.read(getClass().getResource("/APC/APCdownright.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
    }

    public String getCurrentDirection() {
        return currentDirection;
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
    public void setCurrentDirection(String direction) {
        this.currentDirection = direction;
    }// to jest kierunek Soldiera po lodagame - z tego co czaje?

    public void setPosition(int x, int y, ArrayList<PowerPlant> powerPlants, ArrayList<Baracks> baracks, ArrayList<Soldier> soldiers, ArrayList<Hive> hives, ArrayList<BattleVehicle> battleVehicles) {
        for (PowerPlant powerPlant : powerPlants) {
            // Jeśli docelowa pozycja koliduje z elektrownią, nie wykonuj ruchu
            if (isCollidingWithPowerPlant(powerPlant, x, y)) {
                return; // Nie wykonuj ruchu
            }
        }
        for (Soldier soldier : soldiers){
            if (isCollidingWithSoldier(soldier, x, y)){
                return;
            }
        }
        for (Baracks barack : baracks){
            if (isCollidingWithBarack(barack, x, y)){
                return;
            }
        }
        for (Hive hive : hives) {
            if(isCollidingWithHive(hive, x, y)){
                return;
            }
        }
        for (BattleVehicle battleVehicle : battleVehicles) {
            if (isCollidingWithBattleV(battleVehicle, x, y)){
                return;
            }
        }
        if (x > this.x) {
            direction = Direction.RIGHT;
        }
        else if (x < this.x) {
            direction = Direction.LEFT;
        } else if (y > this.y) {
            direction = Direction.DOWN;
        } else if (y < this.y) {
            direction = Direction.UP;
        }

        this.x = x;
        this.y = y;
    }

    public void updateDirection(Point delta) {
        int dx = delta.x;
        int dy = delta.y;

        if (dx > 0 && dy > 0) {
            currentDirection = "downRight";
        } else if (dx > 0 && dy < 0) {
            currentDirection = "upRight";
        } else if (dx < 0 && dy > 0) {
            currentDirection = "downLeft";
        } else if (dx < 0 && dy < 0) {
            currentDirection = "upLeft";
        } else if (dx > 0) {
            currentDirection = "right";
        } else if (dx < 0) {
            currentDirection = "left";
        } else if (dy > 0) {
            currentDirection = "down";
        } else if (dy < 0) {
            currentDirection = "up";
        }
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
    private boolean isCollidingWithPowerPlant(PowerPlant powerPlant, int targetX, int targetY) {
        // Tworzymy prostokąt reprezentujący nową pozycję żołnierza
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);

        // Pobieramy granice elektrowni
        Rectangle powerPlantBounds = powerPlant.getBounds();

        // Sprawdzamy, czy nowa pozycja żołnierza nachodzi na elektrownię
        return targetBounds.intersects(powerPlantBounds);
    }
    private boolean isCollidingWithSoldier(Soldier soldier, int targetX, int targetY) {
        if (this == soldier) { //
            return false; // Ignoruj kolizję z samym sobą
        }
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle soldierBounds = soldier.getBounds();
        return targetBounds.intersects(soldierBounds);
    }
    private  boolean isCollidingWithHive(Hive hive, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle hiveBounds = hive.getBounds();
        return targetBounds.intersects(hiveBounds);
    }
    private boolean isCollidingWithBarack(Baracks barack, int targetX, int targetY) {

        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle barackBounds = barack.getBounds();
        return targetBounds.intersects(barackBounds);
    }
    private boolean isCollidingWithBattleV(BattleVehicle battleVehicle, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle battleBounds = battleVehicle.getBounds();
        return targetBounds.intersects(battleBounds);
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
        Image imgToDraw = switch (currentDirection) {
            case "up" -> imgUp;
            case "down" -> imgDown;
            case "left" -> imgLeft;
            case "right" -> imgRight;
            case "upLeft" -> imgUpLeft;
            case "upRight" -> imgUpRight;
            case "downLeft" -> imgDownLeft;
            case "downRight" -> imgDownRight;
            default -> imgDown; // Domyślny obraz
        };

        g.drawImage(imgToDraw, x, y, width, height, null);

        if (selected) {
            g.setColor(Color.WHITE);
            g.drawRect(x - 2, y - 2, 44, 36);
        }
    }

    // Enum dla kierunków
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}