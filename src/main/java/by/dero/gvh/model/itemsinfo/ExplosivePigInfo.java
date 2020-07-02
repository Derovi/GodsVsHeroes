package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class ExplosivePigInfo extends ItemInfo {
    private int duration;
    private double radius;
    private double damage;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
