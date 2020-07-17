package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class FlyBowInfo extends ItemInfo {
    private double modifier = 1;

    public FlyBowInfo(ItemDescription description) {
        super(description);
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }
}
