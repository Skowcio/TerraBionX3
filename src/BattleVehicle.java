import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


public class BattleVehicle {
    private int x, y;
    private double angle; // kąt obrotu armatki
    private Point target;
    private final int width = 50;
    private final int height = 50;
    private boolean selected;
    private final int range = 240; // Zasięg ataku
    private final int shootCooldown = 750; // Czas odnowienia strzału (w ms)
    private long lastShotTime = 0; // Czas ostatniego strzału
    private List<Bullet> bullets; // Lista pocisków
    private Object currentTarget; // Aktualny cel (Enemy lub EnemyToo)
    private int health = 5;
    private BufferedImage turretImage; // Obraz wieży
    private BufferedImage vehicleImage;
    private double vehicleAngle; // Kąt obrotu pojazdu
    private double targetTurretAngle;  // Docelowy kąt obrotu wieży
    private double targetVehicleAngle; // Docelowy kąt obrotu podwozia
    private final double rotationTime = 800; // Czas obrotu w milisekundach (0.8 sekundy)
    private double rotationStartTime = -1;  // Czas rozpoczęcia obrotu

    private final double rotationSpeed = Math.toRadians(90.0 / 1000.0); // 90 stopni na sekundę, przelicz na radiany
    private final double vehicleRotationSpeed = Math.toRadians(60.0 / 1000.0); // 60°/sekundę


    public boolean takeDamage() {
        health--;
        return health <= 0;
    }

