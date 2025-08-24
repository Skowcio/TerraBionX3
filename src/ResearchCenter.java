import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Consumer;

public class ResearchCenter {
    private int x, y;
    private int width = 120, height = 120;
    private BufferedImage image;
    private boolean selected;

    public ResearchCenter(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(getClass().getResource("/research/research_center.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 🚀 bonus – po zbudowaniu dodajemy limit fabryk
        Factory.increaseMaxFactories(2);
        System.out.println("Zbudowano Research Center! Limit fabryk zwiększony do: " + Factory.getMaxFactories());
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


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
