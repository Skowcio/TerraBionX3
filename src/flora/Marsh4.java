package flora;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Marsh4 extends Flora {
    public Marsh4(int x, int y) {
        super(x, y, 256, 171, loadImage());
    }

    private static java.awt.image.BufferedImage loadImage() {
        try {
            return ImageIO.read(Marsh.class.getResource("/flora/rock1.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

