package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class ExplosiveBowInfo extends ItemInfo {
    private double multiplier;
    private double reclining;
    private double radius;

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getReclining() {
        return reclining;
    }

    public void setReclining(double reclining) {
        this.reclining = reclining;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
