package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class ChainLightningInfo extends ItemInfo {
    private double radius;
    private double damage;
    private int otherDamage;

    public ChainLightningInfo(ItemDescription description) {
        super(description);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getOtherDamage () {
        return otherDamage;
    }

    public void setOtherDamage (int otherDamage) {
        this.otherDamage = otherDamage;
    }
}
