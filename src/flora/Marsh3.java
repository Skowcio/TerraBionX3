package flora;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Marsh3 extends Flora {
    public Marsh3(int x, int y) {
        super(x, y, 200, 220, loadImage());
    }

    private static java.awt.image.BufferedImage loadImage() {
        try {
            return ImageIO.read(Marsh.class.getResource("/flora/Flora3.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

