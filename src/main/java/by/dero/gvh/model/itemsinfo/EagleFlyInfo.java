package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class EagleFlyInfo extends ItemInfo {
    private double speed;
    private int duration;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
