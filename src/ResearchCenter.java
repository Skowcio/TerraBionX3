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

    // 🆕 Licznik budynków
    private static int builtCount = 0;
    private static final int MAX_BUILDINGS = 2;

    // 🆕 Licznik ulepszeń (może być równy MAX_BUILDINGS)
    private static int researchCount = 0;
    private static final int MAX_RESEARCH_UPGRADES = 2;

    public ResearchCenter(int x, int y) {
        if (builtCount >= MAX_BUILDINGS) {
            System.out.println("❌ Nie można zbudować więcej Research Center! Limit osiągnięty.");
            return; // 🚫 przerywamy budowę
        }

        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(getClass().getResource("/research/research_center.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        builtCount++; // ✅ zliczamy tylko te postawione

        // 🚀 bonus – ale tylko 2 razy
        if (researchCount < MAX_RESEARCH_UPGRADES) {
            Factory.increaseMaxFactories(2);
            researchCount++;
            System.out.println("Zbudowano Research Center! Limit fabryk zwiększony do: "
                    + Factory.getMaxFactories() + " (ulepszeń: " + researchCount + ")");
        } else {
            System.out.println("Limit ulepszeń Research Center został osiągnięty! (" + MAX_RESEARCH_UPGRADES + ")");
        }
    }

    public static int getBuiltCount() {
        return builtCount;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Point getPosition() { return new Point(x, y); }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

