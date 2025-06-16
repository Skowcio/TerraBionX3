import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class ResourcesSteel {
    private int x, y; // Pozycja na mapie
    private int width = 80, height = 80; // Rozmiar
    private int resourceAmount; // Ilość zasobów
    private BufferedImage image; // Obraz zasobu

    public ResourcesSteel(int x, int y) {
        this.x = x;
        this.y = y;

        // Losowa ilość zasobów w zakresie 500–8000
        Random rand = new Random();
        this.resourceAmount = rand.nextInt(37501) + 5340;

        // Wczytanie grafiki zasobu
        try {
            image = ImageIO.read(getClass().getResource("/steel/steel2.png")); // Ścieżka do obrazu
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie udało się załadować obrazu dla ResourcesSteel.");
        }
    }

    // Gettery
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getResourceAmount() {
        return resourceAmount;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Metoda zmniejszająca ilość zasobów podczas wydobycia
    public void mineResource(int amount) {
        resourceAmount -= amount;
        if (resourceAmount < 0) {
            resourceAmount = 0;
        }
    }

    // Rysowanie zasobu na mapie
    public void draw(Graphics g) {
        // Jeśli obraz jest poprawnie załadowany, rysujemy grafikę
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Jeśli nie udało się wczytać obrazu, rysujemy prostokąt
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }

        // Wyświetlanie ilości zasobów na górze zasobu
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(resourceAmount), x + 5, y + 20); // Wyświetlanie ilości zasobów
    }

    // Czy zasób jest wyczerpany
    public boolean isDepleted() {
        return resourceAmount <= 0;
    }
}
