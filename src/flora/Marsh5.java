package flora;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Marsh5 extends Flora {
    public Marsh5(int x, int y) {
        super(x, y, 386, 256, loadImage());
    }

    private static java.awt.image.BufferedImage loadImage() {
        try {
            return ImageIO.read(Marsh.class.getResource("/flora/rock3.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

