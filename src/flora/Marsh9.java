package flora;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Marsh9 extends Flora {
    public Marsh9(int x, int y) {
        super(x, y, 256, 171, loadImage());
    }

    private static java.awt.image.BufferedImage loadImage() {
        try {
            return ImageIO.read(Marsh.class.getResource("/flora/rock5.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

