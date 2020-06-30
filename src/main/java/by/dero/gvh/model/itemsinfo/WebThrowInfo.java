package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class WebThrowInfo extends ItemInfo {
    private float multiplier;
    private int duration;
    private int force;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
