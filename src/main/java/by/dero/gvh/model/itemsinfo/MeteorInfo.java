package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class MeteorInfo extends ItemInfo {
    private double damage;
    private double radius;
    private int range;

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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
