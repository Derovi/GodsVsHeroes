package by.dero.gvh;

public class Cooldown {
    private long startTime;
    private final long duration;

    public Cooldown(long duration) {
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public long getSecondsRemaining() {
        return (long) (Math.ceil((double) duration / 20) - (System.currentTimeMillis() - startTime) / 1000);
    }

    // true if finished at last second
    public boolean isJustFinished() {
        return getSecondsRemaining() == 0 || getSecondsRemaining() == -1;
    }

    public void makeReady() {
        startTime = 0;
    }

    public void reload() {
        startTime = System.currentTimeMillis();
    }

    public boolean isReady() {
        final long currentTime = System.currentTimeMillis();
        return startTime + duration * 50 < currentTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }
}
