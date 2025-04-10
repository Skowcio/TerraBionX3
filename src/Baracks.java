import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class Baracks {
    private int x, y; // Pozycja na mapie
    private int width = 80, height = 80;
    private boolean selected; // Czy koszary są zaznaczone
    private BufferedImage baracImage;

    public Baracks(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;

        try {
            baracImage = ImageIO.read(getClass().getResource("/bara/bara.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Gettery
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSelected() {
        return selected;
    }
    public Point getPosition() {
        return new Point(x, y);
    }

    // Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
    }

    // Rysowanie koszar
    public void draw(Graphics g) {


        if (baracImage != null) {
            g.drawImage(baracImage, x, y, width, height, null);
        }
//        else {
//            // Fallback na rysowanie prostokąta, jeśli obraz nie został załadowany
//            g.setColor(Color.MAGENTA);
//            g.fillRect(x, y, width, height);
//        }

//        g.setColor(Color.GRAY); // Kolor koszar
//        g.fillRect(x, y, 50, 50); // Koszary są kwadratem 50x50

        if (selected) {
            g.setColor(Color.RED); // Obramowanie dla zaznaczonych koszar
            g.drawRect(x - 2, y - 2, 84, 84); // Obramowanie o 2px większe
        }
    }

    // Sprawdzanie, czy punkt (px, py) znajduje się wewnątrz koszar
    public boolean contains(int px, int py) {
        return px >= x && px <= x + 50 && py >= y && py <= y + 50;
    }
}
