import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import java.util.Random;




public class QubeTower implements Serializable {
    private int x, y;
    private boolean selected;
    private Point target;
    private final int range = 440;
    private int width = 100, height = 150;
    private final int shootCooldown = 4500; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private int health = 90;
    private long lastShotTime = 0; // Czas ostatniego strzału

    private boolean dead = false;

    // ===== Pole siłowe =====
    private int shield = 20;                    // ile ma dodatkowego życia
    private final int maxShield = 20;           // maksymalna pojemność
    private long lastShieldRegenTime = 0;       // czas ostatniej regeneracji
    private final long shieldRegenInterval = 3000; // co 3 sekundy

    private BufferedImage qubeTowerImage;

    //  czas ostatniego wyszukiwania celu dla kazdego indywidualnie
    private long lastTargetSearchTime = 0;
    //  odstęp między wyszukiwaniami (ms)
    private static final long TARGET_SEARCH_INTERVAL = 800;

    private Random random = new Random();

    public QubeTower(int x, int y) {

        this.x = x;
        this.y = y;

        try {
            qubeTowerImage = ImageIO.read(getClass().getResource("Qube/qubeTower.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }

        this.target = null;

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary
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

    public void shoot(Graphics g,
                      java.util.List<QubeBullet> qubeBullets,
                      java.util.List<Soldier> soldiers,
                      java.util.List<Valkiria> valkirias,
                      java.util.List<SoldierBot> soldierBots,
                      java.util.List<BattleVehicle> battleVehicles,
                      java.util.List<Factory> factories,
                      java.util.List<SteelMine> steelMines,
                      java.util.List<PowerPlant> powerPlants,
                      java.util.List<BuilderVehicle> builderVehicles,
                      java.util.List<Artylery> artylerys,
                      java.util.List<Baracks> baracks,
                      java.util.List<EnemyToo> enemiesToo,
                      java.util.List<EnemyShooter> enemyShooters,
                      java.util.List<EnemyBehemoth> enemyBehemoths,
                      java.util.List<Hive> hives,
                      java.util.List<HiveToo> hiveToos,
                      int cameraX, int cameraY,
                      int screenWidth, int screenHeight
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
            qubeBullets.add(new QubeBullet(
                    x + width / 2,
                    y + height / 2,
                    tx,
                    ty,
                    cameraX, cameraY,
                    screenWidth, screenHeight
            ));
            lastShotTime = currentTime;
        }
    }
    // ==============================
    //  Aktualizacja logiki (co klatkę)
    // ==============================
    public void update(
            java.util.List<SoldierBot> soldierBots,
            java.util.List<Soldier> soldiers,
            java.util.List<Valkiria> valkirias,
            java.util.List<Harvester> harvesters,
            java.util.List<BuilderVehicle> builderVehicles,
            java.util.List<Artylery> artylerys,
            java.util.List<Baracks> baracks,
            java.util.List<BattleVehicle> battleVehicles,
            java.util.List<PowerPlant> powerPlants,
            java.util.List<Factory> factories,
            java.util.List<SteelMine> steelMines,
            java.util.List<Enemy> enemies,
            java.util.List<EnemyToo> enemiesToo,
            java.util.List<EnemyShooter> enemyShooters,
            java.util.List<EnemyBehemoth> enemyBehemoths,
            java.util.List<Hive> hives,
            List<HiveToo> hiveToos
    ) {

        long currentTime = System.currentTimeMillis();

        // 🔋 Regeneracja pola siłowego
        long now = System.currentTimeMillis();
        if (now - lastShieldRegenTime >= shieldRegenInterval) {
            if (shield < maxShield) {
                shield++; // regeneracja 1 punktu
            }
            lastShieldRegenTime = now;
        }
        // 🔹 Szukaj celu tylko co 0.5 sekundy
        if (currentTarget == null || currentTime - lastTargetSearchTime >= TARGET_SEARCH_INTERVAL) {
            currentTarget = getClosestTarget(
                    soldierBots, soldiers, valkirias, powerPlants,
                    battleVehicles, factories, builderVehicles,
                    artylerys, baracks, steelMines, enemies, enemiesToo, enemyShooters, enemyBehemoths, hives, hiveToos
            );
            lastTargetSearchTime = currentTime;
        }

    }



    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // ===== RYSOWANIE POJAZDU =====
        if (qubeTowerImage != null) {
            g2d.drawImage(qubeTowerImage, x, y, height, height, null);
        }

        // ===== RYSOWANIE POLA SIŁOWEGO =====
        if (shield > 0) {
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            int r = width + 22;

            // otoczka
            g2d.setColor(new Color(0, 170, 255, 80));
            g2d.fillOval(centerX - r / 2, centerY - r / 2, r, r);

            // pole siłowe
            g2d.setColor(new Color(100, 200, 255, 140));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawOval(centerX - r / 2, centerY - r / 2, r, r);

            // reset pędzla
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1f));
        }

        // ===== PASEK ŻYCIA =====
        int maxHealth = 70;
        int barWidth = 75;
        int hpWidth = (int) ((health / (double) maxHealth) * barWidth);

        // ===== PASEK TARCZY =====
        int maxShield = 20;
        int barW = 75;
        int shiledW = (int) ((shield / (double) maxShield) * barW);

        // pasek nad pojazdem (bez hover)
        int barY = y - 5;   // HP
        int barYy = y - 10; // Shield

        g.setColor(Color.GREEN);
        g.fillRect(x, barY, hpWidth, 3);

        g.setColor(Color.RED);
        g.fillRect(x, barYy, shiledW, 3);

        g.setColor(Color.BLACK);
        g.drawRect(x, barY, barWidth, 3);

        g.setColor(Color.BLACK);
        g.drawRect(x, barYy, barW, 3);
    }

}
