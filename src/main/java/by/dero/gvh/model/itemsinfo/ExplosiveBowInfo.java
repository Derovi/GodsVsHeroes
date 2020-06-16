package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class ExplosiveBowInfo extends ItemInfo {
    private double multiplier;
    private double reclining;
    private double radiusMultiplier;

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

    public double getRadiusMultiplier() {
        return radiusMultiplier;
    }

    public void setRadiusMultiplier(double radiusMultiplier) {
        this.radiusMultiplier = radiusMultiplier;
    }
}
