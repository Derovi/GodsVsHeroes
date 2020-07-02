package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class WebThrowInfo extends ItemInfo {
    private int level;
    private int duration;
    private float force;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
