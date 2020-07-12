package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class AxeThrowInfo extends ItemInfo {
    private double damage;
    private int meleeDamage;

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public int getMeleeDamage () {
        return meleeDamage;
    }

    public void setMeleeDamage (int meleeDamage) {
        this.meleeDamage = meleeDamage;
    }
}
