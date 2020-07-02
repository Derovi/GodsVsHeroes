package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class SkeletonArmyInfo extends ItemInfo {
    private int melee;
    private int range;

    public int getMelee() {
        return melee;
    }

    public void setMelee(int melee) {
        this.melee = melee;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
