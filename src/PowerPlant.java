import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class PowerPlant {
    private int x, y;
    private int width = 100, height = 100;
    private static final int POWER = 10; // Moc dostarczana przez elektrownię
    private int health = 10;
    private BufferedImage powerImage;

    public PowerPlant(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            powerImage = ImageIO.read(getClass().getResource("/power/power.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int getHealth() {
        return health;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public Point getPosition() {
        return new Point(x, y);
    }

    public static int getPowerGenerated() {
        return POWER;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
    }

    public void draw(Graphics g) {
        if (powerImage != null){
            g.drawImage(powerImage, x, y, height, height, null);
        }
        else {
            // Fallback na rysowanie prostokąta, jeśli obraz nie został załadowany
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, height, height);
            g.drawString("P", x + width / 2 - 5, y + height / 2 + 5);
        }
        int maxHealth = 10; // Maksymalne zdrowie przeciwnika
        int healthBarWidth = 100; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.BLACK);

    }
}
