public class GridMap {
    private final int cols = 3000 / 50;
    private final int rows = 3000 / 50;
    private final boolean[][] occupied;

    public GridMap() {
        occupied = new boolean[cols][rows];
    }

    public void setOccupied(int x, int y, boolean isOccupied) {
        int col = x / 50;
        int row = y / 50;
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            occupied[col][row] = isOccupied;
        }
    }

    public boolean isOccupied(int x, int y) {
        int col = x / 50;
        int row = y / 50;
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            return occupied[col][row];
        }
        return true; // traktuj poza mapą jako zajęte
    }
}
