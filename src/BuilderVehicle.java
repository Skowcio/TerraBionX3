import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class BuilderVehicle {
    private int x, y;
    private int width = 25, height = 25;
    private int speed = 2;
    private boolean selected;
    private int health = 5;
    private Point target;

    public boolean takeDamage() {
        health--;
        return health <= 0; // Zwraca true, jeśli Hive zostało zniszczone
    }

    public BuilderVehicle(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Getter pozycji jako obiekt Point
    public Point getPosition() {
        return new Point(x, y);
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, 35, 35);
        }



        // Pasek zdrowia
        int maxHealth = 5; // Maksymalne zdrowie
        int healthBarWidth = 25; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem
    }
}
