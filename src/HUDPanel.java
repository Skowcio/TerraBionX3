import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private final GamePanel gamePanel;



    public HUDPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLayout(null);

        // ⏱️ Co sekundę odświeżaj HUD (do odliczania)
        Timer hudTimer = new Timer(1000, e -> repaint());
        hudTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 🔹 Wyświetlanie zasobów
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Steel Collected: " + gamePanel.getCollectedSteel(), 20, 30);
        g2d.drawString("Power: " + gamePanel.getTotalPower(), 20, 60);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Factories: " + Factory.getTotalFactories() + " / " + Factory.getMaxFactories(), 450, 30);
        g2d.drawString("Towers: " + Artylery.getTotalArtys() + " / " + Artylery.getMaxArtylerys(), 600, 30);
//        g2d.drawString("Drones: " + gamePanel.getBuilderVehicles().size() + " / 5", 750, 30);
        g2d.drawString("AX-2M Fighter: " + gamePanel.getSoldier().size() + " / 8", 900,30);
        g2d.drawString("Valkiria: " + gamePanel.getValkirias().size() + " / Tech: " + gamePanel.getValkiriaTech().size(), 1100, 30);
        /// //////////////////////////////////////ta forma jest szybsza gamePanel.getBuilderVehicles w praktyce.... na maszynach z lat 90 byla by roznica///////

        /////////////////// do obslugi wyswietlania paska Drony budownicze
        int currentDrones = gamePanel.getBuilderVehicles().size();
        int maxDrones = 5;

// Tekst
        g2d.setColor(Color.WHITE);
        g2d.drawString("Drones: " + currentDrones + " / " + maxDrones, 750, 30);

// Pasek postępu
        int barX = 750;
        int barY = 45;
        int barWidth = 90;
        int barHeight = 10;

        double progress = (double) currentDrones / maxDrones;
        int fillWidth = (int) (barWidth * progress);

        g2d.setColor(Color.GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        g2d.setColor(Color.CYAN);
        g2d.fillRect(barX, barY, fillWidth, barHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        /// ///////////////////////////////////////////////////
        /// /////////////////////////////////////////////////////
///////////////////////////////////////////////////////
        //Punkty za zabitych wrogów (bombardowanie)
        /////////////////////////
        /// /////
        int killPoints = gamePanel.getEnemyKillPoints();

        g2d.setColor(Color.ORANGE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("🔥 Kill Points: " + killPoints + " / 50", 1680, 320);

// Pasek postępu
        int barX2 = 1650;
        int barY2 = 340;
        int barWidth2 = 200;
        int barHeight2 = 10;

        double progress2 = (double) killPoints / 50.0;
        int fillWidth2 = (int) (barWidth2 * progress2);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX2, barY2, barWidth2, barHeight2);

        g2d.setColor(Color.ORANGE);
        g2d.fillRect(barX2, barY2, fillWidth2, barHeight2);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX2, barY2, barWidth2, barHeight2);
        /// ////////

        g2d.drawString("FPS: " + gamePanel.getFPS(), 20, 20);

        //  Wyświetlanie czasu obrony (jeśli to misja DEFEND_FOR_TIME)
        if (gamePanel.getMissionManager() != null) {
            Mission current = gamePanel.getMissionManager().getCurrentMission(); // t osie rozni i chyba powoduje blad jak jest Mission current = gamePanel.getCurrentMission(); i sie nie laduje mijsa nowa
            if (current != null && current.objectiveType == Mission.ObjectiveType.DEFEND_FOR_TIME) {
                long elapsed = System.currentTimeMillis() - gamePanel.getMissionStartTime();
                long remaining = gamePanel.getDefendDurationMillis() - elapsed;
                if (remaining < 0) remaining = 0;

                long seconds = remaining / 1000;
                long minutes = seconds / 60;
                long secondsLeft = seconds % 60;

                String timeText = String.format("⏳ Time to survive: %02d:%02d", minutes, secondsLeft);

                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.YELLOW);
                g2d.drawString(timeText, 20, 90);
            }
            if (current != null && current.objectiveType == Mission.ObjectiveType.COLLECT_RESOURCES) {
                int requiredSteel = 80000; // Tyle ile w GamePanel.updateGame()
                int collected = gamePanel.getCollectedSteel();

                int remaining = Math.max(0, requiredSteel - collected);
                String text = "🪓 Collect steel: " + collected + " / " + requiredSteel;

                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.CYAN);
                g2d.drawString(text, 20, 90);
            }
        }
    }
}