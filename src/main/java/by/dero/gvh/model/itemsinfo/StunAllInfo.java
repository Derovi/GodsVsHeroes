package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class StunAllInfo extends ItemInfo {
    private double radius;
    private int duration;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getDuration () {
        return duration;
    }

    public void setDuration (int duration) {
        this.duration = duration;
    }
}
