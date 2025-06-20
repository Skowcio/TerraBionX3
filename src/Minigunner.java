import java.awt.*;
import java.util.ArrayList;

public class Minigunner {
    private int x, y;
    private boolean selected;
    private Point target;
    private final int range = 230;
    private int width = 20, height = 20;
    private final int shootCooldown = 150;
    private Object currentTarget;
    private long lastShotTime = 0;

    public Minigunner(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Point getPosition() { return new Point(x, y); }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public Point getTarget() { return target; }
    public void setTarget(Point target) { this.target = target; }

    public void setPosition(int x, int y,
                            ArrayList<PowerPlant> powerPlants,
                            ArrayList<Baracks> baracks,
                            ArrayList<Soldier> soldiers,
                            ArrayList<Hive> hives,
                            ArrayList<BattleVehicle> battleVehicles) {

        Rectangle targetBounds = new Rectangle(x, y, width, height);

        for (PowerPlant p : powerPlants)
            if (targetBounds.intersects(p.getBounds())) return;

        for (Baracks b : baracks)
            if (targetBounds.intersects(b.getBounds())) return;

        for (Soldier s : soldiers)
            if (targetBounds.intersects(s.getBounds())) return;

        for (BattleVehicle v : battleVehicles)
            if (targetBounds.intersects(v.getBounds())) return;

        for (Hive h : hives)
            if (targetBounds.intersects(h.getBounds())) return;

        this.x = x;
        this.y = y;
    }

    public boolean isInRange(Enemy enemy) {
        int dx = enemy.getX() - x;
        int dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyShooter es) {
        int dx = es.getX() - x;
        int dy = es.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(Hive hive) {
        int dx = hive.getX() - x;
        int dy = hive.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyHunter eh) {
        int dx = eh.getX() - x;
        int dy = eh.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyToo et) {
        int dx = et.getX() - x;
        int dy = et.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public void shoot(
            Graphics g,
            ArrayList<MinigunnerBullet> minigunnerBullets,
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
                minigunnerBullets.add(new MinigunnerBullet(startX, startY, e.getX() + 15, e.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyToo et && isInRange(et)) {
                minigunnerBullets.add(new MinigunnerBullet(startX, startY, et.getX() + 15, et.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof Hive h && isInRange(h)) {
                minigunnerBullets.add(new MinigunnerBullet(startX, startY, h.getX() + 15, h.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyShooter es && isInRange(es)) {
                minigunnerBullets.add(new MinigunnerBullet(startX, startY, es.getX() + 15, es.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyHunter eh && isInRange(eh)) {
                minigunnerBullets.add(new MinigunnerBullet(startX, startY, eh.getX() + 15, eh.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }

            lastShotTime = currentTime;
        }
    }

    private void chooseTarget(
            ArrayList<Enemy> enemies,
            ArrayList<EnemyToo> enemyToos,
            ArrayList<Hive> hives,
            ArrayList<EnemyShooter> enemyShooters,
            ArrayList<EnemyHunter> enemyHunters
    ) {
        currentTarget = null;

        for (Enemy e : enemies) if (isInRange(e)) { currentTarget = e; return; }
        for (EnemyShooter es : enemyShooters) if (isInRange(es)) { currentTarget = es; return; }
        for (EnemyHunter eh : enemyHunters) if (isInRange(eh)) { currentTarget = eh; return; }
        for (EnemyToo et : enemyToos) if (isInRange(et)) { currentTarget = et; return; }
        for (Hive h : hives) if (isInRange(h)) { currentTarget = h; return; }
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 225, 255));
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(Color.WHITE);
            g.drawRect(x - 2, y - 2, width + 5, height + 5);
        }
    }
}
