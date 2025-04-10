import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
// soldier uzywa bullet do strzelania, a ten minigunerbullet - pamietaj!


public class Minigunner {
    private int x, y;
    private boolean selected;
    private Point target;
    private final int range = 230;
    private int width = 20, height = 20;
    private final int shootCooldown = 150; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0; // Czas ostatniego strzału


    public Minigunner (int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
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
        for (BattleVehicle battleVehicle : battleVehicles){
            if (isCollidingWithBattleVehicle(battleVehicle, x, y)){
                return;
            }
        }
        for (Hive hive : hives) {
            if(isCollidingWithHive(hive, x, y)){
                return;
            }
        }

        this.x = x;
        this.y = y;
    }
    // tu masz metody do nie wchodzenia na dany obiekt
    private boolean isCollidingWithBarack(Baracks barack, int targetX, int targetY) {

        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle barackBounds = barack.getBounds();
        return targetBounds.intersects(barackBounds);
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
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle soldierBounds = soldier.getBounds();
        return targetBounds.intersects(soldierBounds);
    }

    private boolean isCollidingWithBattleVehicle(BattleVehicle battleVehicle, int targetX, int targetY) {
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle battleVehicleBounds = battleVehicle.getBounds();
        return targetBounds.intersects(battleVehicleBounds);
    }
    private  boolean isCollidingWithHive(Hive hive, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle hiveBounds = hive.getBounds();
        return targetBounds.intersects(hiveBounds);
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

    public void shoot(Graphics g, ArrayList<MinigunnerBullet> minigunnerBullets, ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
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
                    minigunnerBullets.add(new MinigunnerBullet(x + 15, y + 15, enemy.getX() + 15, enemy.getY() + 15));
                    lastShotTime = currentTime;
                }
            } else if (currentTarget instanceof EnemyToo enemyToo) {
                if (isInRange(enemyToo)) {
                    minigunnerBullets.add(new MinigunnerBullet(x + 15, y + 15, enemyToo.getX() + 15, enemyToo.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof Hive hive) {
                if (isInRange(hive)) {
                    minigunnerBullets.add(new MinigunnerBullet(x + 15, y + 15, hive.getX() + 15, hive.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof EnemyHunter enemyHunter) {
                if (isInRange(enemyHunter)) {
                    minigunnerBullets.add(new MinigunnerBullet(x + 15, y + 15, enemyHunter.getX() + 15, enemyHunter.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof EnemyShooter enemyShooter) {
                if (isInRange(enemyShooter)) {
                    minigunnerBullets.add(new MinigunnerBullet(x + 15, y + 15, enemyShooter.getX() + 15, enemyShooter.getY() + 15));
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
        g.setColor(new Color(0, 225, 255)); //
        g.fillRect(x, y, 20, 20);
        if (selected) {
            g.setColor(Color.WHITE);
            g.drawRect(x - 2, y - 2, 25, 25);
        }
    }
}
