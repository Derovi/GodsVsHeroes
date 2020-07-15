package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class MagicRodInfo extends ItemInfo {
    private double damage;
    private int duration;

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getDuration () {
        return duration;
    }

    public void setDuration (int duration) {
        this.duration = duration;
    }
}
