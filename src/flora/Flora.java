package flora;

import java.awt.image.BufferedImage;
import java.awt.*;


public abstract class Flora {
    protected int x, y;
    protected int width, height;
    protected BufferedImage image;

    public Flora(int x, int y, int width, int height, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    public Rectangle getCollisionBounds() {
        // 🔹 Dolne 2/3 grafiki blokują budowę
        int baseHeight = (height * 2) / 3;
        return new Rectangle(x, y + height - baseHeight, width, baseHeight);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}