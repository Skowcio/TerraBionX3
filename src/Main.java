import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TerraBionX2");

        // 1. Tworzymy MissionManager
        MissionManager missionManager = new MissionManager();
        Mission current = missionManager.getCurrentMission();

        // 2. Tworzymy główny panel gry (mapę)
        GamePanel gamePanel = new GamePanel(frame, missionManager);
        gamePanel.loadMission(current);

        // 3. Tworzymy panel HUD i przekazujemy mu referencję do gamePanel
        HUDPanel hudPanel = new HUDPanel(gamePanel);
        gamePanel.setHUDPanel(hudPanel);

        // 4. ScrollPane z GamePanel
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 0, 1920, 980);
        gamePanel.setScrollPane(scrollPane);

        // 5. HUDPanel – jako przezroczysty panel na wierzchu
        hudPanel.setBounds(0, 0, 1920, 980);
        hudPanel.setOpaque(false);

        // 6. Tworzymy JLayeredPane i dodajemy komponenty
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new java.awt.Dimension(1920, 980));
        layeredPane.add(scrollPane, Integer.valueOf(1)); // mapa
        layeredPane.add(hudPanel, Integer.valueOf(2));   // HUD

        // 7. Dodajemy minimapę
        MiniMapPanel miniMap = new MiniMapPanel(gamePanel, scrollPane);
        miniMap.setBounds(1600, 0, 300, 300); // pozycja w prawym górnym rogu
        layeredPane.add(miniMap, Integer.valueOf(3));    // minimapa nad wszystkim
        gamePanel.setMiniMapPanel(miniMap);

        // 8. Konfiguracja JFrame
        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
