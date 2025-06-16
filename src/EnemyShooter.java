import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class EnemyShooter {
    private int x, y;
    private final int range = 140; // Zasięg strzelania w pikselach
    private final int width = 25, height = 25;
    private int health = 3;
    private int speed = 3;
    private final int shootCooldown = 1100; // Czas odnowienia strzału (ms)
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private long lastShotTime = 0; // Czas ostatniego strzału
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

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }

    public boolean isInRange(Soldier soldier) {
        int dx = soldier.getX() - x;
        int dy = soldier.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(BattleVehicle battleVehicle) {
        int dx = battleVehicle.getX() - x;
        int dy = battleVehicle.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(PowerPlant powerPlant){
        int dx = powerPlant.getX() - x;
        int dy = powerPlant.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }


    private void chooseTarget(ArrayList<Soldier> soldiers, ArrayList<BattleVehicle> battleVehicles, ArrayList<PowerPlant> powerPlants
//            , ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives
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
        for (BattleVehicle battleVehicle : battleVehicles) {
            if (isInRange(battleVehicle)) {
                currentTarget = battleVehicle;
                return; // Znaleziono cel
            }
        }
        for (PowerPlant powerPlant : powerPlants) {
            if (isInRange(powerPlant)){
                currentTarget = powerPlant;
                return;
            }
        }
    }

    public void shoot(Graphics g, ArrayList<Projectile> projectiles, ArrayList<Soldier> soldiers, ArrayList<BattleVehicle> battleVehicles,ArrayList<PowerPlant> powerPlants
//            , ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives
    ) {
        long currentTime = System.currentTimeMillis();

        // Jeśli aktualny cel został zniszczony lub jest poza zasięgiem, wybierz nowy
        if (currentTarget == null ||
                (currentTarget instanceof Soldier && !soldiers.contains(currentTarget)) ||
               (currentTarget instanceof BattleVehicle && !battleVehicles.contains(currentTarget)) ||
                (currentTarget instanceof PowerPlant && !powerPlants.contains(currentTarget)) ||

//                (currentTarget instanceof Hive && !hives.contains(currentTarget)) ||
                !(currentTarget instanceof Soldier soldier && isInRange(soldier)) &&
                        !(currentTarget instanceof BattleVehicle battleVehicle && isInRange(battleVehicle)) &&
        !(currentTarget instanceof PowerPlant powerPlant && isInRange(powerPlant))

        )
        {
            chooseTarget(soldiers, battleVehicles, powerPlants); // Wybierz nowy cel
        }

        // Jeśli mamy ważny cel, strzelaj
        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (currentTarget instanceof Soldier soldier) {
                if (isInRange(soldier)) {
                    projectiles.add(new Projectile(x + 15, y + 15, soldier.getX() + 15, soldier.getY() + 15));
                    lastShotTime = currentTime;
                }
            }
            if (currentTarget instanceof PowerPlant powerPlant){
                if (isInRange(powerPlant)) {
                    projectiles.add(new Projectile(x + 15, y + 15, powerPlant.getX() + 15, powerPlant.getY() + 15));
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
    public void update(List<Soldier> soldiers, List<Harvester> harvesters, List<BuilderVehicle> builderVehicles, List<Artylery> artylerys, List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants, List<Factory> factorys) {

        moveTowardsSoldier(soldiers);
        moveTowardsBattleVehicle(battleVehicles);
//        moveTowardsArtylery(artylerys);
        moveTowardsPowerPlant(powerPlants);
//        attackClosestSoldier(soldiers);
//        attackClosestBattleVehicle(battleVehicles);
//        moveTowardsHarvester(harvesters);
//        attackClosestHarvester(harvesters);
//        moveTowardsBuilderVehicle(builderVehicles);
//        attackClosestBuilderVehicles(builderVehicles);
//        attackClosestArtylerys(artylerys);
//        attackClosestPowerPlant(powerPlants);
//        attackClosestFactory(factorys);
//        moveTowardsFactory(factorys);

    }

    private Soldier getClosestSoldier(java.util.List<Soldier> soldiers) {
        Soldier closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Soldier soldier : soldiers) {
            double distance = soldier.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = soldier;
            }
        }
        return closest;
    }
    private PowerPlant getClosestPowerPlant(java.util.List<PowerPlant> powerPlants) {
        PowerPlant closest = null;
        double minDistance = Double.MAX_VALUE;

        for (PowerPlant powerPlant : powerPlants) {
            double distance =powerPlant.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = powerPlant;
            }
        }
        return closest;
    }
    private BattleVehicle getClosestBattleVehicle(java.util.List<BattleVehicle> battleVehicles) {
        BattleVehicle closest = null;
        double minDistance = Double.MAX_VALUE;

        for (BattleVehicle battleVehicle : battleVehicles) {
            double distance = battleVehicle.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = battleVehicle;
            }
        }
        return closest;
    }

    public void  moveTowardsPowerPlant(List<PowerPlant> powerPlants){
        PowerPlant closestPowerPlant = getClosestPowerPlant(powerPlants);
        if (closestPowerPlant != null) {
            int dx = closestPowerPlant.getX() - x;
            int dy = closestPowerPlant.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }
    }

    public void moveTowardsSoldier(List<Soldier> soldiers) {
        Soldier closestSoldier = getClosestSoldier(soldiers);

        if (closestSoldier != null) {
            int dx = closestSoldier.getX() - x;
            int dy = closestSoldier.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }
    public void moveTowardsBattleVehicle(List<BattleVehicle> battleVehicles) {
        BattleVehicle closestBattleVehicle = getClosestBattleVehicle(battleVehicles);

        if (closestBattleVehicle != null) {
            int dx = closestBattleVehicle.getX() - x;
            int dy = closestBattleVehicle.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 110) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
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
