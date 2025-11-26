import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Image;        // Do reprezentacji obrazów
import java.io.IOException;   // Do obsługi wyjątków podczas ładowania obrazów
// soldier uzywa bullet do strzelania


public class Valkiria {
    private int x, y;
    private boolean selected;
    private int width = 50, height = 50;
    private int health = 20;
    private final int maxHealth = 20;       // maksymalne HP
    private long lastRegenTime = 0;         // czas ostatniej regeneracji
    private final long regenCooldown = 20_000; // 10 sekund w ms
    private boolean dead = false;
    private Point target;
    private final int range = 230;
    private final int shootCooldown = 150;
    private Object currentTarget;
    private long lastShotTime = 0;

    private static int totalValkirias = 0;
    private static int MAX_VALKIRIAS = 0;

    private int magazineSize = 25;     // liczba pocisków w magazynku
    private int bulletsLeft = magazineSize; // początkowo pełny magazynek
    private boolean reloading = false;
    private long reloadStartTime = 0;
    private final long reloadTime = 8000; // czas przeładowania w ms (8 sekund)

    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.003;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;

    // --- kierunki ---
    private Image[] directionSprites = new Image[16]; // 16 sprite’ów co 22,5°
    private int currentDirectionIndex = 0; // 0–15




    public Valkiria(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;

        // dodaje valkirie do liczby max
        totalValkirias++;

        // Ładowanie grafik – nazwy plików: APC0.png ... APC15.png
        try {
            for (int i = 0; i < 16; i++) {
                directionSprites[i] = ImageIO.read(getClass().getResource("/jetBlue/jet" + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getMaxValkiriaTech() {
        return MAX_VALKIRIAS;
    }
    public static int getFactoryCount() {
        return totalValkirias;
    }
    public static void increaseMaxValkirias(int amount) {
        MAX_VALKIRIAS += amount;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
    }

    public int getCurrentDirection() {
        return currentDirectionIndex;
    }

    public int getHealth() {
        return health;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static int getTotalValkirias() {
        return totalValkirias;
    }

    public static int getMaxValkirias() {
        return MAX_VALKIRIAS;
    }


    public boolean takeDamage() {
        health--;
        if (health <= 0) {
            markAsDead();
            return true;
        }
        return false;
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

//    public void markAsDead() {
//        this.dead = true;
//    }
public void markAsDead() {
    this.dead = true;
    totalValkirias--; // zwalnia slot na nową
    if (totalValkirias < 0) totalValkirias = 0;
}

    // Getter pozycji jako obiekt Point
    public Point getPosition() {
        return new Point(x, y);
    }

    //    public void setPosition(int x, int y, ArrayList<PowerPlant> powerPlants) {
//        for (PowerPlant powerPlant : powerPlants) {
//            // Jeśli docelowa pozycja koliduje z elektrownią, nie wykonuj ruchu
//            if (isCollidingWithPowerPlant(powerPlant, x, y)) {
//                return; // Nie wykonuj ruchu
//            }
//        }
//
//        if (x > this.x) {
//            direction = Direction.RIGHT;
//        }
//        else if (x < this.x) {
//            direction = Direction.LEFT;
//        } else if (y > this.y) {
//            direction = Direction.DOWN;
//        } else if (y < this.y) {
//            direction = Direction.UP;
//        }
//
//        this.x = x;
//        this.y = y;
//    }
    public void setCurrentDirection(int directionIndex) {
        // normalizacja do 0..15
        directionIndex = ((directionIndex % 16) + 16) % 16;
        this.currentDirectionIndex = directionIndex;
    }
    /// ///////// kod odpowiedzialny za teleportacje ////

    public Rectangle getStrictBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Rectangle getAllowedBounds() {
        int collisionW = (int)(width * 0.63);   // ponad 2/3
        int collisionH = (int)(height * 0.63);  // ponad 2/3 wysokości
        int offsetX = (width - collisionW) / 2;
        int offsetY = (height - collisionH) / 2;

        return new Rectangle(x + offsetX, y + offsetY, collisionW, collisionH);
    }

    public void resolveHardOverlap(ArrayList<Valkiria> allValkirias) {
        for (Valkiria other : allValkirias) {
            if (this == other) continue;

            // Mogą na siebie nachodzić częściowo (allowedBounds)
            if (getAllowedBounds().intersects(other.getAllowedBounds())) {
                // ❌ Ale jeśli pełne hitboxy się stykają → teleport
                if (getStrictBounds().intersects(other.getStrictBounds())) {
                    teleportAway(allValkirias);
                }
            }
        }
    }

    private void teleportAway(ArrayList<Valkiria> allValkirias) {
        int offset = 50;
        int dir = (int)(Math.random() * 8);

        int nx = x;
        int ny = y;

        switch (dir) {
            case 0 -> ny -= offset;
            case 1 -> { ny -= offset; nx += offset; }
            case 2 -> nx += offset;
            case 3 -> { ny += offset; nx += offset; }
            case 4 -> ny += offset;
            case 5 -> { ny += offset; nx -= offset; }
            case 6 -> nx -= offset;
            case 7 -> { ny -= offset; nx -= offset; }
        }

        Rectangle newBounds = new Rectangle(nx, ny, width, height);

        // 🔹 sprawdzamy czy nowe miejsce nie koliduje z innymi
        boolean collision = false;
        for (Valkiria other : allValkirias) {
            if (this == other) continue;
            if (newBounds.intersects(other.getStrictBounds())) {
                collision = true;
                break;
            }
        }

        if (!collision) {
            x = nx;
            y = ny;
        }
    }
    /// /////////////////////////////////
    // to jest kierunek Soldiera po lodagame - z tego co czaje?

    public void setPosition(int x, int y,
                            ArrayList<PowerPlant> powerPlants,
                            ArrayList<Baracks> baracks,
                            ArrayList<Valkiria> valkirias,
                            ArrayList<Soldier> soldiers,
                            ArrayList<Hive> hives,
                            ArrayList<BattleVehicle> battleVehicles) {
        // Kolizje (zostawiłem tak jak było)
//        for (PowerPlant p : powerPlants) if (isCollidingWithPowerPlant(p, x, y)) return;
        for (Valkiria valkiria : valkirias) if (isCollidingWithValkiria(valkiria, x, y)) return;
//        for (Baracks b : baracks) if (isCollidingWithBarack(b, x, y)) return;
        for (Hive h : hives) if (isCollidingWithHive(h, x, y)) return;
//        for (BattleVehicle bv : battleVehicles) if (isCollidingWithBattleV(bv, x, y)) return;
        for (Soldier s : soldiers) if (isCollidingWithSoldier(s, x, y)) return;

        this.x = x;
        this.y = y;
    }

    public void updateDirection(Point delta) {
        double angle = Math.atan2(delta.y, delta.x); // -π..π
        angle = Math.toDegrees(angle); // na stopnie
        if (angle < 0) angle += 360;   // 0–360
        currentDirectionIndex = (int)Math.round(angle / 22.5) % 16;
    }

    public void updateFly(long deltaTime) {
        // 🔁 Aktualizacja efektu "unoszenia się"
        hoverTime += deltaTime;
        hoverOffset = Math.sin(hoverTime * hoverSpeed) * hoverAmplitude;
        regenerateHealth();

    }



    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean isSelected() { return selected; }
    private boolean isCollidingWithPowerPlant(PowerPlant powerPlant, int targetX, int targetY) {
        // Tworzymy prostokąt reprezentujący nową pozycję żołnierza
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);

        // Pobieramy granice elektrowni
        Rectangle powerPlantBounds = powerPlant.getBounds();

        // Sprawdzamy, czy nowa pozycja żołnierza nachodzi na elektrownię
        return targetBounds.intersects(powerPlantBounds);
    }
    private boolean isCollidingWithSoldier(Soldier soldier, int targetX, int targetY) {

        int allowedOverlap = 10; // maksymalna liczba pikseli, na którą mogą nachodzić

        // Tworzymy prostokąty kolizji, zmniejszając je o allowedOverlap
        Rectangle targetBounds = new Rectangle(
                targetX + allowedOverlap,
                targetY + allowedOverlap,
                width - 2 * allowedOverlap,
                height - 2 * allowedOverlap
        );

        Rectangle soldierBounds = new Rectangle(
                soldier.getX() + allowedOverlap,
                soldier.getY() + allowedOverlap,
                soldier.getWidth() - 2 * allowedOverlap,
                soldier.getHeight() - 2 * allowedOverlap
        );

        return targetBounds.intersects(soldierBounds);
    }
    private boolean isCollidingWithValkiria(Valkiria valkiria, int targetX, int targetY) {
        if (this == valkiria) return false; // ignoruj kolizję z samym sobą

        int allowedOverlap = 10; // maksymalna liczba pikseli, na którą mogą nachodzić

        // Tworzymy prostokąty kolizji, zmniejszając je o allowedOverlap
        Rectangle targetBounds = new Rectangle(
                targetX + allowedOverlap,
                targetY + allowedOverlap,
                width - 2 * allowedOverlap,
                height - 2 * allowedOverlap
        );

        Rectangle valkiriaBounds = new Rectangle(
                valkiria.getX() + allowedOverlap,
                valkiria.getY() + allowedOverlap,
                valkiria.getWidth() - 2 * allowedOverlap,
                valkiria.getHeight() - 2 * allowedOverlap
        );

        return targetBounds.intersects(valkiriaBounds);
    }
    private  boolean isCollidingWithHive(Hive hive, int targetX, int targetY){
        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
        Rectangle hiveBounds = hive.getBounds();
        return targetBounds.intersects(hiveBounds);
    }
//    private boolean isCollidingWithBarack(Baracks barack, int targetX, int targetY) {
//
//        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
//        Rectangle barackBounds = barack.getBounds();
//        return targetBounds.intersects(barackBounds);
//    }
//    private boolean isCollidingWithBattleV(BattleVehicle battleVehicle, int targetX, int targetY){
//        Rectangle targetBounds = new Rectangle(targetX, targetY, width, height);
//        Rectangle battleBounds = battleVehicle.getBounds();
//        return targetBounds.intersects(battleBounds);
//    }
// 🟢 Miękkie rozpychanie Valkirii z innymi Valkiriami
public void resolveSoftOverlapWithValkirias(ArrayList<Valkiria> allValkirias) {
    for (Valkiria other : allValkirias) {
        if (this == other) continue;

        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double minDistance = 55.0; // dystans komfortowy (można regulować)
        if (distance > 0 && distance < minDistance) {
            double overlap = minDistance - distance;
            double pushX = (dx / distance) * overlap * 0.5;
            double pushY = (dy / distance) * overlap * 0.5;

            this.x += pushX;
            this.y += pushY;
        }
    }
}

    // 🟢 Miękkie rozpychanie Valkirii z żołnierzami
    public void resolveSoftOverlapWithSoldiers(ArrayList<Soldier> soldiers) {
        for (Soldier soldier : soldiers) {
            double dx = this.x - soldier.getX();
            double dy = this.y - soldier.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            double minDistance = 55.0; // dystans komfortowy
            if (distance > 0 && distance < minDistance) {
                double overlap = minDistance - distance;
                double pushX = (dx / distance) * overlap * 0.5;
                double pushY = (dy / distance) * overlap * 0.5;

                this.x += pushX;
                this.y += pushY;
            }
        }
    }


    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public boolean isInRange(Enemy enemy) {
        int dx = enemy.getX() - x;
        int dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyShooter enemyShooter) {
        int dx = enemyShooter.getX() - x;
        int dy = enemyShooter.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(Hive hive){
        int dx = hive.getX() - x;
        int dy = hive.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(HiveToo hiveToo){
        int dx = hiveToo.getX() - x;
        int dy = hiveToo.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(EnemyBehemoth enemyBehemoth) {
        int dx = enemyBehemoth.getX() - x;
        int dy = enemyBehemoth.getY() - y;
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
    public boolean isInRange(Qube qube) {
        int dx = qube.getX() - x;
        int dy = qube.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }
    public boolean isInRange(QubeTower qubeTower) {
        int dx = qubeTower.getX() - x;
        int dy = qubeTower.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }



    public void shoot(
            Graphics g,
            ArrayList<Bullet> Bullets,
            ArrayList<Enemy> enemies,
            ArrayList<EnemyToo> enemyToos,
            ArrayList<Hive> hives,
            ArrayList<HiveToo> hiveToos,
            ArrayList<EnemyShooter> enemyShooters,
            ArrayList<EnemyHunter> enemyHunters,
            ArrayList<EnemyBehemoth> enemyBehemoths,
            ArrayList<Qube> qubes,
            ArrayList<QubeTower> qubeTowers,
            int cameraX, int cameraY,
            int screenWidth, int screenHeight
    ) {
        long currentTime = System.currentTimeMillis();

        // Jeśli w trakcie przeładowania, sprawdzamy czy minął czas
        if (reloading) {
            if (currentTime - reloadStartTime >= reloadTime) {
                reloading = false;
                bulletsLeft = magazineSize;

            } else {
                return; // nie można strzelać w trakcie reload
            }
        }

        boolean outOfRange = false;
        if (currentTarget instanceof Enemy e && !enemies.contains(e)) outOfRange = true;
        if (currentTarget instanceof EnemyToo et && !enemyToos.contains(et)) outOfRange = true;
        if (currentTarget instanceof Hive h && !hives.contains(h)) outOfRange = true;
        if (currentTarget instanceof HiveToo ht && !hiveToos.contains(ht)) outOfRange = true;
        if (currentTarget instanceof EnemyShooter es && !enemyShooters.contains(es)) outOfRange = true;
        if (currentTarget instanceof EnemyHunter eh && !enemyHunters.contains(eh)) outOfRange = true;
        if (currentTarget instanceof  EnemyBehemoth eb && !enemyBehemoths.contains(eb)) outOfRange = true;
        if (currentTarget instanceof  Qube q && !qubes.contains(q)) outOfRange = true;
        if (currentTarget instanceof  QubeTower qt && !qubeTowers.contains(qt)) outOfRange = true;

        boolean notInRange =
                !(currentTarget instanceof Enemy e && isInRange(e)) &&
                        !(currentTarget instanceof EnemyToo et && isInRange(et)) &&
                        !(currentTarget instanceof Hive h && isInRange(h)) &&
                        !(currentTarget instanceof HiveToo ht && isInRange(ht)) &&
                        !(currentTarget instanceof EnemyShooter es && isInRange(es)) &&
                        !(currentTarget instanceof EnemyHunter eh && isInRange(eh)) &&
                        !(currentTarget instanceof EnemyBehemoth eb && isInRange(eb))&&
                         !(currentTarget instanceof Qube q && isInRange(q))&&
        !(currentTarget instanceof QubeTower qt && isInRange(qt));

        if (currentTarget == null || outOfRange || notInRange) {
            chooseTarget(enemies, enemyToos, hives, hiveToos, enemyShooters, enemyHunters, enemyBehemoths, qubes, qubeTowers);
        }

        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {
            if (bulletsLeft <= 0) {
                // Rozpocznij przeładowanie
                reloading = true;
                reloadStartTime = currentTime;

                return;
            }

            int startX = x + 15;
            int startY = y + 15;

            if (currentTarget instanceof Enemy e && isInRange(e)) {
                Bullets.add(new Bullet(startX, startY, e.getX() + 15, e.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyToo et && isInRange(et)) {
                Bullets.add(new Bullet(startX, startY, et.getX() + 15, et.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof Hive h && isInRange(h)) {
                Bullets.add(new Bullet(startX, startY, h.getX() + 15, h.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            else if (currentTarget instanceof HiveToo ht && isInRange(ht)) {
                Bullets.add(new Bullet(startX, startY, ht.getX() + 15, ht.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            else if (currentTarget instanceof EnemyShooter es && isInRange(es)) {
                Bullets.add(new Bullet(startX, startY, es.getX() + 15, es.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            } else if (currentTarget instanceof EnemyHunter eh && isInRange(eh)) {
                Bullets.add(new Bullet(startX, startY, eh.getX() + 15, eh.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            else if (currentTarget instanceof EnemyBehemoth eb && isInRange(eb)) {
                Bullets.add(new Bullet(startX, startY, eb.getX() + 15, eb.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            else if (currentTarget instanceof Qube q && isInRange(q)) {
                Bullets.add(new Bullet(startX, startY, q.getX() + 15, q.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            else if (currentTarget instanceof QubeTower qt && isInRange(qt)) {
                Bullets.add(new Bullet(startX, startY, qt.getX() + 15, qt.getY() + 15, cameraX, cameraY, screenWidth, screenHeight));
            }
            bulletsLeft--; // zmniejszamy liczbę pocisków w magazynku
            lastShotTime = currentTime;
        }
    }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<HiveToo> hiveToos, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters, ArrayList<EnemyBehemoth> enemyBehemoths, ArrayList<Qube> qubes, ArrayList<QubeTower> qubeTowers) {
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
        for (EnemyBehemoth enemyBehemoth : enemyBehemoths) {
            if (isInRange(enemyBehemoth)) {
                currentTarget = enemyBehemoth;
                return;
            }
        }
        for (Hive hive : hives) {
            if (isInRange(hive)) {
                currentTarget = hive;
                return; // Znaleziono cel
            }
        }
        for (HiveToo hiveToo : hiveToos) {
            if (isInRange(hiveToo)) {
                currentTarget = hiveToo;
                return; // Znaleziono cel
            }
        }

        for (Qube qube : qubes) {
            if (isInRange(qube)) {
                currentTarget = qube;
                return; // Znaleziono cel
            }
        }
        for (QubeTower qubeTower : qubeTowers) {
            if (isInRange(qubeTower)) {
                currentTarget = qubeTower;
                return; // Znaleziono cel
            }
        }
    }



    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // ✨ przesunięcie w osi Y o hoverOffset
        int drawX = x;
        int drawY = (int)(y + hoverOffset);

        // Rysowanie sprite'a
        Image imgToDraw = directionSprites[currentDirectionIndex];
        if (imgToDraw != null) {
            g2d.drawImage(imgToDraw, drawX, drawY, width, height, null);
        } else {
            // fallback — obrazek niezaładowany
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(drawX, drawY, width, height);
        }

        // Obrys przy zaznaczeniu
        if (selected) {
            g2d.setColor(Color.WHITE);

            int cornerSize = 8; // długość ramki narożnika
            int offset = 2;

            // Lewy górny
            g2d.drawLine(drawX - offset, drawY - offset,
                    drawX - offset + cornerSize, drawY - offset);
            g2d.drawLine(drawX - offset, drawY - offset,
                    drawX - offset, drawY - offset + cornerSize);

            // Prawy górny
            g2d.drawLine(drawX + width + offset, drawY - offset,
                    drawX + width + offset - cornerSize, drawY - offset);
            g2d.drawLine(drawX + width + offset, drawY - offset,
                    drawX + width + offset, drawY - offset + cornerSize);

            // Lewy dolny
            g2d.drawLine(drawX - offset, drawY + height + offset,
                    drawX - offset + cornerSize, drawY + height + offset);
            g2d.drawLine(drawX - offset, drawY + height + offset,
                    drawX - offset, drawY + height + offset - cornerSize);

            // Prawy dolny
            g2d.drawLine(drawX + width + offset, drawY + height + offset,
                    drawX + width + offset - cornerSize, drawY + height + offset);
            g2d.drawLine(drawX + width + offset, drawY + height + offset,
                    drawX + width + offset, drawY + height + offset - cornerSize);
        }
        // --- Liczba amunicji ---
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String ammoText = bulletsLeft + " / " + magazineSize;
        int textWidth = g2d.getFontMetrics().stringWidth(ammoText);

        // --- Pasek przeładowania (tylko jeśli trwa reload) ---
        if (reloading) {
            long currentTime = System.currentTimeMillis();
            double progress = Math.min(1.0, (double)(currentTime - reloadStartTime) / reloadTime);

            int barWidth = 20;
            int barHeight = 5;
            int barX = drawX + width - textWidth - barWidth - 5; // przed liczbą amunicji
            int barY = drawY + height - barHeight;

            // tło
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(barX, barY, barWidth, barHeight);

            // wypełnienie
            int fillWidth = (int)(barWidth * progress);
            g2d.setColor(Color.CYAN);
            g2d.fillRect(barX, barY, fillWidth, barHeight);

            // ramka
            g2d.setColor(Color.WHITE);
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }

        // --- Rysowanie napisu z amunicją ---
        g2d.setColor(Color.YELLOW);
        g2d.drawString(ammoText, drawX + width - textWidth, drawY + height);


        // Pasek życia (nad sprite'em, unoszony razem z nim)
        g2d.setColor(Color.GREEN);
        int maxHealth = 20;
        int healthBarWidth = 50;
        int currentHealthWidth = (int)((health / (double)maxHealth) * healthBarWidth);
        g2d.fillRect(drawX, drawY - 5, currentHealthWidth, 3);


    }
}



