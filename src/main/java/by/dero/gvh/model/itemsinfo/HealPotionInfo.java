package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class HealPotionInfo extends ItemInfo {
    private double radius = 3;
    private int heal = 4;

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
