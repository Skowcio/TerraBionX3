

import javax.swing.*;
import java.awt.*;


public class HUDPanel extends JPanel {
    private final GamePanel gamePanel;

    public HUDPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLayout(null);

//        JButton btnPowerPlant = new JButton("Power Plant");
//        btnPowerPlant.setBounds(10, 90, 120, 30);
//        add(btnPowerPlant);
//
//        JButton btnSteelMine = new JButton("Steel Mine");
//        btnSteelMine.setBounds(10, 130, 120, 30);
//        add(btnSteelMine);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Steel Collected: " + gamePanel.getCollectedSteel(), 20, 30);
        g2d.drawString("Power: " + gamePanel.getTotalPower(), 20, 60);
    }
}
