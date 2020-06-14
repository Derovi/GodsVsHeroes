package by.dero.gvh;


public class Cooldown {
    private long startTime;
    private long duration;

    public Cooldown(long duration) {
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public long getSecondsRemaining() {
        return duration - (System.currentTimeMillis() - startTime) / 1000;
    }

    public void makeReady() {
        startTime = 0;
    }

    public void reload() {
        startTime = System.currentTimeMillis();
    }

    public boolean isReady() {
        long currentTime = System.currentTimeMillis();
        return startTime + duration * 1000 >= currentTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }
}
