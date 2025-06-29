import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MiniMapPanel extends JPanel {
    private final GamePanel gamePanel;
    private final JScrollPane scrollPane;

    private final int minimapWidth = 300;
    private final int minimapHeight = 300;
    private final int mapWidth = 3000;
    private final int mapHeight = 3000;

    public MiniMapPanel(GamePanel gamePanel, JScrollPane scrollPane) {
        this.gamePanel = gamePanel;
        this.scrollPane = scrollPane;
        setBounds(1600, 10, minimapWidth + 2, minimapHeight + 2); // prawy górny róg
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Tło minimapy
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, minimapWidth, minimapHeight);

        double scaleX = (double) minimapWidth / mapWidth;
        double scaleY = (double) minimapHeight / mapHeight;

        // Jednostki: żołnierze - niebiescy
        g2d.setColor(Color.BLUE);
        for (Soldier soldier : gamePanel.getSoldiers()) {
            int x = (int) (soldier.getX() * scaleX);
            int y = (int) (soldier.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }
        g2d.setColor(Color.BLUE);
        for (SoldierBot soldierBot : gamePanel.getSoldierBots()) {
            int x = (int) (soldierBot.getX() * scaleX);
            int y = (int) (soldierBot.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }

        // Wrogowie - czerwoni
        g2d.setColor(Color.RED);
        for (Enemy enemy : gamePanel.getEnemies()) {
            int x = (int) (enemy.getX() * scaleX);
            int y = (int) (enemy.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }
        g2d.setColor(Color.RED);
        for (EnemyShooter enemyShooter : gamePanel.getenemyShooters()) {
            int x = (int) (enemyShooter.getX() * scaleX);
            int y = (int) (enemyShooter.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }
        g2d.setColor(Color.RED);
        for (Hive hive : gamePanel.getHives()) {
            int x = (int) (hive.getX() * scaleX);
            int y = (int) (hive.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }
        g2d.setColor(Color.RED);
        for (HiveToo hiveToo : gamePanel.getHiveToos()) {
            int x = (int) (hiveToo.getX() * scaleX);
            int y = (int) (hiveToo.getY() * scaleY);
            g2d.fillRect(x, y, 2, 2);
        }


        // Fabryki - żółte
        g2d.setColor(Color.YELLOW);
        for (Factory factory : gamePanel.getFactories()) {
            int x = (int) (factory.getX() * scaleX);
            int y = (int) (factory.getY() * scaleY);
            g2d.fillRect(x, y, 3, 3);
        }

        // Obszar widoku (viewport)
        Rectangle viewRect = scrollPane.getViewport().getViewRect();
        int viewX = (int) (viewRect.x * scaleX);
        int viewY = (int) (viewRect.y * scaleY);
        int viewW = (int) (viewRect.width * scaleX);
        int viewH = (int) (viewRect.height * scaleY);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(viewX, viewY, viewW, viewH);
    }
}
