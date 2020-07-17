package by.dero.gvh.model.itemsinfo;

import by.dero.gvh.model.ItemDescription;
import by.dero.gvh.model.ItemInfo;

public class NinjaRopeInfo extends ItemInfo {
    private double distance;
    private double forceMultiplier;

    public NinjaRopeInfo(ItemDescription description) {
        super(description);
    }

    public double getForceMultiplier () {
        return forceMultiplier;
    }

    public void setForceMultiplier (double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }

    public double getDistance () {
        return distance;
    }

    public void setDistance (double distance) {
        this.distance = distance;
    }
}
