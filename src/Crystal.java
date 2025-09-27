import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Crystal {
    private int x, y;
    private final int size = 150; // rozmiar kryształu
    private int width = 150, height = 150;
    private BufferedImage crystalImage;

    public Crystal(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            crystalImage = ImageIO.read(getClass().getResource("/crystal/energycrystal.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 🔹 Strefa budowania (kwadrat wokół środka kryształu)
    public Rectangle getBuildArea(int radius) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        return new Rectangle(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void draw(Graphics g) {
        if (crystalImage != null) {
            g.drawImage(crystalImage, x, y, width, height, null);
        }
        // (opcjonalne) podgląd strefy budowania:
        // Rectangle area = getBuildArea(200);
        // g.setColor(new Color(0, 255, 0, 50)); // półprzezroczysty zielony
        // g.fillRect(area.x, area.y, area.width, area.height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
