package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class ExchangeInfo extends ItemInfo {
    private double maxRange;

    public ExchangeInfo(ItemDescription description) {
        super(description);
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }
}
