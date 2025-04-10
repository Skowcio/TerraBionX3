import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TerraBionX2");
        GamePanel gamePanel = new GamePanel(frame);

        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(scrollPane);
        frame.setSize(1920, 980);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}



//import javax.swing.*;
//import java.awt.*;
//
//public class Main {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("TerraBionX2");
//        GamePanel gamePanel = new GamePanel();
//
//        frame.add(gamePanel);
//        frame.setSize(1920, 980);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
//}
