package Bulding;

public class Bulding {

    private int x, y; // Pozycja na mapie
    private int collectedSteel;
    private String name;
    private int power;
    private int width;
    private int height;
    private int live;
    private int draw;
    private boolean selected; // Czy koszary są zaznaczone

    public Bulding(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getCollectedSteel() {
        return collectedSteel;
    }
    public void setCollectedSteel(int cost) {
        this.collectedSteel = cost;
    }
    // Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Udostępniamy metody na publiczne, aby klasy dziedziczące mogły ich używać
    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public int getLive() {
        return live;
    }
    public void setLive(int live) {
        this.live = live;
    }

    public int getDraw() {
        return draw;
    }
    public void setDraw(int draw) {
        this.draw = draw;
    }
}