    public BattleVehicle(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
        try {
            vehicleImage = ImageIO.read(getClass().getResource("TowerM1A1/chalanger2.png"));
            turretImage = ImageIO.read(getClass().getResource("/TowerM1A1/chalangerTower.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary pojazdu
    }


    public Point getPosition() {
        return new Point(x, y);
    }

    public void setPosition(int x, int y, ArrayList<PowerPlant> powerPlants, ArrayList<Baracks> baracks, ArrayList<Soldier> soldiers, ArrayList<Hive> hives, ArrayList<BattleVehicle> battleVehicles) {
        for (PowerPlant powerPlant : powerPlants) {
            // Jeśli docelowa pozycja koliduje z elektrownią, nie wykonuj ruchu
            if (isCollidingWithPowerPlant(powerPlant, x, y)) {
                return; // Nie wykonuj ruchu
            }
        }
        for (Soldier soldier : soldiers){
            if (isCollidingWithSoldier(soldier, x, y)){
                return;
            }
        }
        for (Baracks barack : baracks){
            if (isCollidingWithBarack(barack, x, y)){
                return;
            }
        }
        for (Hive hive : hives) {
            if(isCollidingWithHive(hive, x, y)){
                return;
            }
        }
        for (BattleVehicle battleVehicle : battleVehicles){
            if(isCollidingWithBattleV(battleVehicle, x, y))
            {
                return;
            }
        }

        this.x = x;
        this.y = y;
    }
    private boolean isCollidingWithBattleV(BattleVehicle battleVehicle, int targetX, int targetY){
        if (this == battleVehicle) { //
            return false; // Ignoruje kolizję z samym sobą
        }
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle battleBounds = battleVehicle.getBounds();
        return targetBounds.intersects(battleBounds);
    }
    private boolean isCollidingWithBarack(Baracks barack, int targetX, int targetY) {

        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle barackBounds = barack.getBounds();
        return targetBounds.intersects(barackBounds);
    }

    private boolean isCollidingWithPowerPlant(PowerPlant powerPlant, int targetX, int targetY) {
        // Tworzymy prostokąt reprezentujący nową pozycję żołnierza
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);

        // Pobieramy granice elektrowni
        Rectangle powerPlantBounds = powerPlant.getBounds();

        // Sprawdzamy, czy nowa pozycja żołnierza nachodzi na elektrownię
        return targetBounds.intersects(powerPlantBounds);
    }
    private boolean isCollidingWithSoldier(Soldier soldier, int targetX, int targetY) {
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle soldierBounds = soldier.getBounds();
        return targetBounds.intersects(soldierBounds);
    }
    private  boolean isCollidingWithHive(Hive hive, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle hiveBounds = hive.getBounds();
        return targetBounds.intersects(hiveBounds);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Point getTarget() {
        return target;

    }


    public boolean isInRange(Enemy enemy) {
        int dx = enemy.getX() - x;
        int dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(Hive hive){
        int dx = hive.getX() - x;
        int dy = hive.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(EnemyShooter enemyShooter){
        int dx = enemyShooter.getX() - x;
        int dy = enemyShooter.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(EnemyHunter enemyHunters) {
        int dx = enemyHunters.getX() - x;
        int dy = enemyHunters.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyToo enemyToos) {
        int dx = enemyToos.getX() - x;
        int dy = enemyToos.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public void shoot(
            Graphics g,
            ArrayList<Bullet> Bullets,
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
                Bullets.add(new Bullet(startX, startY, e.getX() + 15, e.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyToo et && isInRange(et)) {
                Bullets.add(new Bullet(startX, startY, et.getX() + 15, et.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof Hive h && isInRange(h)) {
                Bullets.add(new Bullet(startX, startY, h.getX() + 15, h.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyShooter es && isInRange(es)) {
                Bullets.add(new Bullet(startX, startY, es.getX() + 15, es.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyHunter eh && isInRange(eh)) {
                Bullets.add(new Bullet(startX, startY, eh.getX() + 15, eh.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }

            lastShotTime = currentTime;
        }
    }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters) {
        currentTarget = null;

        // Szukaj najbliższego Enemy w zasięgu
        for (Enemy enemy : enemies) {
            if (isInRange(enemy)) {
                currentTarget = enemy;
                return; // Znaleziono cel
            }
        }
        for (EnemyShooter enemyShooter : enemyShooters) {
            if (isInRange(enemyShooter)) {
                currentTarget = enemyShooter;
                return; // Znaleziono cel
            }
        }
        for (EnemyHunter enemyHunter : enemyHunters) {
            if (isInRange(enemyHunter)) {
                currentTarget = enemyHunter;
                return; // Znaleziono cel
            }
        }

        // Jeśli nie ma Enemy, szukaj EnemyToo
        for (EnemyToo enemyToo : enemyToos) {
            if (isInRange(enemyToo)) {
                currentTarget = enemyToo;
                return; // Znaleziono cel
            }
        }
        for (Hive hive : hives) {
            if (isInRange(hive)) {
                currentTarget = hive;
                return; // Znaleziono cel
            }
        }
    }

    public void setTarget(Point target) {
        this.target = target;
        if (target != null) {
            // Oblicz docelowy kąt pojazdu, ale NIE zmieniaj od razu vehicleAngle!
            int dx = target.x - (x + width / 2);
            int dy = target.y - (y + height / 2);
            targetVehicleAngle = Math.atan2(dy, dx);

            // Resetujemy timer rotacji, żeby zaczął się nowy płynny obrót
            rotationStartTime = -1;
        }
    }

    public void update(long deltaTime) {
        // 🔥 Obracanie pojazdu w kierunku celu (jeśli ma się ruszyć)
        if (target != null) {
            rotateVehicleTowardsTarget(deltaTime);
        }

        // 🔥 Obracanie wieży w kierunku przeciwnika (jeśli jest cel)
        if (currentTarget != null) {
            rotateTurretTowardsEnemy(deltaTime);
        }
    }

    // Tu PRACUJ NAD PLYNNYM RUCHEM podwozia
    private void rotateVehicleTowardsTarget(long deltaTime) {
        int dx = target.x - (x + width / 2);
        int dy = target.y - (y + height / 2);
        targetVehicleAngle = Math.atan2(dy, dx);

        if (rotationStartTime == -1) {
            rotationStartTime = System.currentTimeMillis(); // Startujemy obrót
        }

        double elapsedTime = System.currentTimeMillis() - rotationStartTime;
        double progress = Math.min(elapsedTime / rotationTime, 1.0); // 0.0 -> 1.0 (w ciągu 0.8s)

        // Obliczamy płynny obrót pojazdu zamiast nagłego skoku
        vehicleAngle = interpolateAngle(vehicleAngle, targetVehicleAngle, progress);

        if (progress >= 1.0) {
            rotationStartTime = -1; // Resetujemy licznik obrotu po zakończeniu
        }
    }

    // Funkcja do płynnej interpolacji kątów (uwzględnia przejścia przez 0°/360°)
    private double interpolateAngle(double from, double to, double progress) {
        double difference = to - from;

        if (difference > Math.PI) {
            difference -= 2 * Math.PI;
        } else if (difference < -Math.PI) {
            difference += 2 * Math.PI;
        }

        return from + difference * progress;
    }

//    public void setTarget(Point target) {
//        this.target = target;
//        if (target != null) {
//            // Oblicz kąt dla pojazdu
//            int dx = target.x - (x + width / 2);
//            int dy = target.y - (y + height / 2);
////            vehicleAngle = Math.atan2(dy, dx); tak bylo by poprostu skakal pojazd
//            targetVehicleAngle = Math.atan2(dy, dx);
//
//            if (targetVehicleAngle < 0) {
//                targetVehicleAngle += 2 * Math.PI;
//            }
//            if (vehicleAngle < 0) {
//                vehicleAngle += 2 * Math.PI;
//            }
//            // Różnica kątowa
//            double angleDifference = targetVehicleAngle - vehicleAngle;
//
//            // Dopasowanie różnicy kątowej do zakresu -PI do PI (najkrótsza droga)
//            if (angleDifference > Math.PI) {
//                angleDifference -= 2 * Math.PI;
//            } else if (angleDifference < -Math.PI) {
//                angleDifference += 2 * Math.PI;
//            }
//            // Płynne obracanie wieży z poprawioną prędkością
//            double maxRotationThisFrame = vehicleRotationSpeed * deltaTime;
//            if (Math.abs(angleDifference) > maxRotationThisFrame) {
//                vehicleAngle += Math.signum(angleDifference) * maxRotationThisFrame;
//            } else {
//                vehicleAngle = targetTurretAngle;  // Jeśli jesteśmy bardzo blisko, ustaw dokładnie na cel
//            }
//
//            // Upewnij się, że kąt mieści się w zakresie 0 - 2*PI
//            if (vehicleAngle < 0) {
//                vehicleAngle += 2 * Math.PI;
//            } else if (vehicleAngle > 2 * Math.PI) {
//                vehicleAngle -= 2 * Math.PI;
//            }
//        }
//    }

    private void rotateTurretTowardsEnemy(long deltaTime) {

        if (currentTarget != null) {
            int targetX = 0;
            int targetY = 0;

            if (currentTarget instanceof Enemy enemy) {
                targetX = enemy.getX();
                targetY = enemy.getY();
            } else if (currentTarget instanceof EnemyToo enemyToo) {
                targetX = enemyToo.getX();
                targetY = enemyToo.getY();
            } else if (currentTarget instanceof Hive hive) {
                targetX = hive.getX();
                targetY = hive.getY();
            } else if (currentTarget instanceof EnemyShooter enemyShooter) {
                targetX = enemyShooter.getX();
                targetY = enemyShooter.getY();
            }

            // Oblicz docelowy kąt obrotu wieży do przeciwnika
            int dx = targetX - (x + width / 2);
            int dy = targetY - (y + height / 2);
            targetTurretAngle = Math.atan2(dy, dx);

            // Konwersja kąta do zakresu 0 - 2*PI
            if (targetTurretAngle < 0) {
                targetTurretAngle += 2 * Math.PI;
            }
            if (angle < 0) {
                angle += 2 * Math.PI;
            }

            // Różnica kątowa
            double angleDifference = targetTurretAngle - angle;

            // Dopasowanie różnicy kątowej do zakresu -PI do PI (najkrótsza droga)
            if (angleDifference > Math.PI) {
                angleDifference -= 2 * Math.PI;
            } else if (angleDifference < -Math.PI) {
                angleDifference += 2 * Math.PI;
            }

// Płynne obracanie wieży z poprawioną prędkością
            double maxRotationThisFrame = rotationSpeed * deltaTime;
            if (Math.abs(angleDifference) > maxRotationThisFrame) {
                angle += Math.signum(angleDifference) * maxRotationThisFrame;
            } else {
                angle = targetTurretAngle;  // Jeśli jesteśmy bardzo blisko, ustaw dokładnie na cel
            }

            // Upewnij się, że kąt mieści się w zakresie 0 - 2*PI
            if (angle < 0) {
                angle += 2 * Math.PI;
            } else if (angle > 2 * Math.PI) {
                angle -= 2 * Math.PI;
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Rysowanie pojazdu z obrotem
        if (vehicleImage != null) {
            AffineTransform transform = new AffineTransform();

            // Ustaw punkt obrotu na środek pojazdu
            transform.translate(x + vehicleImage.getWidth() / 2.0, y + vehicleImage.getHeight() / 2.0);
            transform.rotate(vehicleAngle); // Obrót obrazu pojazdu
            transform.translate(-vehicleImage.getWidth() / 2.0, -vehicleImage.getHeight() / 2.0);

            // Rysuj obraz pojazdu
            g2d.drawImage(vehicleImage, transform, null);
        }

        // Pasek zdrowia
        int maxHealth = 5; // Maksymalne zdrowie
        int healthBarWidth = 25; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        // Rysowanie obramowania, jeśli pojazd jest wybrany
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, vehicleImage.getWidth() + 4, vehicleImage.getHeight() + 4);
        }

        // Rysowanie wieży (obraz)
        if (turretImage != null) {
            AffineTransform transform = new AffineTransform();

            // Ustaw punkt obrotu na środek pojazdu
            transform.translate(x + vehicleImage.getWidth() / 2.0, y + vehicleImage.getHeight() / 2.0);
            transform.rotate(angle); // Obrót obrazu
            transform.translate(-turretImage.getWidth() / 4.0, -turretImage.getHeight() / 2.0);

            // Rysuj obraz
            g2d.drawImage(turretImage, transform, null);
        }
    }
}
