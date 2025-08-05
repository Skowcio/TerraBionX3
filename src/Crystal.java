import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class Crystal {
    private int x, y;
    private final int size = 150; // rozmiar blokera
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

    public void draw(Graphics g) {
        if (crystalImage != null){
            g.drawImage(crystalImage, x, y, height, height, null);
        }
//        g.setColor(Color.GRAY);
//        g.fillRect(x, y, size, size);
//        g.setColor(Color.BLACK);
//        g.drawRect(x, y, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
