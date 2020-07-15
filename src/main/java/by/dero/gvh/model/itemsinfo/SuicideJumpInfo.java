package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class SuicideJumpInfo extends ItemInfo {
    private double radius;
    private double selfDamage;
    private double damage;

    public SuicideJumpInfo(ItemDescription description) {
        super(description);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getSelfDamage() {
        return selfDamage;
    }

    public void setSelfDamage(double selfDamage) {
        this.selfDamage = selfDamage;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
