import java.util.ArrayList;
import java.util.List;

public class BombardmentSequence {
    private int centerX, centerY;
    private int totalExplosions = 6;
    private int explosionsMade = 0;
    private int ticksBetween = 15; // co 15 ticków (ok. 0.25 sekundy)
    private int tickCounter = 0;
    private boolean finished = false;

    private List<Explosion> explosions;

    public BombardmentSequence(int x, int y) {
        this.centerX = x;
        this.centerY = y;
        this.explosions = new ArrayList<>();
    }

    public void update() {
        if (finished) return;
        tickCounter++;

        if (tickCounter >= ticksBetween && explosionsMade < totalExplosions) {
            tickCounter = 0;
            explosionsMade++;

            int offsetX = (int) (Math.random() * 100 - 50);
            int offsetY = (int) (Math.random() * 100 - 50);

            explosions.add(new Explosion(centerX + offsetX, centerY + offsetY));

            if (explosionsMade >= totalExplosions) {
                finished = true;
                System.out.println("💣 Bombardowanie zakończone!");
            }
        }
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public boolean isFinished() {
        return finished;
    }
}