import java.awt.Point;
import java.util.*;

public class Pathfinder {
    private final GridMap grid;
    private final int cols;
    private final int rows;
    private final int tileSize = 40;

    public Pathfinder(GridMap grid) {
        this.grid = grid;
        this.cols = 4000 / tileSize;
        this.rows = 4000 / tileSize;
    }

    public List<Point> findPath(Point start, Point end) {
        int startX = start.x;
        int startY = start.y;
        int endX = end.x;
        int endY = end.y;

        boolean[][] closed = new boolean[cols][rows];
        PriorityQueue<PathNode> open = new PriorityQueue<>(Comparator.comparingInt(PathNode::getFCost));

        PathNode startNode = new PathNode(startX, startY, null, 0, heuristic(startX, startY, endX, endY));
        open.add(startNode);

        while (!open.isEmpty()) {
            PathNode current = open.poll();

            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            closed[current.x][current.y] = true;

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
                if (closed[nx][ny]) continue;
                if (grid.isOccupied(nx * tileSize, ny * tileSize)) continue;

                int gCost = current.gCost + 1;
                int hCost = heuristic(nx, ny, endX, endY);
                PathNode neighbor = new PathNode(nx, ny, current, gCost, hCost);
                open.add(neighbor);
            }
        }

        return new ArrayList<>(); // brak ścieżki
    }

    private int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2); // Manhattan
    }

    private List<Point> reconstructPath(PathNode node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new Point(node.x * tileSize, node.y * tileSize));
            node = node.parent;
        }
        return path;
    }

    private static final int[][] directions = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };
}
