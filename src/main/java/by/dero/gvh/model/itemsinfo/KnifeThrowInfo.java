package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class KnifeThrowInfo extends ItemInfo {
    private int damage;

    public KnifeThrowInfo(ItemDescription description) {
        super(description);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
