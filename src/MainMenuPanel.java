//import javax.swing.*;
//import java.awt.*;
//
//public class MainMenuPanel extends JPanel {
//    public MainMenuPanel(JFrame frame, MissionManager missionManager) {
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setBackground(Color.BLACK);
//
//        JLabel title = new JLabel("TerraBionX2");
//        title.setFont(new Font("Arial", Font.BOLD, 40));
//        title.setForeground(Color.WHITE);
//        title.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JButton newGameButton = new JButton("Nowa gra");
//        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        JButton exitButton = new JButton("Wyjście");
//        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        add(Box.createVerticalGlue());
//        add(title);
//        add(Box.createRigidArea(new Dimension(0, 50)));
//        add(newGameButton);
//        add(Box.createRigidArea(new Dimension(0, 20)));
//        add(exitButton);
//        add(Box.createVerticalGlue());
//
//        // Obsługa przycisku "Nowa gra"
//        newGameButton.addActionListener(e -> {
//            String[] options = {"Misja 1", "Misja 2"};
//            int choice = JOptionPane.showOptionDialog(
//                    frame,
//                    "Wybierz misję:",
//                    "Nowa gra",
//                    JOptionPane.DEFAULT_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    null,
//                    options,
//                    options[0]
//            );
//            if (choice >= 0) {
//                Mission selectedMission = missionManager.getMissionByIndex(choice);
//                startGame(frame, missionManager, selectedMission);
//            }
//        });
//
//        // Obsługa przycisku "Wyjście"
//        exitButton.addActionListener(e -> System.exit(0));
//    }
//
//    private void startGame(JFrame frame, MissionManager missionManager, Mission mission) {
//        GamePanel gamePanel = new GamePanel(frame, missionManager);
//        gamePanel.loadMission(mission);
//
//        HUDPanel hudPanel = new HUDPanel(gamePanel);
//        gamePanel.setHUDPanel(hudPanel);
//
//        JScrollPane scrollPane = new JScrollPane(gamePanel);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setBounds(0, 0, 1920, 980);
//        gamePanel.setScrollPane(scrollPane);
//
//        hudPanel.setBounds(0, 0, 1920, 980);
//        hudPanel.setOpaque(false);
//
//        JLayeredPane layeredPane = new JLayeredPane();
//        layeredPane.setPreferredSize(new Dimension(1920, 980));
//        layeredPane.add(scrollPane, Integer.valueOf(1));
//        layeredPane.add(hudPanel, Integer.valueOf(2));
//
//        MiniMapPanel miniMap = new MiniMapPanel(gamePanel, scrollPane);
//        miniMap.setBounds(1600, 0, 300, 300);
//        layeredPane.add(miniMap, Integer.valueOf(3));
//        gamePanel.setMiniMapPanel(miniMap);
//
//        frame.setContentPane(layeredPane);
//        frame.revalidate();
//        frame.repaint();
//    }
//}