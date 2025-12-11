import java.awt.*;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.Image;


public class EnemyToo {
    private int x, y;
    private int width = 35, height = 35;
    private int speed = 4;
    private int health = 20;
    private boolean dead = false;
    private int currentDirection = 0;

    private static Image dir0, dir1, dir2, dir3, dir4, dir5, dir6, dir7;
    private static Image dir8, dir9, dir10, dir11, dir12, dir13, dir14, dir15;
    private static boolean imagesLoaded = false;

    // kontrola czasowa (w ms)
    private long lastTargetSearchTime;
    private final long SEARCH_INTERVAL_MS = 800; // co 0.8 sekundy szuka nowego celu
    private Object currentTarget = null;

    public EnemyToo(int x, int y) {
        this.x = x;
        this.y = y;
        loadImages();
        // losowy offset, żeby nie wszystkie jednostki szukały jednocześnie
        this.lastTargetSearchTime = System.currentTimeMillis() - new Random().nextInt((int) SEARCH_INTERVAL_MS);
    }
    private static void loadImages() {
        if (imagesLoaded) return; // załadowane tylko raz

        try {
            dir0 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/1.png"));
            dir1 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/2.png"));
            dir2 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/3.png"));
            dir3 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/4.png"));
            dir4 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/5.png"));
            dir5 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/6.png"));
            dir6 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/7.png"));
            dir7 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/8.png"));

            dir8 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/9.png"));
            dir9 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/10.png"));
            dir10 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/11.png"));
            dir11 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/12.png"));
            dir12 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/13.png"));
            dir13 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/14.png"));
            dir14 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/15.png"));
            dir15 = ImageIO.read(EnemyToo.class.getResource("/EnemyToo/16.png"));

            imagesLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateDirection(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        double angle = Math.atan2(-dy, dx); // odwrotny Y jak w soldierBot
        angle = Math.toDegrees(angle);
        if (angle < 0) angle += 360;

        currentDirection = (int)Math.round(angle / 22.5) % 16;
    }
    private Image getCurrentImage() {
        return switch (currentDirection) {
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
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Image img = getCurrentImage();
        if (img != null) {
            g2.drawImage(img, x, y, width, height, null);
        }

        // pasek życia jak SoldierBot
        g.setColor(Color.GREEN);
        int maxHealth = 20;
        int healthBarWidth = width;
        int currentBar = (int)((health / (double)maxHealth) * healthBarWidth);
        g.fillRect(x, y - 5, currentBar, 3);
    }

    public boolean takeDamage() {
        health--;
        if (health <= 0) {
            markAsDead();
            return true;
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDead() { return dead; }
    public void markAsDead() { this.dead = true; }

    public Point getPosition() { return new Point(x, y); }

    public boolean takeDamage2() { health -= 3; return health <= 0; }
    public boolean takeDamage3() { health -= 10; return health <= 0; }

    public void update(List<Soldier> soldiers, List<Valkiria> valkirias, List<Harvester> harvesters,
                       List<Baracks> baracks, List<BuilderVehicle> builderVehicles, List<Artylery> artylerys,
                       List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants, List<SoldierBot> soldierBots,
                       List<Factory> factorys, List<SteelMine> steelMines, List<Explosion> explosions) {

        long currentTime = System.currentTimeMillis();

        // ---------- wyszukiwanie celu co określony czas ----------
        if (currentTarget == null || (currentTime - lastTargetSearchTime) > SEARCH_INTERVAL_MS) {
            currentTarget = getClosestTarget(
                    soldiers, valkirias, harvesters, baracks, builderVehicles,
                    artylerys, battleVehicles, powerPlants, soldierBots, factorys, steelMines
            );
            lastTargetSearchTime = currentTime;
        }

        // sprawdzenie, czy obecny cel wciąż żyje
        if (currentTarget instanceof Soldier s && s.isDead()) currentTarget = null;
        else if (currentTarget instanceof SoldierBot sb && sb.isDead()) currentTarget = null;
        else if (currentTarget instanceof Valkiria v && v.isDead()) currentTarget = null;
        else if (currentTarget instanceof Baracks b && b.getHealth() <= 0) currentTarget = null;
        else if (currentTarget instanceof Factory f && f.getHealth() <= 0) currentTarget = null;
        else if (currentTarget instanceof PowerPlant p && p.getHealth() <= 0) currentTarget = null;
        else if (currentTarget instanceof SteelMine sm && sm.getHealth() <= 0) currentTarget = null;

        // ruch i atak jak wcześniej
        moveTowardsTarget(currentTarget);
        attackIfInRange(currentTarget, resolveListForTarget(currentTarget,
                        soldiers, valkirias, harvesters, baracks, builderVehicles,
                        artylerys, battleVehicles, powerPlants, soldierBots, factorys, steelMines),
                explosions);
    }

    // ------------------ reszta kodu bez zmian ------------------
    private Object getClosestTarget(List<Soldier> soldiers, List<Valkiria> valkirias, List<Harvester> harvesters, List<Baracks> baracks,
                                    List<BuilderVehicle> builderVehicles, List<Artylery> artylerys,
                                    List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants, List<SoldierBot> soldierBots,
                                    List<Factory> factorys, List<SteelMine> steelMines) {
        Object closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Soldier s : soldiers) {
            double dist = s.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = s; }
        }
        for (Valkiria v : valkirias) {
            double dist = v.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = v; }
        }
        for (Harvester h : harvesters) {
            double dist = h.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = h; }
        }
        for (Baracks b : baracks) {
            double dist = b.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = b; }
        }
        for (BuilderVehicle bv : builderVehicles) {
            double dist = bv.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = bv; }
        }
        for (Artylery a : artylerys) {
            double dist = a.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = a; }
        }
        for (BattleVehicle bv : battleVehicles) {
            double dist = bv.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = bv; }
        }
        for (PowerPlant p : powerPlants) {
            double dist = p.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = p; }
        }
        for (SoldierBot sb : soldierBots) {
            double dist = sb.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = sb; }
        }
        for (Factory f : factorys) {
            double dist = f.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = f; }
        }
        for (SteelMine sm : steelMines) {
            double dist = sm.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = sm; }
        }

        return closest;
    }

    public void moveTowardsTarget(Object target) {
        if (target == null) return;

        int tx = 0, ty = 0;

        if (target instanceof Soldier s) {
            tx = s.getX(); ty = s.getY();
        } else if (target instanceof Harvester h) {
            tx = h.getX(); ty = h.getY();
        } else if (target instanceof Baracks b) {
            tx = b.getX(); ty = b.getY();
        } else if (target instanceof BuilderVehicle bv) {
            tx = bv.getX(); ty = bv.getY();
        } else if (target instanceof Artylery a) {
            tx = a.getX(); ty = a.getY();
        } else if (target instanceof BattleVehicle bv) {
            tx = bv.getX(); ty = bv.getY();
        } else if (target instanceof PowerPlant p) {
            tx = p.getX(); ty = p.getY();
        } else if (target instanceof Valkiria v) {
            tx = v.getX(); ty = v.getY();
        } else if (target instanceof SoldierBot sb) {
            tx = sb.getX(); ty = sb.getY();
        } else if (target instanceof Factory f) {
            tx = f.getX(); ty = f.getY();
        } else if (target instanceof SteelMine sm) {
            tx = sm.getX(); ty = sm.getY();
        }

        int dx = tx - x;
        int dy = ty - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        updateDirection(dx, dy);
        if (distance > 0) {
            x += (int) (speed * dx / distance);
            y += (int) (speed * dy / distance);
        }
    }

    private void attackIfInRange(Object target, List<?> list, List<Explosion> explosions) {
        if (target == null) return;

        Rectangle bounds = getBounds();

        if (target instanceof Soldier s && bounds.intersects(s.getBounds())) {
            boolean destroyed = s.takeDamage();
            if (destroyed){
                list.remove(s);
                explosions.add(new Explosion(s.getX(), s.getY())); // efekt wybuchu
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof Harvester h && bounds.intersects(h.getBounds())) {
            list.remove(h);
        } else if (target instanceof Baracks b && bounds.intersects(b.getBounds())) {
            boolean destroyed = b.takeDamage();
            if (destroyed){
                explosions.add(new Explosion(b.getX(), b.getY())); // efekt wybuchu
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof BuilderVehicle bv && bounds.intersects(bv.getBounds())) {
            boolean destroyed = bv.takeDamage();
            if (destroyed){
                list.remove(bv);
                explosions.add(new Explosion(bv.getX(), bv.getY())); // efekt wybuchu
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof Artylery a && bounds.intersects(a.getBounds())) {
            boolean destroyed = a.takeDamage();
            if (destroyed) {
                list.remove(a);
                Artylery.decreaseArtysCount();
                explosions.add(new Explosion(a.getX(), a.getY()));
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof BattleVehicle bv && bounds.intersects(bv.getBounds())) {
            list.remove(bv);
        } else if (target instanceof PowerPlant pp && bounds.intersects(pp.getBounds())) {
            list.remove(pp);
            explosions.add(new Explosion(pp.getX(), pp.getY()));
            currentTarget = null; // zapobiega wielokrotnemu wybuchowi

        } else if (target instanceof SoldierBot sb && bounds.intersects(sb.getBounds())) {
            boolean destroyed = sb.takeDamage();
            if (destroyed){
                list.remove(sb);
                explosions.add(new Explosion(sb.getX(), sb.getY()));
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof Valkiria v && bounds.intersects(v.getBounds())) {
            boolean destroyed = v.takeDamage();
            if (destroyed){
                list.remove(v);
                explosions.add(new Explosion(v.getX(), v.getY()));
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof Factory f && bounds.intersects(f.getBounds())) {
            boolean destroyed = f.takeDamage();
            if (destroyed) {
                list.remove(f);
                Factory.decreaseFactoryCount();
                explosions.add(new Explosion(f.getX(), f.getY()));
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        } else if (target instanceof SteelMine sm && bounds.intersects(sm.getBounds())) {
            boolean destroyed = sm.takeDamage();
            if (destroyed) {
                list.remove(sm);
                Factory.decreaseFactoryCount();
                explosions.add(new Explosion(sm.getX(), sm.getY()));
                currentTarget = null; // zapobiega wielokrotnemu wybuchowi
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<?> resolveListForTarget(Object target,
                                         List<Soldier> soldiers,
                                         List<Valkiria> valkirias,
                                         List<Harvester> harvesters,
                                         List<Baracks> baracks,
                                         List<BuilderVehicle> builderVehicles,
                                         List<Artylery> artylerys,
                                         List<BattleVehicle> battleVehicles,
                                         List<PowerPlant> powerPlants,
                                         List<SoldierBot> soldierBots,
                                         List<Factory> factorys,
                                         List<SteelMine> steelMines) {
        if (target instanceof Soldier) return soldiers;
        if (target instanceof Valkiria) return valkirias;
        if (target instanceof Harvester) return harvesters;
        if (target instanceof Baracks) return baracks;
        if (target instanceof BuilderVehicle) return builderVehicles;
        if (target instanceof Artylery) return artylerys;
        if (target instanceof BattleVehicle) return battleVehicles;
        if (target instanceof PowerPlant) return powerPlants;
        if (target instanceof SoldierBot) return soldierBots;
        if (target instanceof Factory) return factorys;
        if (target instanceof SteelMine) return steelMines;
        return List.of(); // pusta lista, fallback
    }
}
