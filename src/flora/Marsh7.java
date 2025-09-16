package flora;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Marsh7 extends Flora {
    public Marsh7(int x, int y) {
        super(x, y, 384, 256, loadImage());
    }

    private static java.awt.image.BufferedImage loadImage() {
        try {
            return ImageIO.read(Marsh.class.getResource("/flora/rock6.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

