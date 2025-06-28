import java.awt.*;
import java.util.List;

public class EnemyHunter {

    private int x, y;
    private int width = 10, height = 10;
    private int speed = 4; // prędkość poruszania
    private int health = 2;




    public EnemyHunter(int x, int y) {
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
        g.setColor(new Color(255, 98, 0)); //
        g.fillRect(x, y, width, height);
        int maxHealth = 2; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 10; // Stała długość paska zdrowia
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


    public void update(List<Soldier> soldiers, List<Harvester> harvesters, List<BuilderVehicle> builderVehicles, List<Artylery> artylerys, List<BattleVehicle> battleVehicles, List<PowerPlant> powerPlants, List<Factory> factorys,  List<EnemyHunter> enemies) {



        moveTowardsSoldier(soldiers);

        moveTowardsBattleVehicle(battleVehicles);
        moveTowardsArtylery(artylerys);
        moveTowardsPowerPlant(powerPlants);

        moveTowardsHarvester(harvesters);

        moveTowardsBuilderVehicle(builderVehicles);

        moveTowardsFactory(factorys);
        attackClosestSoldier(soldiers, enemies);


    }
    private void attackClosestSoldier(List<Soldier> soldiers, List<EnemyHunter> enemies) {
        Soldier closestSoldier = getClosestSoldier(soldiers);

        if (closestSoldier != null && getBounds().intersects(closestSoldier.getBounds())) {
            // Usunięcie żołnierza z listy
            soldiers.remove(closestSoldier);
            System.out.println("EnemyHunter zaatakował Soldier i usunął go z gry!");

            // Usunięcie EnemyHunter z listy
            enemies.remove(this);
            System.out.println("EnemyHunter zginął podczas ataku!");
        }
    }

    private BuilderVehicle getClosestBuldierVehicle(java.util.List<BuilderVehicle> builderVehicles) {
        BuilderVehicle closest = null;
        double minDistance = Double.MAX_VALUE;

        for (BuilderVehicle builderVehicle : builderVehicles) {
            double distance = builderVehicle.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = builderVehicle;
            }
        }
        return closest;
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

    private Artylery getClosestArtylery(java.util.List<Artylery> artylerys) {
        Artylery closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Artylery artylery : artylerys) {
            double distance = artylery.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = artylery;
            }
        }
        return closest;
    }

    private Factory getClosestFactory(java.util.List<Factory> factorys) {
        Factory closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Factory factory : factorys) {
            double distance = factory.getPosition().distance(x, y);
            if (distance < minDistance) {
                minDistance = distance;
                closest = factory;
            }
        }
        return closest;
    }

    private Harvester getColsestHarvester(java.util.List<Harvester> harvesters){
        Harvester closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Harvester harvester : harvesters) {
            double distance = harvester.getPosition().distance(x, y);
            if (distance < minDistance){
                minDistance = distance;
                closest = harvester;
            }
        }
        return closest;

    }
    private PowerPlant getClosestPowerPlant(java.util.List<PowerPlant> powerPlants){
        PowerPlant closest = null;
        double minDistance = Double.MAX_VALUE;

        for (PowerPlant powerPlant : powerPlants) {
            double distance = powerPlant.getPosition().distance(x, y);
            if (distance < minDistance){
                minDistance = distance;
                closest = powerPlant;
            }
        }
        return closest;

    }

    public void moveTowardsHarvester(List<Harvester> harvesters) {
        Harvester closestHarvester = getColsestHarvester(harvesters);

        if (closestHarvester != null){
            int dx = closestHarvester.getX() - x;
            int dy = closestHarvester.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }
    }
    public void moveTowardsBuilderVehicle(List<BuilderVehicle> builderVehicles) {
        BuilderVehicle closestBuilderVehicle = getClosestBuldierVehicle(builderVehicles);

        if (closestBuilderVehicle != null) {
            int dx = closestBuilderVehicle.getX() - x;
            int dy = closestBuilderVehicle.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
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

            if (distance > 0) {
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

            if (distance > 0) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }

    public void moveTowardsArtylery(List<Artylery> artyleries) {
        Artylery closestArtylery = getClosestArtylery(artyleries);

        if (closestArtylery != null) {
            int dx = closestArtylery.getX() - x;
            int dy = closestArtylery.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }
    public void moveTowardsPowerPlant(List<PowerPlant> powerPlants) {
        PowerPlant closestPowerPlant = getClosestPowerPlant(powerPlants);

        if (closestPowerPlant != null) {
            int dx = closestPowerPlant.getX() - x;
            int dy = closestPowerPlant.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }

    public void moveTowardsFactory(List<Factory> factorys) {
        Factory closestFactory = getClosestFactory(factorys);

        if (closestFactory != null) {
            int dx = closestFactory.getX() - x;
            int dy = closestFactory.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }

}
