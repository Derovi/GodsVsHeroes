package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class AirLeapInfo extends ItemInfo {
    private double force;

    public AirLeapInfo(ItemDescription description) {
        super(description);
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }
}
