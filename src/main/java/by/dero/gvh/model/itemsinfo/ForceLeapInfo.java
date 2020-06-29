package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class ForceLeapInfo extends ItemInfo {
    private double force;
    private int duration;

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
