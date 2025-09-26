import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    private int x, y;
    private final int range = 250; // Zasięg strzelania w pikselach
    private final int width = 30, height = 30;
    private int health = 10;
    private int speed = 1;
    private Random random = new Random();

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
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

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);

        int maxHealth = 10; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 30; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getter pozycji jako obiekt Point
    public Point getPosition() {
        return new Point(x, y);
    }

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }

    public boolean takeDamage2() {
        health -= 3;
        return health <= 0; // Zwraca true, jeśli Enemy zostało zniszczone
    }

    public void move() {
        // Losowy ruch w górę, w dół, w lewo lub w prawo
        int direction = random.nextInt(5);
        switch (direction) {
            case 0 -> x += speed; // prawo
            case 4 -> x -= speed;
            case 5 -> x -= speed;
            case 1 -> x -= speed; // lewo
            case 2 -> y += speed; // dół
            case 3 -> y -= speed; // góra
        }
    }

    public Projectile shootAtNearestSoldier(ArrayList<Soldier> soldiers) {
        Soldier nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Soldier soldier : soldiers) {
            double distance = Math.sqrt(Math.pow(soldier.getX() - x, 2) + Math.pow(soldier.getY() - y, 2));
            if (distance <= range && distance < minDistance) {
                minDistance = distance;
                nearest = soldier;
            }
        }

        if (nearest != null) {
            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
        }
        return null;
    }
    public Projectile shootAtNearestArtylery(ArrayList<Artylery> artylerys) {
        Artylery nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Artylery artylery : artylerys) {
            double distance = Math.sqrt(Math.pow(artylery.getX() - x, 2) + Math.pow(artylery.getY() - y, 2));
            if (distance <= range && distance < minDistance) {
                minDistance = distance;
                nearest = artylery;
            }
        }

        if (nearest != null) {
            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
        }
        return null;
    }

    public Projectile shootAtNearestBattleVehicle(ArrayList<BattleVehicle> battleVehicles) {
        BattleVehicle nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (BattleVehicle battleVehicle : battleVehicles) {
            double distance = Math.sqrt(Math.pow(battleVehicle.getX() - x, 2) + Math.pow(battleVehicle.getY() - y, 2));
            if (distance <= range && distance < minDistance) {
                minDistance = distance;
                nearest = battleVehicle;
            }
        }

        if (nearest != null) {
            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
        }
        return null;
    }
    public Projectile shootAtNearestBuilder(ArrayList<BuilderVehicle> builderVehicles) {
        BuilderVehicle nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (BuilderVehicle builderVehicle : builderVehicles) {
            double distance = Math.sqrt(Math.pow(builderVehicle.getX() - x, 2) + Math.pow(builderVehicle.getY() - y, 2));
            if (distance <= range && distance < minDistance) {
                minDistance = distance;
                nearest = builderVehicle;
            }
        }

        if (nearest != null) {
            return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
        }
        return null;
    }

    public Projectile shootAtNearestHarvaester(ArrayList<Harvester> harvesters){
        Harvester nearest = null;
        double minDistance = Double.MAX_VALUE;
    for (Harvester harvester : harvesters) {
        double distance = Math.sqrt(Math.pow(harvester.getX() - x, 2) + Math.pow(harvester.getY() - y, 2));
        if (distance <= range && distance < minDistance) {
            minDistance = distance;
            nearest = harvester;
        }

    }
    if (nearest != null) {
        return new Projectile(x + width / 2, y + height / 2, nearest.getX(), nearest.getY());
    }
    return null;
    }
}
