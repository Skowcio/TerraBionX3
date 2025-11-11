

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Qube {
    private int x, y;
    private final int range = 250;
    private final int width = 35, height = 35;
    private int health = 30;
    private int speed = 3;
    private final int shootCooldown = 1100; // czas między strzałami (ms)
    private Object currentTarget;
    private long lastShotTime = 0;
    private boolean dead = false;
    private Random random = new Random();

    //  czas ostatniego wyszukiwania celu dla kazdego indywidualnie
    private long lastTargetSearchTime = 0;
    //  odstęp między wyszukiwaniami (ms)
    private static final long TARGET_SEARCH_INTERVAL = 800;

    public Qube(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public Point getPosition() { return new Point(x, y); }

    public boolean takeDamage() {
        health--;
        if (health <= 0) {
            markAsDead();
            return true;
        }
        return false;
    }

    public boolean isDead() { return dead; }
    public void markAsDead() { this.dead = true; }

    // ==============================
    // 🔹 Wyszukiwanie celu
    // ==============================
    private Object getClosestTarget(
            List<SoldierBot> soldierBots,
            List<Soldier> soldiers,
            List<Valkiria> valkirias,
            List<PowerPlant> powerPlants,
            List<BattleVehicle> battleVehicles,
            List<Factory> factories,
            List<BuilderVehicle> builderVehicles,
            List<Artylery> artyleries,
            List<Baracks> baracks,
            List<SteelMine> steelMines,
            List<Enemy> Enemys,
            List<EnemyToo> EnemyToos,
            List<EnemyShooter> EnemyShooters,
            List<EnemyBehemoth> EnemyBehemoths,
            List<Hive> Hives,
            List<HiveToo> HiveToos


    ) {
        Object closest = null;
        double minDist = Double.MAX_VALUE;

        for (Soldier s : soldiers) {
            double d = s.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = s; }
        }
        for (Valkiria v : valkirias) {
            double d = v.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = v; }
        }
        for (SoldierBot b : soldierBots) {
            double d = b.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = b; }
        }
        for (PowerPlant p : powerPlants) {
            double d = p.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = p; }
        }
        for (Factory f : factories) {
            double d = f.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = f; }
        }
        for (SteelMine s : steelMines) {
            double d = s.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = s; }
        }
        for (BuilderVehicle bv : builderVehicles) {
            double d = bv.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = bv; }
        }
        for (Baracks b : baracks) {
            double d = b.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = b; }
        }
        for (Artylery a : artyleries) {
            double d = a.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = a; }
        }
        for (BattleVehicle bv : battleVehicles) {
            double d = bv.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = bv; }
        }

        for (EnemyToo et : EnemyToos) {
            double d = et.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = et; }
        }

        for (Enemy e : Enemys) {
            double d = e.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = e; }
        }
        for (EnemyShooter es : EnemyShooters) {
            double d = es.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = es; }
        }

        for (EnemyBehemoth eb : EnemyBehemoths) {
            double d = eb.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = eb; }
        }

        for (Hive h : Hives) {
            double d = h.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = h; }
        }
        for (HiveToo ht : HiveToos) {
            double d = ht.getPosition().distance(x, y);
            if (d < minDist) { minDist = d; closest = ht; }
        }

        return closest;
    }

    // ==============================
    // 🔹 Ruch w stronę celu
    // ==============================
    public void moveTowardsTarget(Object target) {
        if (target == null) return;

        int tx = 0, ty = 0;

        if (target instanceof Soldier s) { tx = s.getX(); ty = s.getY(); }
        else if (target instanceof Valkiria v) { tx = v.getX(); ty = v.getY(); }
        else if (target instanceof SoldierBot sb) { tx = sb.getX(); ty = sb.getY(); }
        else if (target instanceof PowerPlant pp) { tx = pp.getX(); ty = pp.getY(); }
        else if (target instanceof BattleVehicle bv) { tx = bv.getX(); ty = bv.getY(); }
        else if (target instanceof Artylery a) { tx = a.getX(); ty = a.getY(); }
        else if (target instanceof Baracks b) { tx = b.getX(); ty = b.getY(); }
        else if (target instanceof Factory f) { tx = f.getX(); ty = f.getY(); }
        else if (target instanceof SteelMine sm) { tx = sm.getX(); ty = sm.getY(); }
        else if (target instanceof BuilderVehicle bb) { tx = bb.getX(); ty = bb.getY(); }
        else if (target instanceof Enemy e) { tx = e.getX(); ty = e.getY(); }
        else if (target instanceof EnemyToo et) { tx = et.getX(); ty = et.getY(); }
        else if (target instanceof EnemyShooter es) { tx = es.getX(); ty = es.getY(); }
        else if (target instanceof EnemyBehemoth eb) { tx = eb.getX(); ty = eb.getY(); }
        else if (target instanceof HiveToo ht) { tx = ht.getX(); ty = ht.getY(); }
        else if (target instanceof Hive h) { tx = h.getX(); ty = h.getY(); }


        int dx = tx - x;
        int dy = ty - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 240) {
            x += (int) (speed * dx / dist);
            y += (int) (speed * dy / dist);
        }
    }
    public void shoot(Graphics g,
                      List<QubeBullet> qubeBullets,
                      List<Soldier> soldiers,
                      List<Valkiria> valkirias,
                      List<SoldierBot> soldierBots,
                      List<BattleVehicle> battleVehicles,
                      List<Factory> factories,
                      List<SteelMine> steelMines,
                      List<PowerPlant> powerPlants,
                      List<BuilderVehicle> builderVehicles,
                      List<Artylery> artylerys,
                      List<Baracks> baracks,
                      List<EnemyToo> enemiesToo,
                      List<EnemyShooter> enemyShooters,
                      List<EnemyBehemoth> enemyBehemoths,
                      List<Hive> hives,
                      List<HiveToo> hiveToos
    ) {

        long currentTime = System.currentTimeMillis();
        if (currentTarget == null || dead) return;

        // 🔹 Pobierz współrzędne celu
        int tx = 0, ty = 0;
        if (currentTarget instanceof Soldier s) { tx = s.getX(); ty = s.getY(); }
        else if (currentTarget instanceof Valkiria v) { tx = v.getX(); ty = v.getY(); }
        else if (currentTarget instanceof SoldierBot sb) { tx = sb.getX(); ty = sb.getY(); }
        else if (currentTarget instanceof BattleVehicle bv) { tx = bv.getX(); ty = bv.getY(); }
        else if (currentTarget instanceof Factory f) { tx = f.getX(); ty = f.getY(); }
        else if (currentTarget instanceof SteelMine sm) { tx = sm.getX(); ty = sm.getY(); }
        else if (currentTarget instanceof PowerPlant pp) { tx = pp.getX(); ty = pp.getY(); }
        else if (currentTarget instanceof BuilderVehicle bld) { tx = bld.getX(); ty = bld.getY(); }
        else if (currentTarget instanceof Artylery a) { tx = a.getX(); ty = a.getY(); }
        else if (currentTarget instanceof Baracks b) { tx = b.getX(); ty = b.getY(); }
        else if (currentTarget instanceof EnemyToo et) { tx = et.getX(); ty = et.getY(); }
        else if (currentTarget instanceof EnemyShooter es) { tx = es.getX(); ty = es.getY(); }
        else if (currentTarget instanceof EnemyBehemoth eb) { tx = eb.getX(); ty = eb.getY(); }
        else if (currentTarget instanceof Hive h) { tx = h.getX(); ty = h.getY(); }
        else if (currentTarget instanceof HiveToo ht) { tx = ht.getX(); ty = ht.getY(); }

        double distance = Point.distance(x, y, tx, ty);

        // 🔹 Jeśli cel jest w zasięgu, strzelaj
        if (distance <= range && currentTime - lastShotTime >= shootCooldown) {
            // 👇 dopasowane do Twojego konstruktora (4 parametry)
            qubeBullets.add(new QubeBullet(x + width / 2, y + height / 2, tx, ty));
            lastShotTime = currentTime;
        }
    }
    // ==============================
    //  Aktualizacja logiki (co klatkę)
    // ==============================
    public void update(
            List<SoldierBot> soldierBots,
            List<Soldier> soldiers,
            List<Valkiria> valkirias,
            List<Harvester> harvesters,
            List<BuilderVehicle> builderVehicles,
            List<Artylery> artylerys,
            List<Baracks> baracks,
            List<BattleVehicle> battleVehicles,
            List<PowerPlant> powerPlants,
            List<Factory> factories,
            List<SteelMine> steelMines,
            List<Enemy> enemies,
            List<EnemyToo> enemiesToo,
            List<EnemyShooter> enemyShooters,
            List<EnemyBehemoth> enemyBehemoths,
            List<Hive> hives,
            List<HiveToo> hiveToos
    ) {
        long currentTime = System.currentTimeMillis();

        // 🔹 Szukaj celu tylko co 0.5 sekundy
        if (currentTarget == null || currentTime - lastTargetSearchTime >= TARGET_SEARCH_INTERVAL) {
            currentTarget = getClosestTarget(
                    soldierBots, soldiers, valkirias, powerPlants,
                    battleVehicles, factories, builderVehicles,
                    artylerys, baracks, steelMines, enemies, enemiesToo, enemyShooters, enemyBehemoths, hives, hiveToos
            );
            lastTargetSearchTime = currentTime;
        }

        moveTowardsTarget(currentTarget);
    }

    // ==============================
    // 🔹 Rysowanie przeciwnika
    // ==============================
    public void draw(Graphics g) {
        g.setColor(new Color(24, 64, 204)); // bordowy
        g.fillRect(x, y, width, height);

        int maxHealth = 30;
        int barWidth = 35;
        int hpWidth = (int) ((health / (double) maxHealth) * barWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, hpWidth, 3);
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, barWidth, 3);
    }
}

