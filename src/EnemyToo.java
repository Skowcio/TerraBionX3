import java.awt.*;
import java.util.List;

public class EnemyToo {
    private int x, y;
    private int width = 30, height = 30;
    private int speed = 4; // prędkość poruszania
    private int health = 20;

    public EnemyToo(int x, int y) {
        this.x = x;
        this.y = y;
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

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
        int maxHealth = 20; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 20; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);
    }
    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }
    public Point getPosition() {
        return new Point(x, y);
    }

    public boolean takeDamage2() {
        health -= 3;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }

    public void update(List<Soldier> soldiers, List<Harvester> harvesters, List<Baracks> baracks,
                       List<BuilderVehicle> builderVehicles, List<Artylery> artylerys,
                       List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants,
                       List<Factory> factorys) {

        Object closest = getClosestTarget(soldiers, harvesters, baracks, builderVehicles,
                artylerys, battleVehicles, powerPlants, factorys);

        moveTowardsTarget(closest);
        attackIfInRange(closest, resolveListForTarget(closest,
                soldiers, harvesters, baracks, builderVehicles,
                artylerys, battleVehicles, powerPlants, factorys));
    }


    private Object getClosestTarget(List<Soldier> soldiers, List<Harvester> harvesters, List<Baracks> baracks,
                                    List<BuilderVehicle> builderVehicles, List<Artylery> artylerys,
                                    List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants,
                                    List<Factory> factorys) {

        Object closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Soldier s : soldiers) {
            double dist = s.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = s; }
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
        for (Factory f : factorys) {
            double dist = f.getPosition().distance(x, y);
            if (dist < minDistance) { minDistance = dist; closest = f; }
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
        } else if (target instanceof Factory f) {
            tx = f.getX(); ty = f.getY();
        }

        int dx = tx - x;
        int dy = ty - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            x += (int) (speed * dx / distance);
            y += (int) (speed * dy / distance);
        }
    }


    private void attackIfInRange(Object target, List<?> list) {
        if (target == null) return;

        if (target instanceof Soldier s && getBounds().intersects(s.getBounds())) {
            list.remove(s);
            System.out.println("EnemyToo zaatakował Soldier i usunął go z gry!");
        } else if (target instanceof Harvester h && getBounds().intersects(h.getBounds())) {
            list.remove(h);
            System.out.println("EnemyToo zaatakował Harvester i usunął go z gry!");
        } else if (target instanceof Baracks b && getBounds().intersects(b.getBounds())) {
            list.remove(b);
            System.out.println("EnemyToo zaatakował Baracks i usunął go z gry!");
        } else if (target instanceof BuilderVehicle bv && getBounds().intersects(bv.getBounds())) {
            list.remove(bv);
            System.out.println("EnemyToo zaatakował Builder i usunął go z gry!");
        } else if (target instanceof Artylery a && getBounds().intersects(a.getBounds())) {
            list.remove(a);
            System.out.println("EnemyToo zaatakował Artylery i usunął go z gry!");
        } else if (target instanceof BattleVehicle bv && getBounds().intersects(bv.getBounds())) {
            list.remove(bv);
            System.out.println("EnemyToo zaatakował BattleVehicle i usunął go z gry!");
        } else if (target instanceof PowerPlant pp && getBounds().intersects(pp.getBounds())) {
            list.remove(pp);
            System.out.println("EnemyToo zaatakował PowerPlant i usunął go z gry!");
        } else if (target instanceof Factory f && getBounds().intersects(f.getBounds())) {
            list.remove(f);
            System.out.println("EnemyToo zaatakował Factory i usunął go z gry!");
        }
    }

    @SuppressWarnings("unchecked")
    private List<?> resolveListForTarget(Object target,
                                         List<Soldier> soldiers,
                                         List<Harvester> harvesters,
                                         List<Baracks> baracks,
                                         List<BuilderVehicle> builderVehicles,
                                         List<Artylery> artylerys,
                                         List<BattleVehicle> battleVehicles,
                                         List<PowerPlant> powerPlants,
                                         List<Factory> factorys) {
        if (target instanceof Soldier) return soldiers;
        if (target instanceof Harvester) return harvesters;
        if (target instanceof Baracks) return baracks;
        if (target instanceof BuilderVehicle) return builderVehicles;
        if (target instanceof Artylery) return artylerys;
        if (target instanceof BattleVehicle) return battleVehicles;
        if (target instanceof PowerPlant) return powerPlants;
        if (target instanceof Factory) return factorys;
        return List.of(); // pusta lista, fallback
    }

}
