
import java.awt.*;




import java.awt.*;

public class HitFlash {
    private int x, y;
    private int radius = 0;
    private int maxRadius = 35;
    private long startTime;
    private long duration = 300; // w ms

    public HitFlash(int x, int y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
    }

    public void draw(Graphics g) {
        long elapsed = System.currentTimeMillis() - startTime;
        double progress = Math.min(1.0, (double) elapsed / duration);

        radius = (int) (maxRadius * progress);
        int alpha = (int) (255 * (1.0 - progress));
        alpha = Math.max(0, Math.min(255, alpha)); // ✅ zabezpieczenie

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver);

        // Lekko pomarańczowy błysk
        g2d.setColor(new Color(255, 180, 50, alpha));

        g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        g2d.dispose();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }
}