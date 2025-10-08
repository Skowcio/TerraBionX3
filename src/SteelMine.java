import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class  SteelMine {
    private int x, y;
    private int width = 100, height = 100;
    private boolean selected;
    private BufferedImage steelmineImage;
    private int health = 10;

    public SteelMine(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;

        try {
            steelmineImage = ImageIO.read(getClass().getResource("/steel/crystalfactory2.png"));
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

    public void destroy() {
        health = 0;
    }
    public boolean takeDamage() {
        health--;
        return health <= 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public Point getPosition() {
        return new Point(x, y);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height);
    }

    public void draw(Graphics g) {
        if (steelmineImage != null) {
            g.drawImage(steelmineImage, x, y, width, height, null);
        }
        int maxHealth = 10; // Maksymalne zdrowie
        int healthBarWidth = 100; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek zdrowia

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.BLACK);
    }
}