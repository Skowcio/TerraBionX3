import java.awt.*;

public class ArtBullet {
    private int startX, startY; // Punkt startowy pocisku
    private int targetX, targetY; // Punkt docelowy
    private double x, y; // Aktualna pozycja pocisku (double dla większej precyzji)
    private double time; // Zmienna czasu do generowania trajektorii
    private final double flightDuration = 0.15; // Czas, jaki zajmuje dotarcie pocisku (0.2 sekundy)
    private final int width = 5, height = 5; // Rozmiar pocisku
    private boolean reachedTarget = false; // Flaga, czy pocisk dotarł do celu

    public ArtBullet(int x, int y, int targetX, int targetY) {
        this.startX = x;
        this.startY = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.x = x;
        this.y = y;
        this.time = 0.0; // Startujemy od t = 0
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean hasReachedTarget() {
        return reachedTarget;
    }

    public void move() {
        if (reachedTarget) return; // Nie ruszamy się, jeśli pocisk osiągnął cel

        // Zwiększamy czas o mały krok (symulacja czasu w sekundach)
        time += 0.01; // Każde wywołanie move() to krok 0.02 sekundy

        // Współrzędne X i Y są obliczane na podstawie interpolacji między startem a celem
        double t = time / flightDuration; // Normalizujemy czas od 0 do 1

        // Obliczamy X (interpolacja liniowa)
        x = startX + t * (targetX - startX);

        // Obliczamy Y (parabola w górę i w dół)
        double maxHeight = -99; // Maksymalna wysokość wzniesienia (im większa, tym wyższy łuk)
        y = startY * (1 - t) + targetY * t + maxHeight * (4 * t * (1 - t));

        // Kończymy lot, gdy t >= 1 (pocisk osiągnął cel)
        if (t >= 1.0) {
            x = targetX;
            y = targetY;
            reachedTarget = true; // Flaga, że pocisk osiągnął cel
        }
    }

    public void draw(Graphics g) {
        if (!reachedTarget) {
            g.setColor(Color.YELLOW);
            g.fillOval((int) x, (int) y, width, height); // Pocisk o rozmiarze 5x5
        }
    }
}
