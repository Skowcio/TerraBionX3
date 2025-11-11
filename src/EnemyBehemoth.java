import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class EnemyBehemoth {

    private int x, y;

    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.003;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;

    private int width = 100, height = 100;
    private int speed = 3; // prędkość poruszania
    private int health = 70;
    private final int maxHealth = 70;       // maksymalne HP
    private long lastRegenTime = 0;         // czas ostatniej regeneracji
    private final long regenCooldown = 5_000; // timer do odnowienia hp 10 sekund w ms
    private final int shootCooldown = 900; // Czas odnowienia strzału (ms)
    private final int range = 210; // Zasięg strzelania w pikselach
    private Object currentTarget; // Aktualny cel
    private long lastShotTime = 0; // Czas ostatniego strzału
    private boolean dead = false;

    private final int aggroRange = 500; // maksymalny zasięg "widzenia"

    // 🔥 Nowe pola do patrolowania
    private final int spawnX, spawnY; // punkt narodzin
    private final int patrolRange = 300; // promień sektora patrolu
    private int patrolTargetX, patrolTargetY; // aktualny punkt docelowy patrolu
    private final Random random = new Random();


    private Image dir0, dir1, dir2, dir3, dir4, dir5, dir6, dir7;
    private Image dir8, dir9, dir10, dir11, dir12, dir13, dir14, dir15;

    private int currentDirection = 0; // 0 - 15


    public EnemyBehemoth (int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        pickNewPatrolTarget(); // ustaw początkowy cel patrolu

        try {
            dir0 = ImageIO.read(getClass().getResource("/Behemoth/behemoth00.png"));
            dir1 = ImageIO.read(getClass().getResource("/Behemoth/behemoth15.png"));
            dir2 = ImageIO.read(getClass().getResource("/Behemoth/behemoth14.png"));
            dir3 = ImageIO.read(getClass().getResource("/Behemoth/behemoth13.png"));
            dir4 = ImageIO.read(getClass().getResource("/Behemoth/behemoth12.png"));
            dir5 = ImageIO.read(getClass().getResource("/Behemoth/behemoth11.png"));
            dir6 = ImageIO.read(getClass().getResource("/Behemoth/behemoth10.png"));
            dir7 = ImageIO.read(getClass().getResource("/Behemoth/behemoth09.png"));
            dir8 = ImageIO.read(getClass().getResource("/Behemoth/behemoth08.png"));
            dir9 = ImageIO.read(getClass().getResource("/Behemoth/behemoth07.png"));
            dir10 = ImageIO.read(getClass().getResource("/Behemoth/behemoth06.png"));
            dir11 = ImageIO.read(getClass().getResource("/Behemoth/behemoth05.png"));
            dir12 = ImageIO.read(getClass().getResource("/Behemoth/behemoth04.png"));
            dir13 = ImageIO.read(getClass().getResource("/Behemoth/behemoth03.png"));
            dir14 = ImageIO.read(getClass().getResource("/Behemoth/behemoth02.png"));
            dir15 = ImageIO.read(getClass().getResource("/Behemoth/behemoth01.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// /////////////////////// to jest dzial rysowania obiektu //////////////////////
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

    private void updateDirection(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        double angle = Math.atan2(-dy, dx); // -dy bo Y idzie w dół
        angle = Math.toDegrees(angle);
        if (angle < 0) angle += 360;

        currentDirection = (int) Math.round(angle / 22.5) % 16;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
            updateDirection(dx, dy); // 🔽 tu ustawiamy kierunek
            x += (int) (speed * dx / distance);
            y += (int) (speed * dy / distance);
        } else {
            pickNewPatrolTarget(); // cel osiągnięty → losujemy nowy
        }
    }
    public void updateFly(long deltaTime) {
        // 🔁 Aktualizacja efektu "unoszenia się"
        hoverTime += deltaTime;
        hoverOffset = Math.sin(hoverTime * hoverSpeed) * hoverAmplitude;

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

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Image currentImg = getCurrentImage();
        if (currentImg != null) {
            int drawX = x;
            int drawY = (int)(y + hoverOffset); // ✨ tu efekt unoszenia

            g2d.drawImage(currentImg, drawX, drawY, width, height, null);
        }

        // Pasek życia
        g.setColor(Color.GREEN);
        int maxHealth = 70;
        int healthBarWidth = 100;
        int currentHealthWidth = (int)((health / (double)maxHealth) * healthBarWidth);
        g.fillRect(x, (int)(y + hoverOffset) - 5, currentHealthWidth, 3);
    }
    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }
    private void regenerateHealth() {
        long currentTime = System.currentTimeMillis();
        if (health < maxHealth && currentTime - lastRegenTime >= regenCooldown) {
            health++;
            lastRegenTime = currentTime;
        }
    }
    public boolean isDead() {
        return dead;
    }

    public void markAsDead() {
        this.dead = true;
    }


    /// /////////// sekcja strzelania
    ///
    ///

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
    public boolean isInRange(Valkiria valkiria) {  // 🆕 BuilderVehicle
        int dx = valkiria.getX() - x;
        int dy = valkiria.getY() - y;
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
        for (Valkiria valkiria : valkirias){
            if (isInRange(valkiria)){
                currentTarget = valkiria;
                return;
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
            ArrayList<Valkiria> valkirias,
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
                (currentTarget instanceof BuilderVehicle && !builderVehicles.contains(currentTarget)) ||
                (currentTarget instanceof Artylery && !artyleries.contains(currentTarget))||
                (currentTarget instanceof Baracks && !baracks.contains(currentTarget))||
                (currentTarget instanceof Valkiria && !valkirias.contains(currentTarget))||

                !(currentTarget instanceof Soldier soldier && isInRange(soldier)) &&
                        !(currentTarget instanceof SoldierBot soldierBot&& isInRange(soldierBot)) &&
                        !(currentTarget instanceof BattleVehicle battleVehicle && isInRange(battleVehicle)) &&
                        !(currentTarget instanceof Factory factory && isInRange(factory)) &&
                        !(currentTarget instanceof PowerPlant powerPlant && isInRange(powerPlant)) &&
                        !(currentTarget instanceof BuilderVehicle builderVehicle && isInRange(builderVehicle)) &&
                        !(currentTarget instanceof Artylery artylery && isInRange(artylery)) &&
                        !(currentTarget instanceof  Baracks b && isInRange(b)) &&
                        !(currentTarget instanceof  Valkiria v && isInRange(v))

        )
        {
            chooseTarget(
                    soldiers,
                    valkirias,
                    soldierBots,
                    battleVehicles,
                    factories,
                    powerPlants,
                    builderVehicles,
                    artyleries,
                    baracks
            );
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
            if (currentTarget instanceof Valkiria valkiria) {
                if (isInRange(valkiria)) {
                    projectiles.add(new Projectile(x + 15, y + 15, valkiria.getX() + 15, valkiria.getY() + 15));
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
                       List<Valkiria> valkirias,
                       List<EnemyHunter> enemies,
                        List<Explosion> explosions){

        // jeśli ktoś jest w aggroRange → atakuj
        Soldier closestSoldier = getClosestSoldier(soldiers);
        BattleVehicle closestVehicle = getClosestBattleVehicle(battleVehicles);
        SoldierBot closestBot = getClosestSoldierBot(soldierBots);
        Valkiria closestValkiria = getClosestValkiria(valkirias);

        if (closestSoldier != null || closestVehicle != null || closestBot != null || closestValkiria != null) {
            // atak jak wcześniej
            moveTowardsSoldier(soldiers);
            moveTowardsBattleVehicle(battleVehicles);
            moveTowardsArtylery(artylerys);
            moveTowardsPowerPlant(powerPlants);
            moveTowardsHarvester(harvesters);
            moveTowardsBuilderVehicle(builderVehicles);
            moveTowardsValkiria(valkirias);
            moveTowardsFactory(factorys);
            moveTowardsSoldierBot(soldierBots);
            attackClosestSoldier(soldiers, soldierBots, valkirias, builderVehicles, explosions);
        } else {
            // jeśli nikt nie jest w zasięgu → patrol
            movePatrol();
            regenerateHealth();
        }
    }

    /// ///////////////tu jest to co atakuje jak dotknienb
    private void attackClosestSoldier(List<Soldier> soldiers, List<SoldierBot> soldierBots, List<Valkiria> valkirias, List<BuilderVehicle> builderVehicles, List<Explosion> explosions ) {
        Soldier closestSoldier = getClosestSoldier(soldiers);
        SoldierBot closestSoldierBot = getClosestSoldierBot(soldierBots);
        Valkiria closestValkiria = getClosestValkiria(valkirias);
        BuilderVehicle closestBuilder = getClosestBuldierVehicle(builderVehicles);

        if (closestSoldier != null && getBounds().intersects(closestSoldier.getBounds())) {
            boolean dead = closestSoldier.takeDamage(); // zadaj 1 dmg (albo więcej)

            if (dead) {
                soldiers.remove(closestSoldier);
                explosions.add(new Explosion(closestSoldier.getX(), closestSoldier.getY()));

            }
        }
        if (closestSoldierBot != null && getBounds().intersects(closestSoldierBot.getBounds())) {
            boolean dead = closestSoldierBot.takeDamage(); // zadaj 1 dmg (albo więcej)

            if (dead) {
                soldierBots.remove(closestSoldierBot);
                explosions.add(new Explosion(closestSoldierBot.getX(), closestSoldierBot.getY()));

            }
        }
        if (closestValkiria != null && getBounds().intersects(closestValkiria.getBounds())) {
            boolean dead = closestValkiria.takeDamage(); // zadaj 1 dmg (albo więcej)

            if (dead) {
                valkirias.remove(closestValkiria);
                explosions.add(new Explosion(closestValkiria.getX(), closestValkiria.getY()));

            }
        }
        // Atak na BuilderVehicle
        if (closestBuilder != null && getBounds().intersects(closestBuilder.getBounds())) {
            boolean dead = closestBuilder.takeDamage();

            if (dead) {
                builderVehicles.remove(closestBuilder);
                explosions.add(new Explosion(closestBuilder.getX(), closestBuilder.getY()));

            }
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
    private Valkiria getClosestValkiria(java.util.List<Valkiria> valkirias) {
        Valkiria closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Valkiria valkiria : valkirias) {
            double distance = valkiria.getPosition().distance(x, y);
            if (distance < minDistance && distance <= aggroRange) { // tylko w zasięgu!
                minDistance = distance;
                closest = valkiria;
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }

        }
    }
    public void moveTowardsValkiria(List<Valkiria> valkiriass) {
        Valkiria closestValkiria = getClosestValkiria(valkiriass);

        if (closestValkiria != null) {
            int dx = closestValkiria.getX() - x;
            int dy = closestValkiria.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
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
                updateDirection(dx, dy); // 🔽 dodaj tutaj
                x += (int) (speed * dx / distance);
                y += (int) (speed * dy / distance);
            }
        }

    }

}
