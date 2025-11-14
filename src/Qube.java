

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Qube {
    private int x, y;
    private final int range = 250;
    private final int width = 70, height = 70;
    private int health = 70;
    private int speed = 3;

    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.003;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;

    private final int shootCooldown = 850; // czas między strzałami (ms)
    private Object currentTarget;
    private long lastShotTime = 0;
    private boolean dead = false;
    private Random random = new Random();

    private BufferedImage vehicleImage;

    //  czas ostatniego wyszukiwania celu dla kazdego indywidualnie
    private long lastTargetSearchTime = 0;
    //  odstęp między wyszukiwaniami (ms)
    private static final long TARGET_SEARCH_INTERVAL = 800;

    public Qube(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            vehicleImage = ImageIO.read(getClass().getResource("Qube/qube.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void updateFly(long deltaTime) {
        // 🔁 Aktualizacja efektu "unoszenia się"
        hoverTime += deltaTime;
        hoverOffset = Math.sin(hoverTime * hoverSpeed) * hoverAmplitude;

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

        if (target instanceof Soldier s) { tx = s.getX() + s.getWidth() / 2; ty = s.getY() + s.getHeight() / 2; }
        else if (target instanceof Valkiria v) { tx = v.getX() + v.getWidth() / 2; ty = v.getY() + v.getHeight() / 2; }
        else if (target instanceof SoldierBot sb) { tx = sb.getX() + sb.getWidth() / 2; ty = sb.getY() + sb.getHeight() / 2; }
        else if (target instanceof PowerPlant pp) { tx = pp.getX() + pp.getWidth() / 2; ty = pp.getY() + pp.getHeight() / 2; }
        else if (target instanceof BattleVehicle bv) { tx = bv.getX() + bv.getWidth() / 2; ty = bv.getY() + bv.getHeight() / 2; }
        else if (target instanceof Artylery a) { tx = a.getX() + a.getWidth() / 2; ty = a.getY() + a.getHeight() / 2; }
        else if (target instanceof Baracks b) { tx = b.getX() + b.getWidth() / 2; ty = b.getY() + b.getHeight() / 2; }
        else if (target instanceof Factory f) { tx = f.getX() + f.getWidth() / 2; ty = f.getY() + f.getHeight() / 2; }
        else if (target instanceof SteelMine sm) { tx = sm.getX() + sm.getWidth() / 2; ty = sm.getY() + sm.getHeight() / 2; }
        else if (target instanceof BuilderVehicle bb) { tx = bb.getX() + bb.getWidth() / 2; ty = bb.getY() + bb.getHeight() / 2; }
        else if (target instanceof Enemy e) { tx = e.getX() + e.getWidth() / 2; ty = e.getY() + e.getHeight() / 2; }
        else if (target instanceof EnemyToo et) { tx = et.getX() + et.getWidth() / 2; ty = et.getY() + et.getHeight() / 2; }
        else if (target instanceof EnemyShooter es) { tx = es.getX() + es.getWidth() / 2; ty = es.getY() + es.getHeight() / 2; }
        else if (target instanceof EnemyBehemoth eb) { tx = eb.getX() + eb.getWidth() / 2; ty = eb.getY() + eb.getHeight() / 2; }
        else if (target instanceof HiveToo ht) { tx = ht.getX() + ht.getWidth() / 2; ty = ht.getY() + ht.getHeight() / 2; }
        else if (target instanceof Hive h) { tx = h.getX() + h.getWidth() / 2; ty = h.getY() + h.getHeight() / 2; }


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
        if (currentTarget instanceof Soldier s) {
            tx = s.getX() + s.getWidth() / 2;
            ty = s.getY() + s.getHeight() / 2;
        }
        else if (currentTarget instanceof Valkiria v) {
            tx = v.getX() + v.getWidth() / 2;
            ty = v.getY() + v.getHeight() / 2;
        }
        else if (currentTarget instanceof SoldierBot sb) {
            tx = sb.getX() + sb.getWidth() / 2;
            ty = sb.getY() + sb.getHeight() / 2;
        }
        else if (currentTarget instanceof BattleVehicle bv) {
            tx = bv.getX() + bv.getWidth() / 2;
            ty = bv.getY() + bv.getHeight() / 2;
        }
        else if (currentTarget instanceof Factory f) {
            tx = f.getX() + f.getWidth() / 2;
            ty = f.getY() + f.getHeight() / 2;
        }
        else if (currentTarget instanceof SteelMine sm) {
            tx = sm.getX() + sm.getWidth() / 2;
            ty = sm.getY() + sm.getHeight() / 2;
        }
        else if (currentTarget instanceof PowerPlant pp) {
            tx = pp.getX() + pp.getWidth() / 2;
            ty = pp.getY() + pp.getHeight() / 2;
        }
        else if (currentTarget instanceof BuilderVehicle bld) {
            tx = bld.getX() + bld.getWidth() / 2;
            ty = bld.getY() + bld.getHeight() / 2;
        }
        else if (currentTarget instanceof Artylery a) {
            tx = a.getX() + a.getWidth() / 2;
            ty = a.getY() + a.getHeight() / 2;
        }
        else if (currentTarget instanceof Baracks b) {
            tx = b.getX() + b.getWidth() / 2;
            ty = b.getY() + b.getHeight() / 2;
        }
        else if (currentTarget instanceof EnemyToo et) {
            tx = et.getX() + et.getWidth() / 2;
            ty = et.getY() + et.getHeight() / 2;
        }
        else if (currentTarget instanceof EnemyShooter es) {
            tx = es.getX() + es.getWidth() / 2;
            ty = es.getY() + es.getHeight() / 2;
        }
        else if (currentTarget instanceof EnemyBehemoth eb) {
            tx = eb.getX() + eb.getWidth() / 2;
            ty = eb.getY() + eb.getHeight() / 2;
        }
        else if (currentTarget instanceof Hive h) {
            tx = h.getX() + h.getWidth() / 2;
            ty = h.getY() + h.getHeight() / 2;
        }
        else if (currentTarget instanceof HiveToo ht) {
            tx = ht.getX() + ht.getWidth() / 2;
            ty = ht.getY() + ht.getHeight() / 2;
        }


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
        Graphics2D g2d = (Graphics2D) g;

        if (vehicleImage != null) {
            int drawX = x;
            int drawY = (int) (y + hoverOffset); // ✨ unoszenie
            g2d.drawImage(vehicleImage, drawX, drawY, height, height, null);
        }

        // Pasek życia (cały zestaw przesuwany razem z hoverOffset)
        int maxHealth = 70;
        int barWidth = 75;
        int hpWidth = (int) ((health / (double) maxHealth) * barWidth);

        int barY = (int) (y + hoverOffset) - 5; // pasek razem z unoszeniem

        g.setColor(Color.GREEN);
        g.fillRect(x, barY, hpWidth, 3);

        g.setColor(Color.BLACK);
        g.drawRect(x, barY, barWidth, 3);
    }

}

