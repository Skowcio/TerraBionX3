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


public class Soldier {
    private int x, y;
    private boolean selected;
    private int width = 50, height = 50;
    private int health = 5;
    private boolean dead = false;
    private Point target;
    private final int range = 220;
    private final int shootCooldown = 850;
    private Object currentTarget;
    private long lastShotTime = 0;

    private double hoverOffset = 0;           // Przesunięcie do rysowania w pionie
    private double hoverTime = 0;             // Czas do animacji unoszenia
    private final double hoverSpeed = 0.003;  // Im mniejsze, tym wolniejsze falowanie
    private final int hoverAmplitude = 4;

    // --- kierunki ---
    private Image[] directionSprites = new Image[16]; // 16 sprite’ów co 22,5°
    private int currentDirectionIndex = 0; // 0–15




    public Soldier(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;

        // Ładowanie grafik – nazwy plików: APC0.png ... APC15.png
        try {
            for (int i = 0; i < 16; i++) {
                directionSprites[i] = ImageIO.read(getClass().getResource("/jetBlue/jet" + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void resolveSoftOverlap(ArrayList<Soldier> allSoldiers) {
        for (Soldier other : allSoldiers) {
            if (this == other) continue; // pomijamy siebie samego

            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Odległość, w której zaczyna się "rozpychanie"
            double minDistance = 35.0; // można regulować – mniejsze = gęściej się układają

            if (distance > 0 && distance < minDistance) {
                double overlap = minDistance - distance;

                // kierunek odpychania
                double pushX = (dx / distance) * overlap * 0.5;
                double pushY = (dy / distance) * overlap * 0.5;

                // odsuń obie jednostki
                this.x += pushX;
                this.y += pushY;
                other.x -= pushX;
                other.y -= pushY;
            }
        }
    }

    public void resolveSoftOverlapWithValkirias(ArrayList<Valkiria> valkirias) {
        for (Valkiria valkiria : valkirias) {
            double dx = this.x - valkiria.getX();
            double dy = this.y - valkiria.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            double minDistance = 45.0; // dystans "komfortowy", można regulować

            if (distance > 0 && distance < minDistance) {
                double overlap = minDistance - distance;

                // kierunek odpychania
                double pushX = (dx / distance) * overlap * 0.5;
                double pushY = (dy / distance) * overlap * 0.5;

                // odsuń tylko żołnierza (Valkiria raczej stoi w miejscu)
                this.x += pushX;
                this.y += pushY;
            }
        }
    }

//    public void resolveHardOverlap(ArrayList<Soldier> allSoldiers) {
//        for (Soldier other : allSoldiers) {
//            if (this == other) continue;
//
//            // Mogą na siebie nachodzić częściowo (allowedBounds)
//            if (getAllowedBounds().intersects(other.getAllowedBounds())) {
//                // ❌ Ale jeśli pełne hitboxy się stykają → teleport
//                if (getStrictBounds().intersects(other.getStrictBounds())) {
//                    teleportAway(allSoldiers);
//                }
//            }
//        }
//    }
//
//    private void teleportAway(ArrayList<Soldier> allSoldiers) {
//        int offset = 50;
//        int dir = (int)(Math.random() * 8);
//
//        int nx = x;
//        int ny = y;
//
//        switch (dir) {
//            case 0 -> ny -= offset;
//            case 1 -> { ny -= offset; nx += offset; }
//            case 2 -> nx += offset;
//            case 3 -> { ny += offset; nx += offset; }
//            case 4 -> ny += offset;
//            case 5 -> { ny += offset; nx -= offset; }
//            case 6 -> nx -= offset;
//            case 7 -> { ny -= offset; nx -= offset; }
//        }
//
//        Rectangle newBounds = new Rectangle(nx, ny, width, height);
//
//        // 🔹 sprawdzamy czy nowe miejsce nie koliduje z innymi
//        boolean collision = false;
//        for (Soldier other : allSoldiers) {
//            if (this == other) continue;
//            if (newBounds.intersects(other.getStrictBounds())) {
//                collision = true;
//                break;
//            }
//        }
//
//        if (!collision) {
//            x = nx;
//            y = ny;
//        }
//    }
    /// /////////////////////////////////
    // to jest kierunek Soldiera po lodagame - z tego co czaje?

    public void setPosition(int x, int y,
                            ArrayList<PowerPlant> powerPlants,
                            ArrayList<Baracks> baracks,
                            ArrayList<Soldier> soldiers,
                            ArrayList<Hive> hives,
                            ArrayList<Valkiria> valkirias,
                            ArrayList<BattleVehicle> battleVehicles) {
        // Kolizje (zostawiłem tak jak było)
//        for (PowerPlant p : powerPlants) if (isCollidingWithPowerPlant(p, x, y)) return;
//        for (Soldier s : soldiers) if (isCollidingWithSoldier(s, x, y)) return;
//        for (Baracks b : baracks) if (isCollidingWithBarack(b, x, y)) return;
        for (Hive h : hives) if (isCollidingWithHive(h, x, y)) return;
//        for (BattleVehicle bv : battleVehicles) if (isCollidingWithBattleV(bv, x, y)) return;
        for (Valkiria v : valkirias) if (isCollidingWithValkiria(v, x, y)) return;

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
        if (this == soldier) return false; // ignoruj kolizję z samym sobą

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
    /// //////////////////////
    /// ////////////////
    ///
    /// SPECJALNA METODA NA DUZE OBIEKTY
    ///
    /// ///////////////////////////////
// --- helper: zwraca dystans od środka żołnierza do najbliższego punktu prostokąta obiektu ---
private double distanceToRectEdge(int ox, int oy, int ow, int oh) {
    int sx = x + width / 2;   // środek żołnierza
    int sy = y + height / 2;

    int left = ox;
    int right = ox + ow;
    int top = oy;
    int bottom = oy + oh;

    int closestX = Math.max(left, Math.min(sx, right));
    int closestY = Math.max(top, Math.min(sy, bottom));

    double dx = closestX - sx;
    double dy = closestY - sy;
    return Math.sqrt(dx*dx + dy*dy);
}

    // --- uniwersalna metoda sprawdzania zasięgu względem prostokąta (krawędź) ---
    private boolean isInRangeRectEdge(int ox, int oy, int ow, int oh) {
        return distanceToRectEdge(ox, oy, ow, oh) <= range;
    }

    public boolean isInRange(QubeFactory qubeFactory) {
        return isInRangeRectEdge(qubeFactory.getX(), qubeFactory.getY(), qubeFactory.getWidth(), qubeFactory.getHeight());
    }
/// //////////////////////////////
///
///
/// ///////////////////////////

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
    public boolean isInRange(EnemyBehemoth enemyBehemoth) {
        int dx = enemyBehemoth.getX() - x;
        int dy = enemyBehemoth.getY() - y;
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
    public boolean isInRange(Qube qubes) {
        int dx = qubes.getX() - x;
        int dy = qubes.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    public boolean isInRange(QubeTower qubeTower) {
        int dx = qubeTower.getX() - x;
        int dy = qubeTower.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

//    public boolean isInRange(QubeFactory qubeFactory) {
//        int dx = qubeFactory.getX() - x;
//        int dy = qubeFactory.getY() - y;
//        return Math.sqrt(dx * dx + dy * dy) <= range;
//    }



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
            ArrayList<QubeFactory> qubeFactorys,
            int cameraX, int cameraY,
            int screenWidth, int screenHeight
    ) {
        long currentTime = System.currentTimeMillis();

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
        if (currentTarget instanceof  QubeFactory qf && !qubeFactorys.contains(qf)) outOfRange = true;

        boolean notInRange =
                !(currentTarget instanceof Enemy e && isInRange(e)) &&
                        !(currentTarget instanceof EnemyToo et && isInRange(et)) &&
                        !(currentTarget instanceof Hive h && isInRange(h)) &&
                        !(currentTarget instanceof HiveToo ht && isInRange(ht)) &&
                        !(currentTarget instanceof EnemyShooter es && isInRange(es)) &&
                        !(currentTarget instanceof EnemyHunter eh && isInRange(eh)) &&
                        !(currentTarget instanceof EnemyBehemoth eb && isInRange(eb)) &&
                        !(currentTarget instanceof Qube q && isInRange(q))&&
                        !(currentTarget instanceof QubeTower qt && isInRange(qt))&&
        !(currentTarget instanceof QubeFactory qf && isInRange(qf));

        if (currentTarget == null || outOfRange || notInRange) {
            chooseTarget(enemies, enemyToos, hives, hiveToos, enemyShooters, enemyHunters, enemyBehemoths, qubes, qubeTowers, qubeFactorys);
        }

        if (currentTarget != null && currentTime - lastShotTime >= shootCooldown) {

            // punkt startu pocisku (środek żołnierza)
            int startX = x + width / 2;
            int startY = y + height / 2;

            // --- UNIWERSALNE CELUJ W ŚRODEK DOWOLNEGO OBIEKTU ---
            int tx = 0, ty = 0;

            if (currentTarget instanceof Enemy e) {
                tx = e.getX() + e.getWidth() / 2;
                ty = e.getY() + e.getHeight() / 2;
            } else if (currentTarget instanceof EnemyToo et) {
                tx = et.getX() + et.getWidth() / 2;
                ty = et.getY() + et.getHeight() / 2;
            } else if (currentTarget instanceof Hive h) {
                tx = h.getX() + h.getWidth() / 2;
                ty = h.getY() + h.getHeight() / 2;
            } else if (currentTarget instanceof HiveToo ht) {
                tx = ht.getX() + ht.getWidth() / 2;
                ty = ht.getY() + ht.getHeight() / 2;
            } else if (currentTarget instanceof EnemyShooter es) {
                tx = es.getX() + es.getWidth() / 2;
                ty = es.getY() + es.getHeight() / 2;
            } else if (currentTarget instanceof EnemyHunter eh) {
                tx = eh.getX() + eh.getWidth() / 2;
                ty = eh.getY() + eh.getHeight() / 2;
            } else if (currentTarget instanceof EnemyBehemoth eb) {
                tx = eb.getX() + eb.getWidth() / 2;
                ty = eb.getY() + eb.getHeight() / 2;
            } else if (currentTarget instanceof Qube q) {
                tx = q.getX() + q.getWidth() / 2;
                ty = q.getY() + q.getHeight() / 2;
            } else if (currentTarget instanceof QubeTower qt) {
                tx = qt.getX() + qt.getWidth() / 2;
                ty = qt.getY() + qt.getHeight() / 2;
            }else if (currentTarget instanceof QubeFactory qf) {
                tx = qf.getX() + qf.getWidth() / 2;
                ty = qf.getY() + qf.getHeight() / 2;
            }

            // --- STRZAŁ ---
            Bullets.add(new Bullet(startX, startY, tx, ty, cameraX, cameraY, screenWidth, screenHeight));

            lastShotTime = currentTime;
        }
    }


    private void chooseTarget(ArrayList<Enemy> enemies, ArrayList<EnemyToo> enemyToos, ArrayList<Hive> hives, ArrayList<HiveToo> hiveToos, ArrayList<EnemyShooter> enemyShooters, ArrayList<EnemyHunter> enemyHunters, ArrayList<EnemyBehemoth> enemyBehemoths, ArrayList<Qube> qubes,  ArrayList<QubeTower> qubeTowers, ArrayList<QubeFactory> qubeFactorys) {
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
        for (QubeFactory qubeFactory : qubeFactorys) {
            if (isInRange(qubeFactory)) {
                currentTarget = qubeFactory;
                return; // Znaleziono cel
            }
        }
    }



    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // ✨ przesunięcie w osi Y o hoverOffset
        int drawX = x;
        int drawY = (int)(y + hoverOffset);
        // --- CIEŃ POD ŻOŁNIERZEM ---
        g2d.setColor(new Color(0, 0, 0, 85));
        int shadowW = width;
        int shadowH = 15;

        int shadowX = x;
        int shadowY = y + height - 10;

        g2d.rotate(Math.toRadians(currentDirectionIndex * 22.5), shadowX + shadowW/2, shadowY + shadowH/2);
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(shadowX, shadowY, shadowW, shadowH);
// cofnięcie obrotu
        g2d.rotate(-Math.toRadians(currentDirectionIndex * 22.5), shadowX + shadowW/2, shadowY + shadowH/2);

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

        // Pasek życia (nad sprite'em, unoszony razem z nim)
        g2d.setColor(Color.GREEN);
        int maxHealth = 5;
        int healthBarWidth = 50;
        int currentHealthWidth = (int)((health / (double)maxHealth) * healthBarWidth);
        g2d.fillRect(drawX, drawY - 5, currentHealthWidth, 3);
    }
}