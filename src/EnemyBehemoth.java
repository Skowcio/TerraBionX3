import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class EnemyBehemoth {

    private int x, y;
    private int width = 30, height = 30;
    private int speed = 2; // prędkość poruszania
    private int health = 70;
    private final int shootCooldown = 1100; // Czas odnowienia strzału (ms)
    private final int range = 190; // Zasięg strzelania w pikselach
    private Object currentTarget; // Aktualny cel
    private long lastShotTime = 0; // Czas ostatniego strzału
    private boolean dead = false;

    private final int aggroRange = 500; // maksymalny zasięg "widzenia"

    // 🔥 Nowe pola do patrolowania
    private final int spawnX, spawnY; // punkt narodzin
    private final int patrolRange = 300; // promień sektora patrolu
    private int patrolTargetX, patrolTargetY; // aktualny punkt docelowy patrolu
    private final Random random = new Random();




    public EnemyBehemoth (int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        pickNewPatrolTarget(); // ustaw początkowy cel patrolu
    }

    private void pickNewPatrolTarget() {
        // Losujemy punkt w obrębie patrolRange od spawn
        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = random.nextDouble() * patrolRange;
        patrolTargetX = spawnX + (int) (Math.cos(angle) * radius);
        patrolTargetY = spawnY + (int) (Math.sin(angle) * radius);
    }

    private void movePatrol() {
        int dx = patrolTargetX - x;
        int dy = patrolTargetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 5) { // jeszcze daleko do celu
            x += (int) (speed * dx / distance);
            y += (int) (speed * dy / distance);
        } else {
            pickNewPatrolTarget(); // cel osiągnięty → losujemy nowy
        }
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
        g.setColor(new Color(95, 68, 55)); //
        g.fillRect(x, y, width, height);
        int maxHealth = 70; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 30; // Stała długość paska zdrowia
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
    public boolean isDead() {
        return dead;
    }

    public void markAsDead() {
        this.dead = true;
    }
    /// /////////// sekcja strzelania

    public boolean isInRange(Soldier soldier) {
        int dx = soldier.getX() - x;
        int dy = soldier.getY() - y;
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


                !(currentTarget instanceof Soldier soldier && isInRange(soldier)) &&
                        !(currentTarget instanceof SoldierBot soldierBot&& isInRange(soldierBot)) &&
                        !(currentTarget instanceof BattleVehicle battleVehicle && isInRange(battleVehicle)) &&
                        !(currentTarget instanceof Factory factory && isInRange(factory)) &&
                        !(currentTarget instanceof PowerPlant powerPlant && isInRange(powerPlant)) &&
                        !(currentTarget instanceof BuilderVehicle builderVehicle && isInRange(builderVehicle)) &&
                        !(currentTarget instanceof Artylery artylery && isInRange(artylery)) &&
                        !(currentTarget instanceof  Baracks b && isInRange(b))

        )
        {
            chooseTarget(soldiers, soldierBots, battleVehicles, factories, powerPlants, builderVehicles, artyleries, baracks); // Wybierz nowy cel
        }

        // Jeśli mamy ważny cel, strzelaj
        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (currentTarget instanceof Soldier soldier) {
                if (isInRange(soldier)) {
                    projectiles.add(new Projectile(x + 15, y + 15, soldier.getX() + 15, soldier.getY() + 15));
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
    /// ////////////////////////////////////////
    /// ///////// to za czym biega w updatecie w porusznaiu
    // 🔄 modyfikacja update – najpierw sprawdzamy cele w aggroRange
    public void update(List<Soldier> soldiers,
                       List<Harvester> harvesters,
                       List<BuilderVehicle> builderVehicles,
                       List<Artylery> artylerys,
                       List<BattleVehicle> battleVehicles,
                       List<PowerPlant> powerPlants,
                       List<Factory> factorys,
                       List<SoldierBot> soldierBots,
                       List<EnemyHunter> enemies) {

        // jeśli ktoś jest w aggroRange → atakuj
        Soldier closestSoldier = getClosestSoldier(soldiers);
        BattleVehicle closestVehicle = getClosestBattleVehicle(battleVehicles);
        SoldierBot closestBot = getClosestSoldierBot(soldierBots);

        if (closestSoldier != null || closestVehicle != null || closestBot != null) {
            // atak jak wcześniej
            moveTowardsSoldier(soldiers);
            moveTowardsBattleVehicle(battleVehicles);
            moveTowardsArtylery(artylerys);
            moveTowardsPowerPlant(powerPlants);
            moveTowardsHarvester(harvesters);
            moveTowardsBuilderVehicle(builderVehicles);
            moveTowardsFactory(factorys);
            moveTowardsSoldierBot(soldierBots);
            attackClosestSoldier(soldiers, enemies);
        } else {
            // 👣 jeśli nikt nie jest w zasięgu → patrol
            movePatrol();
        }
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
                minDistance = distance;
                closest = artylery;
            }
        }
        return closest;
    }
    private SoldierBot getClosestSoldierBot(java.util.List<SoldierBot> soldierBots) {
        SoldierBot closest = null;
        double minDistance = Double.MAX_VALUE;

        for (SoldierBot soldierBot : soldierBots) {
            double distance = soldierBot.getPosition().distance(x, y);
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
                minDistance = distance;
                closest = soldierBot;
            }
        }
        return closest;
    }

    private Factory getClosestFactory(java.util.List<Factory> factorys) {
        Factory closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Factory factory : factorys) {
            double distance = factory.getPosition().distance(x, y);
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
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
    public void moveTowardsSoldierBot(List<SoldierBot> soldierBots) {
        SoldierBot closestSoldierBot = getClosestSoldierBot(soldierBots);

        if (closestSoldierBot != null) {
            int dx = closestSoldierBot.getX() - x;
            int dy = closestSoldierBot.getY() - y;
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
