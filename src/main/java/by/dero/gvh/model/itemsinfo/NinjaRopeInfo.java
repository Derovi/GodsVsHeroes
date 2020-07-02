package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class NinjaRopeInfo extends ItemInfo {
    private int range;
    private double forceMultiplier;

    public double getForceMultiplier() {
        return forceMultiplier;
    }

    public void setForceMultiplier(double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
