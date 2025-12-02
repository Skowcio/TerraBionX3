import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TerraBionX2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 980);

        MissionManager missionManager = new MissionManager();

        // Na start włączamy menu
        showMainMenu(frame, missionManager);

        frame.setVisible(true);
    }



    public static void showMainMenu(JFrame frame, MissionManager missionManager) {
        // Ładowanie obrazka z resources (zalecane)
        ImageIcon bgIcon = new ImageIcon(
                Main.class.getResource("/background/TerraBionX3.png")
        );
        Image backgroundImage = bgIcon.getImage();

        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imgWidth = backgroundImage.getWidth(null);
                int imgHeight = backgroundImage.getHeight(null);

                // Wyliczamy przesunięcie, żeby centrować
                int x = (panelWidth - imgWidth) / 2;
                int y = (panelHeight - imgHeight) / 2;

                g.drawImage(backgroundImage, x, y, this);
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false); // żeby widzieć tło

        JLabel title = new JLabel("TerraBionX2");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitButton = new JButton("Exit Game");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPanel.add(newGameButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(exitButton);
        menuPanel.add(Box.createVerticalGlue());

        newGameButton.addActionListener(e -> startGame(frame, missionManager));
        exitButton.addActionListener(e -> System.exit(0));

        frame.setContentPane(menuPanel);
        frame.revalidate();
        frame.repaint();
    }


    private static void startGame(JFrame frame, MissionManager missionManager) {
        Mission current = missionManager.getCurrentMission();

        GamePanel gamePanel = new GamePanel(frame, missionManager);
        gamePanel.loadMission(current);

        HUDPanel hudPanel = new HUDPanel(gamePanel);
        gamePanel.setHUDPanel(hudPanel);

        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 0, 1920, 980);
        gamePanel.setScrollPane(scrollPane);

        hudPanel.setBounds(0, 0, 1920, 980);
        hudPanel.setOpaque(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 980));
        layeredPane.add(scrollPane, Integer.valueOf(1));
        layeredPane.add(hudPanel, Integer.valueOf(2));

        MiniMapPanel miniMap = new MiniMapPanel(gamePanel, scrollPane);
        miniMap.setBounds(1600, 0, 300, 300);
        layeredPane.add(miniMap, Integer.valueOf(3));
        gamePanel.setMiniMapPanel(miniMap);

        frame.setContentPane(layeredPane);
        frame.revalidate();
        frame.repaint();
    }
}



//import javax.swing.*;
//
//public class Main {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("TerraBionX2");
//
//        // 1. Tworzymy MissionManager
//        MissionManager missionManager = new MissionManager();
//        Mission current = missionManager.getCurrentMission();
//
//        // 2. Tworzymy główny panel gry (mapę)
//        GamePanel gamePanel = new GamePanel(frame, missionManager);
//        gamePanel.loadMission(current);
//
//        // 3. Tworzymy panel HUD i przekazujemy mu referencję do gamePanel
//        HUDPanel hudPanel = new HUDPanel(gamePanel);
//        gamePanel.setHUDPanel(hudPanel);
//
//        // 4. ScrollPane z GamePanel
//        JScrollPane scrollPane = new JScrollPane(gamePanel);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setBounds(0, 0, 1920, 980);
//        gamePanel.setScrollPane(scrollPane);
//
//        // 5. HUDPanel – jako przezroczysty panel na wierzchu
//        hudPanel.setBounds(0, 0, 1920, 980);
//        hudPanel.setOpaque(false);
//
//        // 6. Tworzymy JLayeredPane i dodajemy komponenty
//        JLayeredPane layeredPane = new JLayeredPane();
//        layeredPane.setPreferredSize(new java.awt.Dimension(1920, 980));
//        layeredPane.add(scrollPane, Integer.valueOf(1)); // mapa
//        layeredPane.add(hudPanel, Integer.valueOf(2));   // HUD
//
//        // 7. Dodajemy minimapę
//        MiniMapPanel miniMap = new MiniMapPanel(gamePanel, scrollPane);
//        miniMap.setBounds(1600, 0, 300, 300); // pozycja w prawym górnym rogu
//        layeredPane.add(miniMap, Integer.valueOf(3));    // minimapa nad wszystkim
//        gamePanel.setMiniMapPanel(miniMap);
//
//        // 8. Konfiguracja JFrame
//        frame.setContentPane(layeredPane);
//        frame.pack();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
//}

//import javax.swing.*;
//
//public class Main {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("TerraBionX2");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1920, 980);
//
//        MissionManager missionManager = new MissionManager();
//
//        // Startujemy w menu
//        frame.setContentPane(new MainMenuPanel(frame, missionManager));
//        frame.setVisible(true);
//    }
//}