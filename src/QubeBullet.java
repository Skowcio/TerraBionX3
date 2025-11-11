import java.awt.*;

public class QubeBullet {
    private int x, y;
    private int dx, dy;
    private final int speed = 15; // Prędkość pocisku
    private final long lifetime = 3000; // Czas życia pocisku w milisekundach
    private final long creationTime;

    public QubeBullet(int startX, int startY, int targetX, int targetY) {
        this.x = startX;
        this.y = startY;

        // Obliczamy wektor kierunku
        int deltaX = targetX - startX;
        int deltaY = targetY - startY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Normalizujemy wektor kierunku
        this.dx = (int) (speed * deltaX / distance);
        this.dy = (int) (speed * deltaY / distance);

        // Ustawiamy czas utworzenia pocisku
        this.creationTime = System.currentTimeMillis();
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Włącz wygładzanie
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Kierunek lotu (do ogona)
        double angle = Math.atan2(dy, dx);

        // Kolory plazmy: jasny rdzeń i chłodniejszy ogon
        Color coreColor = new Color(0, 255, 255, 220); // cyjanowy rdzeń
        Color tailColor = new Color(0, 100, 255, 0);   // przejście w przezroczysty niebieski

        // Gradientowy ogon (efekt komety)
        int tailLength = 40; // długość warkocza
        GradientPaint plasmaTail = new GradientPaint(
                (float) x, (float) y, coreColor,
                (float) (x - Math.cos(angle) * tailLength), (float) (y - Math.sin(angle) * tailLength),
                tailColor
        );

        g2d.setPaint(plasmaTail);
        g2d.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(
                (int) x,
                (int) y,
                (int) (x - Math.cos(angle) * tailLength),
                (int) (y - Math.sin(angle) * tailLength)
        );

        // Jasny rdzeń pocisku
        g2d.setColor(new Color(200, 255, 255, 230));
        g2d.fillOval(x - 3, y - 3, 6, 6);

        // Delikatna poświata wokół rdzenia
        for (int i = 0; i < 3; i++) {
            int radius = 10 + i * 8;
            float alpha = 0.15f - i * 0.04f;
            g2d.setColor(new Color(0f, 0.6f, 1f, alpha));
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }

        g2d.dispose();
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime > lifetime;
    }


    //////// shrink hitbox ktory ma odjete jakos 8 pxl
    ///
    // --------- Hitbox z shrink ----------
    private boolean intersectsWithShrink(int objX, int objY, int objWidth, int objHeight, int shrink) {
        int offset = shrink / 2;
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle objRect = new Rectangle(
                objX + offset,
                objY + offset,
                objWidth - shrink,
                objHeight - shrink
        );
        return projectileRect.intersects(objRect);
    }

    public boolean checkCollision(Soldier soldier) {
        return intersectsWithShrink(soldier.getX(), soldier.getY(), soldier.getWidth(), soldier.getHeight(), 8);
    }

    public boolean checkCollision(Valkiria valkiria) {
        return intersectsWithShrink(valkiria.getX(), valkiria.getY(), valkiria.getWidth(), valkiria.getHeight(), 8);
    }

    public boolean checkCollision(SoldierBot soldierBot) {
        return intersectsWithShrink(soldierBot.getX(), soldierBot.getY(), soldierBot.getWidth(), soldierBot.getHeight(), 8);
    }

    public boolean checkCollision(Baracks baracks) {
        return intersectsWithShrink(baracks.getX(), baracks.getY(), baracks.getWidth(), baracks.getHeight(), 8);
    }

    public boolean checkCollision(BattleVehicle battleVehicle) {
        return intersectsWithShrink(battleVehicle.getX(), battleVehicle.getY(), battleVehicle.getWidth(), battleVehicle.getHeight(), 8);
    }

    public boolean checkCollision(Factory factory) {
        return intersectsWithShrink(factory.getX(), factory.getY(), factory.getWidth(), factory.getHeight(), 8);
    }
    public boolean checkCollision(SteelMine steelMine) {
        return intersectsWithShrink(steelMine.getX(), steelMine.getY(), steelMine.getWidth(), steelMine.getHeight(), 8);
    }

    public boolean checkCollision(Artylery artylery) {
        return intersectsWithShrink(artylery.getX(), artylery.getY(), artylery.getWidth(), artylery.getHeight(), 8);
    }

    public boolean checkCollision(PowerPlant powerPlant) {
        return intersectsWithShrink(powerPlant.getX(), powerPlant.getY(), powerPlant.getWidth(), powerPlant.getHeight(), 8);
    }

    public boolean checkCollision(BuilderVehicle builderVehicle) {
        return intersectsWithShrink(builderVehicle.getX(), builderVehicle.getY(), builderVehicle.getWidth(), builderVehicle.getHeight(), 8);
    }

    public boolean checkCollision(Harvester harvester) {
        return intersectsWithShrink(harvester.getX(), harvester.getY(), harvester.getWidth(), harvester.getHeight(), 8);
    }

    public boolean checkCollision(EnemyToo enemyToo) {
        return intersectsWithShrink(enemyToo.getX(), enemyToo.getY(), enemyToo.getWidth(), enemyToo.getHeight(), 8);
    }
    public boolean checkCollision(EnemyShooter enemyShooter ){
        return intersectsWithShrink(enemyShooter.getX(), enemyShooter.getY(), enemyShooter.getWidth(), enemyShooter.getHeight(), 8);

    }
    public boolean checkCollision(EnemyBehemoth enemyBehemoth ){
        return intersectsWithShrink(enemyBehemoth.getX(), enemyBehemoth.getY(), enemyBehemoth.getWidth(), enemyBehemoth.getHeight(), 8);
    }
    public boolean checkCollision(Hive hive ){
        return intersectsWithShrink(hive.getX(), hive.getY(), hive.getWidth(), hive.getHeight(), 8);
    }
    public boolean checkCollision(HiveToo hiveToo ){
        return intersectsWithShrink(hiveToo.getX(), hiveToo.getY(), hiveToo.getWidth(), hiveToo.getHeight(), 8);
    }
}