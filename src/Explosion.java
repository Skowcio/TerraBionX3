import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.ArrayList;

public class Explosion {
    private int x, y; // Środek eksplozji
    private final int size = 90; // Rozmiar obszaru eksplozji (70x70)
    private int frameIndex = 0; // Indeks aktualnej klatki animacji
    private int frameCounter = 0; // Licznik czasu trwania danej klatki
    private final int frameDuration = 50; // Liczba "klatek" (np. 5 ticków) na każdą klatkę animacji
    private BufferedImage[] frames; // Tablica przechowująca obrazy eksplozji

    public Explosion(int x, int y) {
        this.x = x - size / 2; // Środek eksplozji
        this.y = y - size / 2; // Środek eksplozji
        loadExplosionImages(); // Wczytaj obrazy eksplozji podczas tworzenia obiektu
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size); // Zwracamy prostokąt o wymiarach 70x70
    }

    /**
     * Wczytuje obrazy eksplozji z plików PNG.
     */
    private void loadExplosionImages() {
        try {
            frames = new BufferedImage[15]; // Zakładamy 6 klatek animacji eksplozji
            for (int i = 0; i < frames.length; i++) {
                // Wczytuje obrazy z folderu /explosion/ jako explosion1.png, explosion2.png, itd.
                frames[i] = ImageIO.read(getClass().getResource("/explosion/explosion" + (i + 1) + ".png"));
                if (frames[i] == null) {
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rysuje eksplozję na ekranie.
     */
    public void draw(Graphics g) {
        if (frameIndex < frames.length) {
            BufferedImage currentFrame = frames[frameIndex];

            g.drawImage(currentFrame, x, y, size, size, null);
            frameCounter++;
            if (frameCounter >= frameDuration) {
                frameCounter = 0;
                frameIndex++;
            }
        }
    }

    /**
     * Sprawdza, czy wróg znajduje się w obszarze eksplozji.
     */
    public void checkEnemyCollision(List<EnemyToo> enemiesToo, List<Enemy> enemies, List<Hive> hives, List<EnemyHunter> enemyHunters, List<EnemyShooter> enemyShooters) {
        Rectangle explosionBounds = new Rectangle(x, y, size, size);
        enemiesToo.removeIf(enemyToo -> explosionBounds.intersects(enemyToo.getBounds()));
        enemyHunters.removeIf(enemyHunter -> explosionBounds.intersects(enemyHunter.getBounds()));
        enemyShooters.removeIf(enemyShooter -> explosionBounds.intersects(enemyShooter.getBounds()));
        enemies.removeIf(enemy -> explosionBounds.intersects(enemy.getBounds()));
        hives.removeIf(hive -> {
            if (explosionBounds.intersects(hive.getBounds())) {
                boolean destroyed = hive.takeDamage(); // Odejmij punkt zdrowia
                return destroyed; // Usuń tylko, jeśli Hive ma 0 życia
            }
            return false;
        });
    }

    /**
     * Sprawdza, czy eksplozja zakończyła się (czy animacja dobiegła końca).
     */
    public boolean isExpired() {
        boolean expired = frameIndex >= frames.length;
        if (expired) {
            System.out.println("Eksplozja zakończona!");
        }
        return expired;
    }
}
