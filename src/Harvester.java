import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Harvester {

    private int x, y; // Pozycja
    private int width = 30, height = 30; // Rozmiar
    private int speed = 1; // Prędkość poruszania
    private boolean selected;
    private Point target;

    public Harvester(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.target = null;
    }

    // Pobieranie obszaru zajmowanego przez Harvestera
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }



    // Gettery
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



    // Rysowanie Harvestera
    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(Color.GRAY);
            g.drawRect(x - 2, y - 2, 35, 35);
        }
    }
}
