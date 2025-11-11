import java.awt.*;
import java.util.List;
import java.util.Random;

public class EnemyToo {
    private int x, y;
    private int width = 30, height = 30;
    private int speed = 4;
    private int health = 20;
    private boolean dead = false;

    // kontrola czasowa (w ms)
    private long lastTargetSearchTime;
    private final long SEARCH_INTERVAL_MS = 800; // co 0.8 sekundy szuka nowego celu
    private Object currentTarget = null;

    public EnemyToo(int x, int y) {
        this.x = x;
        this.y = y;
        // losowy offset, żeby nie wszystkie jednostki szukały jednocześnie
        this.lastTargetSearchTime = System.currentTimeMillis() - new Random().nextInt((int) SEARCH_INTERVAL_MS);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
        int maxHealth = 20;
        int healthBarWidth = 20;
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3);
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);
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
