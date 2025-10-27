import java.awt.*;

public class BuildingPortalEffect {
    private int x, y;
    private int size;
    private long startTime;
    private long duration;
    private double progress;
    private String buildingType;

    public BuildingPortalEffect(int x, int y, int size, long duration, String buildingType) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
        this.buildingType = buildingType;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > duration;
    }

    public String getBuildingType() {
        return buildingType;
    }

    private void update() {
        progress = Math.min(1.0, (double)(System.currentTimeMillis() - startTime) / duration);
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    public void draw(Graphics2D g2) {
        update();

        // 🔹 Efekt pulsacji — oddychanie kuli
        double pulse = 0.5 + 0.5 * Math.sin(progress * Math.PI * 4); // 4 cykle pulsacji
        int radius = (int)(size * (2.0 + 0.5 * pulse)); // większa kula

        // 🔹 Zanikanie przezroczystości
        int alpha = (int)(255 * (1.0 - Math.pow(progress, 1.5))); // wolniejsze znikanie
        alpha = Math.max(0, Math.min(255, alpha));

        // 🔹 Tworzymy "warstwę światła"
        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.setComposite(AlphaComposite.SrcOver);

        // 🔵 Jasna turkusowo-niebieska kula energii
        Color glowColor = new Color(100, 220, 255, alpha);
       //  Bardziej jasna kula — mocniejszy błysk
        RadialGradientPaint gradient = new RadialGradientPaint(
                new Point(x + size / 2, y + size / 2),
                radius / 2f,
                new float[]{0f, 0.3f, 1f},
                new Color[]{
                        new Color(255, 255, 255, alpha),      // intensywny środek
                        new Color(200, 240, 255, alpha / 2),  // miękka poświata
                        new Color(0, 100, 180, 0)             // krawędź zanika
                }
        );

        g2d.setPaint(gradient);
        g2d.fillOval(x + (size - radius) / 2, y + (size - radius) / 2, radius, radius);

        // 🔹 Zewnętrzny pierścień energii
        g2d.setColor(new Color(0, 255, 255, alpha / 2));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawOval(x + (size - radius) / 2, y + (size - radius) / 2, radius, radius);

        g2d.dispose();
    }
}