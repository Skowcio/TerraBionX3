import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class PowerPlant {
    private int x, y;
    private int width = 80, height = 80;
    private static final int POWER = 10; // Moc dostarczana przez elektrownię
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


        g.setColor(Color.BLACK);

    }
}
