import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class Baracks {
    private int x, y; // Pozycja na mapie
    private int width = 140, height = 140;
    private boolean selected; // Czy koszary są zaznaczone
    private BufferedImage baracImage;
    private int health = 40;

    // do produkcji i strzalu

    // --- NOWE ZMIENNE ---
    private int availableShells = 0;        // ile gotowych pocisków mamy
    private boolean producing = false;      // czy trwa produkcja
    private long productionStartTime;       // czas rozpoczęcia produkcji
    private final int productionTime = 5000; // ms -> 5 sekund

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
    public boolean takeDamage() {
        health--;
        return health <= 0;
    }

    // do obslugi pocisku

    public void startProducingShell() {
        if (!producing) {
            producing = true;
            productionStartTime = System.currentTimeMillis();
        }
    }

    public void updateProduction() {
        if (producing) {
            long now = System.currentTimeMillis();
            if (now - productionStartTime >= productionTime) {
                availableShells++;
                producing = false; // produkcja zakończona
                System.out.println("Wyprodukowano pocisk. Dostępne pociski: " + availableShells);
            }
        }
    }

    public boolean useShell() {
        if (availableShells > 0) {
            availableShells--;
            return true;
        }
        return false;
    }

    public int getAvailableShells() {
        return availableShells;
    }


    public boolean isProducing() {
        return producing;
    }
    public int getRemainingProductionTime() {
        if (producing) {
            long now = System.currentTimeMillis();
            long remaining = productionTime - (now - productionStartTime);
            return (int) Math.max(0, remaining / 1000); // zwracamy sekundy
        }
        return 0;
    }

    /// /////////////////////////////////////////////////////

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
            g.setColor(Color.WHITE); // Obramowanie dla zaznaczonych koszar
            g.drawRect(x - 2, y - 2, 144, 144); // Obramowanie o 2px większe
        }
        int maxHealth = 40; // Maksymalne zdrowie
        int healthBarWidth = 140; // Stała długość paska zdrowia
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth);
        // --- INFO o pociskach ---
        g.setColor(Color.YELLOW);
        g.drawString("Pociski: " + availableShells, x, y + height + 15);

        if (producing) {
            g.setColor(Color.RED);
            g.drawString("Produkcja: " + getRemainingProductionTime() + "s", x, y + height + 30);
        }

        g.setColor(Color.GREEN);
        g.fillRect(x, y - 5, currentHealthWidth, 3); // Pasek nad wrogiem

        // Rysowanie obramowania paska zdrowia
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 5, healthBarWidth, 3);

        g.setColor(Color.BLACK);
    }

    // Sprawdzanie, czy punkt (px, py) znajduje się wewnątrz koszar
    public boolean contains(int px, int py) {
        return px >= x && px <= x + 50 && py >= y && py <= y + 50;
    }
}
