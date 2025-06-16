public class PathNode implements Comparable<PathNode> {
    public final int x, y;
    public int gCost, hCost;
    public PathNode parent;

    public PathNode(int x, int y, PathNode parent, int gCost, int hCost) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    @Override
    public int compareTo(PathNode other) {
        int fCompare = Integer.compare(this.getFCost(), other.getFCost());
        if (fCompare == 0) {
            return Integer.compare(this.hCost, other.hCost); // tie-breaker
        }
        return fCompare;
    }
}
