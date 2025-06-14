import java.awt.*;


public class Factory {
    private int x, y; // Pozycja na mapie
    private int width = 50, height = 50;
    private boolean selected; // Czy koszary są zaznaczone
    private boolean producing = false;
    private int productionSecondsLeft = 0;

    public Factory(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
    }
    public Point getPosition() {
        return new Point(x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y,width, height); // width i height to wymiary żołnierza
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

    // Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void startProduction(int duration) {
        producing = true;
        productionSecondsLeft = duration;
    }

    public void updateProduction() {
        if (producing && productionSecondsLeft > 0) {
            productionSecondsLeft--;
            if (productionSecondsLeft == 0) {
                producing = false;
            }
        }
    }

    public boolean isProducing() {
        return producing;
    }

    public int getProductionSecondsLeft() {
        return productionSecondsLeft;
    }

    // Rysowanie koszar
    public void draw(Graphics g) {
        g.setColor(Color.PINK); // Kolor koszar
        g.fillRect(x, y, 50, 50); // Koszary są kwadratem 50x50
        g.setColor(Color.BLACK);
        g.drawString("Factory", x + width / 2 - 19, y + height / 2 + 5);

        if (selected) {
            g.setColor(Color.GRAY); // Obramowanie dla zaznaczonych fabryki
            g.drawRect(x - 2, y - 2, 54, 54); // Obramowanie o 2px większe
        }
        if (producing) {
            g.setColor(Color.WHITE);
            g.drawString(productionSecondsLeft + "s", x + 10, y - 5); // Nad fabryką
        }
    }

    // Sprawdzanie, czy punkt (px, py) znajduje się wewnątrz koszar
    public boolean contains(int px, int py) {
        return px >= x && px <= x + 50 && py >= y && py <= y + 50;
    }
}