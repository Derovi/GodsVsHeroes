package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemInfo;

public class FlyBowInfo extends ItemInfo {
    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    private double modifier = 1;
}
