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
     *
     *
     */



    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (frameIndex < frames.length) {
            BufferedImage currentFrame = frames[frameIndex];
// tu jest zewnetrzny bysk
            float alphaOuter = 0.15f - (frameIndex / (float) frames.length) * 0.15f;
            int outerGlowSize = (int) (size * 5.5);
            int outerGlowX = x + size / 2 - outerGlowSize / 2;
            int outerGlowY = y + size / 2 - outerGlowSize / 2;

            RadialGradientPaint outerGradient = new RadialGradientPaint(
                    new Point(x + size / 2, y + size / 2),
                    outerGlowSize / 2f,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(255, 220, 150, (int) (alphaOuter * 255)),
                            new Color(255, 220, 150, 0)
                    }
            );
            g2d.setPaint(outerGradient);
            g2d.fillOval(outerGlowX, outerGlowY, outerGlowSize, outerGlowSize);
// to wewnetrzny blysk
            // 🔆 1️⃣ Efekt podświetlenia — dynamiczny rozbłysk
            float alpha = 0.4f - (frameIndex / (float) frames.length) * 0.4f; // przezroczystość spada z czasem
            int glowSize = (int) (size * 3.0); // promień rozbłysku
            int glowX = x + size / 2 - glowSize / 2;
            int glowY = y + size / 2 - glowSize / 2;

            // Gradient radialny (jasny środek → ciemny brzeg)
            RadialGradientPaint gradient = new RadialGradientPaint(
                    new Point(x + size / 2, y + size / 2),
                    glowSize / 2f,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(255, 210, 160, (int) (alpha * 255)), // jasny żółty w centrum
                            new Color(255, 220, 150, 0)                    // przezroczysty na krawędzi
                    }
            );

            g2d.setPaint(gradient);
            g2d.fillOval(glowX, glowY, glowSize, glowSize);

            // 🔥 2️⃣ Rysowanie faktycznej eksplozji
            g2d.drawImage(currentFrame, x, y, size, size, null);

            // ⏱️ 3️⃣ Przejście do kolejnej klatki animacji
            frameCounter++;
            if (frameCounter >= frameDuration) {
                frameCounter = 0;
                frameIndex++;
            }
        }

        g2d.dispose();
    }

    /**
     * Sprawdza, czy wróg znajduje się w obszarze eksplozji.
     */
    public void checkEnemyCollision(List<EnemyToo> enemiesToo, List<Enemy> enemies, List<Hive> hives, List<EnemyHunter> enemyHunters, List<EnemyShooter> enemyShooters) {
        Rectangle explosionBounds = new Rectangle(x, y, size, size);
        enemiesToo.removeIf(enemyToo -> {
            if (explosionBounds.intersects(enemyToo.getBounds())) {
                boolean destroyed = enemyToo.takeDamage3(); // Odejmij punkt zdrowia
                return destroyed; // Usuń tylko, jeśli Hive ma 0 życia
            }
            return false;
        });
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
