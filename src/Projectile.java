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

    public boolean checkCollision(Soldier soldier) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle soldierRect = new Rectangle(soldier.getX(), soldier.getY(), 20, 20);
        return projectileRect.intersects(soldierRect);
    }
    public boolean checkCollision(SoldierBot soldierBot) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle soldierRect = new Rectangle(soldierBot.getX(), soldierBot.getY(), 20, 20);
        return projectileRect.intersects(soldierRect);
    }

    public boolean checkCollision(BattleVehicle battleVehicle) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle artyleryRect = new Rectangle(battleVehicle.getX(), battleVehicle.getY(), 20, 20);
        return projectileRect.intersects(artyleryRect);
    }
    public boolean checkCollision(Factory factory) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle artyleryRect = new Rectangle(factory.getX(), factory.getY(), 20, 20);
        return projectileRect.intersects(artyleryRect);
    }


    public boolean checkCollision(Artylery artylery) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle artyleryRect = new Rectangle(artylery.getX(), artylery.getY(), 20, 20);
        return projectileRect.intersects(artyleryRect);
    }

    public boolean checkCollision(PowerPlant powerPlant) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle powerPlantRect = new Rectangle(powerPlant.getX(), powerPlant.getY(), 20, 20);
        return projectileRect.intersects(powerPlantRect);
    }

    public boolean checkCollision(BuilderVehicle builderVehicle) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle builderVehicleRect = new Rectangle(builderVehicle.getX(), builderVehicle.getY(), 20, 20);
        return projectileRect.intersects(builderVehicleRect);
    }

    public boolean checkCollision(Harvester harvester) {
        Rectangle projectileRect = new Rectangle(x, y, 8, 8);
        Rectangle harvesterRect = new Rectangle(harvester.getX(), harvester.getY(), 30, 30);
        return projectileRect.intersects(harvesterRect);
    }
}
