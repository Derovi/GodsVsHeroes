package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class HealAllInfo extends ItemInfo {
    private double radius;
    private int heal;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getHeal() {
        return heal;
    }

    public void setHeal(int heal) {
        this.heal = heal;
    }
}
