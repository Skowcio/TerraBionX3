import java.awt.*;

public class Projectile {
    private int x, y;
    private int dx, dy;
    private final int speed = 15; // Prędkość pocisku
    private final long lifetime = 3000; // Czas życia pocisku w milisekundach
    private final long creationTime;

    public Projectile(int startX, int startY, int targetX, int targetY) {
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
        g.setColor(Color.RED);
        g.fillOval(x, y, 5, 5); // Pocisk w kształcie małej kropki
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
}