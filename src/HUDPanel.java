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
        g2d.drawString("Factories: " + Factory.getTotalFactories(), 650, 30);
        g2d.drawString("Towers: " + Artylery.getTotalArtys(), 850, 30);

        // 🔹 Wyświetlanie czasu obrony (jeśli to misja DEFEND_FOR_TIME)
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
        }
    }
}