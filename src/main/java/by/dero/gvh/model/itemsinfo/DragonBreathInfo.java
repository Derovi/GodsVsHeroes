package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class DragonBreathInfo extends ItemInfo {
    private double radius;
    private double damage;

    public DragonBreathInfo(ItemDescription description) {
        super(description);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getRadius() {
        return radius;
    }

    public double getDamage() {
        return damage;
    }

}
