import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class  SteelMine {
    private int x, y;
    private int width = 80, height = 80;
    private boolean selected;
    private BufferedImage steelmineImage;

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
    }
}