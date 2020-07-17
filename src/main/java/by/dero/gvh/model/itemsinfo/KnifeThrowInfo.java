package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.annotations.CustomDamage;
import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class KnifeThrowInfo extends ItemInfo {
    private double damage;

    @CustomDamage
    private int meleeDamage;

    public KnifeThrowInfo(ItemDescription description) {
        super(description);
    }

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
