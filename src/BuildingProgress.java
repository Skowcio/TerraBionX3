

public class BuildingProgress {
    int x, y;
    String type;        // <-- zamiast BuildingType
    long startTime;
    long buildDuration; // w ms

    public BuildingProgress(int x, int y, String type, long buildDuration) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.buildDuration = buildDuration;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= buildDuration;
    }

    public double getProgress() {
        return Math.min(1.0, (double)(System.currentTimeMillis() - startTime) / buildDuration);
    }

    public String getType() {
        return type;
    }
}