import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TerraBionX2");

        // 1. Tworzymy główny panel gry (mapę)
        GamePanel gamePanel = new GamePanel(frame);

        // 2. Tworzymy panel HUD i przekazujemy mu referencję do gamePanel
        HUDPanel hudPanel = new HUDPanel(gamePanel);

        // 3. Przekazujemy HUD z powrotem do GamePanel, żeby mógł go odświeżać
        gamePanel.setHUDPanel(hudPanel);

        // 4. ScrollPane z GamePanel
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 0, 1920, 980);

        // przekazuje scrollPane do gamePanel
        gamePanel.setScrollPane(scrollPane);

        // 5. HUDPanel – jako przezroczysty panel na wierzchu
        hudPanel.setBounds(0, 0, 1920, 980);
        hudPanel.setOpaque(false);

        // 6. JLayeredPane do nałożenia HUD na mapę
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new java.awt.Dimension(1920, 980));
        layeredPane.add(scrollPane, Integer.valueOf(1));
        layeredPane.add(hudPanel, Integer.valueOf(2));

        // 7. Konfiguracja JFrame
        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
