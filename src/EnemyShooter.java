import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class EnemyShooter {
    private int x, y;
    private final int range = 170; // Zasięg strzelania w pikselach
    private final int width = 25, height = 25;
    private int health = 3;
    private int speed = 3;
    private final int shootCooldown = 1100; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel
    private long lastShotTime = 0; // Czas ostatniego strzału
    private boolean dead = false;
    private Random random = new Random();

    public EnemyShooter(int x, int y) {
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

    public Point getPosition() {
        return new Point(x, y);
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

    public boolean isInRange(Soldier soldier) {
        int dx = soldier.getX() - x;
        int dy = soldier.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(Valkiria valkiria) {
        int dx = valkiria.getX() - x;
        int dy = valkiria.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(BuilderVehicle builderVehicle) {  // 🆕 BuilderVehicle
        int dx = builderVehicle.getX() - x;
        int dy = builderVehicle.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(SoldierBot soldierBot) {
        int dx = soldierBot.getX() - x;
        int dy = soldierBot.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(BattleVehicle battleVehicle) {
        int dx = battleVehicle.getX() - x;
        int dy = battleVehicle.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(Factory factory) {
        int dx = factory.getX() - x;
        int dy = factory.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(PowerPlant powerPlant){
        int dx = powerPlant.getX() - x;
        int dy = powerPlant.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(Artylery artylery){
        int dx = artylery.getX() - x;
        int dy = artylery.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(Baracks baracks){
        int dx = baracks.getX() - x;
        int dy = baracks.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }


    private void chooseTarget(
            ArrayList<Soldier> soldiers,
            ArrayList<Valkiria> valkirias,
            ArrayList<SoldierBot> soldierBots,
            ArrayList<BattleVehicle> battleVehicles,
            ArrayList<Factory> factories,
            ArrayList<PowerPlant> powerPlants,
            ArrayList<BuilderVehicle> builderVehicles, // 🆕 BuilderVehicle
            ArrayList<Artylery> artyleries,
            ArrayList<Baracks> baracks
    )
    {
        currentTarget = null;

        // Szukaj najbliższego do zaatakowania w zasięgu
        for (Soldier soldier : soldiers) {
            if (isInRange(soldier)) {
                currentTarget = soldier;
                return; // Znaleziono cel
            }
        }
        for (Valkiria valkiria : valkirias) {
            if (isInRange(valkiria)) {
                currentTarget = valkiria;
                return; // Znaleziono cel
            }
        }

        for (SoldierBot soldierBot : soldierBots) {
            if (isInRange(soldierBot)) {
                currentTarget = soldierBot;
                return; // Znaleziono cel
            }
        }
        for (BattleVehicle battleVehicle : battleVehicles) {
            if (isInRange(battleVehicle)) {
                currentTarget = battleVehicle;
                return; // Znaleziono cel
            }
        }
        for (Factory factory : factories) {
            if (isInRange(factory)) {
                currentTarget = factory;
                return; // Znaleziono cel
            }
        }
        for (PowerPlant powerPlant : powerPlants) {
            if (isInRange(powerPlant)){
                currentTarget = powerPlant;
                return;
            }
        }

        for (BuilderVehicle builderVehicle : builderVehicles) {  // 🆕 BuilderVehicle
            if (isInRange(builderVehicle)) {
                currentTarget = builderVehicle;
                return; }
        }
        for (Artylery artylery : artyleries) {
            if (isInRange(artylery)){
                currentTarget = artylery;
                return;
            }
        }
        for (Baracks b : baracks) {
            if (isInRange(b)) {
                currentTarget = b;
                return;
            }
        }
    }

    public void shoot(
            Graphics g,
            ArrayList<Projectile> projectiles,
            ArrayList<Soldier> soldiers,
            ArrayList<Valkiria> valkirias,   // 🆕
            ArrayList<SoldierBot> soldierBots,
            ArrayList<BattleVehicle> battleVehicles,
            ArrayList<Factory> factories,
            ArrayList<PowerPlant> powerPlants,
            ArrayList<BuilderVehicle> builderVehicles,
            ArrayList<Artylery> artyleries,
            ArrayList<Baracks> baracks)
            // 🆕 BuilderVehicle

    {
        long currentTime = System.currentTimeMillis();

        // Jeśli aktualny cel został zniszczony lub jest poza zasięgiem, wybierz nowy
        if (currentTarget == null ||
                (currentTarget instanceof Soldier && !soldiers.contains(currentTarget)) ||
                (currentTarget instanceof SoldierBot && !soldierBots.contains(currentTarget)) ||
               (currentTarget instanceof BattleVehicle && !battleVehicles.contains(currentTarget)) ||
                (currentTarget instanceof Factory && !factories.contains(currentTarget)) ||
                (currentTarget instanceof PowerPlant && !powerPlants.contains(currentTarget)) ||
                (currentTarget instanceof BuilderVehicle && !builderVehicles.contains(currentTarget)) // 🆕
                ||
                (currentTarget instanceof Artylery && !artyleries.contains(currentTarget))||
                (currentTarget instanceof Baracks && !baracks.contains(currentTarget))||
                (currentTarget instanceof Valkiria && !valkirias.contains(currentTarget)) ||


                !(currentTarget instanceof Soldier soldier && isInRange(soldier)) &&
                        !(currentTarget instanceof Valkiria v && isInRange(v)) &&
                        !(currentTarget instanceof SoldierBot soldierBot&& isInRange(soldierBot)) &&
                        !(currentTarget instanceof BattleVehicle battleVehicle && isInRange(battleVehicle)) &&
                        !(currentTarget instanceof Factory factory && isInRange(factory)) &&
        !(currentTarget instanceof PowerPlant powerPlant && isInRange(powerPlant)) &&
                        !(currentTarget instanceof BuilderVehicle builderVehicle && isInRange(builderVehicle)) &&
                !(currentTarget instanceof Artylery artylery && isInRange(artylery)) &&
                        !(currentTarget instanceof  Baracks b && isInRange(b))

        )
        {
            chooseTarget(soldiers, valkirias, soldierBots, battleVehicles, factories, powerPlants, builderVehicles, artyleries, baracks); // Wybierz nowy cel
        }

        // Jeśli mamy ważny cel, strzelaj
        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (currentTarget instanceof Soldier soldier) {
                if (isInRange(soldier)) {
                    projectiles.add(new Projectile(x + 15, y + 15, soldier.getX() + 15, soldier.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof Valkiria valkiria) {
                if (isInRange(valkiria)) {
                    projectiles.add(new Projectile(x + 15, y + 15, valkiria.getX() + 15, valkiria.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof SoldierBot soldierBot) {
                if (isInRange(soldierBot)) {
                    projectiles.add(new Projectile(x + 15, y + 15, soldierBot.getX() + 15, soldierBot.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof PowerPlant powerPlant){
                if (isInRange(powerPlant)) {
                    projectiles.add(new Projectile(x + 15, y + 15, powerPlant.getX() + 15, powerPlant.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof Factory factory){
                if (isInRange(factory)) {
                    projectiles.add(new Projectile(x + 15, y + 15, factory.getX() + 15, factory.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof BuilderVehicle builderVehicle ) { // 🆕
                if (isInRange(builderVehicle)) {
                    projectiles.add(new Projectile(x + 15, y + 15, builderVehicle.getX() + 15, builderVehicle.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof Artylery artylery){
                if (isInRange(artylery)){
                    projectiles.add(new Projectile(x + 15, y + 15, artylery.getX() + 15, artylery.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof Baracks b){
                if (isInRange(b)){
                    projectiles.add(new Projectile(x + 15, y + 15, b.getX() + 15, b.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            else if (currentTarget instanceof BattleVehicle battleVehicle) {
                if (isInRange(battleVehicle)) {
                    projectiles.add(new Projectile(x + 15, y + 15, battleVehicle.getX() + 15, battleVehicle.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
//            else if (currentTarget instanceof Hive hive) {
//                if (isInRange(hive)) {
//                    bullets.add(new Bullet(x + 15, y + 15, hive.getX() + 15, hive.getY() + 15));
//                    lastShotTime = currentTime;
//                }
//            }
        }
    }
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
            List<Factory> factorys

    ) {
        Object target = getClosestTarget(soldierBots, soldiers, valkirias, powerPlants, battleVehicles, factorys, builderVehicles, artylerys, baracks);
        moveTowardsTarget(target);
    }

    public Object getClosestTarget(
            List<SoldierBot> soldierBots,
            List<Soldier> soldiers,
            List<Valkiria> valkirias,
            List<PowerPlant> powerPlants,
            List<BattleVehicle> battleVehicles,
            List<Factory> factorys,
            List<BuilderVehicle> builderVehicles,
            List<Artylery> artyleries,
            List<Baracks> baracks
    ) {
        Object closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Soldier soldier : soldiers) {
            double dist = soldier.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = soldier;
            }
        }

        for (Valkiria valkiria : valkirias) {
            double dist = valkiria.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = valkiria;
            }
        }

        for (SoldierBot bot : soldierBots) {
            double dist = bot.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = bot;
            }
        }

        for (PowerPlant plant : powerPlants) {
            double dist = plant.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = plant;
            }
        }
        for (Factory factory : factorys) {
            double dist = factory.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = factory;
            }
        }

        for (BuilderVehicle builderVehicle : builderVehicles){
            double dist = builderVehicle.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = builderVehicle;
            }
        }
        for (Baracks baracks1 : baracks){
            double dist = baracks1.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = baracks1;
            }
        }
        for (Artylery artylery : artyleries){
            double dist = artylery.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = artylery;
            }
        }

        for (BattleVehicle vehicle : battleVehicles) {
            double dist = vehicle.getPosition().distance(x, y);
            if (dist < minDistance) {
                minDistance = dist;
                closest = vehicle;
            }
        }

        return closest;
    }
    public void moveTowardsTarget(Object target) {
        if (target == null) return;

        int tx = 0, ty = 0;

        if (target instanceof Soldier s) {
            tx = s.getX();
            ty = s.getY();
        }
        else if (target instanceof Valkiria v) {
            tx = v.getX();
            ty = v.getY();
        }else if (target instanceof SoldierBot sb) {
            tx = sb.getX();
            ty = sb.getY();
        } else if (target instanceof PowerPlant pp) {
            tx = pp.getX();
            ty = pp.getY();
        } else if (target instanceof BattleVehicle bv) {
            tx = bv.getX();
            ty = bv.getY();
        }
        else if (target instanceof Artylery a) {
            tx = a.getX();
            ty = a.getY();
        }
        else if (target instanceof Baracks b) {
            tx = b.getX();
            ty = b.getY();
        }
        else if (target instanceof Factory f) {
            tx = f.getX();
            ty = f.getY();
        }
        else if (target instanceof BuilderVehicle bb){
            tx = bb.getX();
            ty = bb.getY();
        }


                int dx = tx - x;
        int dy = ty - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 110) {
            x += (int) (speed * dx / distance);
            y += (int) (speed * dy / distance);
        }
    }



    public void draw(Graphics g) {
        g.setColor(new Color(109, 0, 24)); // Bordowy
        g.fillRect(x, y, width, height);

        int maxHealth = 3; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 25; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);
    }

//    public Projectile shootAtNearestSoldier(ArrayList<Soldier> soldiers) {
//        Soldier nearest = null;
//        double minDistance = Double.MAX_VALUE;
//
//        for (Soldier soldier : soldiers) {
//            double distance = Math.sqrt(Math.pow(soldier.getX() - x, 2) + Math.pow(soldier.getY() - y, 2));
//            if (distance <= range && distance < minDistance) {
//                minDistance = distance;
//                nearest = soldier;
//            }
//        }
//        if (nearest != null) {
//            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
//        }
//        return null;
//    }

}
