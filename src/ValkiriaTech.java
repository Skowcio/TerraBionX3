import java.awt.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;


public class ValkiriaTech {
    private int x, y;
    private final int width = 80;
    private final int height = 80;

    public ValkiriaTech(int x, int y) {
        this.x = x;
        this.y = y;
        // 🔥 Każdy ValkiriaTech pozwala zbudować jedną dodatkową Valkirię
        Valkiria.increaseMaxValkirias(1);
    }

    public void draw(Graphics g) {
        g.setColor(Color.MAGENTA); // testowo różowy kwadrat
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
